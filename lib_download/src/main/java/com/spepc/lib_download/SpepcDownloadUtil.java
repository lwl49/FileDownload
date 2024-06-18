package com.spepc.lib_download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.spepc.utils.FileUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Author lwl
 * 日期    2024/3/6
 * 目的    下载工具类
 */
public class SpepcDownloadUtil {

    File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    public static final int DIRTYPE_IMG = 1;
    public static final int DIRTYPE_VED = 2;
    public static final int DIRTYPE_DOWNLOAD = 3;

    static SpepcDownloadUtil ins;
    Context context;
    String packName = ""; // 如果设置了，就用外部设置的包名，如果没有设置  就是context.getPackageName()
    String downloadDesc;
    LinkedHashMap<Long, ModelBean> downMap; // 通知栏 消息ID 确定是下载哪一个
    DownloadListener downloadListener;
    DownloadManager dm;
    int dirType; // 文件下载类型
    String suffix;

    String TAG = "SpepcDownloadUtil";

    /***
     *
     * */
    public static SpepcDownloadUtil getIns(Context context) {
        if (ins == null) {
            ins = new SpepcDownloadUtil(context);
        }
        return ins;
    }

    Handler handler;

    public SpepcDownloadUtil(Context context) {
        this.context = context;

        handler = new MyHandler(Looper.getMainLooper(), context);

        dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downMap = new LinkedHashMap<>();

        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(broadcastReceiver, intentFilter);


    }

    /**
     * 设置 文件后缀名
     *
     * @param dirType 下载文件类型  1，图片 2 视频 3 download 目录
     */
    public SpepcDownloadUtil setFilePosition(int dirType) {
        this.dirType = dirType;
        return ins;
    }

    /**
     * 设置 文件保存包名（一般是app名称）不设置就默认app 包名
     *
     * @param packName 外部设置 保存文件夹的名称
     */
    public SpepcDownloadUtil setPackName(String packName) {
        this.packName = packName;
        return ins;
    }

    /**
     * 设置 下载完成监听
     */
    public SpepcDownloadUtil downloadComplete(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
        return ins;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //   String  path = "content://com.android.providers.downloads.documents/document/"+ID;
            ModelBean mb = downMap.get(ID);
            if (mb != null) {
                Message message = new Message();
                Gson gson = new Gson();
                message.obj = gson.toJson(mb);
                handler.sendMessage(message);
            }

        }
    };

    /**
     * @param url 文件下载地址
     * @param fileName 文件名称 包含后缀名
     * 使用 系统下载器 下载 图片
     */
    public void startDMDownLoad(String url, String fileName) {

        Toast.makeText(context, "开始下载", Toast.LENGTH_SHORT).show();
        DownloadManager.Request dmRequest = new DownloadManager.Request(
                Uri.parse(url));
        dmRequest.allowScanningByMediaScanner();
        dmRequest.setTitle(fileName);
        dmRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
//        dmRequest.setDestinationUri(Uri.fromFile(new File(downloadDesc, fileName)));  // 自定义目录
//        dmRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM , fileName);  // 系统目录
        dmRequest.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName);//android/data/包名/

        long id = dm.enqueue(dmRequest);
        Log.e("DownloadUtils", "id = " + id);
        downMap.put(id, new ModelBean(id, fileName, context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()));
    }

    /**
     * 取消原生下载
     */
    public void stopDMRequest(long id) {
        if (downMap.containsKey(id)) {
            dm.remove(id);
        }
    }

    /**
     * 获取文件夹下所有文件
     */
    public List<File> getDocFile(String path) {
        List<File> files = new ArrayList<>();

        File file = new File(path);
        if (file.exists()) {
            File[] ff = file.listFiles();
            files = Arrays.asList(ff);
        }
        return files;
    }

    /**
     * 文本文件默认在download/packName/ 下面
     * 没有文件就下载，有文件就打开
     */
    public void noFileToDownload(String url, String fileName) {
        String filePath = downloadDir.getAbsolutePath() + "/" + (packName.isEmpty() ? context.getPackageName() : packName) + "/" + fileName;
        if (new File(filePath).exists()) {
            if (!checkWps()) {
                Toast.makeText(context, "请使用 wps 再打开文件", Toast.LENGTH_LONG).show();
                return;
            }
            //OPEN file
            Log.e(TAG, "xxx-----filePath  = " + filePath);
            Uri uri = Uri.parse(filePath);
            Log.e(TAG, "xxx-----uri  = " + uri);
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//设置标记
            intent.setAction(Intent.ACTION_VIEW);//动作，查看
            intent.setClassName("cn.wps.moffice_eng",
                    "cn.wps.moffice.documentmanager.PreStartActivity2");
            intent.setData(uri);
            context.startActivity(intent);
        } else {
            startDMDownLoad(url, fileName);
        }

    }

    private boolean checkWps() {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage("cn.wps.moffice_eng");//WPS个人版的包名
        if (intent == null) {
            return false;
        } else {
            return true;
        }
    }

    class MyHandler extends Handler {
        WeakReference<Context> mainActivityWeakReference;

        //构造函数传入外部的this
        public MyHandler(@NonNull Looper looper, Context mainActivity) {
            super(looper);
            //构建一个弱引用对象
            mainActivityWeakReference = new WeakReference<Context>(mainActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            //判断活动是否还存在，如果不存在则结束
            Context context = mainActivityWeakReference.get();
            if (context == null) {
                return;
            }

            Gson gson = new Gson();
            ModelBean modelBean = gson.fromJson((String) msg.obj, ModelBean.class);
//            FileUtils.copyFile(context, modelBean.filePath, modelBean.name, Environment.DIRECTORY_DCIM);
            String filePath = modelBean.filePath + "/" + modelBean.name; // 目标原路径
            Log.i(TAG, "filePath = " + filePath);
            String outFilePath = "";
            if (modelBean.name.endsWith(".apk")) {
                // 安装apk
                FileUtils.installApk(context, filePath);
                return;
            } else if (modelBean.name.endsWith(".jpg")) {
                //保存图片文件
                outFilePath = MediaStoreInsertHelper.insertImage(context, StringUtils.isNotEmpty(packName) ? packName : context.getPackageName(), new File(filePath));
            } else if (modelBean.name.endsWith(".mp4")) {
                //保存视频文件
                outFilePath = MediaStoreInsertHelper.insertVideo(context, StringUtils.isNotEmpty(packName) ? packName : context.getPackageName(), new File(filePath));
            } else {
                //全部存入download目录
                outFilePath = MediaStoreInsertHelper.insertFileIntoDownload(context, StringUtils.isNotEmpty(packName) ? packName : context.getPackageName(), new File(filePath));
            }
//            MediaScannerConnection.scanFile(context, new String[]{mb.filePath}, null, null);
            if (downloadListener != null) {
                if (StringUtils.isNotEmpty(outFilePath)) {
                    ModelBean modelBean1 = new ModelBean();
                    modelBean1.filePath = outFilePath;
                    modelBean1.name = modelBean.name;
                    downloadListener.post(modelBean1);
                } else {
                    downloadListener.post(null);
                }

            }

        }
    }


}
