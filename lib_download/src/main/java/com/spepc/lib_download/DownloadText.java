package com.spepc.lib_download;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.spepc.updateapp.UpdateAppUtils;
import com.spepc.updateapp.UpdateParamBuild;

/**
 * @Author lwl
 * 日期    2024/6/13
 * 目的    直接按照如下执行下载操作  ，内部会根据后缀名 保存相应文件夹，图片，视频 保存在  Environment.DIRECTORY_PICTURES
 * 其他文件保存在  Environment.DIRECTORY_DOWNLOAD/XXX/  XXX是 setPackName "框架采集仪"
 */
public class DownloadText {
    public static void download(Context context) {

        String imgPath = "http://120.25.166.132:49090/fcu/990580/20240613145242/1.jpg";
        String vedPath = "http://120.25.166.132:49090/fcu/9905d8/20240613144536/1.mp4";
        String filePath = "http:\\/\\/192.168.1.33:9000\\/spepc-maintenance-dfw-test\\/2024\\/05\\/d2b5dc89093d4804ae9272a03bc64f64.docx".replace("\\", "");
        SpepcDownloadUtil.getIns(context).setPackName("框架采集仪TEST").downloadComplete(new DownloadListener() {
                    @Override
                    public void post(ModelBean bean) {
                        try {
                            if (bean == null) {
                                Toast.makeText(context, "下载异常", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "下载完成 ： 文件保存在" + bean.filePath, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {

                        }

                    }
                })
                .startDMDownLoad(imgPath, System.currentTimeMillis() + "_fileName.jpg");
    }

    public static void upgrade(UpdateParamBuild paramBuild) {

        UpdateAppUtils.updateAPP(paramBuild);
    }
}
