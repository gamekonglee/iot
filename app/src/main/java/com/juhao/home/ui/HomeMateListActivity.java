package com.juhao.home.ui;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.BaseActivity;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.juhao.home.R;

import java.util.HashMap;
import java.util.Map;

public class HomeMateListActivity extends BaseActivity {
    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_home_mate);
        setStatuTextColor(this, Color.TRANSPARENT);
//        setFullScreenColor(Color.TRANSPARENT,this);

        TextView tv_add_home=findViewById(R.id.tv_add_home);
        tv_add_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> maps = new HashMap<>();
                IoTRequestBuilder builder = new IoTRequestBuilder()
                        .setPath("/uc/virtual/user/create")
                        .setApiVersion("1.0.0")
                        .setAuthType("iotAuth")
                        .setParams(maps);

                IoTRequest request = builder.build();

                IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
                ioTAPIClient.send(request, new IoTCallback() {
                    @Override
                    public void onFailure(IoTRequest ioTRequest, Exception e) {

                    }

                    @Override
                    public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {

                    }
                });
            }
        });
    }

    @Override
    protected void initData() {

    }
}
