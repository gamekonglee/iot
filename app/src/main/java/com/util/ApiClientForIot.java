package com.util;

import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;

import java.util.Map;

public class ApiClientForIot {
    public static void getIotClient(String requestPath, String apiVersion, Map<String,Object>maps, final IoTCallback ioTCallback){
        IoTRequestBuilder ioTRequestBuilder = new IoTRequestBuilder()
                .setPath(requestPath)
                .setApiVersion(apiVersion)
                .setAuthType("iotAuth")
                .setParams(maps);
        IoTRequest request = ioTRequestBuilder.build();
        IoTAPIClient client = new IoTAPIClientFactory().getClient();
        client.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                ioTCallback.onFailure(ioTRequest,e);
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                ioTCallback.onResponse(ioTRequest,ioTResponse);
            }
        });
    }
}
