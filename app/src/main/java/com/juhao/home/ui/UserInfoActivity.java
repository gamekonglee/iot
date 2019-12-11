package com.juhao.home.ui;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.BaseActivity;
import com.alibaba.sdk.android.openaccount.OpenAccountSDK;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.model.OpenAccountSession;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIService;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.login.data.UserInfo;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.juhao.home.IssApplication;
import com.juhao.home.R;
import com.juhao.home.scene.IotSceneAddActivity;
import com.util.Constance;
import com.util.FileUtil;
import com.util.ImageUtil;
import com.util.LogUtils;
import com.util.NetWorkConst;
import com.util.NetWorkUtils;
import com.util.photo.CameraUtil;
import com.view.MyToast;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener {

    private View rl_head;
    private View rl_nickname;
    private View rl_account;
    private View rl_timezone;
    private TextView tv_nickname;
    private ImageView iv_head;
    private TextView tv_account;
    private TextView tv_timezone;
    private String imageURL;

    @Override
    protected void InitDataView() {
        UserInfo userInfo= LoginBusiness.getUserInfo();
        if(userInfo!=null){
            String nick=userInfo.userNick;
            if(!TextUtils.isEmpty(nick)){

            }
        }
    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
    setContentView(R.layout.activity_user_info);
        rl_head = findViewById(R.id.rl_head);
        rl_nickname = findViewById(R.id.rl_nickname);
        rl_account = findViewById(R.id.rl_account);
        rl_timezone = findViewById(R.id.rl_timezone);
        tv_nickname = findViewById(R.id.tv_nickname);
        iv_head = findViewById(R.id.iv_head);
        tv_account = findViewById(R.id.tv_account);
        tv_timezone = findViewById(R.id.tv_timezone);
        rl_head.setOnClickListener(this);
        rl_nickname.setOnClickListener(this);
        rl_account.setOnClickListener(this);
        rl_timezone.setOnClickListener(this);


    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
    switch (view.getId()){
        case R.id.rl_head:
            setHead();
            break;
        case R.id.rl_nickname:
            final Dialog dialog=new Dialog(this,R.style.customDialog);
            dialog.setContentView(R.layout.dialog_name_input);
            final EditText et_name=dialog.findViewById(R.id.et_name);
            TextView tv_cancel=dialog.findViewById(R.id.tv_cancel);
            TextView tv_ensure=dialog.findViewById(R.id.tv_ensure);
            TextView tv_title=dialog.findViewById(R.id.tv_title);
            tv_title.setText(getString(R.string.str_nickname_edit));
            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            tv_ensure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name=et_name.getText().toString();
                    if(TextUtils.isEmpty(name)){
                        MyToast.show(UserInfoActivity.this,getString(R.string.str_not_empty));
                        return;
                    }
                    tv_nickname.setText(name);
                    dialog.dismiss();
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("displayName", name);
                    OpenAccountUIService oas = OpenAccountSDK.getService(OpenAccountUIService.class);
                    oas.updateProfile(getApplicationContext(), map, new LoginCallback() {
                        @Override
                        public void onSuccess(OpenAccountSession openAccountSession) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MyToast.show(UserInfoActivity.this,getString(R.string.str_update_success));
                                }
                            });
                        }

                        @Override
                        public void onFailure(int i, String s) {
                        }
                    });
                }
            });
            String nickname=tv_nickname.getText().toString();
            if(!TextUtils.isEmpty(nickname)){
            et_name.setText(tv_nickname.getText().toString());
            }
            dialog.show();

            break;
        case R.id.rl_account:
            break;
        case R.id.rl_timezone:
            break;

    }
    }

    /**
     * 头像
     */
    private CameraUtil camera;
    public void setHead() {

        FileUtil.openImage(this);

        if (camera == null) {
            camera = new CameraUtil(this, new CameraUtil.CameraDealListener() {
                @Override
                public void onCameraTakeSuccess(String path) {
                    camera.cropImageUri(1, 1, 256);
                }

                @Override
                public void onCameraPickSuccess(String path) {
                    Uri uri ;
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                        uri= FileProvider.getUriForFile(UserInfoActivity.this, "com.juhao.home.fileprovider", new File(path));
                    }else {
                        uri = Uri.parse("file://" + path);
                    }
                    camera.cropImageUri(uri, 1, 1, 256);
                }

                @Override
                public void onCameraCutSuccess(final String uri) {
                    File file = new File(uri);
                    Uri uriTemp;
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                        uriTemp= FileProvider.getUriForFile(UserInfoActivity.this, "com.juhao.home.fileprovider", new File(uri));
                    }else {
                        uriTemp = Uri.parse("file://" + uri);
                    }
                    iv_head.setImageURI(uriTemp);
                    upLoad(uri.toString());
                }
            });
        }

//        mHeadView.show();
    }

    private void upLoad(final String uri) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String,String> map=new HashMap<>();
                final String resultJson = NetWorkUtils.uploadFile(ImageUtil.drawable2Bitmap(iv_head.getDrawable()), NetWorkConst.UPLOADAVATAR, map, uri);
                //                            //分享的操作
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.logE("result",resultJson+"");
                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("avatarUrl", resultJson);
                        OpenAccountUIService oas = OpenAccountSDK.getService(OpenAccountUIService.class);
                        oas.updateProfile(getApplicationContext(), map, new LoginCallback() {
                            @Override
                            public void onSuccess(OpenAccountSession openAccountSession) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyToast.show(UserInfoActivity.this,getString(R.string.str_upload_success));
                                    }
                                });
                            }

                            @Override
                            public void onFailure(int i, String s) {
                            }
                        });

//                        onRefresh();
                    }
                });
            }
        }).start();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (camera != null)
            camera.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            switch (requestCode) {
                case Constance.PHOTO_WITH_CAMERA: {// 拍照获取图片
                    String status = Environment.getExternalStorageState();
                    if (status.equals(Environment.MEDIA_MOUNTED)) { // 是否有SD卡
                        File imageFile = new File(DemoApplication.cameraPath, DemoApplication.imagePath + ".jpg");
                        imageURL = "file://" + imageFile;
                        final Uri uri = Uri.parse("file://" + imageFile);
                        iv_head.setImageURI(uri);
                        upLoad(uri.toString());
                    }
                }
                break;
                case Constance.PHOTO_WITH_DATA: // 从图库中选择图片
                    // 照片的原始资源地址
                    imageURL = data.getData().toString();
                    iv_head.setImageURI(data.getData());
                    upLoad(imageURL.toString());
                    break;
                case Constance.FLAG_UPLOAD_IMAGE_CUT:
                    final Uri uri=data.getData();
                    iv_head.setImageURI(uri);
                    upLoad(uri.toString());
                    break;
            }
        }else if(requestCode== Constance.FLAG_UPLOAD_IMAGE_CUT){
            final Uri uri=data.getData();
            iv_head.setImageURI(uri);
            upLoad(uri.toString());
        }
    }
}
