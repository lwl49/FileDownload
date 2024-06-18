package com.spepc.updateapp;

import android.app.Activity;

/**
 * @Author lwl
 * 日期    2024/6/17
 * 目的    配置请求更新参数
 */
public class UpdateParamBuild {
    public Activity activity;
    public String apiKey;
    public String appKey;
    public boolean showToast;     //是否弹出提示
    public boolean useCostDialog; // 是否使用自定义升级弹窗  默认false 默认使用 sdk 的弹窗
    public UpdateAppUtils.LoadingInterface loadingInterface; // 结果返回接口
}
