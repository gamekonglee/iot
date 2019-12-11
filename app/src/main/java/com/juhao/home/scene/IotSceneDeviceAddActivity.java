package com.juhao.home.scene;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

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
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.bean.AccountDevDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.juhao.home.MyLoginActivity;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.juhao.home.adapter.BaseAdapterHelper;
import com.juhao.home.adapter.QuickAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.util.Constance;
import com.util.LogUtils;
import com.view.EndOfListView;
import com.view.MyToast;
import com.view.PMSwipeRefreshLayout;
import com.view.TextViewPlus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IotSceneDeviceAddActivity extends BaseActivity {

    private EndOfListView lv_devices;
    private QuickAdapter adapter;
    private List<AccountDevDTO> accountDevDTOS;
//    private ArrayList<JSONObject> mDeviceList;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                tv_none_devices.setVisibility(View.VISIBLE);
                lv_devices.setVisibility(View.GONE);
            }else if(msg.what==0){
                tv_none_devices.setVisibility(View.GONE);
                lv_devices.setVisibility(View.VISIBLE);
                adapter.replaceAll(accountDevDTOS);
            }else if(msg.what==3){
//                MyToast.show(getActivity(),"设置成功");
            }
        }
    };
    private TextViewPlus tv_none_devices;
    private String iotId;
    private int status;
    private int current;
    private boolean isAuto;
    private boolean isCondition;
    private com.alibaba.fastjson.JSONArray temp;
    private com.alibaba.fastjson.JSONArray temp2;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_iot_device_add);
        lv_devices = findViewById(R.id.lv_devices);
        tv_none_devices = findViewById(R.id.tv_none_devices);
        adapter = new QuickAdapter<AccountDevDTO>(this, R.layout.item_dev_scene_selected) {
            @Override
            protected void convert(BaseAdapterHelper helper, AccountDevDTO item) {


                String productName=item.getNickName();
                if(productName==null)productName=item.getProductName();
                if(productName==null)productName=item.getName();

                helper.setText(R.id.tv_name,productName);
//                helper.setImageResource(R.id.iv_img,resId);
                ImageView iv_img=helper.getView(R.id.iv_img);
                ImageLoader.getInstance().displayImage(item.getCategoryImage(),iv_img);
                boolean notContain=true;
                for(int i=0;i<temp.size();i++){
                    if(temp.getJSONObject(i).getJSONObject(Constance.params).getString(Constance.iotId).contains(item.getIotId())){
                        notContain=false;
                        break;
                    }
                }
                for(int i=0;i<temp2.size();i++){
                    if(temp2.getJSONObject(i).toString().contains(item.getIotId())){
                        notContain=false;
                        break;
                    }
                }
//                if(notContain){
//                   helper.setVisible(R.id.view_disabled,false);
//                }else {
//                    helper.setVisible(R.id.view_disabled,true);
//                }

            }
        };
        lv_devices.setAdapter(adapter);
        listByAccount();
        lv_devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                boolean notContain=true;
//                for(int x=0;x<temp.size();x++){
//                    if(temp.getJSONObject(x).getJSONObject(Constance.params).getString(Constance.iotId).contains(accountDevDTOS.get(i).getIotId())){
//                        notContain=false;
//                        break;
//                    }
//                }
//                for(int x=0;x<temp2.size();x++){
//                    if(temp2.getJSONObject(x).toString().contains(accountDevDTOS.get(i).getIotId())){
//                        notContain=false;
//                        break;
//                    }
//                }
//                if(notContain){
//                }else {
//                    return;
//                }
                current = i;
//                Map<String,Object> map=new HashMap<>();
//                map.put("iotId",accountDevDTOS.get(i).getIotId()+"");
//                IoTRequestBuilder builder=new IoTRequestBuilder()
////                        .setPath("/thing/tsl/get")
//                        .setPath("/thing/events/get")
//                        .setApiVersion("1.0.2")
//                        .setAuthType("iotAuth")
//                        .setParams(map);
//                IoTRequest request=builder.build();
//                IoTAPIClient ioTAPIClient=new IoTAPIClientFactory().getClient();
//                ioTAPIClient.send(request, new IoTCallback() {
//                    @Override
//                    public void onFailure(IoTRequest ioTRequest, Exception e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
//                        final int code = response.getCode();
//                        final String msg = response.getMessage();
//                        Object data = response.getData();
//                        if (null != data) {
//                            LogUtils.logE("tsl/get",data.toString());
//                        }
//                    }
//                });
                Intent intent=new Intent(IotSceneDeviceAddActivity.this,IotSceneFunctionDevSetActivity.class);
                intent.putExtra(Constance.is_auto,isAuto);
                intent.putExtra(Constance.is_condition,isCondition);
                intent.putExtra(Constance.iotId,accountDevDTOS.get(i).getIotId()+"");
                startActivityForResult(intent,200);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200&&resultCode==200){
            if(data!=null){
                boolean isOpen=data.getBooleanExtra(Constance.is_open,false);
                data.putExtra(Constance.img,accountDevDTOS.get(current).getCategoryImage());
                String productName=accountDevDTOS.get(current).getNickName();
                if(productName==null)productName=accountDevDTOS.get(current).getProductName();
                if(productName==null)productName=accountDevDTOS.get(current).getName();
                data.putExtra(Constance.name,productName);
                data.putExtra(Constance.deviceName,accountDevDTOS.get(current).getDeviceName());
                data.putExtra(Constance.productKey,accountDevDTOS.get(current).getProductKey());
                setResult(200,data);
                finish();
            }
        }else if(requestCode==300&&resultCode==200){

        }
    }

    int page=1;
    private void listByAccount(){
        accountDevDTOS = new ArrayList<>();
        Map<String, Object> maps = new HashMap<>();
        maps.put("pageSize","20");
        maps.put("pageNo", page);
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/uc/listBindingByAccount")
                .setApiVersion("1.0.2")
                .setAuthType("iotAuth")
                .setParams(maps);
        IoTRequest request = builder.build();

        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                final int code = response.getCode();
                final String msg = response.getMessage();

                Object data = response.getData();
                if(data instanceof JSONObject){
                    JSONObject result= (JSONObject) data;
                    JSONArray listData= null;
                    try {
                        listData = result.getJSONArray(Constance.data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    accountDevDTOS=new Gson().fromJson(((JSONArray)listData).toString(),new TypeToken<List<AccountDevDTO>>(){}.getType());
                    if(accountDevDTOS==null||accountDevDTOS.size()==0){
                        mHandler.sendEmptyMessage(1);
                        return;
                    }
                        for(int i=0;i<accountDevDTOS.size();i++){
                            for(int j=0;j<accountDevDTOS.size();j++){
                                if(i!=j&&accountDevDTOS.get(i).getIotId().equals(accountDevDTOS.get(j).getIotId())){
                                    accountDevDTOS.remove(j);
                                    if(j!=0)j--;
                                }
                            }
                        }
//                        AccountDevDTO accountDevDTO=new AccountDevDTO();
//                        accountDevDTO.setName("冷暖灯");
//                        accountDevDTO.setCategoryImage("http://iotx-paas-admin.oss-cn-shanghai.aliyuncs.com/publish/image/1559630650729.png");
//                        accountDevDTO.setProductKey("a1srueEQffB");

                        mHandler.sendEmptyMessage(0);
                    }

            }
        });
    }
    @Override
    protected void initData() {
        isAuto = getIntent().getBooleanExtra(Constance.is_auto,false);
        isCondition = getIntent().getBooleanExtra(Constance.is_condition,false);
        temp=DemoApplication.actions;
        temp2 = DemoApplication.caConditions;

    }


    private ArrayList<JSONObject> parseDeviceListFromSever(JSONArray jsonArray) {
        int virturlDeviceCount = 0;
        ArrayList<JSONObject> arrayList = new ArrayList<>();
        ArrayList<String> deviceStrList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject device = new JSONObject();
                device.put("name", jsonObject.getString("productName"));

                String type = "虚拟";
                if ("VIRTUAL".equalsIgnoreCase(jsonObject.getString("thingType"))){
                    type = "虚拟";
                    virturlDeviceCount++;
                }else{
                    type = jsonObject.getString("netType");
                }
                device.put("type", type);
                String statusStr = "离线";
                if (1 == jsonObject.getInt("status")){
                    statusStr = "在线";
                }
                device.put("status", statusStr);
                device.put("productKey", jsonObject.getString("productKey"));
                device.put("iotId", jsonObject.getString("iotId"));
                device.put("deviceName", jsonObject.getString("deviceName"));
                deviceStrList.add(jsonObject.getString("productKey") + jsonObject.getString("deviceName"));
                arrayList.add(device);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
//        mBundle.putStringArrayList("deviceList", deviceStrList);
        return arrayList;
    }

}
