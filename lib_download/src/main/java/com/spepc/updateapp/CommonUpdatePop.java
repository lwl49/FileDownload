package com.spepc.updateapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.util.XPopupUtils;
import com.spepc.libDownload.R;
import com.spepc.libDownload.databinding.LibDownloadUpdatePopBinding;
import com.spepc.lib_download.MyPreferencesUtil;
import com.spepc.lib_download.SpepcDownloadUtil;
import com.spepc.lib_download.StringUtils;
import com.spepc.utils.ZLog;

/**
 * @Author lwl
 * 日期    2022/10/31
 * 目的    升级更新提示框
 */
public class CommonUpdatePop extends CenterPopupView implements View.OnClickListener {
    static String TAG = CommonUpdatePop.class.getName();

    String title, content, version, url, code;
    LibDownloadUpdatePopBinding mBinding;
    boolean isNeedForceUpdate;  // 是否强制更新

    UpdateChecker.UpdateInfo updateInfo;

    public CommonUpdatePop(@NonNull Context context, boolean isNeedForceUpdate, String content, String version, String url, String code) {
        super(context);
        this.content = content;
        this.version = version;
        this.code = code; // 保存最新版本的 code  用于 忽略该版本使用，需要结合 报名来保存
        this.url = url;
        this.isNeedForceUpdate = isNeedForceUpdate;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.lib_download_update_pop;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mBinding = LibDownloadUpdatePopBinding.bind(getPopupImplView());
        mBinding.tvVersion.setText(version);
        if (StringUtils.isNotEmpty(content)) {
            mBinding.tvDescribe.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
        }
        mBinding.btnLeft.setVisibility(isNeedForceUpdate ? View.GONE : View.VISIBLE);
        mBinding.cbIgnoreVersion.setVisibility(isNeedForceUpdate ? View.GONE : View.VISIBLE);


        mBinding.btnLeft.setOnClickListener(this);
        mBinding.btnRight.setOnClickListener(this);
        mBinding.cbIgnoreVersion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 保存忽略的版本号
                if (isChecked) {
                    //忽略
                    MyPreferencesUtil.getInstance(getContext()).saveString(MyPreferencesUtil.APK_VERSION_CODE, code);
                    new XPopup.Builder(getContext())
                            .isDestroyOnDismiss(true)
                            .dismissOnBackPressed(false)
                            .dismissOnTouchOutside(false)
                            .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                            .asConfirm("警告", "点击将永久忽略该版本升级提示，如需打开，清除应用缓存即可", new OnConfirmListener() {
                                @Override
                                public void onConfirm() {
                                    dismiss();
                                }
                            }, new OnCancelListener() {
                                @Override
                                public void onCancel() {
                                    mBinding.cbIgnoreVersion.setChecked(false);
                                }
                            })
                            .show();
                } else {
                    //开放
                    MyPreferencesUtil.getInstance(getContext()).saveString(MyPreferencesUtil.APK_VERSION_CODE, "");
                }
            }
        });


        //--------------------
//        ZLog.log(getClass(), "packName = "+getContext().getPackageName());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnLeft) {
            if (isNeedForceUpdate) {
                System.exit(0);  // 强制更新  退出应用
            } else {
                dismiss();
            }
        } else if (v.getId() == R.id.btnRight) {
            ApplicationInfo appInfo;
            String appName;
            try {

                appInfo = getContext().getPackageManager().getApplicationInfo(getContext().getPackageName(), PackageManager.GET_META_DATA);

                appName = appInfo.loadLabel(getContext().getPackageManager()) + "";


                PackageManager packageManager = getContext().getPackageManager();
                // getPackageName()是你当前类的包名，0代表是获取版本信息
                String version = this.version;


                Log.d("PhoneUtils", "appName=" + appName);
                String fileName = appName + version + ".apk";
                SpepcDownloadUtil.getIns(getContext()).setFilePosition(3).downloadComplete(
                                bean -> Toast.makeText(getContext(), "下载完成 ： " + bean.name, Toast.LENGTH_SHORT).show())
                        .startDMDownLoad(url.replace("\\", ""), fileName);
                if (!isNeedForceUpdate) {
                    dismiss();
                }

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }


        }
    }
}
