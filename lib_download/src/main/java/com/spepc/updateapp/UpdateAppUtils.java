package com.spepc.updateapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.lxj.xpopup.XPopup;
import com.spepc.lib_download.StringUtils;
import com.spepc.utils.FileUtils;
import com.spepc.utils.ZLog;
//import com.pgyer.pgyersdk.PgyerSDKManager;
//import com.pgyer.pgyersdk.callback.CheckoutCallBack;
//import com.pgyer.pgyersdk.model.CheckSoftModel;

/**
 * @Author lwl
 * 日期    2024/4/22
 * 目的    更新app
 */
public class UpdateAppUtils {
    static String TAG = UpdateAppUtils.class.getName();


    /**
     * 使用4.+以上版本
     * CommonLogUtils.e("xxx - checkSoftModel = "+checkSoftModel);
     * CommonLogUtils.e("xxx - checkSoftModel = onFail = "+s);
     * 蒲公英升级  需要蒲公英SDK
     * @param activity
     * @param apiKey  用户身份 api_key
     * @param appKey  应用 app_key
     * @param showToast 是否需要弹出新版提示
     * @param loadingInterface 检查开始，结束，便于外部增加 加载框等操作
     */
    public static void updateAPP(Activity activity, String apiKey, String appKey, boolean showToast,LoadingInterface loadingInterface) {
        if(loadingInterface!=null){
            loadingInterface.startLoading();
        }
        new UpdateChecker(apiKey).check(appKey, "", null, ""
                , new UpdateChecker.Callback() {
                    @Override
                    public void result(UpdateChecker.UpdateInfo updateInfo) {
                        if(loadingInterface!=null){
                            loadingInterface.endLoading();
                        }
                        ZLog.log(UpdateAppUtils.class, TAG, "updateInfo = " + updateInfo.toString());
                        int code = FileUtils.getAppVersionCode(activity);
                        if (updateInfo.buildVersionNo != null && code < Integer.parseInt(updateInfo.buildVersionNo)) {
                            new XPopup.Builder(activity)
                                    .isDestroyOnDismiss(true)
                                    .dismissOnBackPressed(false)
                                    .dismissOnTouchOutside(false)
                                    .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                                    .asCustom(new CommonUpdatePop(activity, StringUtils.isNotEmpty(updateInfo.forceUpdateVersion), updateInfo.buildUpdateDescription, updateInfo.buildVersion, updateInfo.downloadURL))
                                    .show();
                        } else {
                            if (showToast) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity, "已是最新版本", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void error(String message) {
                        if (showToast) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                                    if(loadingInterface!=null){
                                        loadingInterface.endLoading();
                                    }
                                }
                            });
                        }
                    }
                });

//        PgyerSDKManager.checkVersionUpdate(activity, new CheckoutCallBack() {
//            @Override
//            public void onNewVersionExist(CheckSoftModel checkSoftModel) {
//                Log.e(TAG,"checkSoftModel onNewVersionExist = " + checkSoftModel);
//                new XPopup.Builder(activity)
//                        .isDestroyOnDismiss(true)
//                        .dismissOnBackPressed(false)
//                        .dismissOnTouchOutside(false)
//                        .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
//                        .asCustom(new CommonUpdatePop(activity, checkSoftModel.isNeedForceUpdate(), checkSoftModel.getBuildUpdateDescription(), checkSoftModel.getBuildVersion(), checkSoftModel.getDownloadURL()))
//                        .show();
//            }
//
//            @Override
//            public void onNonentityVersionExist(String s) {
//                if (needShowToast) {
//
//                    Toast.makeText(activity,"已是最新版本",Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFail(String s) {
//                Log.e(TAG,"checkVersionUpdate  onFail= " + s);
//            }
//        });
    }


    public interface LoadingInterface{
        void startLoading();
        void endLoading();
    }
}
