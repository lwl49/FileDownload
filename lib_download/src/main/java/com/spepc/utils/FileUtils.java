package com.spepc.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

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
     */
    public static void installApk(Context context, String apkPath) {
        Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", new File(apkPath));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);

    }
    /**
     *  反射获取配置文件某参数值，需要先设置 packetName
     * */
    public static String getFieldValue(String packetName,String key) {

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
            ZLog.log(FileUtils.class,TAG,"VersionInfo Exception" + e);
        }
        return 0;
    }

}
