package com.spepc.filedownload;

import android.Manifest;
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

import java.util.List;


public class MainActivity extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            public void onClick(View v) {
                DownloadText.download(getApplicationContext());
            }
        });


    }

}