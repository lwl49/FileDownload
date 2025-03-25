package com.spepc.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.spepc.broadcast.InstallerBroadcast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @Author lwl
 * 日期    2024/6/11
 * 目的
 */
public class FileUtils {
    public static String packetName = "";

    private static final String TAG = FileUtils.class.getName();

    public static void copyFile(Context mContext, String filePath, String fileName, String envType) {
        File sourceFile = new File(filePath, fileName);
        if (!sourceFile.exists()) {
            Log.i("FileUtils", "文件不存在");
            return;
        }

        File destFile = new File(Environment.getExternalStoragePublicDirectory(envType), fileName);

        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(destFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

            // 文件复制成功
            MediaScannerConnection.scanFile(mContext, new String[]{destFile.getAbsolutePath()}, null, null);
            if (sourceFile.exists()) {
                sourceFile.delete();
            }
        } catch (IOException e) {
            Log.e("FileUtils", e.getMessage());
        }
    }

    /**
     * 7.0以上安装 APK 需要 manifest 清单文件 支持 FileProvider
     * 使用硬编码 provide.download.fileDownloadProvider  与宿主区分开，避免宿主已经声明了 ${applicationId}.fileProvider 产生冲突
     */
    public static void installApk(Context context, String apkPath) {
        Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provide.download.fileDownloadProvider", new File(apkPath));
//        Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", new File(apkPath));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);

    }

    /**
     * 这是隐式安装，需要让用户打开安装未知来源，已经被弃用，需要反射，而且由于 google 保护机制，可能无效的，所以弃用
     *
     * @param apkPath 文件一定是可读的
     */
    public static void installApkByInstaller(Context context, String apkPath) {

        if (!new File(apkPath).exists()) {
            ZLog.log(FileUtils.class, "文件不存在");
            return;
        }

        //获取PackageInstaller的实例
        PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
        //创建一个安装会话（PackageInstaller.Session）
        int sessionId = 0;
        try {
            sessionId = packageInstaller.createSession(new PackageInstaller.SessionParams(
                    PackageInstaller.SessionParams.MODE_FULL_INSTALL));
            PackageInstaller.Session session = packageInstaller.openSession(sessionId);

            InputStream in = Files.newInputStream(Paths.get(apkPath));
            OutputStream out = session.openWrite("package", 0, new File(apkPath).length());

            byte[] buffer = new byte[65536];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            session.fsync(out);
            in.close();
            out.close();
            statusReceiver = PendingIntent.getBroadcast(
                    context,
                    sessionId,
                    new Intent(context, InstallerBroadcast.class),
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            session.commit(statusReceiver.getIntentSender());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    static PendingIntent statusReceiver;

    /**
     * 反射获取配置文件某参数值，需要先设置 packetName
     */
    public static String getFieldValue(String packetName, String key) {

        try {
            Class<?> cls = Class.forName(packetName);
            Field serverAddress = cls.getDeclaredField(key);
            serverAddress.setAccessible(true);
            String value = (String) serverAddress.get(null);
//            Log.e("RxHttp - value", "value = " + value);
            ZLog.log(FileUtils.class, "xxx", "key = " + key + " value = " + value);
            return value;
        } catch (Exception e) {
            return null;
        }
    }

    //获取当前程序版本名(对消费者不可见的版本号)
    public static int getAppVersionCode(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (Exception e) {
            ZLog.log(FileUtils.class, TAG, "VersionInfo Exception" + e);
        }
        return 0;
    }

    /**
     * 如果未被 MediaStore 存入，需要手动索引到 MediaStore
     */
    public static Uri indexFileToMediaStore(Context context, String filePath) {
        try {
            File file = new File(filePath);
            ContentValues values = new ContentValues();
            values.put(MediaStore.Files.FileColumns.DATA, file.getAbsolutePath());
            values.put(MediaStore.Files.FileColumns.TITLE, file.getName());
            values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, file.getName());
            values.put(MediaStore.Files.FileColumns.MIME_TYPE, getMimeType(filePath));
            values.put(MediaStore.Files.FileColumns.SIZE, file.length());
            Uri uri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
            return uri;
        } catch (Exception e) {
            return null;
        }

    }

    public static String getMimeType(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf(".") + 1);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    /**
     * 使用saf 框架选中某个需要打开的文件
     */
    public static void openSAF(Activity activity, int REQUEST_CODE_PICK_FILE) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        activity.startActivityForResult(intent, REQUEST_CODE_PICK_FILE);
    }

    public static Uri getUriFromFilePath(Context context, String filePath) {
        try {
            File file = new File(filePath);
            String[] projection = {MediaStore.Files.FileColumns._ID};
            String selection = MediaStore.Files.FileColumns.DATA + "=?";
            String[] selectionArgs = new String[]{file.getAbsolutePath()};

            Uri uri = MediaStore.Files.getContentUri("external");
            ContentResolver resolver = context.getContentResolver();
            Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
                long fileId = cursor.getLong(idIndex);
                cursor.close();
                return ContentUris.withAppendedId(uri, fileId);
            }
            return null;
        } catch (Exception e) {
            return null;
        }

    }


    /**
     * @param uri 通过saf选中的uri，或者 别的方式获取的uri
     */
    public static void openOfficeFile(Context context, Uri uri) {
        if (uri != null) {
            String mimeType = context.getContentResolver().getType(uri);
            Log.d("spdownload-utils", "xxx-- mimeType = " + mimeType);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, mimeType == null ? "*/*" : mimeType);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // 创建选择器
            Intent chooser = Intent.createChooser(intent, "文件打开");
            // 检查是否有应用可以处理该 Intent
            context.startActivity(chooser);
        } else {
            Toast.makeText(context, "uri 无效,请去文件管理器查找文件", Toast.LENGTH_LONG).show();
        }
    }

}
