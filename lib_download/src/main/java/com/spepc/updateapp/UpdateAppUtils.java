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
     */
    public static void updateAPP(Activity activity, String apiKey, String appKey, boolean showToast) {

        new UpdateChecker(apiKey).check(appKey, "", null, ""
                , new UpdateChecker.Callback() {
                    @Override
                    public void result(UpdateChecker.UpdateInfo updateInfo) {
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
}
