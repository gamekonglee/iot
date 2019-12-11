package com.juhao.home.scene;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.BaseActivity;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.bean.Scenes;
import com.bean.ScenesBean;
import com.google.gson.Gson;
import com.juhao.home.R;
import com.juhao.home.adapter.BaseAdapterHelper;
import com.juhao.home.adapter.QuickAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.util.Constance;
import com.view.MyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IotSceneActionAutoSelectActivity extends BaseActivity {

    private ListView lv_auto;
    private QuickAdapter<Scenes> adapter;
    private List<Scenes> scenesList;
    private boolean[] select;
    private boolean isEdit;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_iot_scene_action_auto_select);
        lv_auto = findViewById(R.id.lv_auto);
        adapter = new QuickAdapter<Scenes>(this,R.layout.item_auto_select) {
            @Override
            protected void convert(BaseAdapterHelper helper, Scenes item) {
                ImageView iv_bg=helper.getView(R.id.iv_bg);
                ImageView iv_bg_color=helper.getView(R.id.iv_bg_color);
                helper.setText(R.id.tv_name,item.getName());
                try {
                    JSONObject desc=new JSONObject(item.getDescription());
                    String bg_pic=desc.optString(Constance.bg_pic);
                    if(!TextUtils.isEmpty(bg_pic)){
                        ImageLoader.getInstance().displayImage(bg_pic,iv_bg);
                    }
                    ColorDrawable colorDrawabl=new ColorDrawable();
                    colorDrawabl.setColor(Color.parseColor(item.getIconColor()));
                    iv_bg_color.setImageDrawable(colorDrawabl);
                    iv_bg_color.setAlpha(212);
                    if(select[helper.getPosition()]){
                        helper.setText(R.id.tv_select,getString(R.string.icon_dot_select));
                        helper.setTextColor(R.id.tv_select,getResources().getColor(R.color.theme));
                    }else {
                        helper.setText(R.id.tv_select,getString(R.string.icon_dot_normal));
                        helper.setTextColor(R.id.tv_select,getResources().getColor(R.color.white));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        lv_auto.setAdapter(adapter);
        scenesList = new ArrayList<>();
        getSceneList();
        lv_auto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                select[i]=!select[i];
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void initData() {
        isEdit = getIntent().getBooleanExtra(Constance.is_edit,false);
    }

    private void getSceneList() {

        Map<String, Object> maps = new HashMap<>();
        maps.put("pageNo",1);
        maps.put("pageSize","20");
        maps.put("groupId","0");
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/scene/list/get")
                .setApiVersion("1.0.5")
                .setAuthType("iotAuth")
                .setParams(maps);

//        ApiClientForIot.getIotClient("/scene/list/get", "1.0.5", maps, new IoTCallback() {
//            @Override
//            public void onFailure(IoTRequest ioTRequest, Exception e) {
//
//            }
//
//            @Override
//            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
//                LogUtils.logE("");
//            }
//        });


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
                if (code != 200){
                    return;
                }

                Object data = response.getData();
                if(data!=null){
                    ScenesBean scenesBean=new Gson().fromJson(data.toString(),ScenesBean.class);
                    List<Scenes>temp=scenesBean.getScenes();
                    if(temp==null||temp.size()==0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                    adapter.replaceAll(scenesList);
                            }
                        });
                        return;
                    }
                    if(scenesBean.getPageNo()==1){
                        scenesList =temp;
                    }else {
                        scenesList.addAll(temp);
                    }
                    select = new boolean[scenesList.size()];

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                adapter.replaceAll(scenesList);

                        }
                    });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                adapter.replaceAll(scenesList);
                        }
                    });
                }
//                LogUtils.logE("sceneList",""+data.toString());
            }
        });
    }

    @Override
    public void save(View v) {
        super.save(v);
        boolean atLeastOne=false;
        for(int i=0;i<select.length;i++){
            if(select[i]){
                atLeastOne=true;
                break;
            }
        }
        if(!atLeastOne){
            MyToast.show(this,getString(R.string.str_atleastone));
            return;
        }
        String result="";
        String icon="";
        String iconColor="";
        String name="";
        for(int j=0;j<scenesList.size();j++){
            if(select[j]){
                    result+=scenesList.get(j).getId();
                    icon+=scenesList.get(j).getIcon();
                    name+=scenesList.get(j).getName();
                    iconColor+=scenesList.get(j).getIconColor();
                    result+=",";
                    icon+=",";
                    iconColor+=",";
                    name+=",";
            }
        }

        result=result.substring(0,result.length()-1);
        Intent intent=new Intent();
        intent.putExtra(Constance.scene_id,result);
        intent.putExtra(Constance.icon,icon);
        intent.putExtra(Constance.iconColor,iconColor);
        intent.putExtra(Constance.name,name);
        intent.putExtra(Constance.uri,"action/scene/trigger");
        setResult(200,intent);
        finish();
    }
}
