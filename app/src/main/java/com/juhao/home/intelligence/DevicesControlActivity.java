package com.juhao.home.intelligence;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.BaseActivity;
import com.aliyun.alink.linksdk.tmp.TmpSdk;
import com.aliyun.alink.linksdk.tmp.api.OutputParams;
import com.aliyun.alink.linksdk.tmp.api.TmpInitConfig;
import com.aliyun.alink.linksdk.tmp.device.panel.PanelDevice;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;
import com.aliyun.alink.linksdk.tmp.listener.IDevListener;
import com.aliyun.alink.linksdk.tmp.utils.ErrorInfo;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.bean.CountDownBean;
import com.example.yzz.sodemo.BluetoothUtil;
import com.example.yzz.sodemo.CHexConver;
import com.example.yzz.sodemo.DeviceUtil;
import com.example.yzz.sodemo.JniControl;
import com.google.gson.Gson;
import com.juhao.home.SplashActivity;
import com.net.ApiClient;
import com.util.Constance;
import com.util.LogUtils;
import com.view.MyToast;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.juhao.home.R;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/7/31.
 */

public class DevicesControlActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_PHONE_STATE = 300;
    private TextView tv_turn;
    private String iotId;
    private boolean isOpen;
    private boolean isOpen1;
    private boolean isOpen2;
    private boolean isOpen3;
    private boolean isOpen4;
    private String identify;
    private String type;
    private TextView tv_title;
    private TextView tv_status;
    private ImageView iv_control;
    private FrameLayout fl_bg;
    private TextView tv_tips;
    private LinearLayout ll_seek;
    private SeekBar seekb_night;
    private int colorT;
    private TextView tv_countdown;
    private LinearLayout ll_kaiguan;
    private LinearLayout ll_kaiguan2;
    private LinearLayout ll_kg_1;
    private LinearLayout ll_kg_2;
    private LinearLayout ll_kg_3;
    private LinearLayout ll_kg_4;
    private View view_kg_1;
    private View view_kg_2;
    private View view_kg_3;
    private View view_kg_4;
    private TextView tv_kg_tips;
    private TextView tv_kg_all_open;
    private TextView tv_kg_daojishi;
    private TextView tv_kg_dingshi;
    private TextView tv_kg_all_close;
    private int status;
    private LinearLayout ll_light;
    private int intColorT;
    private boolean isbluetooth;
    private BluetoothUtil mBluetoothUtil;
    private String uuid;
    private int group_addr;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initView() {
        setContentView(R.layout.activity_devices);
        setColor(this, Color.WHITE);
        fl_bg = findViewById(R.id.fl_bg);
        tv_turn = findViewById(R.id.tv_turn);
        tv_title = findViewById(R.id.tv_title);
        tv_status = findViewById(R.id.tv_status);
        iv_control = findViewById(R.id.iv_control);
        tv_tips = findViewById(R.id.tv_tips);
        ll_seek = findViewById(R.id.ll_seek);
        seekb_night = findViewById(R.id.seekb_night);
        tv_countdown = findViewById(R.id.tv_countdown);
        ll_light = findViewById(R.id.ll_light);

        ll_kaiguan = findViewById(R.id.ll_kaiguan);
        ll_kaiguan2 = findViewById(R.id.ll_kaiguan_2);
        ll_kg_1 = findViewById(R.id.ll_kg_1);
        ll_kg_2 = findViewById(R.id.ll_kg_2);
        ll_kg_3 = findViewById(R.id.ll_kg_3);
        ll_kg_4 = findViewById(R.id.ll_kg_4);

        view_kg_1 = findViewById(R.id.view_kg_1);
        view_kg_2 = findViewById(R.id.view_kg_2);
        view_kg_3 = findViewById(R.id.view_kg_3);
        view_kg_4 = findViewById(R.id.view_kg_4);

        tv_kg_tips = findViewById(R.id.tv_kg_tips);

        tv_kg_all_open = findViewById(R.id.tv_kg_all_open);
        tv_kg_daojishi = findViewById(R.id.tv_kg_daojishi);
        tv_kg_dingshi = findViewById(R.id.tv_kg_dingshi);
        tv_kg_all_close = findViewById(R.id.tv_kg_all_close);


        tv_countdown.setOnClickListener(this);
        iv_control.setOnClickListener(this);
        tv_turn.setOnClickListener(this);
        ll_kg_1.setOnClickListener(this);
        ll_kg_2.setOnClickListener(this);
        ll_kg_3.setOnClickListener(this);
        ll_kg_4.setOnClickListener(this);
        tv_kg_all_open.setOnClickListener(this);
        tv_kg_all_close.setOnClickListener(this);
        tv_kg_daojishi.setOnClickListener(this);
        tv_kg_dingshi.setOnClickListener(this);

        iotId = getIntent().getStringExtra(Constance.iotId);
        type = getIntent().getStringExtra(Constance.type);
        isbluetooth = getIntent().getBooleanExtra(Constance.isbluetooth,false);
        if(type.equals(Constance.night)){
            if(isbluetooth) {
                tv_countdown.setVisibility(View.GONE);
                init();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            toast("需要动态获取权限");
                    ActivityCompat.requestPermissions(DevicesControlActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PHONE_STATE);
                }else{
//            toast("不需要动态获取权限");
                    uuid = DeviceUtil.getUUID(this);
                group_addr = DeviceUtil.getMasterControlAddr(uuid);
                lamp_setup(1);
                }
//                lamp_power_on_off(1, false);
            }else {
                tv_countdown.setVisibility(View.VISIBLE);
            }
            identify = "LightSwitch";
            tv_title.setText(getString(R.string.str_Intelligent_white_light));
            fl_bg.setBackgroundResource(R.mipmap.bg_night_default);
            tv_status.setVisibility(View.GONE);
            tv_tips.setVisibility(View.VISIBLE);
            iv_control.setImageResource(R.mipmap.page_lamp_default);
            tv_turn.setVisibility(View.GONE);
            ll_seek.setVisibility(View.VISIBLE);
            ll_light.setVisibility(View.VISIBLE);
        }else if(type.equals(Constance.socket)){
            identify="PowerSwitch";
            tv_title.setText(getString(R.string.str_Intelligent_socket));
            fl_bg.setBackgroundResource(R.mipmap.bg_socket);
            tv_status.setVisibility(View.VISIBLE);
            iv_control.setImageResource(R.mipmap.page_socket_default);
            tv_tips.setVisibility(View.INVISIBLE);
            tv_turn.setVisibility(View.VISIBLE);
            ll_seek.setVisibility(View.GONE);
            ll_light.setVisibility(View.VISIBLE);
        }else if(type.equals(Constance.nightswitch)){
            identify="PowerSwitch";
            tv_title.setText(getString(R.string.str_Intelligent_switch));
            fl_bg.setBackgroundResource(R.mipmap.bg_socket);
            tv_status.setVisibility(View.GONE);
            iv_control.setImageResource(R.mipmap.page_socket_default);
            tv_tips.setVisibility(View.INVISIBLE);
            tv_turn.setVisibility(View.VISIBLE);
            ll_seek.setVisibility(View.GONE);
            ll_kaiguan.setVisibility(View.VISIBLE);
            ll_kaiguan2.setVisibility(View.VISIBLE);
            tv_kg_tips.setVisibility(View.VISIBLE);
            ll_light.setVisibility(View.GONE);
            fl_bg.setBackgroundColor(getResources().getColor(R.color.color_kg));
        }else if(type.equals(Constance.lock)){
            identify="LockState";
            tv_title.setText(getString(R.string.str_Intelligent_lock));
        }

        initTmp();
        final PanelDevice panelDevice = new PanelDevice(iotId);
        panelDevice.init(this, new IPanelCallback() {
            @Override
            public void onComplete(boolean b, Object o) {
                Log.e("panelDevice","onComplete:"+b+o);
                panelDevice.getStatus(new IPanelCallback() {
                    @Override
                    public void onComplete(boolean bSuc, Object o) {
                        Log.e("getStatus","onComplete:"+bSuc);
                        try {
                            JSONObject data = new JSONObject((String)o);
                            status = data.getJSONObject(Constance.data).getInt(Constance.status);
                            if(status !=1){
                                ToastStatus();
                            }
                            Log.e("panelDevice_status", status +"");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        initStatus();
                    }
                });
            }
        });
        seekb_night.setMax(6000);
        seekb_night.setProgress(400);
        seekb_night.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e("progress",""+progress);
                if(Math.abs(progress-colorT)>500||progress==seekBar.getMax()||progress==0){
                    colorT=progress;
                    Log.e("colorT",colorT+"");
                    intColorT = ((int)((colorT+1000)/500))*500;
                    if(intColorT<1000)intColorT=1000;
                    float alpha=1f*progress/6000.0f;
                    if(alpha<0.15f){
                        alpha=0.15f;
                    }

                    Log.e("changeNight",intColorT+"");
                    if(isbluetooth){
                        lamp_dimming(1,Integer.toHexString((int) (alpha*255)),Integer.toHexString((int) (alpha*255)));
                    }else {
                    changeNight("ColorTemperature", intColorT);
                    }

                    Log.e("alpha",""+alpha);
                    iv_control.setAlpha(alpha);
//                    iv_control.setImageAlpha(progress/7000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(intColorT-1000);
            }
        });
    }

    private void init() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothUtil = BluetoothUtil.getIntstance(this);
            if (mBluetoothUtil != null) {
                sendBleData();
            } else {
                Toast.makeText(this, getString(R.string.str_init_exception), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.str_version_sdk_old), Toast.LENGTH_SHORT).show();
        }

    }
    int num = 0;
    boolean istrue = true;
    byte[] aByte = new byte[25];
    //发送蓝牙数据包
    public void sendBleData() {
        if (mBluetoothUtil != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (istrue) {
                        Log.e("run: ", "-------------");
                        mBluetoothUtil.advertising(aByte);
                        try {
                            num++;
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (num == 10) {
                            num = 0;
                            istrue = false;
                            mBluetoothUtil.stopAdvertising();
                        }
                    }
                }
            }).start();
        }
    }

    //对码
    public void lamp_setup(int group_index) {
        JniControl.buildpayload(0x28, group_addr, group_index, group_addr >> 8, group_addr, getCount(), 0x81, 0x0, (int) (Math.random() * 16), aByte);
        Log.e("---对码---- ", CHexConver.byte2HexStr(aByte, aByte.length));
        istrue = true;
        sendBleData();
    }

    //清码
    public void lamp_clear_code() {
        JniControl.buildpayload(0x45, 0xFFFE, 0x0000, 0x01, 0x00, getCount(), 0x81, 0x00, (int) (Math.random() * 16), aByte);
        Log.e("---清码---- ", CHexConver.byte2HexStr(aByte, aByte.length));
        istrue = true;
        sendBleData();
    }

    //开灯/关灯
    public void lamp_power_on_off(int group_index, boolean on) {
        if (on) {
            JniControl.buildpayload(0x10, group_addr, group_index, 0x00, 0x00, getCount(), 0x00, 0x00, (int) (Math.random() * 16), aByte);
            Log.e("---开灯---- ", CHexConver.byte2HexStr(aByte, aByte.length));
        } else {
            JniControl.buildpayload(0x11, group_addr, group_index, 0x00, 0x00, getCount(), 0x00, 0x00, (int) (Math.random() * 16), aByte);
            Log.e("---关灯---- ", CHexConver.byte2HexStr(aByte, aByte.length));
        }
        istrue = true;
        sendBleData();
    }


    //夜灯
    public void lamp_night_light(int group_index) {
        JniControl.buildpayload(0x23, group_addr, group_index, 0x00, 0x00, getCount(), 0x00, 0x00, (int) (Math.random() * 16), aByte);
        Log.e("---夜灯---- ", CHexConver.byte2HexStr(aByte, aByte.length));
        istrue = true;
        sendBleData();
    }

    //辅灯开关
    public void lamp_aida_on_off(int group_index, boolean on) {
        if (on) {
            JniControl.buildpayload(0x12, group_addr, group_index, 0x00, 0x00, getCount(), 0x00, 0x00, (int) (Math.random() * 16), aByte);
            Log.e("---辅灯开---- ", CHexConver.byte2HexStr(aByte, aByte.length));
        } else {
            JniControl.buildpayload(0x13, group_addr, group_index, 0x00, 0x00, getCount(), 0x00, 0x00, (int) (Math.random() * 16), aByte);
            Log.e("---辅灯关---- ", CHexConver.byte2HexStr(aByte, aByte.length));
        }
        istrue = true;
        sendBleData();
    }

    //调光
    public void lamp_dimming(int group_index, String w, String y) {
        JniControl.buildpayload(0x21, group_addr, group_index, Integer.parseInt(w,16), Integer.parseInt(y,16), getCount(), 0x00, 0x00, (int) (Math.random() * 16), aByte);
        Log.e("---调光---- ", CHexConver.byte2HexStr(aByte, aByte.length));
        istrue = true;
        sendBleData();
    }
    int count = 0;
    private int getCount() {
        count++;
        return count;
    }


    private void turnOnOrOff(String identify, Object value) {
        Map<String, Object> maps = new HashMap<>();
        maps.put("iotId", iotId);
        com.alibaba.fastjson.JSONObject jsonObject=new com.alibaba.fastjson.JSONObject();
        jsonObject.put(identify,value);
        maps.put("items",jsonObject);
        IoTRequestBuilder ioTRequestBuilder = new IoTRequestBuilder()
                .setPath("/thing/properties/set")
                .setApiVersion("1.0.2")
                .setAuthType("iotAuth")
                .setParams(maps);
        IoTRequest request = ioTRequestBuilder.build();
        IoTAPIClient client = new IoTAPIClientFactory().getClient();
        client.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                LogUtils.logE("onResponse", ioTResponse.getCode() + "," + ioTResponse.getMessage() + "," + ioTResponse.getData());
                if(ioTResponse.getCode()==200){
                        isOpen=!isOpen;
                    handler.sendEmptyMessage(1);
                }
            }
        });
    }



    private void turnOnOrOff(String identify, final Object value, final int  isOpenx) {
        Map<String, Object> maps = new HashMap<>();
        maps.put("iotId", iotId);
        com.alibaba.fastjson.JSONObject jsonObject=new com.alibaba.fastjson.JSONObject();
        jsonObject.put(identify,value);
        maps.put("items",jsonObject);
        IoTRequestBuilder ioTRequestBuilder = new IoTRequestBuilder()
                .setPath("/thing/properties/set")
                .setApiVersion("1.0.2")
                .setAuthType("iotAuth")
                .setParams(maps);
        IoTRequest request = ioTRequestBuilder.build();
        IoTAPIClient client = new IoTAPIClientFactory().getClient();
        client.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                LogUtils.logE("onResponse", ioTResponse.getCode() + "," + ioTResponse.getMessage() + "," + ioTResponse.getData());
                if(ioTResponse.getCode()==200){
                    switch (isOpenx){
                        case 0:
                            if(value instanceof Integer &&(int)value==1){
                                isOpen1=true;
                            }else {
                                isOpen1=false;
                            }
                            break;
                        case 1:
                            if(value instanceof Integer &&(int)value==1){
                                isOpen2=true;
                            }else {
                                isOpen2=false;
                            }
                            break;
                        case 2:
                            if(value instanceof Integer &&(int)value==1){
                                isOpen3=true;
                            }else {
                                isOpen3=false;
                            }
                            break;
                        case 3:
                            if(value instanceof Integer &&(int)value==1){
                                isOpen4=true;
                            }else {
                                isOpen4=false;
                            }
                            break;
                    }
                    handler.sendEmptyMessage(1);
                }
            }
        });
    }


    private void initTmp() {
        TmpSdk.init(this, new TmpInitConfig(TmpInitConfig.ONLINE));
        TmpSdk.getDeviceManager().discoverDevices(null, 5000, new IDevListener() {
            @Override
            public void onSuccess(Object o, OutputParams outputParams) {

            }

            @Override
            public void onFail(Object o, ErrorInfo errorInfo) {

            }
        });
    }
Handler handler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
    refreshUI();
    }
};
    private void initStatus() {
        Map<String, Object> maps = new HashMap<>();
        maps.put("iotId", iotId);
        IoTRequestBuilder ioTRequestBuilder = new IoTRequestBuilder()
                .setPath("/thing/properties/get")
                .setApiVersion("1.0.2")
                .setAuthType("iotAuth")
                .setParams(maps);
        IoTRequest request = ioTRequestBuilder.build();
        IoTAPIClient client = new IoTAPIClientFactory().getClient();
        client.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                LogUtils.logE("onResponse", ioTResponse.getCode() + "," + ioTResponse.getMessage() + "," + ioTResponse.getData());
                try {
                    JSONObject jsonObject= new JSONObject(String.valueOf(ioTResponse.getData())) ;
                    if(type.equals(Constance.night)){
                        JSONObject colorTemperature=jsonObject.getJSONObject("ColorTemperature");
                        colorT = colorTemperature.getInt(Constance.value)-1000;
                        intColorT=colorT/500*500;
                        if(intColorT<1000)intColorT=1000;
                        seekb_night.setProgress(colorT);
                    }
                   if(type.equals(Constance.nightswitch)){
                       JSONObject powerSwitch_1=jsonObject.getJSONObject("PowerSwitch_1");
                       JSONObject powerSwitch_2=jsonObject.getJSONObject("PowerSwitch_2");
                       JSONObject powerSwitch_3=jsonObject.getJSONObject("PowerSwitch_3");
                       JSONObject powerSwitch_4=jsonObject.getJSONObject("PowerSwitch_4");
                       int valueOfPs1=powerSwitch_1.getInt(Constance.value);
                       int valueOfPs2=powerSwitch_2.getInt(Constance.value);
                       int valueOfPs3=powerSwitch_3.getInt(Constance.value);
                       int valueOfPs4=powerSwitch_4.getInt(Constance.value);
                       if(valueOfPs1==0){isOpen1 = false;}else {isOpen1 = true;}
                       if(valueOfPs2==0){isOpen1 = false;}else {isOpen2 = true;}
                       if(valueOfPs3==0){isOpen1 = false;}else {isOpen3 = true;}
                       if(valueOfPs4==0){isOpen1 = false;}else {isOpen4 = true;}

                   }else {
                       JSONObject powerSwitch=jsonObject.getJSONObject(identify);
                       int valueOfPs=powerSwitch.getInt(Constance.value);
                       if(valueOfPs==0){
                           isOpen = false;
                       }else {
                           isOpen = true;
                       }
                   }

                    handler.sendEmptyMessage(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    @Override
    protected void initData() {

    }

    public void refreshUI(){
        if(type.equals(Constance.socket)){
            if(isOpen){
                tv_status.setText(getString(R.string.str_socket_open));
                iv_control.setImageResource(R.mipmap.page_socket_selected);

            }else {
                tv_status.setText(getString(R.string.str_socket_close));
                iv_control.setImageResource(R.mipmap.page_socket_default);
            }
        }else if(type.equals(Constance.night)){
            if(isOpen){
                iv_control.setImageResource(R.mipmap.page_dark);
                tv_tips.setVisibility(View.INVISIBLE);
                ll_seek.setVisibility(View.VISIBLE);
            }else {
                iv_control.setImageResource(R.mipmap.page_lamp_default);
                tv_tips.setVisibility(View.VISIBLE);
                ll_seek.setVisibility(View.GONE);
            }
        }else if(type.equals(Constance.nightswitch)){
            if(isOpen1||isOpen2||isOpen3||isOpen4){
                tv_kg_tips.setText(getString(R.string.str_light_open));
            }else {
                tv_kg_tips.setText(getString(R.string.str_light_close));
            }
            if(isOpen1){
                ll_kg_1.setAlpha(1f);
            }else {
                ll_kg_1.setAlpha(0.3f);
            }
            if(isOpen2){
                ll_kg_2.setAlpha(1f);
            }else {
                ll_kg_2.setAlpha(0.3f);
            }
            if(isOpen3){
                ll_kg_3.setAlpha(1f);
            }else {
                ll_kg_3.setAlpha(0.3f);
            }
            if(isOpen4){
                ll_kg_4.setAlpha(1f);
            }else {
                ll_kg_4.setAlpha(0.3f);
            }
        }

    }
    private void changeNight(String identify, Object value) {
        Map<String, Object> maps = new HashMap<>();
        maps.put("iotId", iotId);
        com.alibaba.fastjson.JSONObject jsonObject=new com.alibaba.fastjson.JSONObject();
        jsonObject.put(identify,value);
        maps.put("items",jsonObject);
        IoTRequestBuilder ioTRequestBuilder = new IoTRequestBuilder()
                .setPath("/thing/properties/set")
                .setApiVersion("1.0.2")
                .setAuthType("iotAuth")
                .setParams(maps);
        IoTRequest request = ioTRequestBuilder.build();
        IoTAPIClient client = new IoTAPIClientFactory().getClient();
        client.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                LogUtils.logE("onResponse", ioTResponse.getCode() + "," + ioTResponse.getMessage() + "," + ioTResponse.getData());
                if(ioTResponse.getCode()==200){
//                    isOpen=!isOpen;
//                    handler.sendEmptyMessage(1);
                }
//                final ProgressDialog progressDialog=ProgressDialog.show(DevicesControlActivity.this,"请求中","");
//                new Thread(){
//                    @Override
//                    public void run() {
//                        super.run();
//                        SystemClock.sleep(1000);
//                        initStatus();
//                        progressDialog.dismiss();
//
//                    }
//                }.start();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isbluetooth){
            lamp_clear_code();
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.tv_countdown:
                Intent intent=new Intent(this,CountDownListActivity.class);
                intent.putExtra(Constance.iotId,iotId);
                intent.putExtra(Constance.type,type);
                startActivityForResult(intent,200);
//                MyToast.show(DevicesControlActivity.this,"该功能尚未开放");
                break;
            case R.id.tv_turn:
            case R.id.iv_control:
                int power=0;
                if(!isOpen){
                    power=1;
                }
                if(isbluetooth){
                    isOpen=!isOpen;
                    lamp_power_on_off(1, power==1?true:false);
                    iv_control.setImageResource(power!=1?R.mipmap.page_lamp_default:R.mipmap.page_dark);
                }else {
                turnOnOrOff(identify,power);
                }
                break;
            case R.id.ll_kg_1:
                if(status !=1){
                    ToastStatus();
                }
                int power1=0;
                if(!isOpen1){
                    power1=1;
                }
                turnOnOrOff("PowerSwitch_1",power1,0);
                break;
            case R.id.ll_kg_2:
                if(status !=1){
                    ToastStatus();
                }
                int power2=0;
                if(!isOpen2){
                    power2=1;
                }
                turnOnOrOff("PowerSwitch_2",power2,1);
                break;
            case R.id.ll_kg_3:
                if(status !=1){
                    ToastStatus();
                }
                int power3=0;
                if(!isOpen3){
                    power3=1;
                }
                turnOnOrOff("PowerSwitch_3",power3,2);
                break;
            case R.id.ll_kg_4:
                if(status !=1){
                    ToastStatus();
                }
                int power4=0;
                if(!isOpen4){
                    power4=1;
                }
                turnOnOrOff("PowerSwitch_4",power4,3);
                break;
            case R.id.tv_kg_all_open:
                if(status !=1){
                    ToastStatus();
                }
                turnOnOrOff("PowerSwitch_1",1,0);
                turnOnOrOff("PowerSwitch_2",1,1);
                turnOnOrOff("PowerSwitch_3",1,2);
                turnOnOrOff("PowerSwitch_4",1,3);
                break;
            case R.id.tv_kg_all_close:
                if(status !=1){
               ToastStatus();
                }
                turnOnOrOff("PowerSwitch_1",0,0);
                turnOnOrOff("PowerSwitch_2",0,1);
                turnOnOrOff("PowerSwitch_3",0,2);
                turnOnOrOff("PowerSwitch_4",0,3);
                break;
            case R.id.tv_kg_daojishi:

                break;
            case R.id.tv_kg_dingshi:
                Intent intent2=new Intent(this,CountDownListActivity.class);
                intent2.putExtra(Constance.iotId,iotId);
                intent2.putExtra(Constance.type,type);
                startActivityForResult(intent2,200);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200&&(resultCode==200||resultCode==300)){
            String days=data.getStringExtra(Constance.days);
            String hour=data.getStringExtra(Constance.hour);
            String minute=data.getStringExtra(Constance.minute);
            int status=data.getIntExtra(Constance.status,1);
            final com.alibaba.fastjson.JSONObject jsonObject=new com.alibaba.fastjson.JSONObject();
            if(type.equals(Constance.nightswitch)){
                int ps1=1;
                if(!isOpen1){ps1=0;}
                int ps2=1;
                if(!isOpen2){ps2=0;}
                int ps3=1;
                if(!isOpen3){ps3=0;}
                int ps4=1;
                if(!isOpen4){ps4=0;}
                jsonObject.put(Constance.PowerSwitch_1,ps1);
                jsonObject.put(Constance.PowerSwitch_2,ps2);
                jsonObject.put(Constance.PowerSwitch_3,ps3);
                jsonObject.put(Constance.PowerSwitch_4,ps4);
            }else if(type.equals(Constance.night)){
                jsonObject.put(Constance.ColorTemperature,colorT);
                jsonObject.put(identify,isOpen);
            }else if(type.equals(Constance.socket)){
                jsonObject.put(identify,isOpen);
            }
            if(resultCode==200){
            Log.e("params:",iotId+","+days+","+hour+","+minute+","+status+","+jsonObject.toJSONString());
            ApiClient.IotCreatTimer(iotId,days,hour,minute,status,jsonObject.toJSONString(), new Callback<String>() {
                @Override
                public String parseNetworkResponse(Response response, int id) throws Exception {
                    return null;
                }

                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public String onResponse(String response, int id) {
                    Log.e("IotCreatTimer",response);
                    try {
                        JSONObject jsonObject1=new JSONObject(response);
                        if(jsonObject1.getBoolean(Constance.success)){
                            MyToast.show(DevicesControlActivity.this,getString(R.string.str_add_success));
                            Intent intent=new Intent(DevicesControlActivity.this,CountDownListActivity.class);
                            intent.putExtra(Constance.iotId,iotId);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
        }else if(resultCode==300){
                CountDownBean countDownBean=new Gson().fromJson(data.getStringExtra(Constance.count_down_json),CountDownBean.class);
                ApiClient.IotTimerUpdate(countDownBean.getPid(),iotId,jsonObject.toJSONString(),days,hour,minute,status, new Callback<String>() {
                    @Override
                    public String parseNetworkResponse(Response response, int id) throws Exception {
                        return null;
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public String onResponse(String response, int id) {
                        Log.e("IotUpdateTimer",response);
                        try {
                            JSONObject jsonObject1=new JSONObject(response);
                            if(jsonObject1.getBoolean(Constance.success)){
                                MyToast.show(DevicesControlActivity.this,getString(R.string.str_edit_success));
                                Intent intent=new Intent(DevicesControlActivity.this,CountDownListActivity.class);
                                intent.putExtra(Constance.iotId,iotId);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
            }
        }
    }

    public void ToastStatus(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(DevicesControlActivity.this!=null&&!DevicesControlActivity.this.isFinishing()){
                MyToast.show(DevicesControlActivity.this,getString(R.string.str_device_offline));
                }
            }
        });
    }
}
