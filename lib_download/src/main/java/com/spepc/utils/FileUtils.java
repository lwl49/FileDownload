package com.spepc.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

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
        Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName()+".provide.download.fileDownloadProvider", new File(apkPath));
//        Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", new File(apkPath));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);

    }
    /**
     *
     *  这是隐式安装，需要让用户打开安装未知来源，已经被弃用，需要反射，而且由于 google 保护机制，可能无效的，所以弃用
     * @param apkPath 文件一定是可读的
     * */
    public static void installApkByInstaller(Context context, String apkPath) {

        if(!new File(apkPath).exists()){
            ZLog.log(FileUtils.class,"文件不存在");
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

}
