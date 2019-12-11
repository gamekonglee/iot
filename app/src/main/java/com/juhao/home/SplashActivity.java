package com.juhao.home;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aliyun.iot.aep.sdk.framework.AActivity;
import com.aliyun.iot.ilop.demo.page.ilopmain.MainActivity;
import com.net.ApiClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.util.AppUtils;
import com.util.CommonUtil;
import com.util.Constance;
import com.util.MyShare;
import com.util.NetWorkConst;
import com.util.json.JSONArray;
import com.util.json.JSONObject;
import com.zhy.http.okhttp.callback.Callback;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author Jun
 * @time 2017/1/5  10:29
 * @desc 启动页
 */
public class SplashActivity extends AActivity {
    public ImageView mLogoIv;
    public AlphaAnimation mAnimation;
    public TextView version_tv;
    private String imei;
    private int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;
    public String version="";
    private boolean remember;
    public TimerSchedule mTimerSc;
    private int count;
    private TextView tv_countDown;
    private TextView tv_jump;

//    @Override
//    protected void InitDataView() {
//
//    }
//
//    @Override
//    protected void initController() {
//
//    }
//
//
//    @Override
//    protected void initView() {
//        //去除title
//
//
//    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        startAct();
     /*   mTimerSc = new TimerSchedule();
        count = 4;
        version_tv = (TextView)findViewById(R.id.version_tv);
//        setColor(this, Color.TRANSPARENT);
        mLogoIv = (ImageView) findViewById(R.id.logo_iv);
        mLogoIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        tv_countDown = findViewById(R.id.tv_countdown);
        tv_jump = findViewById(R.id.tv_jump);
        tv_jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTimerSc!=null)mTimerSc.cancel();
                boolean remember= MyShare.get(SplashActivity.this).getBoolean(Constance.apply_remember);
//                if(!remember){
//                    showDialog();
//                }else {
//                }
                    startAct();
            }
        });
        String localVersion = CommonUtil.localVersionName(this);
        version_tv.setText("V "+localVersion);
        version=localVersion;
        ApiClient.sendBannerIndex(new Callback<String>() {
            @Override
            public String parseNetworkResponse(Response response, int id) throws Exception {
                return null;
            }

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public String onResponse(String response, int id) {
                return null;
            }
        });

            mLogoIv.setVisibility(View.VISIBLE);
            mAnimation = new AlphaAnimation(0.2f, 1.0f);
            mAnimation.setDuration(2500);
            mAnimation.setFillAfter(true);
            mLogoIv.startAnimation(mAnimation);
            mAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    try {
                        if(mTimerSc!=null)new Timer().schedule(mTimerSc, 0,1000);
                    }catch (Exception e){

                    }
                    String token = MyShare.get(SplashActivity.this).getString(Constance.TOKEN);
                    if (AppUtils.checkNetwork() && !AppUtils.isEmpty(token)){
                        getSuccessLogin();
                    }
                    version = Build.VERSION.RELEASE;
//        LogUtils.logE("codename",Build.VERSION.CODENAME);
//        LogUtils.logE("realease",Build.VERSION.RELEASE);
                    int osVersion = Integer.valueOf(Build.VERSION.SDK);
                    if (osVersion>22){

                        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.UNINSTALL_SHORTCUT)
                                != PackageManager.PERMISSION_GRANTED) {
                            //申请WRITE_EXTERNAL_STORAGE权限
                            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission_group.STORAGE,Manifest.permission_group.PHONE},
                                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                        }else{
                            getImei();
                        }
                    }else{
                        //如果SDK小于6.0则不去动态申请权限
                        getImei();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
*/

//        mNetWork.sendBannerIndex(new INetworkCallBack() {
//            @Override
//            public void onSuccessListener(String requestCode, JSONObject ans) {
////                LogUtils.logE("bannerIndex",ans.toString());
//                if(ans==null){
//                    return;
//                }
//                JSONArray banners=ans.getJSONArray(Constance.banners);
//                if(banners==null||banners.length()==0||banners.getJSONObject(0)==null){
//                    startAni();
//                    return;
//                }
//                ImageLoader.getInstance().loadImage(NetWorkConst.SCENE_HOST + "/data/afficheimg/" + ans.getJSONArray(Constance.banners).getJSONObject(0).getString(Constance.ad_code), new ImageLoadingListener() {
//                    @Override
//                    public void onLoadingStarted(String s, View view) {
//
//                    }
//
//                    @Override
//                    public void onLoadingFailed(String s, View view, FailReason failReason) {
//                        startAni();
//                    }
//
//                    @Override
//                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
//                        mLogoIv.setImageBitmap(bitmap);
//                        startAni();
//                    }
//
//                    @Override
//                    public void onLoadingCancelled(String s, View view) {
//
//                    }
//                });
//
//            }
//
//            @Override
//            public void onFailureListener(String requestCode, JSONObject ans) {
////            LogUtils.logE("requestCode",requestCode+ans);
//                startAni();
//            }
//        });
//        //布置透明度动画

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            getImei();
        }else{
        }
    }

    /**
     * 登录成功处理事件
     */
    public void getSuccessLogin() {
        final String uid= MyShare.get(this).getString(Constance.USERID);
        if(AppUtils.isEmpty(uid)){
            return;
        }



    }

//    /**
//     * 环信注册
//     */
//    private void sendRegiestSuccess() {
//        final String uid= MyShare.get(this).getString(Constance.USERID);
//        if(AppUtils.isEmpty(uid)){
//            return;
//        }
//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    EMClient.getInstance().createAccount(uid,uid);//同步方法
//                    SplashActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            MyShare.get(SplashActivity.this).putBoolean(Constance.EMREGIEST, true);//保存TOKEN
//                            Log.e("520it", "S注册成功!");
//                            getSuccessLogin();
//
//                        }
//                    });
//
//                } catch (final HyphenateException e) {
//                    SplashActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.e("520it", "S注册失败!：" + e.getMessage());
//                            getSuccessLogin();
//                        }
//                    });
//
//                }
//            }
//        }).start();
//    }


//    @Override
//    protected void initData() {
//
//    }
    public static int countDown=0;
    public static int finishEnd=1;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==countDown){
                tv_countDown.setText(count+"s");
            }else {
                mTimerSc.cancel();
                boolean remember= MyShare.get(SplashActivity.this).getBoolean(Constance.apply_remember);
//                if(!remember){
//                    showDialog();
//                }else {
                    startAct();
//                }

            }

        }
    };
    public class TimerSchedule extends TimerTask {
        @Override
        public void run() {
            count--;
            if(count==0){
            handler.sendEmptyMessage(1);

            }else {
                handler.sendEmptyMessage(0);
            }

        }
    }
    public static boolean isInstallShortcut(Context context, String applicationName) {
        boolean isInstallShortcut = false;
        ContentResolver cr = context.getContentResolver();
        //sdk大于8的时候,launcher2的设置查找
        String AUTHORITY = "com.android.launcher2.settings";
        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
        Cursor c = cr.query(CONTENT_URI, new String[] { "title", "iconResource" },
                "title=?", new String[] { applicationName }, null);
        if (c != null && c.getCount() > 0) {
            isInstallShortcut = true;
        }
        if (c != null) {
            c.close();
        }
        //如果存在先关闭cursor，再返回结果
        if (isInstallShortcut) {
            return isInstallShortcut;
        }
        //android.os.Build.VERSION.SDK_INT < 8时
        AUTHORITY = "com.android.launcher.settings";
        CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
        c = cr.query(CONTENT_URI, new String[] { "title", "iconResource" }, "title=?",
                new String[] {applicationName}, null);
        if (c != null && c.getCount() > 0) {
            isInstallShortcut = true;
        }
        if (c != null) {
            c.close();
        }
        return isInstallShortcut;
    }
    //获取当前app的应用程序名称
    public static String getApplicationName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext().getPackageManager();
            applicationInfo=packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName =(String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }
    //删除shortcut
    public static void delShortcut(Context cx) {
        Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
        // 获取当前应用名称
        String title = null;
        try {
            final PackageManager pm=cx.getPackageManager();     title=pm.getApplicationLabel(pm.getApplicationInfo(cx.getPackageName(), PackageManager.GET_META_DATA)).toString();
        } catch (Exception e) {
        }
        // 快捷方式名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        Intent shortcutIntent = cx.getPackageManager().getLaunchIntentForPackage(cx.getPackageName());
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT,shortcutIntent);
        cx.sendBroadcast(shortcut);
    }
    public void getImei(){
//        if(version.equals("1.26")){
//            if(isInstallShortcut(this, getApplicationName(this))){
//                delShortcut(this);
//            }
//        }

    }
    public void startAct(){
        Boolean isFinish= MyShare.get(SplashActivity.this).getBoolean(Constance.ISFIRSTISTART);
            String token= MyShare.get(SplashActivity.this).getString(Constance.TOKEN);
            String userCode= MyShare.get(SplashActivity.this).getString(Constance.USERCODE);
            boolean isNotFirst=MyShare.get(this).getBoolean(Constance.is_not_first);
//            if(!isNotFirst){
//                MyShare.get(this).putBoolean(Constance.is_not_first,true);
//                startActivity(new Intent(SplashActivity.this, LoginIndexActivity.class));
//            }else {
//            }
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
    }
//    public void showDialog(){
//        final Dialog dialog=new Dialog(this,R.style.customDialog);
//        dialog.setContentView(R.layout.dialog_apply);
//        ImageView iv_dismiss=dialog.findViewById(R.id.iv_dismiss);
//        TextView tv_dimiss=dialog.findViewById(R.id.tv_dismiss);
//        ImageView iv_apply=dialog.findViewById(R.id.iv_apply);
//        final TextView tv_remember=dialog.findViewById(R.id.tv_remember);
//        remember = false;
//        iv_dismiss.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MyShare.get(SplashActivity.this).putBoolean(Constance.apply_remember,remember);
//                dialog.dismiss();
//                startAct();
//            }
//        });
//        tv_dimiss.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MyShare.get(SplashActivity.this).putBoolean(Constance.apply_remember,remember);
//                dialog.dismiss();
//                startAct();
//            }
//        });
//        tv_remember.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            if(remember){
//                remember =false;
//                Drawable drawable=getResources().getDrawable(R.mipmap.jm_icom_nor);
//                drawable.setBounds(0,0,drawable.getMinimumHeight(),drawable.getMinimumWidth());
//                tv_remember.setCompoundDrawables(drawable,null,null,null);
//            }else {
//                remember=true;
//                Drawable drawable=getResources().getDrawable(R.mipmap.jm_icon_sel);
//                drawable.setBounds(0,0,drawable.getMinimumHeight(),drawable.getMinimumWidth());
//                tv_remember.setCompoundDrawables(drawable,null,null,null);
//            }
//            }
//        });
//        iv_apply.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                startActivity(new Intent(SplashActivity.this,BussinessApplyActivity.class));
//                finish();
//            }
//        });
//        try {
//            dialog.show();
//        }catch (Exception e){
//            startActivity(new Intent(this,MainActivity.class));
//            finish();
//        }
//    }
}
