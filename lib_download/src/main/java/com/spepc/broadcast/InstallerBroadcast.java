package com.spepc.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;

import com.spepc.utils.FileUtils;
import com.spepc.utils.ZLog;

/**
 * @Author lwl
 * 日期    2024/9/29
 * 目的
 */
public class InstallerBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int statusCode = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE);
        String statusMessage = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE);
        ZLog.log(InstallerBroadcast.class, "xxxx - InstallerBroadcast", "statusCode = " + statusCode + " statusMessage = " + statusMessage);
        switch (statusCode) {
            case PackageInstaller.STATUS_SUCCESS:
                // 安装成功
                break;
            case PackageInstaller.STATUS_FAILURE:
            case PackageInstaller.STATUS_FAILURE_ABORTED:
            case PackageInstaller.STATUS_FAILURE_BLOCKED:
            case PackageInstaller.STATUS_FAILURE_CONFLICT:
            case PackageInstaller.STATUS_FAILURE_INCOMPATIBLE:
            case PackageInstaller.STATUS_FAILURE_INVALID:
            case PackageInstaller.STATUS_FAILURE_STORAGE:
                // 安装失败
                break;
            // 处理其他状态码...
        }
    }
}
