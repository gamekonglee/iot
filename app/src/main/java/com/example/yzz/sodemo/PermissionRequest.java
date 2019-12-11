package com.example.yzz.sodemo;

import android.Manifest;
import android.content.Context;


import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;

import java.util.List;

/**
 * <p>这就是随便封装一下</p>
 * Created by yzz on 2019/07/22.
 */
public class PermissionRequest {

    private Context mContext;
    private PermissionCallback mCallback;

    public PermissionRequest(Context context, PermissionCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
    }

    public void request() {
        AndPermission.with(mContext)
                .requestCode(110)
                .permission(Permission.PHONE)
                .callback(this)
                .start();
    }

    @PermissionYes(110)
    public void yes(List<String> permissions) {
        this.mCallback.onSuccessful();
    }

    @PermissionNo(110)
    public void no(List<String> permissions) {
        System.out.println(permissions.toString());
        this.mCallback.onFailure();
    }

    public interface PermissionCallback {
        void onSuccessful();

        void onFailure();
    }

}
