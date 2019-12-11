package com.aliyun.iot.ilop.demo.page.ilopmain;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.alibaba.sdk.android.openaccount.OpenAccountSDK;
import com.alibaba.sdk.android.openaccount.OpenAccountService;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.callback.LogoutCallback;
import com.alibaba.sdk.android.openaccount.model.OpenAccountSession;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIService;
import com.alibaba.sdk.android.openaccount.ui.impl.OpenAccountUIServiceImpl;
import com.alibaba.sdk.android.openaccount.ui.ui.LoginActivity;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.aliyun.alink.business.devicecenter.extbone.BoneAddDeviceBiz;
import com.aliyun.alink.business.devicecenter.extbone.BoneHotspotHelper;
import com.aliyun.alink.business.devicecenter.extbone.BoneLocalDeviceMgr;
import com.aliyun.alink.linksdk.channel.core.base.AError;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileConnectListener;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileDownstreamListener;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileRequestListener;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileChannel;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileConnectConfig;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileConnectState;
import com.aliyun.alink.linksdk.tmp.extbone.BoneSubDeviceService;
import com.aliyun.alink.linksdk.tmp.extbone.BoneThing;
import com.aliyun.alink.linksdk.tmp.extbone.BoneThingDiscovery;
import com.aliyun.alink.sdk.bone.plugins.config.BoneConfig;
import com.aliyun.alink.sdk.jsbridge.BonePluginRegistry;
import com.aliyun.iot.aep.component.router.Router;
//import com.aliyun.iot.aep.component.scan.ScanManager;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.connectchannel.BoneChannel;
import com.aliyun.iot.aep.sdk.connectchannel.log.ALog;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialListener;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManage;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageError;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageImpl;
import com.aliyun.iot.aep.sdk.credential.data.IoTCredentialData;
import com.aliyun.iot.aep.sdk.login.IRefreshSessionCallback;
import com.aliyun.iot.aep.sdk.login.data.UserInfo;
import com.aliyun.iot.aep.sdk.login.plugin.BoneUserAccountPlugin;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.aliyun.iot.ilop.demo.utils.FloatWindowHelper;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.push.HmsMessaging;
import com.juhao.home.FragmentTabHost;
import com.juhao.home.IssApplication;
import com.juhao.home.LoginIndexActivity;
import com.juhao.home.MyLoginActivity;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.juhao.home.ui.ItHomeMainFragment;
import com.mainintelligence.ItApplicationFragment;
import com.mainintelligence.ItMineMainFragment;
import com.pgyersdk.crash.PgyCrashManager;
import com.util.AppUtils;
import com.util.CommonUtil;
import com.util.Constance;
import com.util.LogUtils;
import com.util.MyShare;
import com.util.NetWorkConst;
import com.util.NetWorkUtils;
import com.util.json.JSONObject;
import com.view.MyToast;

import org.mozilla.javascript.tools.jsc.Main;

import java.lang.reflect.Field;
import java.util.Arrays;

import de.greenrobot.event.EventBus;


public class MainActivity extends FragmentActivity {
    private String TAG = MainActivity.class.getSimpleName();

    private MyFragmentTabLayout fragmentTabHost;

    private Class fragmentClass[] = {ItHomeMainFragment.class, ItApplicationFragment.class, ItMineMainFragment.class};
    private String textViewArray[] = {DemoApplication.getContext().getString(R.string.str_home), DemoApplication.getContext().getString(R.string.str_Intelligent),DemoApplication.getContext(). getString(R.string.str_me)};
    private Integer drawables[] = {R.drawable.top_bot_bar_wise, R.drawable.product_bot_bar_intelligent, R.drawable.mine_bot_bar_wise};
    private LinearLayout ll_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FloatWindowHelper helper = FloatWindowHelper.getInstance(getApplication());
        if (helper != null) {
            helper.setNeedShowFloatWindowFlag(false);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatuTextColor(this, Color.TRANSPARENT);
        setFullScreenColor(Color.TRANSPARENT,this);
        sendVersion();

//        HmsInstanceId inst  = HmsInstanceId.getInstance(this);
//        getToken();

//        HmsMessaging.getInstance(this).turnOnPush().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Log.i(TAG, "turnOnPush Complete");
//                } else {
//                    Log.e(TAG, "turnOnPush failed: ret=" + task.getException().getMessage());
//                }
//            }
//        });
        DemoApplication.activityList.add(this);
        ll_home = findViewById(R.id.ll_home);
        fragmentTabHost =  findViewById(R.id.tab_layout);
        fragmentTabHost.init(getSupportFragmentManager())
                .setFragmentTabLayoutAdapter(new DefaultFragmentTabAdapter(Arrays.asList(fragmentClass), Arrays.asList(textViewArray), Arrays.asList(drawables)) {
                    @Override
                    public View createView(int pos) {
                        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.tab_item, null);
                        ImageView imageView = (ImageView) view.findViewById(R.id.img);
                        imageView.setImageResource(drawables[pos]);
                        TextView textView = (TextView) view.findViewById(R.id.tab_text);
                        textView.setText(textViewArray[pos]);
                        return view;
                    }
                    @Override
                    public void onClick(int pos) {

                    }
                }).creat();

        //扫码添加设备 注册
//        ScanManager.getInstance().registerPlugin(AddDeviceScanPlugin.NAME, new AddDeviceScanPlugin(this));
        initMobileConnection();
//        refreshLogin();

//        BonePluginRegistry.register("BoneAddDeviceBiz", BoneAddDeviceBiz.class);
//        BonePluginRegistry.register("BoneLocalDeviceMgr", BoneLocalDeviceMgr.class);
//        BonePluginRegistry.register("BoneHotspotHelper", BoneHotspotHelper.class);
//        BonePluginRegistry.register("BoneChannel", BoneChannel.class);
//        BonePluginRegistry.register("BoneThing", BoneThing.class);
//        BonePluginRegistry.register("BoneSubDeviceService", BoneSubDeviceService.class);
//        BonePluginRegistry.register("BoneThingDiscovery", BoneThingDiscovery.class);

        // 要申请的权限 数组 可以同时申请多个权限
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION};

        if (Build.VERSION.SDK_INT >= 23) {
            //如果超过6.0才需要动态权限，否则不需要动态权限
            //如果同时申请多个权限，可以for循环遍历
            int check = ContextCompat.checkSelfPermission(this,permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (check == PackageManager.PERMISSION_GRANTED) {
                //写入你需要权限才能使用的方法
            } else {
                //手动去请求用户打开权限(可以在数组中添加多个权限) 1 为请求码 一般设置为final静态变量
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        } else {
            //写入你需要权限才能使用的方法
        }
    }

    private void initMobileConnection() {
        MobileConnectConfig config = new MobileConnectConfig();
        // 设置 appKey 和 authCode(必填)
        config.appkey = DemoApplication.app_key;
        if(DemoApplication.is_national){
            config.securityGuardAuthcode = "develop_oversea";
        }else {
        config.securityGuardAuthcode = "114d";
        }
        // 设置验证服务器（默认不填，SDK会自动使用“API通道SDK“的Host设定）
//        config.authServer = "";
        // 指定长连接服务器地址。 （默认不填，SDK会使用默认的地址及端口。默认为国内华东节点。）
//        config.channelHost = "{长连接服务器域名}";
        // 开启动态选择Host功能。 (默认false，海外环境建议设置为true。此功能前提为ChannelHost 不特殊指定。）
        config.autoSelectChannelHost = false;
        MobileChannel.getInstance().startConnect(this, config, new IMobileConnectListener() {
            @Override
            public void onConnectStateChange(MobileConnectState state) {
//                Log.e(TAG,"onConnectStateChange(), state = "+state.toString());
//                PgyCrashManager.reportCaughtException(MainActivity.this,new Exception("MobileChannel_state,"+state.toString()));
//                if(state.equals(MobileConnectState.CONNECTED)){

//                    initLoginStatus();
//                }
//                if(LoginBusiness.isLogin()&&(state.equals(MobileConnectState.CONNECTED)||state.equals(MobileConnectState.CONNECTING))){
//                    bindmqtt();
//                    initLoginStatus();
//                }
            }
        });
        PushServiceFactory.init(DemoApplication.getContext());
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.setSecurityGuardAuthCode(DemoApplication.securityIndex);


        pushService.register(DemoApplication.getContext(), new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("aep-demo", "init cloudchannel success");
//                String path = LoginBusiness.isLogin() ? BIND : UN_BIND;
//                request(path);
                // set device id
//
//
//
// 设置
//                BoneConfig.set("region", "china");

// 获取

                String deviceId = PushServiceFactory.getCloudPushService().getDeviceId();
                if (TextUtils.isEmpty(deviceId)) {
                    deviceId = "没有获取到";
                }
//                EnvConfigure.putEnvArg(EnvConfigure.KEY_DEVICE_ID, deviceId);
//                LogUtils.logE("deviceid",deviceId+"");
                if (LoginBusiness.isLogin()) {
                    request(DemoApplication.BIND);
                }else {
                    request(DemoApplication.UN_BIND);
                }
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.d("aep-demo", "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });
        MobileChannel.getInstance().registerDownstreamListener(true, new IMobileDownstreamListener() {
            @Override
            public void onCommand(String method, String data) {
                ALog.d(TAG,"接收到Topic = "+method+", data="+data);
            }

            @Override
            public boolean shouldHandle(String method) {
                // method 即为Topic，如果该Topic需要处理，返回true后onCommand才会回调。
                return true;
            }
        });
/** 注册通道的状态变化,记得调用 unRegisterConnectListener */
        MobileChannel.getInstance().registerConnectListener(true, new IMobileConnectListener() {
            @Override
            public void onConnectStateChange(MobileConnectState state) {
                ALog.d(TAG,"通道状态变化，state="+state);
                LogUtils.logE("registerConnectListener","onConnectStateChange"+state);
            }
        });

//                BonePluginRegistry.register(BoneUserAccountPlugin.API_NAME, BoneUserAccountPlugin.class);
//                BonePluginRegistry.register("BoneThing", BoneThing.class);
//                BonePluginRegistry.register("BoneSubDeviceService", BoneSubDeviceService.class);
//                BonePluginRegistry.register("BoneThingDiscovery", BoneThingDiscovery.class);
//                BonePluginRegistry.register("BoneAddDeviceBiz",BoneAddDeviceBiz.class);
//                BonePluginRegistry.register("BoneLocalDeviceMgr", BoneLocalDeviceMgr.class);
//                BonePluginRegistry.register("BoneHotspotHelper", BoneHotspotHelper.class);
//                BonePluginRegistry.register("BoneChannel", BoneChannel.class);
    }
    private long exitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    exitTime = System.currentTimeMillis();
                    MyToast.show(this,getString(R.string.str_exit_toast));
                } else {
                    finish();
                }
                return true;
            }
        return super.onKeyDown(keyCode, event);
    }
    public void getToken() {
        new Thread() {
            @Override
            public void run() {
                try {
                    String getToken =  HmsInstanceId.getInstance(MainActivity.this).getToken("100663653", "HCM");
                    if (!TextUtils.isEmpty(getToken)) {
                        PgyCrashManager.reportCaughtException(MainActivity.this,new Exception("token:"+getToken));
//                        LogUtils.logE("token",getToken);
//                        sendRegistrationToServer(getToken);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "getToken failed.", e);
                }
            }
        }.start();
    }
    private void bindmqtt() {
        IoTCredentialManage ioTCredentialManage= IoTCredentialManageImpl.getInstance(DemoApplication.getInstance());

        MobileChannel.getInstance().bindAccount(ioTCredentialManage.getIoTToken(), new IMobileRequestListener() {
            @Override
            public void onSuccess(String jsonData) {
                Log.e(TAG,"bindAccount,onSuccess"+jsonData+"");
            }

            @Override
            public void onFailure(AError error) {
                Log.e(TAG,"bindAccount,onFailure,error");
            }
        });
    }
    private void refreshLogin() {
        WebView webview=new WebView(this);
        webview.setTag("wv");
        webview.setVisibility(View.GONE);
        for(int i=0;i<ll_home.getChildCount();i++){
            if(ll_home.getChildAt(i).getTag()!=null&&ll_home.getChildAt(i).getTag().equals("wv")){
                ll_home.removeViewAt(i);
            }
        }
        ll_home.addView(webview);
        WebSettings settings=webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setAllowFileAccess(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setAppCacheEnabled(true);
        webview.setWebViewClient(new WebViewClient(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.e("shouldOverrideUrlurl", String.valueOf(request.getUrl()));
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                Log.e("onLoadResource url", url);
                super.onLoadResource(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(url.contains("callback&response_type=code&scope=")){

                }else {
                    Log.e("pagefinish",url);
                    final String code=url.substring(url.indexOf("code=")+5);
                    Log.e("code",code);
                    Message msg=new Message();
                    msg.obj=code;
                    handler.sendMessage(msg);
                }

//                if(isLogin){
//                }else {
//                login(code);
//                }
//                login("");
            }
        });

        String username= MyShare.get(this).getString(Constance.username);
        String pwd=MyShare.get(this).getString(Constance.pwd);
        if(TextUtils.isEmpty(username)||TextUtils.isEmpty(pwd)){
//            MyToast.show(this,getString(R.string.str_login_timeout));
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        webview.loadUrl("http://smart.juhao.com/login?username="+username+"&password="+pwd);
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(DemoApplication.is_created_fragment){
            Intent intent=new Intent();
            intent.putExtra(Constance.msg,"111");
            intent.setAction("recevie.login.status");
            sendBroadcast(intent);
            }
//            String code= (String) msg.obj;
//            login(code);
        }
    };
    private void initLoginStatus() {
        final IoTCredentialManageImpl ioTCredentialManage =  IoTCredentialManageImpl.getInstance(DemoApplication.getInstance());
        if(TextUtils.isEmpty(ioTCredentialManage.getIoTToken())){
            ioTCredentialManage.asyncRefreshIoTCredential(new IoTCredentialListener() {
                @Override
                public void onRefreshIoTCredentialSuccess(final IoTCredentialData ioTCredentialData) {
                    MobileChannel.getInstance().bindAccount(ioTCredentialData.iotToken, new IMobileRequestListener() {
                        @Override
                        public void onSuccess(String s) {
//                            LogUtils.logE("ioTCredentialData",ioTCredentialData.identity);
//                            PgyCrashManager.reportCaughtException(MainActivity.this,new Exception("onRefreshIoTCredentialSuccess_bindAccount success"));
                            Log.e(TAG,"mqtt bindAccount onSuccess");
                            SendLoginSuccessMsg();
                        }

                        @Override
                        public void onFailure(AError aError) {
//                            LogUtils.logE("ioTCredentialData",ioTCredentialData.identity);
//                            PgyCrashManager.reportCaughtException(MainActivity.this,new Exception("onRefreshIoTCredentialSuccess_bindAccount faliure"));
                            Log.e(TAG,"mqtt bindAccount onFailure aError = " + aError.getMsg());
//                            refreshLogin();
//                            Intent intent=new Intent(MainActivity.this,IndexActivity.class);
//                            intent.putExtra(Constance.islogin,true);
//                            startActivity(intent);
//                            finish();
                            MobileChannel.getInstance().bindAccount(ioTCredentialData.iotToken, new IMobileRequestListener() {
                                @Override
                                public void onSuccess(String s) {
                                    SendLoginSuccessMsg();
                                }

                                @Override
                                public void onFailure(AError aError) {
                                    showLogin();
                                }
                            });


                        }
                    });
                }

                @Override
                public void onRefreshIoTCredentialFailed(IoTCredentialManageError ioTCredentialManageError) {
                    Log.e(TAG,"mqtt bindAccount onFailure ");
//                    PgyCrashManager.reportCaughtException(MainActivity.this,new Exception("onRefreshIoTCredentialFailed"));
////                    refreshLogin();
//                    Intent intent=new Intent(MainActivity.this,IndexActivity.class);
//                    intent.putExtra(Constance.islogin,true);
//                    startActivity(intent);
//                    finish();
                    showLogin();
                }
            });
        } else {
//            MobileChannel.getInstance().startConnect(context, config, new IMobileConnectListener() {
//                @Override
//                public void onConnectStateChange(MobileConnectState state) {
//                    Log.e(TAG,"onConnectStateChange(), state = "+state.toString());
//                }
//            });
            MobileChannel.getInstance().bindAccount(ioTCredentialManage.getIoTToken(), new IMobileRequestListener() {
                @Override
                public void onSuccess(String s) {
//                    LogUtils.logE("ioTCredentialData",ioTCredentialManage.getIoTIdentity());
                    SendLoginSuccessMsg();
//                    PgyCrashManager.reportCaughtException(MainActivity.this,new Exception("Main_bindAccount_onsuccess"));
                    Log.e(TAG,"mqtt bindAccount onSuccess ");
                }

                @Override
                public void onFailure(AError aError) {
                    Log.e(TAG,"mqtt bindAccount onFailure aError = " + aError.getMsg());
//                    refreshLogin();
//                    PgyCrashManager.reportCaughtException(MainActivity.this,new Exception("Main_bindAccount_onfailure"));
//                    Intent intent=new Intent(MainActivity.this,IndexActivity.class);
//                    intent.putExtra(Constance.islogin,true);
//                    startActivity(intent);
//                    finish();
                    showLogin();

                }
            });
        }
    }

    private void showLogin() {
        startActivity(new Intent(this, LoginIndexActivity.class));
//        OpenAccountUIService openAccountUIService=new OpenAccountUIServiceImpl();
//        openAccountUIService.showLogin(MainActivity.this, MyLoginActivity.class, new LoginCallback() {
//            @Override
//            public void onSuccess(OpenAccountSession openAccountSession) {
//
////                Toast.makeText(MainActivity.this, "login"+LoginBusiness.isLogin(), Toast.LENGTH_SHORT).show();
//                LogUtils.logE("login","success"+openAccountSession.getLoginId());
//            }
//            @Override
//            public void onFailure(int i, String s) {
//                LogUtils.logE("login","failure"+s);
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        //退出首页不显示浮窗
//        FloatWindowHelper helper = FloatWindowHelper.getInstance(getApplication());
//        if (helper != null) {
//            helper.setNeedShowFloatWindowFlag(false);
//        }
//        MainActivity.mFragmentPosition=0;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(LoginBusiness.isLogin()){
//            MyToast.show(this,"Main_isLogin");
            LoginBusiness.refreshSession(true, new IRefreshSessionCallback() {
                @Override
                public void onRefreshSuccess() {
//                    MyToast.show(MainActivity.this,"Main_onRefreshSuccess");
//                    Log.i(TAG,"刷新Session成功");
                    String sessionid=LoginBusiness.getSessionId();
                    UserInfo info=LoginBusiness.getUserInfo();
                }
                @Override
                public void onRefreshFailed() {
                    Log.i(TAG,"刷新Session失败");
//                    MyToast.show(MainActivity.this,"Main_onRefreshFailed");
                }
            });

            String sessionid=LoginBusiness.getSessionId();
            UserInfo info=LoginBusiness.getUserInfo();
            IoTCredentialManage ioTCredentialManage=IoTCredentialManageImpl.getInstance(DemoApplication.getInstance());
//            LogUtils.logE("ioTCredentialManage",ioTCredentialManage.getIoTIdentity());
            MobileChannel.getInstance().bindAccount(ioTCredentialManage.getIoTToken(), new IMobileRequestListener() {
                @Override
                public void onSuccess(String jsonData) {

                    LogUtils.logE("bindAccount","onSuccess");
                }
                @Override
                public void onFailure(AError error) {
                    LogUtils.logE("bindAccount","onFailure");

                }
            });
            SendLoginSuccessMsg();
        }else {
//            MyToast.show(this,"Main_isNotLogin");
            initLoginStatus();
        }

//        if (!LoginBusiness.isLogin()) {
//            Intent intent = new Intent(getApplicationContext(), IndexActivity.class);
//            startActivity(intent);
//            finish();
//        }
    }

    private void SendLoginSuccessMsg() {
//        UserInfo userInfo=LoginBusiness.getUserInfo();
        Message msg=new Message();
        msg.arg1=1;
//        EventBus.getDefault().postSticky(msg);
//        handler.sendEmptyMessage(0);
        handler.postDelayed(runnable,0);
    }
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            if(!DemoApplication.is_created_fragment){
                handler.postDelayed(runnable,500);
            }else {
                handler.sendEmptyMessage(0);
            }
        }
    };
    public void setHandler(Handler handler){
        this.handler = handler;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Log.d(TAG, "onActivityResult");
            if (data!=null&&data.getStringExtra("productKey") != null){
                Bundle bundle = new Bundle();
                bundle.putString("productKey", data.getStringExtra("productKey"));
                bundle.putString("deviceName", data.getStringExtra("deviceName"));
                bundle.putString("token", data.getStringExtra("token"));
                Intent intent = new Intent(this, BindAndUseActivity.class);
                intent.putExtras(bundle);
                this.startActivity(intent);
            }
        }
    }
    public void goBack(View v){
        finish();
    }

    /**
     * 获取版本号
     */
    private void sendVersion(){
//        mNetWork.sendVersion(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ans = NetWorkUtils.doGet(NetWorkConst.VERSION_URL_CONTENT);
                final JSONObject jsonObject=new JSONObject(ans);
                final String mAppVersion = jsonObject.getString(Constance.version);
                if(AppUtils.isEmpty(mAppVersion)) return;
                String localVersion = CommonUtil.localVersionName(MainActivity.this);
                if ("-1".equals(mAppVersion)) {

                } else {
                    boolean isNeedUpdate = CommonUtil.isNeedUpdate(localVersion, mAppVersion);
                    if (isNeedUpdate){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                final Dialog dialog=new Dialog(MainActivity.this,R.style.customDialog);
                                dialog.setContentView(R.layout.dialog_update);
                                TextView tv_version=dialog.findViewById(R.id.tv_content);
                                TextView tv_upgrate=dialog.findViewById(R.id.tv_update);
                                TextView iv_close=dialog.findViewById(R.id.tv_cancel);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.setCancelable(false);
                                iv_close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                tv_version.setText("V"+mAppVersion+"\n "+jsonObject.getString(Constance.text));
                                tv_upgrate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent();
                                        intent.setAction("android.intent.action.VIEW");
                                        Uri content_url = Uri.parse("https://a.app.qq.com/o/simple.jsp?pkgname=com.juhao.home");
                                        intent.setData(content_url);
                                        startActivity(intent);
                                    }
                                });
                                dialog.show();
//                                UIUtils.showSingleWordDialog(MainActivity.this, "发现有新版本，是否更新？", new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Intent intent = new Intent();
//                                        intent.setAction("android.intent.action.VIEW");
//                                        Uri content_url = Uri.parse("http://app.08138.com/txdc.apk");
//                                        intent.setData(content_url);
//                                        startActivity(intent);
//                                    }
//                                });

                            }
                        });


                    }
                }

            }
        }).start();

    }

    private void login(final String authCode) {
        OpenAccountService service = OpenAccountSDK.getService(OpenAccountService.class);
        service.authCodeLogin(MainActivity.this, authCode, new LoginCallback() {
            @Override
            public void onSuccess(OpenAccountSession openAccountSession) {

                IoTCredentialManageImpl ioTCredentialManage =  IoTCredentialManageImpl.getInstance(DemoApplication.getInstance());
                if(TextUtils.isEmpty(ioTCredentialManage.getIoTToken())){
                    ioTCredentialManage.asyncRefreshIoTCredential(new IoTCredentialListener() {
                        @Override
                        public void onRefreshIoTCredentialSuccess(IoTCredentialData ioTCredentialData) {
                            MobileChannel.getInstance().bindAccount(ioTCredentialData.iotToken, new IMobileRequestListener() {
                                @Override
                                public void onSuccess(String s) {
                                    Log.e(TAG,"mqtt bindAccount onSuccess");
                                }

                                @Override
                                public void onFailure(AError aError) {
                                    Log.e(TAG,"mqtt bindAccount onFailure aError = " + aError.getMsg());
                                }
                            });
                        }

                        @Override
                        public void onRefreshIoTCredentialFailed(IoTCredentialManageError ioTCredentialManageError) {
                            Log.e(TAG,"mqtt onRefreshIoTCredentialFailed ");

                        }
                    });
                } else {
                    MobileChannel.getInstance().bindAccount(ioTCredentialManage.getIoTToken(), new IMobileRequestListener() {
                        @Override
                        public void onSuccess(String s) {
                            Log.e(TAG,"mqtt bindAccount onSuccess ");
                        }

                        @Override
                        public void onFailure(AError aError) {
                            Log.e(TAG,"mqtt bindAccount onFailure aError = " + aError.getMsg());

                        }
                    });
                }
//                            //注册虚拟设备
//                            String[] pks = {"a1nbd4BjB3N", "a186j8fot9K", "a1XKEJTOkPL", "a1aJCduQG0p"};
//                            for (String pk : pks) {
//                                registerVirtualDevice(pk);
//                            }
                mH.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Router.getInstance().toUrl(MainActivity.this, "page/ilopmain");
                        finish();
                    }
                }, 0);
            }

            @Override
            public void onFailure(int code, String msg) {
//                MyToast.show(MainActivity.this,getString(R.string.str_login_timeout));
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return;
//                            Toast.makeText(getApplicationContext(), "auth授权登录 失败 code = " + code + " message = " + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void logout() {
        OpenAccountService openAccountService = OpenAccountSDK.getService(OpenAccountService.class);

        try {
            openAccountService.logout(this, new LogoutCallback() {
                @Override
                public void onSuccess() {
//                    ToastUtil.show(getApplicationContext(), "登出成功");
                    Log.e("logout","登出成功");
                    IoTCredentialManageImpl.getInstance(DemoApplication.getInstance()).clearIoTTokenInfo();
                    refreshLogin();
                }

                @Override
                public void onFailure(int i, String s) {
//                    ToastUtil.show(getApplicationContext(), "登出失败 : " + s);
                    Log.e("logout","登出失败");
                    refreshLogin();
                }
            });
        } catch (Exception e) {
            refreshLogin();
            Log.e("logout","登出异常");
//            ToastUtil.show(getApplicationContext(), "登出异常 : " + e.toString());
        }
    }
    Handler mH=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };


    public void setStatuTextColor(Activity activity, int color) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            //取消状态栏透明
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //添加Flag把状态栏设为可绘制模式
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(color);
            //设置系统状态栏处于可见状态
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                //设置状态栏文字颜色及图标为深色
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }
    public static void setFullScreenColor(int color,Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            // 生成一个状态栏大小的矩形
//            View statusView = createStatusView(activity, color);
            // 添加 statusView 到布局中
//            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
//            decorView.addView(statusView);
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
//            rootView.setFitsSystemWindows(true);
//            rootView.setClipToPadding(true);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                try {
                    Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                    Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                    field.setAccessible(true);
                    field.setInt(activity.getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
                } catch (Exception e) {}
            }
        }
    }
    void request(String path) {
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        String deviceId = pushService.getDeviceId();
        if (TextUtils.isEmpty(deviceId)) {
            return;
        }
        String apiVersion = "1.0.2";
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setAuthType("iotAuth")
                .setScheme(Scheme.HTTPS)
                .setPath(path)
                .setApiVersion(apiVersion)
                .addParam("deviceType", "ANDROID")
                .addParam("deviceId", deviceId);
        IoTRequest request = builder.build();
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                e.printStackTrace();
                LogUtils.logE("binddevice","onfailure");
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                LogUtils.logE("binddevice","onresponse");

            }
        });
    }

}
