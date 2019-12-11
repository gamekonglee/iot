package com.juhao.home.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.BaseActivity;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.juhao.home.R;
import com.util.ApiClientForIot;
import com.util.Constance;
import com.util.LogUtils;
import com.view.MyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LockSettingActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_complete;
    private EditText et_mima;
    private TextView tv_random_mima;
    private EditText et_cishu;
    private EditText et_time;
    private ImageView iv_kaiguan;
    private String iotId;
    private boolean isOpen;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
    setContentView(R.layout.activity_lock_setting);
        tv_complete = findViewById(R.id.tv_complete);
        et_mima = findViewById(R.id.et_mima);
        tv_random_mima = findViewById(R.id.tv_random_mima);
        et_cishu = findViewById(R.id.et_cishu);
        et_time = findViewById(R.id.et_time);
        iv_kaiguan = findViewById(R.id.iv_kaiguan);

        tv_complete.setOnClickListener(this);
        tv_random_mima.setOnClickListener(this);
        iv_kaiguan.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        iotId = getIntent().getStringExtra(Constance.iotId);
    }

    @Override
    public void onClick(View view) {
    switch (view.getId()){
        case R.id.tv_complete:
            Map<String,Object> map=new HashMap<>();
            com.alibaba.fastjson.JSONObject jsonObject=new com.alibaba.fastjson.JSONObject();
            String pwd=et_mima.getText().toString();
            String count=et_cishu.getText().toString();
            String time=et_time.getText().toString();
            if(TextUtils.isEmpty(pwd)||
            TextUtils.isEmpty(count)||
            TextUtils.isEmpty(time)){
                MyToast.show(this,"请填写完整相关信息");
                return;
            }
            jsonObject.put("TemporaryAuthorizedUser",pwd+"+"+count+"+"+time);
            map.put("iotId",iotId);
//            map.put("","TemporaryAuthorizedUser");
            map.put("items",jsonObject);
            try{
            setLockProperties(map);
            }catch (Exception e){
            }
            MyToast.show(this,"设置成功");
            break;
        case R.id.tv_random_mima:
            String ranStr="";

            Random random=new Random();
            int randomNum=random.nextInt(1000000);
            et_mima.setText(""+randomNum);
            break;
        case R.id.iv_kaiguan:
            isOpen=!isOpen;
            if(isOpen){
                iv_kaiguan.setImageResource(R.mipmap.set_btn_on);
            }else {
                iv_kaiguan.setImageResource(R.mipmap.set_btn_off);
            }

            break;

    }
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
            }
        });
    }
}
