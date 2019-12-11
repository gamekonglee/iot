package com.juhao.home.ui;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.BaseActivity;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.bean.AccountDevDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.juhao.home.adapter.BaseAdapterHelper;
import com.juhao.home.adapter.QuickAdapter;
import com.util.ApiClientForIot;
import com.util.Constance;
import com.util.LogUtils;
import com.view.MyToast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TmActivity extends BaseActivity {
    private List<AccountDevDTO> accountDevDTOS=new ArrayList<>();
    private QuickAdapter<AccountDevDTO> accountDevDTOQuickAdapter;
    private ListView lv_devices;
    private TextView tv_bind;
    private boolean isBind;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_tm);
        tv_bind = findViewById(R.id.tv_bind);
        lv_devices = findViewById(R.id.lv_devices);
        accountDevDTOQuickAdapter = new QuickAdapter<AccountDevDTO>(this,R.layout.item_home_dev) {
            @Override
            protected void convert(BaseAdapterHelper helper, AccountDevDTO item) {
                helper.setText(R.id.tv_scene,getString(R.string.str_keting));
                helper.setText(R.id.tv_status,item.getStatus().equals("1")?"在线":"离线");
                int resId=R.mipmap.home_kg;
                String productName=item.getProductName();
                if(productName==null)productName=item.getName();
                helper.setText(R.id.tv_name,productName);
                if(productName!=null){
                    if(productName.contains(getString(R.string.str_socket))){
                        resId=R.mipmap.home_cz;
                    }else if(productName.contains(getString(R.string.str_kaiguan))){
                        resId=R.mipmap.home_kg;
                    }else if(productName.contains(getString(R.string.str_light))){
                        resId=R.mipmap.home_zm;
                    }
                }
                helper.setImageResource(R.id.iv_img,resId);
            }
        };
        lv_devices.setAdapter(accountDevDTOQuickAdapter);

        tv_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isBind){
                 unbind();
                }else {
                startActivityForResult(new Intent(TmActivity.this, WebViewActivity.class),222);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDevicesList();
        getBindStatus();
    }

    private void unbind() {
        Map<String, Object> map=new HashMap<>();
        map.put("accountType","TAOBAO");
        ApiClientForIot.getIotClient("/account/thirdparty/unbind", "1.0.5", map, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.show(TmActivity.this,"解绑成功！");
                        finish();
//                        getBindStatus();
                    }
                });
            }
        });
    }

    private void getBindStatus() {
        Map<String,Object> map=new HashMap<>();
        map.put("accountType","TAOBAO");
        ApiClientForIot.getIotClient("/account/thirdparty/get", "1.0.5", map, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
            if(ioTResponse.getCode()==200){
                org.json.JSONObject data= (org.json.JSONObject) ioTResponse.getData();
                if(data!=null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isBind = true;
                            //已绑定
                            tv_bind.setText("解除绑定");
                        }
                    });
                    LogUtils.logE("data",data.toString());
                    try {
                        String linkIndentityId=data.getString(Constance.linkIndentityId);
                        LogUtils.logE("linkIndentityId",linkIndentityId+"");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    isBind=false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //未绑定
                            tv_bind.setText("绑定账号");
                        }
                    });

                }
            }
            }
        });
    }

    private void getDevicesList() {
        ApiClientForIot.getIotClient("/uc/listByAccount", "1.0.0", new HashMap<String, Object>(), new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                accountDevDTOS=new Gson().fromJson(((JSONArray)ioTResponse.getData()).toString(),new TypeToken<List<AccountDevDTO>>(){}.getType());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(accountDevDTOS!=null&&accountDevDTOS.size()>0){
                            accountDevDTOQuickAdapter.replaceAll(accountDevDTOS);
                            UIUtils.initListViewHeight(lv_devices);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==222&&resultCode==222&&data!=null&&data.getStringExtra("AuthCode")!=null){
            bindAccount(data.getStringExtra("AuthCode"));
        }
    }

    public void bindAccount(String authCode) {

        JSONObject params = new JSONObject();
        if (null != authCode) {
            params.put("authCode", authCode);
        }
        Map<String, Object> requestMap = params.getInnerMap();

        IoTRequest ioTRequest = new IoTRequestBuilder()
                .setAuthType("iotAuth")
                .setApiVersion("1.0.5")
                .setPath("/account/taobao/bind")
                .setParams(requestMap)
                .setScheme(Scheme.HTTPS)
                .build();
        new IoTAPIClientFactory().getClient().send(ioTRequest, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, final IoTResponse ioTResponse) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                if(ioTResponse.getCode()==200){
                            MyToast.show(TmActivity.this,"绑定成功！");
                    finish();
//                    getBindStatus();
                }else {
                            }
                        }
                    });


            }
        });
    }

}
