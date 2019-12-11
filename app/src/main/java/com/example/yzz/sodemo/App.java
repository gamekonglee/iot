package com.example.yzz.sodemo;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

public class App extends Application {
    private PermissionRequest permissionRequest;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        initPersion();
    }

    private void initPersion() {
        permissionRequest = new PermissionRequest(this, new PermissionRequest.PermissionCallback() {
            @Override
            public void onSuccessful() {
                Toast.makeText(context, "权限申请成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(context, "权限申请失败", Toast.LENGTH_SHORT).show();
            }
        });
        permissionRequest.request();
    }

}
