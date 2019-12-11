package com.aliyun.iot.ilop.demo.page.ilopmain;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aliyun.alink.business.devicecenter.api.discovery.IOnDeviceTokenGetListener;
import com.aliyun.alink.business.devicecenter.api.discovery.LocalDeviceMgr;
import com.aliyun.alink.linksdk.tmp.TmpSdk;
import com.aliyun.alink.linksdk.tmp.api.TmpInitConfig;
import com.aliyun.alink.linksdk.tmp.device.panel.PanelDevice;
import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.framework.AActivity;
import com.aliyun.iot.aep.sdk.log.ALog;

import java.util.HashMap;
import java.util.Map;

import com.aliyun.iot.ilop.demo.DemoApplication;
import com.juhao.home.R;
import com.util.ApiClientForIot;


public class BindAndUseActivity extends AActivity {
    private String TAG = BindAndUseActivity.class.getSimpleName();
    private Button bindAndUseBtn;
    private View mBackBtn;
    private Handler mHandler = new Handler();
    private ProgressDialog progressDialog;
    private String pk;
    private String dn;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bind_and_use_activity);
        mBackBtn =  findViewById(R.id.ilop_bind_back_btn);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        pk = "";
        dn = "";
//        String token = "";
        Bundle data = getIntent().getExtras();

        if (null != data) {
            pk = data.getString("productKey");
            dn = data.getString("deviceName");
            token = data.getString("token");
//            token = data.getString("token");
        }

        final String productKey = pk;
        final String deviceName = dn;
//        final String iotToken = token;
        bindAndUseBtn = (Button) findViewById(R.id.bind_and_use_btn);
        bindAndUseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(token)){
                    enrolleeUserBind(productKey, deviceName, token);
                    return;
                }
                ALog.d("TAG", "LocalDeviceMgr.getInstance().getDeviceToken");
//                bindDevice();
                progressDialog = ProgressDialog.show(BindAndUseActivity.this,"请稍等","绑定设备中");
//                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                LocalDeviceMgr.getInstance().getDeviceToken(productKey, deviceName, 2, new IOnDeviceTokenGetListener() {
//                    @Override
//                    public void onSuccess(String token) {
//                        ALog.d("TAG", "getDeviceToken onSuccess token = " + token);
//                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                        enrolleeUserBind(productKey, deviceName, token);
//                    }
//
//                    @Override
//                    public void onFail(String s) {
//                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                        ALog.e("TAG", "getDeviceToken onFail s = " + s);
//                        Toast.makeText(getApplicationContext(), "getTokenFailed", Toast.LENGTH_SHORT).show();
//                    }
//                });
                LocalDeviceMgr.getInstance().getDeviceToken(BindAndUseActivity.this, pk, dn, 60 * 1000, 5 * 1000, new IOnDeviceTokenGetListener() {
                    @Override
                    public void onSuccess(String token) {
                        progressDialog.dismiss();
                        ALog.d("TAG", "getDeviceToken onSuccess token = " + token);
//                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        enrolleeUserBind(productKey, deviceName, token);
                    }

                    @Override
                    public void onFail(String s) {
                        progressDialog.dismiss();
//                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        ALog.e("TAG", "getDeviceToken onFail s = " + s);
                        Toast.makeText(getApplicationContext(), "getTokenFailed", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


    }

    private void bindDevice() {
        Map<String, Object> maps = new HashMap<>();
        maps.put("productKey", pk);
        maps.put("deviceName", dn);
        ApiClientForIot.getIotClient("/awss/time/window/user/bind", "1.0.3", maps, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                final int code = response.getCode();
                final String msg = response.getMessage();
                if (code != 200){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "code = " +code + " msg =" + msg, Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
//                Router.getInstance().toUrl(BindAndUseActivity.this, "page/ilopmain");
                DemoApplication.productKey=pk;
                DemoApplication.productName=dn;
//                DemoApplication.token=token;
                startActivity(new Intent(BindAndUseActivity.this,MainActivity.class));
                finish();
            }
        });
    }

    private void gprsUserBind(String pk, String dn){
        Map<String, Object> maps = new HashMap<>();
        maps.put("productKey", pk);
        maps.put("deviceName", dn);
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/awss/gprs/user/bind")
                .setApiVersion("1.0.2")
                .setAuthType("iotAuth")
                .setParams(maps);

        IoTRequest request = builder.build();

        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                ALog.d("TAG", "onFailure");
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                ALog.d("TAG", "onResponse gprsUserBind ok, rout to ilopmain page");
                Router.getInstance().toUrl(BindAndUseActivity.this, "page/ilopmain");
            }
        });
    }

    private void enrolleeUserBind(final String pk, final String dn, final String token){
        Map<String, Object> maps = new HashMap<>();
        maps.put("productKey", pk);
        maps.put("deviceName", dn);
        maps.put("token", token);
//        maps.put("groupIds","\"[\"123\"]");
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/awss/enrollee/user/bind")
                .setApiVersion("1.0.3")
                .setAuthType("iotAuth")
                .setParams(maps);

        IoTRequest request = builder.build();

        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                ALog.d("TAG", "onFailure");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, final IoTResponse response) {
                ALog.d("TAG", "onResponse enrolleeUserBind ok, rout to ilopmain page");
                final int code = response.getCode();
                final String msg = response.getMessage();
                if (code != 200){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(response.getLocalizedMsg()!=null){
                            Toast.makeText(getApplicationContext(), response.getLocalizedMsg()+"", Toast.LENGTH_SHORT).show();
                            }else {
                            Toast.makeText(getApplicationContext(), "code = " +code + " msg =" + msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    return;
                }
//                Router.getInstance().toUrl(BindAndUseActivity.this, "page/ilopmain");
                DemoApplication.productKey=pk;
                DemoApplication.productName=dn;
                DemoApplication.token=token;
                startActivity(new Intent(BindAndUseActivity.this,MainActivity.class));
                finish();
            }
        });
    }

}
