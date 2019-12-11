package com.juhao.home.deviceBiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.BaseActivity;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.alink.business.devicecenter.api.add.DeviceInfo;
import com.aliyun.alink.business.devicecenter.api.discovery.DiscoveryType;
import com.aliyun.alink.business.devicecenter.api.discovery.IDeviceDiscoveryListener;
import com.aliyun.alink.business.devicecenter.api.discovery.LocalDeviceMgr;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.aliyun.iot.ilop.demo.page.ilopmain.AddDeviceActivity;
import com.juhao.home.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.util.ApiClientForIot;
import com.util.Constance;
import com.util.LogUtils;
import com.view.MyToast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddDeviceTipsActivity extends BaseActivity {

    private ProgressDialog progressDialog;
    private ImageView iv_img;
    private TextView tv_tips1;
    private TextView tv_upset_device;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_device_tips_add);
        fullScreen(this);
        iv_img = findViewById(R.id.iv_img);
        tv_tips1 = findViewById(R.id.tv_tips1);
        tv_upset_device = findViewById(R.id.tv_upset_device);

        Map<String ,Object> map=new HashMap<>();
        map.put("productKey", DemoApplication.productKey);
        ApiClientForIot.getIotClient("/awss/enrollee/guide/get", "1.1.3", map, new IoTCallback() {

            private String url;

            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
//                int code=ioTResponse.getCode();
//                String data= String.valueOf(ioTResponse.getData());
                org.json.JSONArray jsonArray= (org.json.JSONArray) ioTResponse.getData();
                LogUtils.logE("data",jsonArray.toString());
                if(jsonArray!=null&&jsonArray.length()>0){
                    try {
                        final org.json.JSONObject jsonObject = jsonArray.getJSONObject(0);
                        if (jsonObject != null) {
                            url = jsonObject.getString(Constance.dnGuideIcon);
                            final String helpCopywriting=jsonObject.getString(Constance.helpCopywriting);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ImageLoader.getInstance().displayImage(url, iv_img);
                                    tv_tips1.setText(helpCopywriting);
                                }
                            });
                        }
                    }catch (Exception e){
                    }
                }
            }
        });
        findViewById(R.id.tv_ensure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddDeviceTipsActivity.this,AddDeviceBizActivity.class));
                finish();
            }
        });


        EnumSet<DiscoveryType> enumSet=EnumSet.allOf(DiscoveryType.class);
//                            PgyCrashManager.reportCaughtException(AddDeviceActivity.this,new Exception("EnumSet.allOf"));
//        progressDialog = ProgressDialog.show(AddDeviceTipsActivity.this,"","设备搜索中");
        LocalDeviceMgr.getInstance().startDiscovery(AddDeviceTipsActivity.this, enumSet, null, new IDeviceDiscoveryListener() {
            @Override
            public void onDeviceFound(DiscoveryType discoveryType, List<DeviceInfo> list) {
//                                    PgyCrashManager.reportCaughtException(AddDeviceActivity.this,new Exception("startDiscovery"));
                List<DeviceInfo>deviceInfos=list;
                if(deviceInfos!=null&&deviceInfos.size()>0){
//                                        PgyCrashManager.reportCaughtException(AddDeviceActivity.this,new Exception("deviceInfos.size()>0"));
//                    LogUtils.logE("devices",deviceInfos.toString());
                    if(DemoApplication.productName.contains("摄像头")
                            ){
//                        progressDialog.dismiss();
//                        startActivity(new Intent(AddDeviceTipsActivity.this, WifiSelectActivity.class));
                    }else {
                        boolean hasDevice=false;
                        for(int i=0;i<deviceInfos.size();i++){
                            if(deviceInfos.get(i).productKey.equals(DemoApplication.productKey)){
//                                        PgyCrashManager.reportCaughtException(AddDeviceActivity.this,new Exception(".equals(DemoApplication.productKey"));
                                DemoApplication.productApId=deviceInfos.get(i).id;
//                                progressDialog.dismiss();
                                hasDevice=true;
//                                startActivity(new Intent(AddDeviceTipsActivity.this, WifiSelectActivity.class));
                                break;
                            }
                        }
                        if(!hasDevice){
//                            MyToast.show(AddDeviceTipsActivity.this,"请先打开热点");
                        }


                    }
//                                        PgyCrashManager.reportCaughtException(AddDeviceActivity.this,new Exception("startActivity"));

                }
                // 发现的设备
                // LOCAL_ONLINE_DEVICE 当前和手机在同一局域网已配网在线的设备
                // CLOUD_ENROLLEE_DEVICE 零配或智能路由器发现的待配设备
                // BLE_ENROLLEE_DEVICE 发现的是蓝牙Wi-Fi双模设备（蓝牙模块广播的subType=2即为双模设备）
                // SOFT_AP_DEVICE 发现的设备热点
                // BEACON_DEVICE 一键配网发现的待配设备
                // 注意：发现蓝牙设备需添加 breeze-biz SDK依赖
            }
        });


    }

    @Override
    protected void initData() {

    }
}
