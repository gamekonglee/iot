package com.aliyun.iot.ilop.demo;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
//import androidx.annotation.RequiresApi;
//import android.support.multidex.MultiDex;

import androidx.multidex.MultiDex;

import com.BaseApplication;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.sdk.android.openaccount.ConfigManager;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.wireless.security.jaq.JAQException;
import com.alibaba.wireless.security.jaq.SecurityInit;
import com.aliyun.alink.alirn.RNGlobalConfig;
import com.aliyun.alink.business.devicecenter.extbone.BoneAddDeviceBiz;
import com.aliyun.alink.business.devicecenter.extbone.BoneHotspotHelper;
import com.aliyun.alink.business.devicecenter.extbone.BoneLocalDeviceMgr;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileConnectListener;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileChannel;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileConnectConfig;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileConnectState;
import com.aliyun.alink.linksdk.tmp.TmpSdk;
import com.aliyun.alink.linksdk.tmp.api.TmpInitConfig;
import com.aliyun.alink.linksdk.tmp.extbone.BoneSubDeviceService;
import com.aliyun.alink.linksdk.tmp.extbone.BoneThing;
import com.aliyun.alink.linksdk.tmp.extbone.BoneThingDiscovery;
import com.aliyun.alink.linksdk.tools.ThreadTools;
import com.aliyun.alink.page.rn.InitializationHelper;
import com.aliyun.alink.sdk.bone.plugins.BaseBoneServiceFactory;
import com.aliyun.alink.sdk.bone.plugins.config.BoneConfig;
import com.aliyun.alink.sdk.jsbridge.BonePluginRegistry;
import com.aliyun.iot.aep.component.router.IUrlHandler;
import com.aliyun.iot.aep.oa.OALanguageHelper;
import com.aliyun.iot.aep.routerexternal.RouterExternal;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientImpl;
import com.aliyun.iot.aep.sdk.apiclient.adapter.APIGatewayHttpAdapterImpl;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Env;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.hook.IoTAuthProvider;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestWrapper;
//import com.aliyun.iot.aep.sdk.base.delegate.delegate.DownstreamConnectorSDKDelegate;
import com.aliyun.iot.aep.sdk.bridge.core.BoneServiceFactoryRegistry;
import com.aliyun.iot.aep.sdk.connectchannel.BoneChannel;
import com.aliyun.iot.aep.sdk.connectchannel.log.ALog;
import com.aliyun.iot.aep.sdk.credential.IoTCredentialProviderImpl;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageImpl;
import com.aliyun.iot.aep.sdk.credential.listener.IoTTokenInvalidListener;
import com.aliyun.iot.aep.sdk.framework.bundle.BundleManager;
import com.aliyun.iot.aep.sdk.framework.bundle.IBundleRegister;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.login.oa.OALoginAdapter;
import com.aliyun.iot.aep.sdk.login.plugin.BoneUserAccountPlugin;
import com.aliyun.iot.aep.sdk.framework.bundle.PageConfigure;
//import com.aliyun.iot.ilop.page.scan.ScanPageInitHelper;
import com.aliyun.iot.push.PushManager;
//import com.facebook.react.FrescoPackage;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.juhao.home.R;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.pgyersdk.crash.PgyCrashManager;
import com.util.Constance;
import com.util.LogUtils;
import com.util.MyShare;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Created by wuwang on 2017/10/30.
 */

public class DemoApplication extends BaseApplication {
//    public static String app_key="27615405";
    public static String app_key="27843375";
    public static String productKey="";
    public static String token="";
    public static boolean isEng=false;
    public static String productName;
    public static String wifi;
    public static String pwd;
    public static String productApId;
    public static JSONArray newOrders=new JSONArray();
    public static JSONArray caConditions=new JSONArray();
    public static JSONArray actions=new JSONArray();
    public static boolean is_created_fragment;
    public static boolean has_got;
    private static Application instance;
    public static boolean isShowLogin;


//    public static JSONObject UserInfo;
    public static int unreadMsgCount;
//    private static ArrayList<CommentBean> commentList;
    private static DisplayImageOptions options;
    private static DisplayImageOptions defaultOptions;
    private static ImageLoaderConfiguration config;
    private Context mContext;
    public static List<Activity> activityList = new LinkedList();
    public static boolean is_national;
    private static String host;
    public static String securityIndex = "114d";;
    private OALoginAdapter adapter;
    List<Activity> activities=new ArrayList<>();
    public static List<Activity> getActivityList() {
        return  activityList;
    }

    @Override
    public void onCreate() {
        MultiDex.install(this);
        super.onCreate();
        mContext = getApplicationContext();
        is_national = MyShare.get(mContext).getBoolean(Constance.is_national);

//        String build_country = BuildConfig.BUILD_COUNTRY;// 在buildTypes 内配置不同版本类别 CHINA 国内版  OVERSEA 海外版
        app_key=is_national?"27939889":"27843375";
                if(is_national){
//            BuildConfig.BUILD_COUNTRY="SINGAPORE";
            // 海外环境，请参考如下设置
            //设置authCode
            securityIndex="develop_oversea";
            host = "api-iot.ap-southeast-1.aliyuncs.com";
//            app_key="27939889";
        }else {
            // 国内环境
            host="api.link.aliyun.com";
            securityIndex="114d";
        }
//        PushManager.getInstance().init(this,securityIndex); // app => application
        // 三方推送通道初始化
//        PushManager.getInstance().initHuaweiPush(this);
        initImageLoader();
//        ImageLoadProxy.initImageLoader(mContext);
//        JPushInterface.setDebugMode(false); 	// 设置开启日志,发布时请关闭日志
//        JPushInterface.init(this);     		// 初始化 JPush
//        JAnalyticsInterface.init(mContext);
//        JAnalyticsInterface.initCrashHandler(mContext);
        PgyCrashManager.register(this);
        instance=this;
        //         其他 SDK, 仅在 主进程上初始化
        String packageName = this.getPackageName();
        if (!packageName.equals(ThreadTools.getProcessName(this, android.os.Process.myPid()))) {
            return;
        }
        com.aliyun.iot.aep.sdk.log.ALog.setLevel(ALog.LEVEL_DEBUG);
        com.aliyun.alink.linksdk.tools.ALog.setLevel(com.aliyun.alink.linksdk.tools.ALog.LEVEL_DEBUG);


  /*      // api gateway
        EnvConfigure.putEnvArg(APIGatewaySDKDelegate.ENV_KEY_API_CLIENT_API_ENV, "RELEASE");
        //CHINA SINGAPORE
        if ("CHINA".equalsIgnoreCase(build_country)) {
            EnvConfigure.putEnvArg(APIGatewaySDKDelegate.ENV_KEY_API_CLIENT_DEFAULT_HOST, "api.link.aliyun.com");
        } else if ("SINGAPORE".equalsIgnoreCase(build_country)) {
            // 新加坡版初始化
            EnvConfigure.putEnvArg(APIGatewaySDKDelegate.ENV_KEY_API_CLIENT_DEFAULT_HOST, "api-iot.ap-southeast-1.aliyuncs.com");
        }
        // OA
        if ("CHINA".equalsIgnoreCase(build_country)) {
            EnvConfigure.putEnvArg(OpenAccountSDKDelegate.ENV_KEY_OPEN_ACCOUNT_HOST, null);
        } else if ("SINGAPORE".equalsIgnoreCase(build_country)) {
            // 新加坡初始化
            EnvConfigure.putEnvArg(OpenAccountSDKDelegate.ENV_KEY_OPEN_ACCOUNT_HOST, "sgp-sdk.openaccount.aliyun.com");
        }
        // MQTT
        EnvConfigure.putEnvArg(DownstreamConnectorSDKDelegate.ENV_KEY_MQTT_HOST, null);
        if ("CHINA".equalsIgnoreCase(build_country)) {
            EnvConfigure.putEnvArg(DownstreamConnectorSDKDelegate.ENV_KEY_MQTT_AUTO_HOST, "false");
            EnvConfigure.putEnvArg(DownstreamConnectorSDKDelegate.ENV_KEY_MQTT_SERVER_AUTO_SELECT_CHANNEL, "");
        } else if ("SINGAPORE".equalsIgnoreCase(build_country)) {
            // 新加坡版初始化
            EnvConfigure.putEnvArg(DownstreamConnectorSDKDelegate.ENV_KEY_MQTT_AUTO_HOST, "true");
            EnvConfigure.putEnvArg(DownstreamConnectorSDKDelegate.ENV_KEY_MQTT_SERVER_AUTO_SELECT_CHANNEL, "");
        }
        EnvConfigure.putEnvArg(DownstreamConnectorSDKDelegate.ENV_KEY_MQTT_CHECK_ROOT_CRT, "true");
        // set env for sdks .end

        EnvConfigure.putEnvArg(RNContainerComponentDelegate.KEY_RN_CONTAINER_PLUGIN_ENV, "RELEASE");
        EnvConfigure.putEnvArg(EnvConfigure.KEY_LANGUAGE, getString(R.string.str_language));

        // the key set from sp that need to be put into AConfigure.envArgs
        HashSet spKeySet = new HashSet();
        EnvConfigure.init(this, spKeySet);

        new ApplicationHelper().onCreate(this);

//         加载Native页面
        BundleManager.init(this, new IBundleRegister() {
            @Override
            public void registerPage(Application application, PageConfigure configure) {
                if (null == configure || null == configure.navigationConfigures)
                    return;

                ArrayList<String> nativeUrls = new ArrayList<>();
                ArrayList<PageConfigure.NavigationConfigure> configures = new ArrayList<>();

                PageConfigure.NavigationConfigure deepCopyItem = null;
                for (PageConfigure.NavigationConfigure item : configure.navigationConfigures) {
                    if (null == item.navigationCode || item.navigationCode.isEmpty() || null == item.navigationIntentUrl || item.navigationIntentUrl.isEmpty())
                        continue;

                    deepCopyItem = new PageConfigure.NavigationConfigure();
                    deepCopyItem.navigationCode = item.navigationCode;
                    deepCopyItem.navigationIntentUrl = item.navigationIntentUrl;
                    deepCopyItem.navigationIntentAction = item.navigationIntentAction;
                    deepCopyItem.navigationIntentCategory = item.navigationIntentCategory;

                    configures.add(deepCopyItem);

                    nativeUrls.add(deepCopyItem.navigationIntentUrl);

                    com.aliyun.iot.aep.sdk.log.ALog.d("BundleManager", "register-native-page: " + item.navigationCode + ", " + item.navigationIntentUrl);

                    RouterExternal.getInstance().registerNativeCodeUrl(deepCopyItem.navigationCode, deepCopyItem.navigationIntentUrl);
                    RouterExternal.getInstance().registerNativePages(nativeUrls, new NativeUrlHandler(deepCopyItem));
                }
            }
        });
      */

        //初始化pagescan页面的router配置
        // 初始化无线保镖
        try {
            SecurityInit.Initialize(mContext);
        } catch (JAQException ex) {

        } catch (Exception ex) {

        }
        // 初始化 IoTAPIClient
        IoTAPIClientImpl.InitializeConfig config = new IoTAPIClientImpl.InitializeConfig();
//        if(is_national){
////            BuildConfig.BUILD_COUNTRY="SINGAPORE";
//            // 海外环境，请参考如下设置
//            //设置authCode
//            securityIndex="develop_oversea";
//            host = "api-iot.ap-southeast-1.aliyuncs.com";
////            app_key="27939889";
//        }else {
//            // 国内环境
//            host="api.link.aliyun.com";
//            securityIndex="114d";
//        }

        if(is_national){
            // 海外环境，请参考如下设置
            config.host = "api-iot.ap-southeast-1.aliyuncs.com";
            //设置authCode
            securityIndex="develop_oversea";
            host = "api-iot.ap-southeast-1.aliyuncs.com";
//        app_key="27939889";
        }else {
            // 国内环境
            config.host = "api.link.aliyun.com";
            host="api.link.aliyun.com";
            securityIndex="114d";
        }
        config.appKey=DemoApplication.app_key;
        config.authCode = securityIndex;
        config.apiEnv = Env.RELEASE; //只支持RELEASE
        //设置请求超时（可选）默认超时时间10s
        config.connectTimeout=10_000L;
        config.readTimeout=10_000L;
        config.writeTimeout=10_000L;

        IoTAPIClientImpl impl = IoTAPIClientImpl.getInstance();
        impl.init(mContext, config);
        impl.setLanguage(getString(R.string.str_language));
        // 日志处理
        impl.registerTracker(new Tracker());
//        APIGatewaySDKDelegate.apiEnv = Env.RELEASE; //初始化中config.apiEnv的值
//        APIGatewaySDKDelegate.host =host; //初始化中config.host的值

//如果需要切换到海外环境，请执行下面setDefaultOAHost方法，默认为大陆环境
//adapter.setDefaultOAHost("sgp-sdk.openaccount.aliyun.com");

        ConfigManager.getInstance().setSecGuardImagePostfix(securityIndex);
        if(DemoApplication.is_national){
            adapter = new OALoginAdapter(mContext);
            adapter.setDefaultOAHost("sgp-sdk.openaccount.aliyun.com");
            adapter.init("online");
        }else {
            adapter = new OALoginAdapter(mContext);
            adapter.setDefaultOAHost(null);
            adapter.init("online");
        }

        LoginBusiness.init(mContext, adapter, "online");
//务必注意在调用之前，保证完成了用户和账号SDK的初始化
//        if(is_national){
        app_key=APIGatewayHttpAdapterImpl.getAppKey(mContext,  securityIndex);
//        }
//        LogUtils.logE("appkey",app_key);
        IoTCredentialManageImpl.init(app_key);
        IoTAuthProvider provider = new IoTCredentialProviderImpl(IoTCredentialManageImpl.getInstance((Application) mContext));
        IoTAPIClientImpl.getInstance().registerIoTAuthProvider("iotAuth", provider);
//        Map<String,Object>paramMap=new HashMap<>();
//         IoTRequest request = new IoTRequestBuilder()
//                .setPath("path")
//                .setApiVersion("1.0.4")
//                .addParam("request", paramMap)
//                .setAuthType("iotAuth")
//                .build();
//        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
//        ioTAPIClient.send(request, new IoTCallback() {
//            @Override
//            public void onFailure(IoTRequest ioTRequest, Exception e) {
//
//            }
//
//            @Override
//            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
////                if(ioTResponse.getCode()==401){
////                    LoginBusiness.logout(new ILogoutCallback() {
////                        @Override
////                        public void onLogoutSuccess() {
////                            final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
////                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                            startActivity(intent);
////                            android.os.Process.killProcess(android.os.Process.myPid());
////                        }
////                        @Override
////                        public void onLogoutFailed(int i, String s) {
////                            final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
////                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                            startActivity(intent);
////                            android.os.Process.killProcess(android.os.Process.myPid());
////                        }
////                    });
////                }
//            }
//        });
        IoTCredentialManageImpl.getInstance((Application) mContext).setIotTokenInvalidListener(new IoTTokenInvalidListener() {
            @Override
            public void onIoTTokenInvalid() {
                LogUtils.logE("iot","onIoTTokenInvalid");
            }
        });

        // the key set from sp that need to be put into AConfigure.envArgs

//        EnvConfigure.putEnvArg(EnvConfigure.KEY_LANGUAGE, getString(R.string.str_language) );

//        IBreeze breeze = Factory.createBreeze(this);
//        Config config = new Config.Builder()
//                .debug(BuildConfig.DEBUG)
//                .log(BuildConfig.DEBUG)
//                .logLevel(BuildConfig.DEBUG ? Log.VERBOSE :Log.WARN)
//                .build();
//        breeze.configure(config);
        // 支持扫码调试
//        ScanManager.getInstance().registerPlugin("boneMobile", new BoneMobileScanPlugin());
        //初始化pagescan页面的router配置
//        ScanPageInitHelper.initPageScanRouterConfig();
        new Thread(){
            @Override
            public void run() {
                super.run();
                SystemClock.sleep(2000);

            }
        }.start();
//        initMobileConnect();
//        initBone();

        initMobileConnect();
        initBone();
        String laguage=getString(R.string.str_language);
//
        IoTAPIClientImpl.getInstance().setLanguage(laguage); // 全局配置，设置后立即起效
//        // APIClient更改语言后，push通道重新绑定即可更改push语言
//        PushManager.getInstance().bindUser();
        if(laguage.equals("zh-CN")){
            OALanguageHelper.setLanguageCode(Locale.SIMPLIFIED_CHINESE);
            isEng=false;
        }else if(laguage.equals("en-US")) {
            isEng=true;
            OALanguageHelper.setLanguageCode(Locale.US);
        }
        // 容器更改语言
        BoneConfig.set("language", laguage);
//目前支持中国“china”，新加坡“singapore”
        BoneConfig.set("region", is_national?"singapore":"china");

    }

    private void initBone() {
        String serverEnv = "production";//仅支持"production",即生产环境
        String pluginEnv = "release";//仅支持“release”
        String language =getString(R.string.str_language) ;//语言环境，目前支持中文“zh-CN”, 英文"en-US"，法文"fr-FR",德文"de-DE",日文"ja-JP",韩文"ko-KR",西班牙文"es-ES",俄文"ru-RU"，八种语言

        // 初始化 BoneMobile RN 容器
        InitializationHelper.initialize(mContext, pluginEnv, serverEnv,language);

//        RNGlobalConfig.addBizPackage(new FrescoPackage());
        // 添加基于 Fresco 的图片组件支持
//        RNGlobalConfig.addBizPackage(new ReactPackage() {
//            @Nonnull
//            @Override
//            public List<NativeModule> createNativeModules(@Nonnull ReactApplicationContext reactApplicationContext) {
//                return null;
//
//            }
//
//            @Nonnull
//            @Override
//            public List<ViewManager> createViewManagers(@Nonnull ReactApplicationContext reactApplicationContext) {
//                return null;
//            }
//        });
//        BoneServiceFactoryRegistry.register(new BaseBoneServiceFactory());
        BonePluginRegistry.register("BoneThing", BoneThing.class);
        BonePluginRegistry.register("BoneSubDeviceService", BoneSubDeviceService.class);
        BonePluginRegistry.register("BoneThingDiscovery", BoneThingDiscovery.class);
        BonePluginRegistry.register("BoneAddDeviceBiz",BoneAddDeviceBiz.class);
        BonePluginRegistry.register("BoneLocalDeviceMgr",BoneLocalDeviceMgr.class);
        BonePluginRegistry.register("BoneHotspotHelper",BoneHotspotHelper.class);
        BonePluginRegistry.register(BoneUserAccountPlugin.API_NAME, BoneUserAccountPlugin.class);


// 如果需要绑定蓝牙设备，需要添加如下代码
//        BonePluginRegistry.register("BoneThing", BoneThing.class);


//
//        BonePluginRegistry.register(BoneChannel.API_NAME, BoneChannel.class);
//        RouterExternal.getInstance().init(this, pluginEnv);

    }
    public static final String BIND = "/uc/bindPushChannel";
    public static final String UN_BIND = "/uc/unbindPushChannel";
    private void initMobileConnect() {
        //打开Log 输出
//        ALog.setLevel(ALog.LEVEL_DEBUG);

        MobileConnectConfig config = new MobileConnectConfig();
        // 设置 appKey 和 authCode(必填)
        config.appkey = "{"+app_key+"}";
        if(DemoApplication.is_national){
            config.securityGuardAuthcode = securityIndex;
            config.autoSelectChannelHost = true;
        }else {
        config.securityGuardAuthcode = securityIndex;
            config.autoSelectChannelHost = false;
        }


        // 设置验证服务器（默认不填，SDK会自动使用“API通道SDK“的Host设定）
//        config.authServer = "";

        // 指定长连接服务器地址。 （默认不填，SDK会使用默认的地址及端口。默认为国内华东节点。）
//        config.channelHost = "{长连接服务器域名}";

        // 开启动态选择Host功能。 (默认false，海外环境建议设置为true。此功能前提为ChannelHost 不特殊指定。）
        MobileChannel.getInstance().startConnect(this, config, new IMobileConnectListener() {
            @Override
            public void onConnectStateChange(MobileConnectState state) {
                ALog.d("mobileC","onConnectStateChange(), state = "+state.toString());
                BonePluginRegistry.register("BoneChannel", BoneChannel.class);
//                PushServiceFactory.init(DemoApplication.getContext());
//                CloudPushService pushService = PushServiceFactory.getCloudPushService();
//                pushService.setSecurityGuardAuthCode(DemoApplication.securityIndex);
//
//
//                pushService.register(DemoApplication.getContext(), new CommonCallback() {
//                    @Override
//                    public void onSuccess(String response) {
//                        Log.d("aep-demo", "init cloudchannel success");
////                String path = LoginBusiness.isLogin() ? BIND : UN_BIND;
////                request(path);
//
//                        // set device id
//                        String deviceId = PushServiceFactory.getCloudPushService().getDeviceId();
//                        if (TextUtils.isEmpty(deviceId)) {
//                            deviceId = "没有获取到";
//                        }
////                EnvConfigure.putEnvArg(EnvConfigure.KEY_DEVICE_ID, deviceId);
//                        LogUtils.logE("deviceid",deviceId);
//                        if (LoginBusiness.isLogin()) {
//                            request(BIND);
//                        }
//                    }
//
//                    @Override
//                    public void onFailed(String errorCode, String errorMessage) {
//                        Log.d("aep-demo", "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
//                    }
//                });
            }
        });

        TmpSdk.init(getBaseContext(), new TmpInitConfig(TmpInitConfig.ONLINE));
        TmpSdk.getDeviceManager().discoverDevices(null,5000,null);
    }

    /* methods: AApplication */
//
//    @Override
//    protected void onFirstActivityOnCreated(Activity activity, Bundle savedInstanceState) {
//        super.onFirstActivityOnCreated(activity, savedInstanceState);
//    }
//
//    @Override
//    protected void onFirstActivityOnVisible(Activity activity) {
//        super.onFirstActivityOnVisible(activity);
//    }
//
//    @Override
//    protected void onLastActivityDestroyed(Activity activity) {
//        super.onLastActivityDestroyed(activity);
//    }
//
//    @Override
//    protected void onLastActivityInvisible(Activity activity) {
//        super.onLastActivityInvisible(activity);
//    }

    private static class Tracker implements com.aliyun.iot.aep.sdk.apiclient.tracker.Tracker {
        final String TAG = "APIGatewaySDKDelegate$Tracker";

        @Override
        public void onSend(IoTRequest request) {
            ALog.i(TAG, "onSend:\r\n" + toString(request));
        }

        @Override
        public void onRealSend(IoTRequestWrapper ioTRequest) {
            ALog.d(TAG, "onRealSend:\r\n" + toString(ioTRequest));
        }

        @Override
        public void onRawFailure(IoTRequestWrapper ioTRequest, Exception e) {
            ALog.d(TAG, "onRawFailure:\r\n" + toString(ioTRequest) + "ERROR-MESSAGE:" + e.getMessage());
            e.printStackTrace();
        }

        @Override
        public void onFailure(IoTRequest request, Exception e) {
            ALog.i(TAG, "onFailure:\r\n" + toString(request) + "ERROR-MESSAGE:" + e.getMessage());
        }

        @Override
        public void onRawResponse(IoTRequestWrapper request, IoTResponse response) {
            ALog.d(TAG, "onRawResponse:\r\n" + toString(request) + toString(response));
        }

        @Override
        public void onResponse(IoTRequest request, IoTResponse response) {
            ALog.i(TAG, "onResponse:\r\n" + toString(request) + toString(response));
        }

        private static String toString(IoTRequest request) {
            return new StringBuilder("Request:").append("\r\n")
                    .append("url:").append(request.getScheme()).append("://").append(null == request.getHost() ? "" : request.getHost()).append(request.getPath()).append("\r\n")
                    .append("apiVersion:").append(request.getAPIVersion()).append("\r\n")
                    .append("params:").append(null == request.getParams() ? "" : JSON.toJSONString(request.getParams())).append("\r\n").toString();
        }

        private static String toString(IoTRequestWrapper wrapper) {
            IoTRequest request = wrapper.request;

            return new StringBuilder("Request:").append("\r\n")
                    .append("id:").append(wrapper.payload.getId()).append("\r\n")
                    .append("apiEnv:").append(Env.RELEASE).append("\r\n")
                    .append("url:").append(request.getScheme()).append("://").append(TextUtils.isEmpty(wrapper.request.getHost()) ? host : wrapper.request.getHost()).append(request.getPath()).append("\r\n")
                    .append("apiVersion:").append(request.getAPIVersion()).append("\r\n")
                    .append("params:").append(null == request.getParams() ? "" : JSON.toJSONString(request.getParams())).append("\r\n")
                    .append("payload:").append(JSON.toJSONString(wrapper.payload)).append("\r\n").toString();
        }

        private static String toString(IoTResponse response) {
            return new StringBuilder("Response:").append("\r\n")
                    .append("id:").append(response.getId()).append("\r\n")
                    .append("code:").append(response.getCode()).append("\r\n")
                    .append("message:").append(response.getMessage()).append("\r\n")
                    .append("localizedMsg:").append(response.getLocalizedMsg()).append("\r\n")
                    .append("data:").append(null == response.getData() ? "" : response.getData().toString()).append("\r\n").toString();
        }
    }
//    public class FrescoPackage implements ReactPackage {
//
//        @Override
//        public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
//            return Arrays.<NativeModule>asList(
//                    new ImageLoaderModule(reactContext)
//            );
//        }
//
//
////        public List<Class<? extends JavaScriptModule>> createJSModules() {
////            return Collections.emptyList();
////        }
//
//        @Override
//        public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
//            return Arrays.<ViewManager>asList(
//                    new ReactImageManager(),
//                    new FrescoBasedReactTextInlineImageViewManager()
//            );
//        }
//    }

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


    /**
     * help class
     */
    static final private class NativeUrlHandler implements IUrlHandler {

        private final String TAG = "ApplicationHelper$NativeUrlHandler";

        private final PageConfigure.NavigationConfigure navigationConfigure;

        NativeUrlHandler(PageConfigure.NavigationConfigure configures) {
            this.navigationConfigure = configures;
        }

        @Override
        public void onUrlHandle(Context context, String url, Bundle bundle, boolean startActForResult, int reqCode) {
            com.aliyun.iot.aep.sdk.log.ALog.d(TAG, "onUrlHandle: url: " + url);
            if (null == context || null == url || url.isEmpty())
                return;

            /* prepare the intent */
            Intent intent = new Intent();
            intent.setData(Uri.parse(url));

            if (null != this.navigationConfigure.navigationIntentAction)
                intent.setAction(this.navigationConfigure.navigationIntentAction);
            if (null != this.navigationConfigure.navigationIntentCategory)
                intent.addCategory(this.navigationConfigure.navigationIntentCategory);

            /* start the navigated activity */
            com.aliyun.iot.aep.sdk.log.ALog.d(TAG, "startActivity(): url: " + this.navigationConfigure.navigationIntentUrl + ", startActForResult: " + startActForResult + ", reqCode: " + reqCode);
            this.startActivity(context, intent, bundle, startActForResult, reqCode);
        }

        private void startActivity(Context context, Intent intent, Bundle bundle, boolean startActForResult, int reqCode) {
            if (null == context || null == intent)
                return;


            if (null != bundle) {
                intent.putExtras(bundle);
            }
            /* startActivityForResult() 场景，只能被 Activity 调用 */
            if (startActForResult) {
                if (false == (context instanceof Activity))
                    return;

                ((Activity) context).startActivityForResult(intent, reqCode);

                return;
            }

            /* startActivity 被 Application 调用时的处理 */
            if (context instanceof Application) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
            /* startActivity 被 Activity、Service 调用时的处理 */
            else if (context instanceof Activity || context instanceof Service) {
                context.startActivity(intent);
            }
            /* startActivity 被其他组件调用时的处理 */
            else {
                // 暂不支持
            }
        }
    }

    //初始化网络图片缓存库
    private void initImageLoader(){
        //网络图片例子,结合常用的图片缓存库UIL,你可以根据自己需求自己换其他网络图片库
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).defaultDisplayImageOptions(defaultOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

    public  static DisplayImageOptions getImageLoaderOption() {
       if(defaultOptions==null) {
           defaultOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565)
                   .cacheInMemory(true).imageScaleType(ImageScaleType.EXACTLY)
                   .cacheOnDisk(true).build();
       }
        // default
// .memoryCache(new LruMemoryCache((int) (6 * 1024 * 1024)))
// Remove
        if(config==null){
           config = new ImageLoaderConfiguration.Builder(
                    getInstance())
                    .threadPoolSize(4)
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .denyCacheImageMultipleSizesInMemory()
// .memoryCache(new LruMemoryCache((int) (6 * 1024 * 1024)))
                    .memoryCache(new LruMemoryCache(5 * 1024 * 1024))
                    .memoryCacheSize((int) (2 * 1024 * 1024))
                    .memoryCacheSizePercentage(25)
                    .diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(200)
                    .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                    .defaultDisplayImageOptions(defaultOptions).writeDebugLogs() // Remove
                    .build();
        }
// Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

        if(options==null){
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.bg_default) // resource or
                    // drawable
                    .showImageForEmptyUri(R.drawable.bg_default) // resource or
                    // drawable
                    .showImageOnFail(R.drawable.bg_default) // resource or
                    .cacheInMemory(true)// drawable
                    .resetViewBeforeLoading(false) // default
                    .delayBeforeLoading(1000).cacheInMemory(true) // default
                    .cacheOnDisk(true) // default
                    .considerExifParams(false) // default
                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                    .bitmapConfig(Bitmap.Config.RGB_565) // default
                    .displayer(new SimpleBitmapDisplayer()) // default
                    .handler(new Handler()) // default
                    .build();
        }
        return options;
    }
    public  static String mCId="";
//
//    public static JSONObject mUserObject;

    public  static String imagePath="";

    public  static File cameraPath;

    public static  boolean isClassify=false;

    public static int mCartCount=0;

    public static int mLightIndex = 0;//点出来的灯的序号

    public  static boolean isGoProgramme=false;

//    public static JSONArray mSelectProducts=new JSONArray();
//    public static JSONArray mSelectScreens=new JSONArray();
    public static final String ACTION_REMOVE_SHORTCUT = "com.android.launcher.action.UNINSTALL_SHORTCUT";
//
}
