package com.spepc.lib_download;

/**
 * @Author lwl
 * 日期    2024/3/6
 * 目的    下载完成 发送 广播通知
 */
public interface DownloadListener  {
   void post(ModelBean bean);  // 下载完成
}
