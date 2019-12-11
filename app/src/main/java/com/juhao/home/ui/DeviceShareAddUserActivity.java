package com.juhao.home.ui;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.BaseActivity;
import com.aliyun.alink.apiclient.IoTApiClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.bean.AccountDevDTO;
import com.bean.AreaCodeBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.juhao.home.adapter.BaseAdapterHelper;
import com.juhao.home.adapter.QuickAdapter;
import com.net.ApiClient;
import com.util.ApiClientForIot;
import com.util.Constance;
import com.view.MyToast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DeviceShareAddUserActivity extends BaseActivity {

    private AccountDevDTO accountDevDTO;
    private ArrayList<String> list;
    private TextView tv_region;
    private List<AreaCodeBean> areaCodeBeans=new ArrayList<>(

    );

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_device_share_add_user);
        final EditText et_phone=findViewById(R.id.et_phone);
        TextView tv_save=findViewById(R.id.tv_save);
        tv_region = findViewById(R.id.tv_region);
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String phone=et_phone.getText().toString();
                if(TextUtils.isEmpty(phone)){
                    MyToast.show(DeviceShareAddUserActivity.this,getString(R.string.str_enter_account));
                    return;
                }
                List<String > list=new ArrayList<>();
                if(DeviceShareAddUserActivity.this.list!=null&&DeviceShareAddUserActivity.this.list.size()>0){
                    list=DeviceShareAddUserActivity.this.list;
                }else {
                    list.add(accountDevDTO.getIotId());
                }
                Map<String, Object> map=new HashMap<>();
                map.put("iotIdList",list);
                map.put("accountAttr",phone);
                if(phone.contains("@")){
                    map.put("accountAttrType","EMAIL");
                }else{
                    map.put("accountAttrType","MOBILE");
                    map.put("mobileLocationCode",tv_region.getText().toString().substring(1));
                }
                IoTRequestBuilder builder=new IoTRequestBuilder()
                        .setPath("/uc/shareDevicesAndScenes")
                        .setApiVersion("1.0.7")
                        .setAuthType("iotAuth")
                        .setParams(map);
                IoTRequest request=builder.build();
                IoTAPIClient ioTAPIClient= new IoTAPIClientFactory().getClient();
                ioTAPIClient.send(request, new IoTCallback() {
                    @Override
                    public void onFailure(IoTRequest ioTRequest, Exception e) {

                    }

                    @Override
                    public void onResponse(IoTRequest ioTRequest, final IoTResponse ioTResponse) {
                      int code=ioTResponse.getCode();
                      Object data=ioTResponse.getData();
                      if(code==200){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MyToast.show(DeviceShareAddUserActivity.this,getString(R.string.str_share_success));
                                    Intent intent=new Intent();
                                    intent.putExtra(Constance.phone,phone);
                                    setResult(200);
                                    finish();
                                }
                            });
                      }else {
                          runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  MyToast.show(DeviceShareAddUserActivity.this,ioTResponse.getLocalizedMsg());
                              }
                          });
                      }
                    }
                });
            }
        });
        tv_region.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog=new Dialog(DeviceShareAddUserActivity.this,R.style.customDialog);
                dialog.setContentView(R.layout.dialog_region_number_choose);
                ListView lv_region_num=dialog.findViewById(R.id.lv_region_num);
                final QuickAdapter<AreaCodeBean> adapter=new QuickAdapter<AreaCodeBean>(DeviceShareAddUserActivity.this,R.layout.item_area_choose) {
                    @Override
                    protected void convert(BaseAdapterHelper helper, AreaCodeBean item) {
                        helper.setText(R.id.tv_area, DemoApplication.isEng?item.getEn():item.getName());
                        helper.setText(R.id.tv_code,"+"+item.getTel());

                    }
                };
                lv_region_num.setAdapter(adapter);
                lv_region_num.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        tv_region.setText("+"+areaCodeBeans.get(i).getTel());
                        dialog.dismiss();
                    }
                });
                ApiClient.getAreaCode(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                            String result=response.body().string();
                        try {
                            JSONArray jsonArray=new JSONArray(result);
                            if(jsonArray!=null&&jsonArray.length()>0){
                                List<AreaCodeBean> temp=new Gson().fromJson(jsonArray.toString(),new TypeToken<List<AreaCodeBean>>(){}.getType());
                                List<AreaCodeBean> temp2=new ArrayList<>();
                                temp2.add(new AreaCodeBean("中国大陆","China","86","zhongguo"));
                                temp2.add(new AreaCodeBean("法国","France","33","faguo"));
                                temp2.add(new AreaCodeBean("德国","Germany","49","deguo"));
                                temp2.add(new AreaCodeBean("日本","Japan","81","riben"));
                                temp2.add(new AreaCodeBean("韩国","Korea","82","hanguo"));
                                temp2.add(new AreaCodeBean("俄罗斯","Russian","7","eluosi"));
                                temp2.add(new AreaCodeBean("西班牙","Spain","34","xibanya"));
                                temp2.add(new AreaCodeBean("英国","United Kingdom","44","yingguo"));
                                areaCodeBeans.addAll(temp2);
                                areaCodeBeans.addAll(temp);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                adapter.replaceAll(areaCodeBeans);
                                dialog.show();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void initData() {
        if(getIntent()!=null){
            accountDevDTO = (AccountDevDTO) getIntent().getSerializableExtra(Constance.data);
            list = getIntent().getStringArrayListExtra(Constance.list);
        }
    }
}
