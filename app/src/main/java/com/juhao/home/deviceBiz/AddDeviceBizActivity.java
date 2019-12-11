package com.juhao.home.deviceBiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;

import com.BaseActivity;
import com.aliyun.alink.business.devicecenter.api.add.AddDeviceBiz;
import com.aliyun.alink.business.devicecenter.api.add.DeviceInfo;
import com.aliyun.alink.business.devicecenter.api.add.IAddDeviceListener;
import com.aliyun.alink.business.devicecenter.api.add.LinkType;
import com.aliyun.alink.business.devicecenter.api.add.ProvisionStatus;
import com.aliyun.alink.business.devicecenter.api.discovery.DiscoveryType;
import com.aliyun.alink.business.devicecenter.api.discovery.IDeviceDiscoveryListener;
import com.aliyun.alink.business.devicecenter.api.discovery.LocalDeviceMgr;
import com.aliyun.alink.business.devicecenter.base.DCErrorCode;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.aliyun.iot.ilop.demo.page.ilopmain.BindAndUseActivity;
import com.juhao.home.R;
import com.util.LogUtils;
import com.view.CircleProgressView;

import java.util.EnumSet;
import java.util.List;

public class AddDeviceBizActivity extends BaseActivity {

    private int i;
    private TextView tv_tips;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_tips.setText("正在配网......");
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_device_biz_add);
        fullScreen(this);
        final CircleProgressView pv=findViewById(R.id.pv);
        tv_tips = findViewById(R.id.tv_tips);
        pv.setMaxProgress(100);
        pv.setProgress(0);
        i = 0;

                // 开始发现设备
// enumSet 是需要使用的防发现方式 EnumSet<DiscoveryType>, 请根据需要选择发现方式，并添加对应的依赖；
// 第三个参数是获取零配或智能路由器发现的待配设备 请求时需要携带的参数
                            EnumSet<DiscoveryType> enumSet= EnumSet.allOf(DiscoveryType.class);
                            LocalDeviceMgr.getInstance().startDiscovery(AddDeviceBizActivity.this, enumSet, null, new IDeviceDiscoveryListener() {
                                @Override
                                public void onDeviceFound(DiscoveryType discoveryType, List<DeviceInfo> list) {
                                    List<DeviceInfo>deviceInfos=list;
                                    if(deviceInfos!=null){
//                                    LogUtils.logE("startDiscovery",deviceInfos.toString());
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
                            DeviceInfo deviceInfo = new DeviceInfo();
                            deviceInfo.productKey = DemoApplication.productKey; // 商家后台注册的 productKey，不可为空
//                            deviceInfo.deviceName = DemoApplication.productName;
                            String linkType = LinkType.ALI_SOFT_AP.getName();
                            if(DemoApplication.productName.contains("摄像头")){
//                            DemoApplication.productName.contains("锁")){
                                linkType = LinkType.ALI_DEFAULT.getName();
                            }else {
                                deviceInfo.id=DemoApplication.productApId;
                            }
// 设置配网模式
                            AddDeviceBiz.getInstance().setDevice(deviceInfo);
                            AddDeviceBiz.getInstance().setAliProvisionMode(linkType);
//                            String currentid=AddDeviceBiz.getInstance().getCurrentSsid(AddDeviceBizActivity.this);
//                            int wifirsd=AddDeviceBiz.getInstance().getWifiRssid(AddDeviceBizActivity.this);
//                            LogUtils.logE("current,wifirssid",currentid+"<"+wifirsd);
//                             开始添加设备
                            AddDeviceBiz.getInstance().startAddDevice(AddDeviceBizActivity.this, new IAddDeviceListener(){
                                @Override
                                public void onPreCheck(boolean b, DCErrorCode dcErrorCode) {
//                                    LogUtils.logE("iot","onPreCheck");
                                    // 参数检测回调
                                }

                                @Override
                                public void onProvisionPrepare(int prepareType) {
//                                    LogUtils.logE("iot","onProvisionPrepare");
                                    // 手机热点配网、设备热点配网、一键配网、蓝牙辅助配网、二维码配网会走到该流程，
                                    // 零配和智能路由器配网不会走到该流程。
                                    // prepareType = 1提示用户输入账号密码
                                    // prepareType = 2提示用户手动开启指定热点 aha 12345678
                                    // 执行完上述操作之后，调用toggleProvision接口继续执行配网流程
                                    String ssid = DemoApplication.wifi;// 热点配网的时候注意 要先获取ssid，然后再开启热点，否则无法正确获取到SSID
                                    String password = DemoApplication.pwd;
                                    int timeout = 60;//单位秒 目前最短只能设置60S
                                    AddDeviceBiz.getInstance().toggleProvision(ssid, password, timeout);
                                }

                                @Override
                                public void onProvisioning() {
//                                    LogUtils.logE("iot","onProvisioning");
                                    // 配网中
                                    new Thread(){
                                        @Override
                                        public void run() {
                                            super.run();
                                            while (i <60){
                                                SystemClock.sleep(100);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        pv.setProgress(i);
                                                        i++;
                                                    }
                                                });
                                            }
                                        }
                                    }.start();
                                }

                                @Override
                                public void onProvisionStatus(ProvisionStatus provisionStatus) {
                                    LogUtils.logE("iot","onProvisionStatus");
                                    // 二维码配网会走到这里  provisionStatus=ProvisionStatus.QR_PROVISION_READY表示二维码ready了
                                    // ProvisionStatus.QR_PROVISION_READY.message() 获取二维码内容
                                    // 注意：返回二维码时已开启监听设备是否已配网成功的通告，并开始计时，UI端应提示用户尽快扫码；
                                    // 如果在指定时间配网超时了，重新调用开始配网流程并刷新二维码；
                                }

                                @Override
                                public void onProvisionedResult(boolean b, DeviceInfo deviceInfo, DCErrorCode errorCode) {
                                    // 配网结果 如果配网成功之后包含token，请使用配网成功带的token做绑定
//                                    LogUtils.logE("iot","onProvisionedResult:"+errorCode);
                                    if(errorCode==null){
                                        pv.setProgress(100);
                                        tv_tips.setText(getString(R.string.add_device_net_success));
                                        Intent intent = new Intent(getApplicationContext(), BindAndUseActivity.class);
                                        final Bundle bundle = new Bundle();
                                        bundle.putString("productKey", deviceInfo.productKey);
                                        bundle.putString("deviceName", deviceInfo.deviceName);
                                        bundle.putString("token",deviceInfo.token);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }else {
                                        pv.setProgress(0);
                                        tv_tips.setText(getString(R.string.add_device_net_failed));
                                    }
//                                        final String productKey = data.getStringExtra("productKey");
//                                        final String deviceName = data.getStringExtra("deviceName");
//
//
                                }
                            });

    }

    @Override
    protected void initData() {

    }
}
