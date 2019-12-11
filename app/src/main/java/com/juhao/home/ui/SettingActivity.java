package com.juhao.home.ui;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.BaseActivity;
import com.alibaba.sdk.android.openaccount.OpenAccountSDK;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.model.OpenAccountSession;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIService;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileRequestListener;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileChannel;
import com.aliyun.alink.linksdk.tools.AError;
import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.login.data.UserInfo;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.util.CacheClear;
import com.util.CameraUtil;
import com.util.Constance;
import com.util.FileUtil;
import com.util.LogUtils;
import com.util.MyLog;
import com.util.NetWorkConst;
import com.util.NetWorkUtils;
import com.view.MyToast;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

//    private TextView tv_account;
//    private TextView tv_set_nickname;
    private UserInfo userInfo;
    private ImageView iv_head;
    private String imageURL;
    private String dataSize;
    private TextView tv_cache;

    @Override
    protected void InitDataView() {
        getDataSize();
    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
    setContentView(R.layout.activity_setting);
    findViewById(R.id.rl_about).setOnClickListener(this);
    findViewById(R.id.rl_clear_cache).setOnClickListener(this);
        tv_cache = findViewById(R.id.tv_cache);
//        tv_account = findViewById(R.id.tv_account);
//        tv_set_nickname = findViewById(R.id.tv_set_nickname);
        iv_head = findViewById(R.id.iv_head);
        iv_head.setOnClickListener(this);
//        tv_set_nickname.setOnClickListener(this);

        userInfo = LoginBusiness.getUserInfo();

        if(userInfo !=null){
            String nickname= userInfo.userNick;
            if(nickname!=null){
//                tv_set_nickname.setText(nickname);
            }
            String account=userInfo.userPhone;
            if(account==null){
                account=userInfo.userEmail;
            }
//            tv_account.setText(account);
            String url=userInfo.userAvatarUrl;
            if(!TextUtils.isEmpty(url)){
                ImageLoader.getInstance().displayImage(url,iv_head);
            }
        }
        View tv_logout=findViewById(R.id.tv_logout);
        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.showSingleWordDialog(SettingActivity.this, getString(R.string.str_sure_logout), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        MobileChannel.getInstance().unBindAccount(new IMobileRequestListener() {
                            @Override
                            public void onSuccess(String jsonData) {
                            }

                            @Override
                            public void onFailure(com.aliyun.alink.linksdk.channel.core.base.AError aError) {

                            }

                        });

                        LoginBusiness.logout(new ILogoutCallback() {
                            @Override
                            public void onLogoutSuccess() {
                                MyToast.show(SettingActivity.this,getString(R.string.str_logout_success));
                                SettingActivity.this.finish();
                                DemoApplication.activityList.get(0).finish();
                                System.exit(0);
//                                Log.i(TAG,"登出成功");
                            }

                            @Override
                            public void onLogoutFailed(int code, String error) {
//                                Log.i(TAG,"登出失败");
                                SettingActivity.this.finish();
                                DemoApplication.activityList.get(0).finish();
                                System.exit(0);
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    protected void initData() {

    }
    private final int CLEAN_SUC = 1001;
    private final int CLEAN_FAIL = 1002;

    //计算缓存
    private String getDataSize() {
        long fileSize = 0;
        File filesDir = getFilesDir();
        File cacheDir = getCacheDir();
        fileSize += CacheClear.getDirSize(filesDir);
        fileSize += CacheClear.getDirSize(cacheDir);
        String formatSize = CacheClear.getFormatSize(fileSize);
        return formatSize;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_head:
                setHead();
                break;
            case R.id.tv_set_nickname:
                final Dialog dialog = new Dialog(this, R.style.customDialog);
                dialog.setContentView(R.layout.dialog_layout_input_nickname);
                final EditText et_nickname=dialog.findViewById(R.id.et_nickname);
                TextView tv_ensure=dialog.findViewById(R.id.tv_ensure);
                TextView tv_cancel=dialog.findViewById(R.id.tv_cancel);

                tv_ensure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String nickname=et_nickname.getText().toString();
                        if(TextUtils.isEmpty(nickname)){
                            MyToast.show(SettingActivity.this,getString(R.string.str_nickname_input));
                            return;
                        }
                        dialog.dismiss();
//                        tv_set_nickname.setText(nickname);
                        updateNickName(nickname);
                    }
                });
                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                if(userInfo==null||TextUtils.isEmpty(userInfo.userNick)){
//                    et_nickname.setText("");
                }else {
//                et_nickname.setText(tv_set_nickname.getText());
                }
                Window dialogWindow = dialog.getWindow();
                dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                break;
            case R.id.rl_about:
                startActivity(new Intent(SettingActivity.this, AboutActivity.class));
                break;
            case R.id.rl_clear_cache:
                clearAppCache();
                break;
        }
    }



    // 清除app缓存
    public void clearAppCache() {
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    msg.what = CLEAN_SUC;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = CLEAN_FAIL;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    // 清除缓存Handler
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CLEAN_FAIL:
                    //ToastUtils.showToastNew("清除失败");
                    break;
                case CLEAN_SUC:
                    CacheClear.cleanApplicationDataNoSP(SettingActivity.this);
                    //获取缓存大小
                    dataSize = getDataSize();
                    tv_cache.setText(dataSize.equals("0.0Byte")?"": dataSize);
                    //ToastUtils.showToastNew("清除成功");
                    break;
            }
        }
    };

    private void updateNickName(String nickname) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("displayName", nickname);
        OpenAccountUIService oas = OpenAccountSDK.getService(OpenAccountUIService.class);
        oas.updateProfile(getApplicationContext(), map, new LoginCallback() {
            @Override
            public void onSuccess(OpenAccountSession openAccountSession) {
                LogUtils.logE("updateNick",openAccountSession.getNick());
            }

            @Override
            public void onFailure(int i, String s) {
            }
        });
    }

    private CameraUtil camera;

    /**
     * 头像
     */
    public void setHead() {

        FileUtil.openImage(this);

        if (camera == null) {
            camera = new CameraUtil(this, new CameraUtil.CameraDealListener() {
                @Override
                public void onCameraTakeSuccess(String path) {
                    MyLog.e("onCameraTakeSuccess: " + path);
                    camera.cropImageUri(1, 1, 256);
                }

                @Override
                public void onCameraPickSuccess(String path) {
                    MyLog.e("onCameraPickSuccess: " + path);
                    Uri uri ;
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                        uri= FileProvider.getUriForFile(SettingActivity.this, "com.juhao.home.hms.update.provider", new File(path));
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
                        uriTemp= FileProvider.getUriForFile(SettingActivity.this, "com.juhao.home.hms.update.provider", new File(uri));
                    }else {
                        uriTemp = Uri.parse("file://" + uri);
                    }
                    iv_head.setImageURI(uriTemp);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String resultJson = NetWorkUtils.uploadFile(iv_head.getDrawable(), NetWorkConst.UPLOADAVATAR, null, uri.toString());
                            LogUtils.logE("uploadhead",resultJson);
                            //                            //分享的操作
                            SettingActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        }
                    }).start();
                }
            });
        }
//        mHeadView.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==-1){
            switch (requestCode) {
                case Constance.PHOTO_WITH_CAMERA: {// 拍照获取图片
                    String status = Environment.getExternalStorageState();
                    if (status.equals(Environment.MEDIA_MOUNTED)) { // 是否有SD卡
                        File imageFile = new File(DemoApplication.cameraPath, DemoApplication.imagePath + ".jpg");
                        imageURL = "file://"+imageFile;
                        final Uri uri=Uri.parse("file://"+imageFile);
                        iv_head.setImageURI(uri);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final String resultJson = NetWorkUtils.uploadFile(iv_head.getDrawable(), NetWorkConst.UPLOADAVATAR, null, uri.toString());
                                LogUtils.logE("uploadhead",resultJson);
                                //                            //分享的操作
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                    }
                                });
                            }
                        }).start();
                    }
                    break;
                }
                case Constance.PHOTO_WITH_DATA: // 从图库中选择图片
                    // 照片的原始资源地址
                    imageURL = data.getData().toString();
                    iv_head.setImageURI(data.getData());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String resultJson = NetWorkUtils.uploadFile(iv_head.getDrawable(), NetWorkConst.UPLOADAVATAR, null, imageURL.toString());
                            LogUtils.logE("uploadhead",resultJson);
                            //                            //分享的操作
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        }
                    }).start();
                    break;
                case Constance.FLAG_UPLOAD_IMAGE_CUT:
                    final Uri uri=data.getData();
                    iv_head.setImageURI(uri);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String resultJson = NetWorkUtils.uploadFile(iv_head.getDrawable(), NetWorkConst.UPLOADAVATAR, null, uri.toString());
                            LogUtils.logE("uploadhead",resultJson);
                            //                            //分享的操作
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        }
                    }).start();
                    break;
            }
        }else if(requestCode== Constance.FLAG_UPLOAD_IMAGE_CUT){
            final Uri uri=data.getData();
            iv_head.setImageURI(uri);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String resultJson = NetWorkUtils.uploadFile(iv_head.getDrawable(), NetWorkConst.UPLOADAVATAR, null, uri.toString());
                    LogUtils.logE("uploadhead",resultJson);
                    //                            //分享的操作
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
            }).start();
        }
    }
}
