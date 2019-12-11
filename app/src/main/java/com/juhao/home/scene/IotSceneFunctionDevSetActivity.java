package com.juhao.home.scene;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.BaseActivity;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.bean.DevFunctionBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.juhao.home.adapter.BaseAdapterHelper;
import com.juhao.home.adapter.QuickAdapter;
import com.net.ApiClient;
import com.util.ApiClientForIot;
import com.util.Constance;
import com.util.LogUtils;
import com.view.MyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IotSceneFunctionDevSetActivity extends BaseActivity implements View.OnClickListener {

    private ListView lv_function;
    private String iotId;
    private QuickAdapter<DevFunctionBean> adapter;
    private boolean isOpen;
    private List<DevFunctionBean> devFunctionBeans;
    private int current;
    private TextView tv_property;
    private boolean is_auto;
    private boolean iscondition;
    private TextView tv_title;
    private boolean is_edit;
    private int positon;
    private String uri;
    private String icon;
    private String name;
    private int[] progressArray;
    private String propertyName;
    private com.alibaba.fastjson.JSONArray temp;

    @Override
    protected void InitDataView() {
        if(iscondition){
            tv_title.setText(getString(R.string.str_choose_condition));
        }
    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_iot_scene_function);
        lv_function = findViewById(R.id.lv_function);
        tv_title = findViewById(R.id.tv_title);

        TextView tv_save=findViewById(R.id.tv_save);
        tv_save.setOnClickListener(this);
        adapter = new QuickAdapter<DevFunctionBean>(this,R.layout.item_dev_function) {
            @Override
            protected void convert(final BaseAdapterHelper helper, final DevFunctionBean item) {
                helper.setText(R.id.tv_name,item.getName());
                boolean notContain=true;
                for(int i=0;i<temp.size();i++){
                    if(temp.getJSONObject(i).getJSONObject(Constance.params).getString(Constance.propertyName).contains(item.getIdentifier())){
                        notContain=false;
                        break;
                    }
                }
                if(!is_edit){
                if(notContain){
                    helper.setVisible(R.id.view_disabled,false);
                }else {
                    helper.setVisible(R.id.view_disabled,true);
                }
                }

//                tv_property = helper.getView(R.id.tv_property);
                helper.getView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!is_edit){
                            boolean notContain=true;
                            for(int x=0;x<temp.size();x++){
                                if(temp.getJSONObject(x).getJSONObject(Constance.params).getString(Constance.propertyName).contains(item.getIdentifier())){
                                    notContain=false;
                                    break;
                                }
                            }
                            if(notContain){
                            }else {
                                return;
                            }
                        }
                        current = helper.getPosition();
                        Map<String,Object>map=new HashMap<>();
                        map.put(Constance.iotId,iotId);
                        ApiClientForIot.getIotClient("/thing/tsl/get", "1.0.2", map, new IoTCallback() {
                            @Override
                            public void onFailure(IoTRequest ioTRequest, Exception e) {

                            }

                            @Override
                            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                                LogUtils.logE("tslget", String.valueOf(ioTResponse.getData()));
                                try {
                                    JSONObject result=new JSONObject(String.valueOf(ioTResponse.getData()));
                                    JSONArray properties =result.getJSONArray(Constance.properties);
                                    if(properties.length()>0){
                                        for(int i=0;i<properties.length();i++){

                                            if(properties.getJSONObject(i).getString(Constance.identifier).contains(item.getIdentifier())){
                                                final JSONObject dataType=properties.getJSONObject(i).getJSONObject(Constance.dataType);
                                                final String type=dataType.getString(Constance.type);
                                                runOnUiThread(new Runnable() {
                                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                                    @Override
                                                    public void run() {
                                                        if(!type.equals("int")){
                                                            final Dialog dialog=UIUtils.showBottomInDialog(IotSceneFunctionDevSetActivity.this,R.layout.dialog_property_set,UIUtils.dip2PX(100));
                                                            TextView tv_open=dialog.findViewById(R.id.tv_open);
                                                            TextView tv_close=dialog.findViewById(R.id.tv_close);


                                                            tv_open.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
//                                tv_property.setText(getString(R.string.str_open));
                                                                    helper.setText(R.id.tv_property,getString(R.string.str_open));
                                                                    dialog.dismiss();
                                                                    isOpen = true;
                                                                    item.property_value="1";
                                                                    item.property=getString(R.string.str_open);
                                                                }
                                                            });
                                                            tv_close.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
//                                tv_property.setText(getString(R.string.str_close));
                                                                    helper.setText(R.id.tv_property,getString(R.string.str_close));
                                                                    item.property=getString(R.string.str_close);
                                                                    item.property_value="0";
                                                                    dialog.dismiss();
                                                                    isOpen=false;
                                                                }
                                                            });
                                                        }else {
                                                            final Dialog dialog=UIUtils.showBottomInDialog(IotSceneFunctionDevSetActivity.this,R.layout.dialog_property_set_temp,UIUtils.dip2PX(100));
                                                            SeekBar seekb_night=dialog.findViewById(R.id.seekb_night);
                                                            TextView tv_property=dialog.findViewById(R.id.tv_property);
                                                            TextView tv_max=dialog.findViewById(R.id.tv_max);
                                                            TextView tv_min=dialog.findViewById(R.id.tv_min);
                                                            final TextView tv_progress=dialog.findViewById(R.id.tv_progress);
                                                            tv_property.setText(item.getName());
                                                            try {
                                                                JSONObject specs=dataType.getJSONObject(Constance.specs);
                                                                final String min=specs.getString("min");
                                                                String max=specs.getString("max");
                                                                final String step=specs.getString("step");
                                                                seekb_night.setMax(Integer.parseInt(max)-Integer.parseInt(min));
                                                                tv_max.setText(max+"");
                                                                tv_min.setText(min+"");

                                                                final int stepInt=Integer.parseInt(step);
                                                                seekb_night.setProgress(stepInt);
                                                                int oneStep=stepInt+Integer.parseInt(min);
                                                                tv_progress.setText(""+(oneStep));

                                                                item.property=""+oneStep;
                                                                item.property_value=""+oneStep;
//                                                                seekb_night.setMin(Integer.parseInt(min));
                                                                seekb_night.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                                    @Override
                                                                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                                                        LogUtils.logE("progress",i+"");
                                                                        i+=Integer.parseInt(min);
                                                                        int currentProgress=progressArray[helper.getPosition()];
                                                                        if(Math.abs(currentProgress-i)>=stepInt){
                                                                            i=i/stepInt*stepInt;
                                                                            tv_progress.setText(""+i);
                                                                            progressArray[helper.getPosition()]=i;
                                                                            helper.setText(R.id.tv_property,i+"");
                                                                            item.property=i+"";
                                                                            item.property_value=i+"";

                                                                        }


                                                                    }

                                                                    @Override
                                                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                                                    }

                                                                    @Override
                                                                    public void onStopTrackingTouch(SeekBar seekBar) {

                                                                    }
                                                                });


                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                                @Override
                                                                public void onDismiss(DialogInterface dialogInterface) {
                                                                    adapter.notifyDataSetChanged();
                                                                }
                                                            });


//                                                            tv_open.setOnClickListener(new View.OnClickListener() {
//                                                                @Override
//                                                                public void onClick(View view) {
////                                tv_property.setText(getString(R.string.str_open));
//                                                                    helper.setText(R.id.tv_property,getString(R.string.str_open));
//                                                                    dialog.dismiss();
//                                                                    isOpen = true;
//                                                                }
//                                                            });
//                                                            tv_close.setOnClickListener(new View.OnClickListener() {
//                                                                @Override
//                                                                public void onClick(View view) {
////                                tv_property.setText(getString(R.string.str_close));
//                                                                    helper.setText(R.id.tv_property,getString(R.string.str_close));
//                                                                    dialog.dismiss();
//                                                                    isOpen=false;
//                                                                }
//                                                            });
                                                        }

                                                    }
                                                });

                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });


                    }
                });
            }
        };
        lv_function.setAdapter(adapter);
//        lv_function.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//            }
//        });
        getFunction();
    }

    private void getFunction() {
        Map<String,Object>map=new HashMap<>();
        map.put(Constance.iotId,iotId);
        map.put("flowType",is_auto?1:2);
        ApiClientForIot.getIotClient("/iotid/scene/ability/list", "1.0.2", map, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                LogUtils.logE("actions_dev", String.valueOf(ioTResponse.getData()));
                devFunctionBeans = new Gson().fromJson(String.valueOf(ioTResponse.getData()),new TypeToken<List<DevFunctionBean>>(){}.getType());
                if(devFunctionBeans !=null&& devFunctionBeans.size()>0){
                    if(is_edit){
                        for(int i=0;i<devFunctionBeans.size();i++){
                            if(!devFunctionBeans.get(i).getIdentifier().equals(propertyName)){
                                devFunctionBeans.remove(i);
                                i--;
                            }
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.replaceAll(devFunctionBeans);
                            progressArray = new int[devFunctionBeans.size()];
                        }
                    });
                }
            }
        });

    }

    @Override
    protected void initData() {
        iotId = getIntent().getStringExtra(Constance.iotId);
        is_auto = getIntent().getBooleanExtra(Constance.is_auto,false);
        iscondition = getIntent().getBooleanExtra(Constance.is_condition,false);
        is_edit = getIntent().getBooleanExtra(Constance.is_edit,false);
        positon = getIntent().getIntExtra(Constance.position,0);
        uri = getIntent().getStringExtra(Constance.uri);
        icon = getIntent().getStringExtra(Constance.icon);
        name = getIntent().getStringExtra(Constance.name);
        propertyName = getIntent().getStringExtra(Constance.propertyName);
        temp = DemoApplication.actions;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_save:
                Intent intent=new Intent();
                List<DevFunctionBean> devResult=new ArrayList<>();
                boolean notSet=true;
                for(int i=0;i<devFunctionBeans.size();i++){
                    if(!TextUtils.isEmpty(devFunctionBeans.get(i).property)){
                        notSet=false;
                        devResult.add(devFunctionBeans.get(i));
                    }
                }
                if(notSet){
                    MyToast.show(this,getResources().getString(R.string.str_atleastone));
                    return;
                }

                intent.putExtra(Constance.functionList,new Gson().toJson(devResult,new TypeToken<List<DevFunctionBean>>(){}.getType()));
                intent.putExtra(Constance.is_open,isOpen);
                intent.putExtra(Constance.iotId,iotId);
                intent.putExtra(Constance.propertyName,devFunctionBeans.get(current).getIdentifier());
                String url="action/device/setProperty";
                if(devFunctionBeans.get(current).getType()==2) {
                    url = "action/device/invokeService";
                    intent.putExtra(Constance.propertyValue,isOpen?"1":"0");
                }else {
                    intent.putExtra(Constance.propertyValue,isOpen?"1":"0");
                }

                intent.putExtra(Constance.propertyName_desc,devFunctionBeans.get(current).getName());
                intent.putExtra(Constance.propertyValue_desc,isOpen?"开启":"关闭");

                intent.putExtra(Constance.actionsUrl,url);
                if(is_edit){
                    intent.putExtra(Constance.position,positon);
                    intent.putExtra(Constance.uri,url);
                    intent.putExtra(Constance.name,name);
                    intent.putExtra(Constance.img,icon);
                setResult(300,intent);
                }else {
                setResult(200,intent);
                }
                finish();
                break;

        }
    }
}
