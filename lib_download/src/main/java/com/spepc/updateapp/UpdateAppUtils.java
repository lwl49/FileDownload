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
     * @param paramBuild 参数  ;
     *                   activity  必填
     *                   apiKey  用户身份 api_key; 必填
     *                   appKey  应用 app_key;必填
     *                   showToast 是否需要弹出新版提示;
     *                   useCostDialog 默认使用sdk内部弹窗;
     *                   loadingInterface 检查开始，结束，便于外部增加 加载框等操作
     */
    public static void updateAPP(UpdateParamBuild paramBuild) {
        if(paramBuild==null){
            return;
        }
        if(paramBuild.activity==null){
            return;
        }
        if(StringUtils.isNullOrEmpty(paramBuild.apiKey)){
            Toast.makeText(paramBuild.activity,"缺少apiKey",Toast.LENGTH_SHORT).show();
            return;
        }
        if(StringUtils.isNullOrEmpty(paramBuild.appKey)){
            Toast.makeText(paramBuild.activity,"缺少appKey",Toast.LENGTH_SHORT).show();
            return;
        }
        if(paramBuild.loadingInterface!=null){
            paramBuild.loadingInterface.startLoading();
        }
        new UpdateChecker(paramBuild.apiKey).check(paramBuild.appKey, "", null, ""
                , new UpdateChecker.Callback() {
                    @Override
                    public void result(UpdateChecker.UpdateInfo updateInfo) {
                        if(paramBuild.loadingInterface!=null){
                            paramBuild.activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    paramBuild.loadingInterface.success(updateInfo);
                                }
                            });
                        }
                        ZLog.log(UpdateAppUtils.class, TAG, "updateInfo = " + updateInfo.toString());
                        int code = FileUtils.getAppVersionCode(paramBuild.activity);
                        if (updateInfo.buildVersionNo != null && code < Integer.parseInt(updateInfo.buildVersionNo)) {
                            if(paramBuild.useCostDialog){
                                return;
                            }
                            new XPopup.Builder(paramBuild.activity)
                                    .isDestroyOnDismiss(true)
                                    .dismissOnBackPressed(false)
                                    .dismissOnTouchOutside(false)
                                    .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                                    .asCustom(new CommonUpdatePop(paramBuild.activity, StringUtils.isNotEmpty(updateInfo.forceUpdateVersion), updateInfo.buildUpdateDescription, updateInfo.buildVersion, updateInfo.downloadURL))
                                    .show();
                        } else {
                            if (paramBuild.showToast) {
                                paramBuild.activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(paramBuild.activity, "已是最新版本", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void error(String message) {
                        if (paramBuild.showToast) {
                            paramBuild.activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(paramBuild.activity, message, Toast.LENGTH_SHORT).show();
                                    if(paramBuild.loadingInterface!=null){
                                        paramBuild.loadingInterface.error(message);
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
        void success(UpdateChecker.UpdateInfo updateInfo);
        void error(String message);
    }
}
