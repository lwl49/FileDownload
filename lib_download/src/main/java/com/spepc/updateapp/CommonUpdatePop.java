package com.spepc.updateapp;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.CenterPopupView;
import com.spepc.libDownload.R;
import com.spepc.libDownload.databinding.LibDownloadUpdatePopBinding;
import com.spepc.lib_download.SpepcDownloadUtil;

/**
 * @Author lwl
 * 日期    2022/10/31
 * 目的    升级更新提示框
 */
public class CommonUpdatePop extends CenterPopupView implements View.OnClickListener {
    static String TAG = CommonUpdatePop.class.getName();

    String title, content, version, url;
    LibDownloadUpdatePopBinding mBinding;
    boolean isNeedForceUpdate;  // 是否强制更新

    public CommonUpdatePop(@NonNull Context context, boolean isNeedForceUpdate, String content, String version, String url) {
        super(context);
        this.content = content;
        this.version = version;
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
        mBinding.tv1.setText(version);
        mBinding.tv2.setText(content);
        mBinding.btnLeft.setVisibility(isNeedForceUpdate ? View.GONE : View.VISIBLE);


        mBinding.btnLeft.setOnClickListener(this);
        mBinding.btnRight.setOnClickListener(this);
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
                PackageInfo packInfo = packageManager.getPackageInfo(getContext().getPackageName(), 0);
                String version = packInfo.versionName;


                Log.d("PhoneUtils", "appName=" + appName);
                String fileName = appName + version + ".apk";
                SpepcDownloadUtil.getIns(getContext()).setFilePosition(3).downloadComplete(
                        bean -> Toast.makeText(getContext(), "下载完成 ： " + bean.name, Toast.LENGTH_SHORT).show())
                        .startDMDownLoad(url.replace("\\",""), fileName);
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
