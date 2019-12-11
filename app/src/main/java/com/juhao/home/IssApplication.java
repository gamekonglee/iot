package com.juhao.home;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.alibaba.wireless.security.jaq.JAQException;
import com.alibaba.wireless.security.jaq.SecurityInit;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileConnectListener;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileChannel;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileConnectConfig;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileConnectState;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientImpl;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Env;
import com.aliyun.iot.aep.sdk.connectchannel.log.ALog;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageImpl;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.login.oa.OALoginAdapter;
import com.aliyun.iot.ilop.demo.DemoApplication;

public class IssApplication extends Application {
    public static int ori;
    String TAG="iss_security";
    private static Context context;
    String appKey="27615405";

//    public static Context getInstance() {
//        return context;
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        // 初始化无线保镖
        try {
            SecurityInit.Initialize(context);
        } catch (JAQException ex) {
            Log.e(TAG, "security-sdk-initialize-failed");
        } catch (Exception ex) {
            Log.e(TAG, "security-sdk-initialize-failed");
        }

        // 初始化 IoTAPIClient
        IoTAPIClientImpl.InitializeConfig config = new IoTAPIClientImpl.InitializeConfig();
        // 国内环境
        config.host = "api.link.aliyun.com";
        // 海外环境，请参考如下设置
        //config.host = “api-iot.ap-southeast-1.aliyuncs.com”;
        config.apiEnv = Env.RELEASE; //只支持RELEASE
        config.appKey=appKey;
        //设置请求超时（可选）默认超时时间10s
        config.connectTimeout=10_000L;
        config.readTimeout=10_000L;
        config.writeTimeout=10_000L;

        IoTAPIClientImpl impl = IoTAPIClientImpl.getInstance();
        impl.init(context, config);


        initMobileConnect();

        OALoginAdapter adapter = new OALoginAdapter(this);
        if(DemoApplication.is_national){
            adapter.setDefaultOAHost("sgp-sdk.openaccount.aliyun.com");
            adapter.init("online","develop_oversea");
        }else {
            adapter.init("online","114d");
        }
//如果需要切换到海外环境，请执行下面setDefaultOAHost方法，默认为大陆环境
//adapter.setDefaultOAHost("sgp-sdk.openaccount.aliyun.com");



        LoginBusiness.init(this, adapter, "online");
//务必注意在调用之前，保证完成了用户和账号SDK的初始化
        IoTCredentialManageImpl.init(appKey);

    }

    private void initMobileConnect() {
        //打开Log 输出
//        ALog.setLevel(ALog.LEVEL_DEBUG);

        MobileConnectConfig config = new MobileConnectConfig();
        // 设置 appKey 和 authCode(必填)
        config.appkey = "{YOUR_APP_KEY}";
        config.securityGuardAuthcode = DemoApplication.is_national?"develop_oversea":"114d";


        // 设置验证服务器（默认不填，SDK会自动使用“API通道SDK“的Host设定）
        config.authServer = "";

        // 指定长连接服务器地址。 （默认不填，SDK会使用默认的地址及端口。默认为国内华东节点。）
        config.channelHost = "{长连接服务器域名}";

        // 开启动态选择Host功能。 (默认false，海外环境建议设置为true。此功能前提为ChannelHost 不特殊指定。）
        config.autoSelectChannelHost = false;

        MobileChannel.getInstance().startConnect(context, config, new IMobileConnectListener() {
            @Override
            public void onConnectStateChange(MobileConnectState state) {
                ALog.d(TAG,"onConnectStateChange(), state = "+state.toString());
            }
        });
    }
}
