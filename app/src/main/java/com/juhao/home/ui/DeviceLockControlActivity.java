package com.juhao.home.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.BaseActivity;
import com.aliyun.alink.linksdk.tmp.device.panel.PanelDevice;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.juhao.home.R;
import com.util.ApiClientForIot;
import com.util.Constance;
import com.util.LogUtils;
import com.view.FontIconView;
import com.view.MyToast;
import com.view.TextViewPlus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DeviceLockControlActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_edit;
    private FontIconView iv_battery;
    private TextView tv_battery;
    private TextView tv_msg_1;
    private TextView tv_msg_2;
    private TextView tv_msg_3;
    private TextViewPlus tv_baojing;
    private TextViewPlus tv_kaisuo;
    private TextViewPlus tv_linshimima;
    private String iotId;
    private boolean isDoorOpen;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_device_lock_controll);
        fullScreen(this);
        iv_edit = findViewById(R.id.iv_edit);
        iv_battery = findViewById(R.id.iv_battery);
        tv_battery = findViewById(R.id.tv_battery);
        tv_msg_1 = findViewById(R.id.tv_msg_1);
        tv_msg_2 = findViewById(R.id.tv_msg_2);
        tv_msg_3 = findViewById(R.id.tv_msg_3);
        tv_baojing = findViewById(R.id.tv_baojing);
        tv_kaisuo = findViewById(R.id.tv_kaisuo);
        tv_linshimima = findViewById(R.id.tv_linshimima);
        findViewById(R.id.iv_lock).setOnClickListener(this);
        iv_edit.setOnClickListener(this);
        tv_baojing.setOnClickListener(this);
        tv_kaisuo.setOnClickListener(this);
        tv_linshimima.setOnClickListener(this);
        getLockState(Constance.Doorbell);
//        ApiClientForIot.getIotClient();
    }

    @Override
    protected void initData() {
        if(getIntent()!=null&&getIntent().getStringExtra(Constance.iotId)!=null){
            iotId = getIntent().getStringExtra(Constance.iotId);
        }else {
            iotId="342";
        }

    }

    @Override
    public void onClick(View view) {
    switch (view.getId()){
            case R.id.tv_baojing:
                break;
            case R.id.tv_kaisuo:
            case R.id.iv_lock:
            if(!isDoorOpen){
                getLockState("Doorbell2");
                return;
            }
                String ranStr="";
                for(int i=0;i<8;i++){
                    Random random=new Random();
                    String  randomNum=random.nextInt(10)+"";
                    ranStr+=randomNum;
                }
                Map<String ,Object> map=new HashMap<>();
                map.put("iotId",iotId);
                com.alibaba.fastjson.JSONObject jsonObject=new com.alibaba.fastjson.JSONObject();
                jsonObject.put("Random",ranStr+"");
                map.put("items",jsonObject);
                setLockProperties(map);

//                getLockState();

//                final Dialog dialog=new Dialog(this,R.style.customDialog);
//                dialog.setContentView(R.layout.dialog_pwd_input);
//                final EditText et_pwd_input=dialog.findViewById(R.id.et_pwd_input);
//                TextView tv_ensure=dialog.findViewById(R.id.tv_ensure);
//                tv_ensure.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Map<String ,Object> map=new HashMap<>();
//                        map.put("iotId",iotId);
//                        com.alibaba.fastjson.JSONObject jsonObject=new com.alibaba.fastjson.JSONObject();
//                        jsonObject.put("OpenClose",et_pwd_input.getText().toString());
//                        map.put("items",jsonObject);
//                        setLockProperties(map);
//                        MyToast.show(DeviceLockControlActivity.this,"开锁密码已发送");
//                        dialog.dismiss();
//                    }
//                });
//                dialog.show();




//                final PanelDevice panelDevice = new PanelDevice(iotId);
//                panelDevice.init(this, new IPanelCallback() {
//                    @Override
//                    public void onComplete(boolean b, Object o) {
//                        panelDevice.getProperties(new IPanelCallback() {
//                            @Override
//                            public void onComplete(boolean bSuc, Object o) {
////                                ALog.d(TAG,"getProps(), request complete," + bSuc);
//                                try {
//                                    JSONObject data = new JSONObject((String)o);
//                                    LogUtils.logE("properties",data.toString());
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                    }
//                });
//                addKey();
//                getKeyList();
                break;
            case R.id.tv_linshimima:
                Intent intent=new Intent(this,LockSettingActivity.class);
                intent.putExtra(Constance.iotId,iotId);
                startActivity(intent);
                break;
            case R.id.iv_edit:
                break;

        }
    }

    private void getKeyList() {
        Map<String ,Object> map=new HashMap<>();
        map.put("iotId",iotId);
        map.put("identifier","GetKeyList");
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("LockType",0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        map.put("args",jsonObject);
        invokeService(map);
    }

    private void addKey() {
        Map<String ,Object> map=new HashMap<>();
        map.put("iotId",iotId);
        map.put("identifier","AddKey");
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("LockType",1);
            jsonObject.put("UserLimit",2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        map.put("args",jsonObject);
        invokeService(map);
    }

    public void getLockState(final String tpye){
        Map<String ,Object> map=new HashMap<>();
        map.put("iotId",iotId);
        ApiClientForIot.getIotClient("/thing/properties/get", "1.0.2", map, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                LogUtils.logE("property",((JSONObject)ioTResponse.getData()).toString());
                JSONObject data= (JSONObject) ioTResponse.getData();
                if(data!=null){
                    try {
                        if(tpye.equals("Doorbell")){
                            JSONObject Doorbell=data.getJSONObject(Constance.Doorbell);
                            JSONObject OpenLock=data.getJSONObject(Constance.OpenLock);
                            JSONObject BatteryPercentage=data.getJSONObject(Constance.BatteryPercentage);
                            if(Doorbell!=null){
                                final int value=Doorbell.getInt(Constance.value);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(value==1){
                                            tv_msg_1.setText(getResources().getString(R.string.str_doorbell_open));
                                            isDoorOpen = true;
                                        }else {
                                            tv_msg_1.setText(getResources().getString(R.string.str_doorbell_close));
                                            isDoorOpen=false;
                                        }
                                    }
                                });
                            }
                            if(OpenLock!=null){
                                final int value=OpenLock.getInt(Constance.value);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(value==1){
                                            tv_msg_2.setText(getResources().getString(R.string.str_the_lock_is_opened));
                                        }else {
                                            tv_msg_2.setText(getResources().getString(R.string.str_the_lock_is_not_opened));
                                        }
                                    }
                                });
                            }
                            if(BatteryPercentage!=null){
                                final int value=BatteryPercentage.getInt(Constance.value);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(value<30){
                                            iv_battery.setText(getString(R.string.icon_battery_low));
                                            tv_battery.setText("低");
                                            iv_battery.setTextColor(Color.RED);
                                        }else if(value>=30&&value<60){
                                            iv_battery.setText(getString(R.string.icon_battery_mid));
                                            tv_battery.setText("中");
                                        }else {
                                            tv_battery.setText("高");
                                            iv_battery.setText(getString(R.string.icon_battery_high));
                                        }
                                    }
                                });
                            }
                        }else if(tpye.equals(Constance.Random)){
                            JSONObject OpenLock=data.getJSONObject(Constance.OpenLock);
                            if(OpenLock!=null){
                                final int value=OpenLock.getInt(Constance.value);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(value==1){
                                           MyToast.show(DeviceLockControlActivity.this,getString(R.string.str_the_lock_is_opened));
                                        }
                                    }
                                });
                            }

                        }else if(tpye.equals("Doorbell2")){
                            JSONObject Doorbell=data.getJSONObject(Constance.Doorbell);
                            if(Doorbell!=null){
                                final int value=Doorbell.getInt(Constance.value);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(value==1){
                                            String ranStr="";
                                            for(int i=0;i<8;i++){
                                                Random random=new Random();
                                                String  randomNum=random.nextInt(10)+"";
                                                ranStr+=randomNum;
                                            }
                                            Map<String ,Object> map=new HashMap<>();
                                            map.put("iotId",iotId);
                                            com.alibaba.fastjson.JSONObject jsonObject=new com.alibaba.fastjson.JSONObject();
                                            jsonObject.put("Random",ranStr+"");
                                            map.put("items",jsonObject);
                                            setLockProperties(map);
                                            tv_msg_1.setText(getResources().getString(R.string.str_doorbell_open));
                                            isDoorOpen = true;
                                        }else {
                                            MyToast.show(DeviceLockControlActivity.this,getString(R.string.str_the_door_is_not_open));
                                            tv_msg_1.setText(getResources().getString(R.string.str_doorbell_close));
                                            isDoorOpen=false;
                                        }
                                    }
                                });
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public void setLockProperties(Map<String ,Object> map){
//        JSONObject jsonObject=new JSONObject();
//        try {
//            jsonObject.put("LockState",state?0:1);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Map<String ,Object> map=new HashMap<>();
//        map.put("iotId",iotId);
//        map.put("items",jsonObject);
        ApiClientForIot.getIotClient("/thing/properties/set", "1.0.2", map, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
            LogUtils.logE("propertSet", String.valueOf(ioTResponse.getData()));
            getLockState(Constance.Random);
            }
        });
    }

    private void invokeService(Map<String,Object> maps) {


        IoTRequestBuilder ioTRequestBuilder = new IoTRequestBuilder()
                .setPath("/thing/service/invoke")
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
                }

            }
        });
    }
}
