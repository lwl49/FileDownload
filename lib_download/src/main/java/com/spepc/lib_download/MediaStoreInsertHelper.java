package com.spepc.lib_download;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

/**
 * @Author lwl
 * 日期    2024/4/16
 * 目的
 */
public class MediaStoreInsertHelper {
    static String TAG = MediaStoreInsertHelper.class.getName();

    /**
     * @param file        目录 可读的文件
     * @param destDir     文件保存目录  picture/xxx
     */
    public static String insertImage(Context context, String destDir, File file) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, file.getName());
        values.put(MediaStore.Images.Media.DISPLAY_NAME, file.getName());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + destDir);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        // 如果externalImageUri不为null，你可以使用OutputStream将其写入
        try {
            OutputStream outputStream = contentResolver.openOutputStream(uri);
            FileInputStream inputStream = new FileInputStream(file.getAbsoluteFile());
            if (outputStream != null) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + destDir + "/" + file.getName();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            return "";
            // Handle exception
        }
    }

    /**
     * @param file 目录 可读的文件
     * @param destDir     文件保存目录  picture/xxx
     */
    public static String insertVideo(Context context, String destDir, File file) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.TITLE, file.getName());
        values.put(MediaStore.Video.Media.DISPLAY_NAME, file.getName());
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + destDir);
        values.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        try {
            OutputStream outputStream = contentResolver.openOutputStream(uri);
            FileInputStream inputStream = new FileInputStream(file.getAbsoluteFile());
            if (outputStream != null) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + destDir + "/" + file.getName();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            return "";
            // Handle exception
        }
    }

    /**
     * @param file 目录 可读的文件
     * @param destDir     文件保存目录  picture/xxx
     */
    public static String insertFileIntoDownload(Context context,String destDir, File file) {
        ContentResolver contentResolver = context.getContentResolver();

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, file.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + destDir);

        Uri externalUri = MediaStore.Files.getContentUri("external");
        Uri insertUri = contentResolver.insert(externalUri, values);

        // 如果需要将文件数据写入MediaStore
        if (insertUri != null) {
            // 这里需要使用FileProvider或者直接文件路径来获取文件的Uri
            // 然后使用ContentResolver将文件数据写入insertUri

            try {
                OutputStream outputStream = contentResolver.openOutputStream(insertUri);
                FileInputStream inputStream = new FileInputStream(file.getAbsoluteFile());
                if (outputStream != null) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + destDir + "/" + file.getName();
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                return "";
                // Handle exception
            }
        }
        return "";
    }
}
