package com.aliyun.iot.ilop.demo.page.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.sdk.android.openaccount.OpenAccountSDK;
import com.alibaba.sdk.android.openaccount.OpenAccountService;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.model.OpenAccountSession;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIService;
import com.aliyun.alink.linksdk.channel.core.base.AError;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileRequestListener;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileChannel;
import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialListener;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageError;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageImpl;
import com.aliyun.iot.aep.sdk.credential.data.IoTCredentialData;
import com.aliyun.iot.aep.sdk.framework.AActivity;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.juhao.home.R;
import com.juhao.home.SplashActivity;


/**
 * Created by feijie.xfj on 17/11/27.
 */
//launcher页面不可以singleTask
public class StartActivity extends AActivity {
    private static final String TAG = "StartActivity";

    private CountDownTimer countDownTimer;
    private Handler mH = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        countDownTimer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                startActivity(new Intent(StartActivity.this, SplashActivity.class));
//                Router.getInstance().toUrl(StartActivity.this, "page/splash");
//                if (LoginBusiness.isLogin()) {
//                    Router.getInstance().toUrl(StartActivity.this, "page/ilopmain");
//                    finish();
//                } else {
//                    LoginBusiness.login(new ILoginCallback() {
//                        @Override
//                        public void onLoginSuccess() {
//                            //登录成功后，bindaccount
//                            IoTCredentialManageImpl ioTCredentialManage =  IoTCredentialManageImpl.getInstance(DemoApplication.getInstance());
//                            if(TextUtils.isEmpty(ioTCredentialManage.getIoTToken())){
//                                ioTCredentialManage.asyncRefreshIoTCredential(new IoTCredentialListener() {
//                                    @Override
//                                    public void onRefreshIoTCredentialSuccess(IoTCredentialData ioTCredentialData) {
//                                        MobileChannel.getInstance().bindAccount(ioTCredentialData.iotToken, new IMobileRequestListener() {
//                                            @Override
//                                            public void onSuccess(String s) {
//                                                ALog.i(TAG,"mqtt bindAccount onSuccess");
//                                            }
//
//                                            @Override
//                                            public void onFailure(AError aError) {
//                                                ALog.i(TAG,"mqtt bindAccount onFailure aError = " + aError.getMsg());
//                                            }
//                                        });
//                                    }
//
//                                    @Override
//                                    public void onRefreshIoTCredentialFailed(IoTCredentialManageError ioTCredentialManageError) {
//                                        ALog.i(TAG,"mqtt bindAccount onFailure ");
//
//                                    }
//                                });
//                            } else {
//                                MobileChannel.getInstance().bindAccount(ioTCredentialManage.getIoTToken(), new IMobileRequestListener() {
//                                    @Override
//                                    public void onSuccess(String s) {
//                                        ALog.i(TAG,"mqtt bindAccount onSuccess ");
//                                    }
//
//                                    @Override
//                                    public void onFailure(AError aError) {
//                                        ALog.i(TAG,"mqtt bindAccount onFailure aError = " + aError.getMsg());
//
//                                    }
//                                });
//                            }
//
////                            //注册虚拟设备
////                            String[] pks = {"a1nbd4BjB3N", "a186j8fot9K", "a1XKEJTOkPL", "a1aJCduQG0p"};
////                            for (String pk : pks) {
////                                registerVirtualDevice(pk);
////                            }
//
//                            mH.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Router.getInstance().toUrl(StartActivity.this, "page/ilopmain");
//                                }
//                            }, 0);
//
//                        }
//
//
//                        @Override
//                        public void onLoginFailed(int i, String s) {
//                            Toast.makeText(getApplicationContext(), "登录失败 :" + s, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    finish();
//                }

            }
        };
        countDownTimer.start();
    }

    private String mDeviceName;
    private void bindVirturalToUser(String pk, String dn){
        Map<String, Object> maps = new HashMap<>();
        maps.put("productKey", pk);
        maps.put("deviceName", dn);
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/thing/virtual/binduser")
                .setApiVersion("1.0.0")
                .setAuthType("iotAuth")
                .setParams(maps);

        IoTRequest request = builder.build();

        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                ALog.d("JC", "onFailure");
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                ALog.d("JC", "onResponse bindVirturalToUser ok, rout to ilopmain page");
                final int code = response.getCode();
                final String msg = response.getMessage();
                if (code != 200){
                    mH.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "code = " +code + " msg =" + msg, Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                Router.getInstance().toUrl(StartActivity.this, "page/ilopmain");
            }
        });
    }

    private void registerVirtualDevice(String pk) {
        Map<String, Object> maps = new HashMap<>();
        maps.put("productKey", pk);
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/thing/virtual/register")
                .setApiVersion("1.0.0")
                .setAuthType("iotAuth")
                .setParams(maps);

        IoTRequest request = builder.build();

        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                ALog.d("JC", "onFailure");
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                ALog.d("JC", "onResponse registerVirtualDevice");
                final int code = response.getCode();
                final String msg = response.getMessage();
                if (code != 200){
                    mH.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "code = " +code + " msg =" + msg, Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                Object data = response.getData();
                if (null != data) {
                    if (data instanceof JSONObject) {
                        try {
                            String dn = ((JSONObject) data).getString("deviceName");
                            String pk = ((JSONObject) data).getString("productKey");
                            bindVirturalToUser(pk, dn);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });
    }

    private void listByAccount(){
        Map<String, Object> maps = new HashMap<>();
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/uc/listByAccount")
                .setApiVersion("1.0.0")
                .setAuthType("iotAuth")
                .setParams(maps);

        IoTRequest request = builder.build();

        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                ALog.d("JC", "onFailure");
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                ALog.d("JC", "onResponse listByAccount");
                final int code = response.getCode();
                final String msg = response.getMessage();
                if (code != 200){
                    mH.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "code = " +code + " msg =" + msg, Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                Object data = response.getData();
                if (null != data) {
                    if(data instanceof JSONArray){
                        if (((JSONArray)data).length() > 0){
                            return;
                        }else{

                        }
                    }
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = null;
        super.onDestroy();
    }
}
