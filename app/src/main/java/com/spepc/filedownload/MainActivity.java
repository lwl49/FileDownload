package com.spepc.filedownload;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.spepc.lib_download.DownloadText;
import com.spepc.filedownload.R;
import com.spepc.updateapp.UpdateAppUtils;
import com.spepc.updateapp.UpdateChecker;
import com.spepc.updateapp.UpdateParamBuild;
import com.spepc.utils.FileUtils;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    TextView textView;
    MainActivity ins;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ins = this;
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.tvProgress);
        PermissionX.init(MainActivity.this)
                .permissions(Manifest.permission_group.STORAGE)

                .onExplainRequestReason(new ExplainReasonCallback() {
                    @Override
                    public void onExplainReason(@androidx.annotation.NonNull ExplainScope explainScope, @NonNull List<String> list) {
                        String message = "需要您同意以下权限才能正常使用";
                        explainScope.showRequestReasonDialog(list, message, "同意", "不同意");
                    }
                }).request(new RequestCallback() {
                    @Override
                    public void onResult(boolean b, @NonNull List<String> list, @NonNull List<String> list1) {

                    }
                });

        findViewById(R.id.main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
//                DownloadText.download(getApplicationContext());
                inspectUpdate(ins, true, false, new UpdateAppUtils.LoadingInterface() {
                    @Override
                    public void startLoading() {

                    }

                    @Override
                    public void success(UpdateChecker.UpdateInfo updateInfo) {

                    }

                    @Override
                    public void error(String message) {

                    }
                });
            }
        });


    }
    /**
     *
     *   检查更新
     *   @param activity 用于弹窗使用，权限使用
     *   @param showToast 显示检查消息
     *   @param useCostDialog 是否自定义升级弹窗
     * */
    public static void inspectUpdate(Activity activity, boolean showToast, boolean useCostDialog, UpdateAppUtils.LoadingInterface loadingInterface){
        String apiKey = FileUtils.getFieldValue(activity.getPackageName()+".BuildConfig","PGYER_API_KEY");
        String appKey = FileUtils.getFieldValue(activity.getPackageName()+".BuildConfig","PGYER_APP_KEY");
        UpdateParamBuild paramBuild = new UpdateParamBuild();
        paramBuild.activity = activity;
        paramBuild.apiKey = "406e6044fabebca774797c93a140795c";
        paramBuild.appKey = "4b9d41eff7d6bccf3ad7b113b48745a4";
        paramBuild.showToast = showToast;
        paramBuild.useCostDialog = useCostDialog;
        paramBuild.loadingInterface = loadingInterface;
        UpdateAppUtils.updateAPP(paramBuild);
    }
}