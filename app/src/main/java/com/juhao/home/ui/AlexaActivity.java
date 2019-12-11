package com.juhao.home.ui;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;

import com.BaseActivity;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.bean.AccountDevDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.juhao.home.adapter.BaseAdapterHelper;
import com.juhao.home.adapter.QuickAdapter;
import com.util.ApiClientForIot;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AlexaActivity extends BaseActivity {
    private List<AccountDevDTO> accountDevDTOS=new ArrayList<>();
    private QuickAdapter<AccountDevDTO> accountDevDTOQuickAdapter;
    private ListView lv_devices;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
    setContentView(R.layout.activity_alexa);
    findViewById(R.id.tv_bind).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(AlexaActivity.this,AlexaDetailActivity.class));
        }
    });

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

    }

    @Override
    protected void onResume() {
        super.onResume();
        getDevicesList();
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
}
