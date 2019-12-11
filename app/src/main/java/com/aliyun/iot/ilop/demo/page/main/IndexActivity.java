package com.aliyun.iot.ilop.demo.page.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.sdk.android.openaccount.OpenAccountSDK;
import com.alibaba.sdk.android.openaccount.OpenAccountService;
import com.alibaba.sdk.android.openaccount.OpenAccountSessionService;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.callback.LogoutCallback;
import com.alibaba.sdk.android.openaccount.model.OpenAccountSession;
import com.alibaba.sdk.android.openaccount.ui.ui.LoginActivity;
import com.aliyun.alink.linksdk.channel.core.base.AError;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileRequestListener;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileChannel;
import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialListener;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManage;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageError;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageImpl;
import com.aliyun.iot.aep.sdk.credential.data.IoTCredentialData;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.aliyun.iot.ilop.demo.page.ilopmain.*;

import com.juhao.home.R;
import com.view.MyToast;

/**
 * Created by gamekonglee on 2018/8/1.
 */

public class IndexActivity extends Activity {
    private static final String TAG = "Index";
    private boolean isLogin;
    private boolean getCode;
    private String code;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
//        com.tencent.smtt.sdk.WebView webview=findViewById(R.id.webView);
//        com.tencent.smtt.sdk.WebSettings settings=webview.getSettings();
//        settings.setJavaScriptEnabled(true);
//        settings.setSupportZoom(true);
//        settings.setBuiltInZoomControls(true);
//        settings.setAllowFileAccess(true);
//        settings.setUseWideViewPort(true);
//        settings.setLoadWithOverviewMode(true);
//        settings.setJavaScriptCanOpenWindowsAutomatically(true);
//        settings.setDatabaseEnabled(true);
//        settings.setDomStorageEnabled(true);
//        settings.setGeolocationEnabled(true);
//        settings.setAppCacheEnabled(true);
//        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//        webview.getSettings().setJavaScriptEnabled(true);
//        webview.getSettings().setDomStorageEnabled(true);
//        webview.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
//        String appCachePath = getApplicationContext().getCacheDir()
//                .getAbsolutePath();
//        webview.getSettings().setAppCachePath(appCachePath);
//        webview.getSettings().setAllowFileAccess(true);
//        webview.getSettings().setAppCacheEnabled(true);
////        progressDialog = ProgressDialog.show(this,"登录中","正在获取钜豪商城授权");
//        progressDialog=new ProgressDialog(IndexActivity.this);
//        progressDialog.setTitle("登录中");//设置标题
//        progressDialog.setMessage("正在获取钜豪商城授权");//设置消息
//        progressDialog.setCancelable(true);//设置进度条是不是可以取消
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置对话框的进度条的风格
//        progressDialog.setIndeterminate(true);//设置对话框进度条是否显示进度
//        progressDialog.show();//显示进度条
//
//        getCode = false;
//        webview.setWebViewClient(new com.tencent.smtt.sdk.WebViewClient(){
//            @Override
//            public void onPageFinished(com.tencent.smtt.sdk.WebView webView, String url) {
//                super.onPageFinished(webView, url);
//                if(getCode){
//                    return;
//                }
//
//                if(url.contains("callback&response_type=code&scope=")){
//                    if(progressDialog!=null)progressDialog.dismiss();
//                }else {
//                    Log.e("pagefinish",url);
//                    code = url.substring(url.indexOf("code=")+5);
//                    Log.e("code", code);
//                    if(code !=null&& code.length()>0){
//                        getCode=true;
//                    }
//                    webView.loadUrl("about:blank");
//                    mH.sendEmptyMessage(1);
//
//                }
//
//            }
//
//        });
//        String username= MyShare.get(this).getString(Constance.username);
//        String pwd=MyShare.get(this).getString(Constance.pwd);
//        webview.loadUrl("http://smart.juhao.com/login?username="+username+"&password="+pwd);
//        isLogin = getIntent().getBooleanExtra(Constance.islogin,false);
    }

    private void login(final String authCode) {
        OpenAccountService service = OpenAccountSDK.getService(OpenAccountService.class);
//        PgyCrashManager.reportCaughtException(IndexActivity.this,new Exception("authCodeLogin."+authCode));
        service.authCodeLogin(IndexActivity.this, authCode, new LoginCallback() {
            @Override
            public void onSuccess(OpenAccountSession openAccountSession) {
//                PgyCrashManager.reportCaughtException(IndexActivity.this,new Exception("authCodeLogin_success"));
                Log.e("login","authCodeLogin_success");

//                IoTCredentialManageImpl ioTCredentialManage =  IoTCredentialManageImpl.getInstance(DemoApplication.getInstance());
//                if(TextUtils.isEmpty(ioTCredentialManage.getIoTToken())){
//                    ioTCredentialManage.asyncRefreshIoTCredential(new IoTCredentialListener() {
//                        @Override
//                        public void onRefreshIoTCredentialSuccess(IoTCredentialData ioTCredentialData) {
//                            MobileChannel.getInstance().bindAccount(ioTCredentialData.iotToken, new IMobileRequestListener() {
//                                @Override
//                                public void onSuccess(String s) {
//                                    PgyCrashManager.reportCaughtException(IndexActivity.this,new Exception("onRefreshIoTCredentialSuccess_success"));
//                                    Log.e(TAG,"mqtt bindAccount onSuccess");
//                                    mH.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
////                        Router.getInstance().toUrl(IndexActivity.this, "page/ilopmain");
//                                            PgyCrashManager.reportCaughtException(IndexActivity.this,new Exception("gotoMainActivity"));
//                                            startActivity(new Intent(IndexActivity.this, com.aliyun.iot.ilop.demo.page.ilopmain.MainActivity.class));
//                                            finish();
//                                        }
//                                    }, 0);
//                                }
//
//                                @Override
//                                public void onFailure(AError aError) {
//
//                                    PgyCrashManager.reportCaughtException(IndexActivity.this,new Exception("onRefreshIoTCredentialSuccess_failure"));
//                                    Log.e(TAG,"mqtt bindAccount onFailure aError = " + aError.getMsg());
//
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onRefreshIoTCredentialFailed(IoTCredentialManageError ioTCredentialManageError) {
//                            PgyCrashManager.reportCaughtException(IndexActivity.this,new Exception("onRefreshIoTCredentialFailed"));
//                            Log.e(TAG,"mqtt onRefreshIoTCredentialFailed ");
//
//
//                        }
//                    });
//                } else {
//                    MobileChannel.getInstance().bindAccount(ioTCredentialManage.getIoTToken(), new IMobileRequestListener() {
//                        @Override
//                        public void onSuccess(String s) {
//                            PgyCrashManager.reportCaughtException(IndexActivity.this,new Exception("bindAccount,onsuccess"));
//                            Log.e(TAG,"mqtt bindAccount onSuccess ");
//                        }
//
//                        @Override
//                        public void onFailure(AError aError) {
//                            PgyCrashManager.reportCaughtException(IndexActivity.this,new Exception("bindAccount,onfalire"));
//                            Log.e(TAG,"mqtt bindAccount onFailure aError = " + aError.getMsg());
//
//                        }
//                    });
//                }

                mH.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        Router.getInstance().toUrl(IndexActivity.this, "page/ilopmain");
//                        PgyCrashManager.reportCaughtException(IndexActivity.this,new Exception("gotoMainActivity"));
                        startActivity(new Intent(IndexActivity.this, com.aliyun.iot.ilop.demo.page.ilopmain.MainActivity.class));
                        if(progressDialog!=null)progressDialog.dismiss();
                        finish();
                    }
                }, 0);
//                            //注册虚拟设备
//                            String[] pks = {"a1nbd4BjB3N", "a186j8fot9K", "a1XKEJTOkPL", "a1aJCduQG0p"};
//                            for (String pk : pks) {
//                                registerVirtualDevice(pk);
//                            }
            }

            @Override
            public void onFailure(int code, String msg) {
//                PgyCrashManager.reportCaughtException(IndexActivity.this,new Exception("authCodeLogin_failure"));
                MyToast.show(IndexActivity.this,msg);
                startActivity(new Intent(IndexActivity.this, LoginActivity.class));
                if(progressDialog!=null)progressDialog.dismiss();
                finish();
                return;
//                            Toast.makeText(getApplicationContext(), "auth授权登录 失败 code = " + code + " message = " + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void logout(final String authCode) {
        OpenAccountService openAccountService = OpenAccountSDK.getService(OpenAccountService.class);

        try {
            openAccountService.logout(this, new LogoutCallback() {
                @Override
                public void onSuccess() {
//                    ToastUtil.show(getApplicationContext(), "登出成功");
                    Log.e("logout","登出成功");
//                    PgyCrashManager.reportCaughtException(IndexActivity.this,new Exception("logout_success"));
                    IoTCredentialManageImpl.getInstance(DemoApplication.getInstance()).clearIoTTokenInfo();
                    login(authCode);
                }

                @Override
                public void onFailure(int i, String s) {
//                    ToastUtil.show(getApplicationContext(), "登出失败 : " + s);
//                    PgyCrashManager.reportCaughtException(IndexActivity.this,new Exception("logout_failure"));
                    Log.e("logout","登出失败");
                    login(authCode);
                }
            });
        } catch (Exception e) {
            login(authCode);
            Log.e("logout","登出异常");
//            ToastUtil.show(getApplicationContext(), "登出异常 : " + e.toString());
        }
    }
    Handler mH=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            PgyCrashManager.reportCaughtException(IndexActivity.this,new Exception("MobileChannel,unBindAccount"));
            MobileChannel.getInstance().unBindAccount(new IMobileRequestListener() {
                @Override
                public void onSuccess(String s) {
                    Log.e("unBindAccount",s+"");
//                    PgyCrashManager.reportCaughtException(IndexActivity.this,new Exception("MobileChannel,unBindAccount_success"));
                }

                @Override
                public void onFailure(AError aError) {
                    Log.e("unBindAccount","error"+aError.getMsg());
//                    PgyCrashManager.reportCaughtException(IndexActivity.this,new Exception("MobileChannel,unBindAccount_failure"));
                }
            });

//            if(url.contains("callback?code=")){
//                PgyCrashManager.reportCaughtException(IndexActivity.this,new Exception("callback?code=,logout"));
                logout(code);
//            }
        }
    };
}
