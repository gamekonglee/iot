package com.juhao.home.scene;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.BaseActivity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.bean.AccountDevDTO;
import com.bean.DevFunSetBean;
import com.bean.DevFunctionBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.juhao.home.adapter.BaseAdapterHelper;
import com.juhao.home.adapter.QuickAdapter;
import com.net.ApiClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pgyersdk.crash.PgyCrashManager;
import com.util.ApiClientForIot;
import com.util.Constance;
import com.util.LogUtils;
import com.view.FontIconView;
import com.view.MyToast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IotSceneAddActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_name;
    private TextView tv_save;
//    private ImageView iv_img;
    private ImageView iv_add;
    private ListView lv_action;
    private RelativeLayout rl_name;
    private RelativeLayout rl_style;
    private RelativeLayout rl_home_disappear;
    private ImageView iv_kaiguan;
    private QuickAdapter<DevFunSetBean> devFunctionBeanQuickAdapter;
    private List<DevFunSetBean> devFunSetBeans=new ArrayList<>();
    List<DevFunSetBean> devCondition=new ArrayList<>();
    private TextView tv_add_mission;
    private ListView lv_missions;
    private LinearLayout ll_mission;
    private int currentStyle;
    private TextView tv_color;
    private TextView tv_icon;
    private TextView tv_pic;
    private GridView gv_style;
    private Dialog dialog;
    private QuickAdapter<String> styleAdapter;
    private List<String> iconList;
    private List<String> imgList;
    private List<String> color_img_list;
    private List<String> color_list;
    private String currenIconColor;
    private String currentIcon="";;
    private ImageView iv_color;
    private ImageView iv_pic;
    private String currentPic;
    private FontIconView iv_icon;
    private String currentName="钜豪智能";
    private boolean isEdit;
    private int scenetype;
    private String sceneid;
    private TextView tv_title;
    private TextView tv_delete;
    private boolean is_auto;
    private View ll_condition;
    private ImageView iv_add_condition;
    private TextView tv_add_condition;
    private ListView lv_condition;
    private View rl_effect_period;
    private TextView tv_effect;
    private QuickAdapter<DevFunSetBean> adapterCondition;
    private List<String> zdhimg_list;
    private String caConditionJsonStr;
    private String weekCron;

    @Override
    protected void InitDataView() {
        if(isEdit){
            tv_title.setText(getString(R.string.str_edit));
            tv_delete.setVisibility(View.VISIBLE);
            if(is_auto){
                tv_delete.setText(getString(R.string.str_delete_auto));
            }else {
                tv_delete.setText(getString(R.string.str_delete_one_key_do));

            }
            final Map<String, Object> map=new HashMap<>();
            map.put("sceneId",sceneid);
            map.put("groupId",scenetype+"");
            ApiClientForIot.getIotClient("/scene/info/get", "1.0.5", map, new IoTCallback() {
                @Override
                public void onFailure(IoTRequest ioTRequest, Exception e) {
                }
                @Override
                public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                LogUtils.logE("sceneDetail", String.valueOf(ioTResponse.getData()));
                    org.json.JSONObject sceneDetail= (org.json.JSONObject) ioTResponse.getData();
                    try {
                        final String name=sceneDetail.getString(Constance.name);
                        final String icon=sceneDetail.getString(Constance.icon);
                        final String iconColor=sceneDetail.getString(Constance.iconColor);
                        final String desc=sceneDetail.optString(Constance.description,"");
                        String actionsStr=sceneDetail.getString(Constance.actionsJson);
                        if(is_auto){
                            caConditionJsonStr = sceneDetail.getString(Constance.caConditionsJson);
                        }
                        final org.json.JSONArray actionsArray=new org.json.JSONArray(actionsStr);
                        PgyCrashManager.reportCaughtException(IotSceneAddActivity.this,new Exception(actionsStr));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_name.setText(name);
                                currentName=name;
//                                ImageLoader.getInstance().displayImage(icon,iv_icon);
                                iv_icon.setText(Html.fromHtml("&#x"+icon+";"));
                                currenIconColor=iconColor;
                                currentIcon=icon;
                                org.json.JSONObject jsonObjectDes= null;
                                try {
                                    jsonObjectDes = new org.json.JSONObject(desc);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String pic=jsonObjectDes.optString(Constance.bg_pic);
                                currentPic=pic;



                                ColorDrawable colorDrawable=new ColorDrawable();
                                colorDrawable.setColor(Color.parseColor(iconColor));
                                iv_color.setImageDrawable(colorDrawable);
                                ImageLoader.getInstance().displayImage(pic,iv_pic);
                                if(is_auto){
                                    try {
                                        org.json.JSONArray caConditionArray=new org.json.JSONArray(caConditionJsonStr);
                                        for(int i=0;i<caConditionArray.length();i++){
                                            DevFunSetBean devFunSetBean=new DevFunSetBean();
                                            org.json.JSONObject caCondition=new org.json.JSONObject(caConditionArray.getString(i));
                                            org.json.JSONObject params=caCondition.getJSONObject(Constance.params);

                                            String uri=caCondition.getString(Constance.uri);
                                            int status=caCondition.getInt(Constance.status);
                                            devFunSetBean.uri=uri;
                                            com.alibaba.fastjson.JSONObject jsonObject=new JSONObject();
                                            jsonObject.put("uri",uri);
                                            com.alibaba.fastjson.JSONObject paramsNew=new com.alibaba.fastjson.JSONObject();

                                            if(uri.contains("condition")&&uri.contains("timer")){
                                                String cron=params.getString(Constance.cron);
                                                devFunSetBean.setIcon(R.mipmap.icon_dingshi+"");
                                                String[] timerRange=cron.split(" ");
                                            try {
                                                devFunSetBean.setName(getString(R.string.str_countdown)+"："+timerRange[1]+":"+timerRange[0]);
                                                String weekStr="";
                                                if(timerRange[4].equals("*")){
                                                    weekStr=timerRange[3]+"月"+timerRange[2]+"日";
                                                    devFunSetBean.setPropertyName_desc(weekStr);
                                                }else{
                                                    if(timerRange[4].contains("1")){
                                                        weekStr+=getString(R.string.str_mon)+",";
                                                    }
                                                    if(timerRange[4].contains("2")){
                                                        weekStr+=getString(R.string.str_tue)+",";
                                                    }
                                                    if(timerRange[4].contains("3")){
                                                        weekStr+=getString(R.string.str_wed)+",";
                                                    }
                                                    if(timerRange[4].contains("4")){
                                                        weekStr+=getString(R.string.str_thurs)+",";
                                                    }
                                                    if(timerRange[4].contains("5")){
                                                        weekStr+=getString(R.string.str_fri)+",";
                                                    }
                                                    if(timerRange[4].contains("6")){
                                                        weekStr+=getString(R.string.str_sat)+",";
                                                    }
                                                    if(timerRange[4].contains("7")||timerRange[4].contains("0")){
                                                        weekStr+=getString(R.string.str_sun)+",";
                                                    }
                                                    weekStr=weekStr.substring(0,weekStr.length()-1);
                                                    devFunSetBean.setPropertyName_desc(weekStr);
                                                }
                                                devFunSetBean.hour=timerRange[1];
                                                devFunSetBean.min=timerRange[0];
                                                devFunSetBean.cron=cron;
                                                devFunSetBean.weeks=weekStr;
                                            }catch (Exception e){
                                            }
                                                paramsNew.put("cron",cron);
                                                paramsNew.put("cronType","linux");
                                            }else if(uri.contains("condition")&&uri.contains("property")&&uri.contains("device")){
                                                devFunSetBean.setIcon(params.getString(Constance.productImage));
                                                devFunSetBean.setName(params.getString(Constance.deviceName));
                                                devFunSetBean.setPropertyName_desc(params.getString(Constance.localizedPropertyName));
                                                devFunSetBean.setPropertyName(params.getString(Constance.propertyName));
                                                devFunSetBean.setPropertyValue_dec(params.getString(Constance.localizedCompareValueName));
                                                devFunSetBean.setPropertyValue(params.getString(Constance.compareValue));
                                                devFunSetBean.setIotId(params.getString(Constance.iotId));
                                                paramsNew= (JSONObject) JSON.parseObject(params.toString());

                                            }else if(uri.contains("condition")&&uri.contains("timeRange")){
                                                String beginDate=params.getString(Constance.beginDate);
                                                String endDate=params.getString(Constance.endDate);
                                                String repeat=params.optString(Constance.repeat);
                                                String format=params.getString(Constance.format);
                                                paramsNew.put("beginDate",beginDate);
                                                paramsNew.put("endDate",endDate);
                                                String weekStr=getString(R.string.str_onetime);
                                                if(!TextUtils.isEmpty(repeat)){
                                                paramsNew.put("repeat",repeat);
                                                weekStr="";
                                                    if(repeat.contains("1")){
                                                        if(weekStr.length()>2){
                                                        weekStr+=getString(R.string.str_monday)+",";
                                                        }else {
                                                            weekStr+=getString(R.string.str_mon)+",";
                                                        }
                                                    }
                                                    if(repeat.contains("2")){
                                                        if(weekStr.length()>2){
                                                            weekStr+=getString(R.string.str_tuesday)+",";
                                                        }else {
                                                            weekStr+=getString(R.string.str_tue)+",";
                                                        }
                                                    }
                                                    if(repeat.contains("3")){
                                                        if(weekStr.length()>2){
                                                            weekStr+=getString(R.string.str_wednesday)+",";
                                                        }else {
                                                        weekStr+=getString(R.string.str_wed)+",";
                                                        }
                                                    }
                                                    if(repeat.contains("4")){
                                                        if(weekStr.length()>2){
                                                            weekStr+=getString(R.string.str_thursday)+",";
                                                        }else {
                                                            weekStr+=getString(R.string.str_thurs)+",";
                                                        }
                                                    }
                                                    if(repeat.contains("5")){
                                                        if(weekStr.length()>2){
                                                            weekStr+=getString(R.string.str_friday)+",";
                                                        }else {
                                                        weekStr+=getString(R.string.str_fri)+",";
                                                        }
                                                    }
                                                    if(repeat.contains("6")){
                                                        if(weekStr.length()>2){
                                                            weekStr+=getString(R.string.str_saturday)+",";
                                                        }else {
                                                        weekStr+=getString(R.string.str_sat)+",";
                                                        }
                                                    }
                                                    if(repeat.contains("7")||repeat.contains("0")){
                                                        if(weekStr.length()>2){
                                                            weekStr+=getString(R.string.str_sunday)+",";
                                                        }else {
                                                        weekStr+=getString(R.string.str_sun)+",";
                                                        }
                                                    }
                                                    weekStr=weekStr.substring(0,weekStr.length()-1);
                                                }
                                                devFunSetBean.setPropertyName_desc(weekStr);
                                                paramsNew.put("format",format);
                                                devFunSetBean.setIcon(R.mipmap.icon_dingshi+"");
                                                devFunSetBean.setName(getString(R.string.str_time_range)+":"+beginDate+"-"+endDate);
                                            }
                                            jsonObject.put("params",paramsNew);
                                            caConditions.add(jsonObject);
                                            DemoApplication.caConditions=caConditions;
                                            devCondition.add(devFunSetBean);
                                        }
                                        tv_add_condition.setVisibility(View.GONE);
                                        lv_condition.setVisibility(View.VISIBLE);
                                        adapterCondition.replaceAll(devCondition);
                                        LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) ll_condition.getLayoutParams();
                                        layoutParams.height= UIUtils.dip2PX(60+60*devCondition.size()+20);
                                        ll_condition.setLayoutParams(layoutParams);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }

                                for (int i=0;i<actionsArray.length();i++){
                                    try {
                                        org.json.JSONObject action=new org.json.JSONObject(actionsArray.getString(i));
                                        org.json.JSONObject params=action.getJSONObject(Constance.params);
                                        com.alibaba.fastjson.JSONObject jsonObject=new JSONObject();
                                        final com.alibaba.fastjson.JSONObject paramsObjec=new com.alibaba.fastjson.JSONObject();
                                        final DevFunSetBean devFunSetBean=new DevFunSetBean();
                                        String uri=action.getString(Constance.uri);
                                        if(uri.contains("scene")){
                                            final String id=params.getString(Constance.sceneId);
                                            String name=params.getString(Constance.name);
                                            boolean vaild=params.getBoolean(Constance.valid);
                                            String icon=params.getString(Constance.icon);
                                            String iconColor=params.getString(Constance.iconColor);
                                            devFunSetBean.setName(name);
                                            devFunSetBean.setIcon("&#x"+icon+";");
                                            devFunSetBean.setPropertyValue(iconColor);

                                            paramsObjec.put("sceneId",id);
                                            //自动化id
//                                            HashMap<String,Object> map1=new HashMap<>();
//                                            map1.put("sceneId",id);
//                                            map1.put("groupId","1");
//                                            ApiClientForIot.getIotClient("/scene/info/get", "1.0.5", map1, new IoTCallback() {
//                                                @Override
//                                                public void onFailure(IoTRequest ioTRequest, Exception e) {
//
//                                                }
//
//                                                @Override
//                                                public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
//                                                    org.json.JSONObject sceneDetail= (org.json.JSONObject) ioTResponse.getData();
//                                                    try {
//                                                        final String name = sceneDetail.getString(Constance.name);
//                                                        final String icon = sceneDetail.getString(Constance.icon);
//                                                        final String iconColor = sceneDetail.getString(Constance.iconColor);
//                                                        final String desc = sceneDetail.optString(Constance.description, "");
//                                                        devFunSetBean.setName(name);
//                                                        devFunSetBean.setIcon("&#x"+icon+";");
//                                                        devFunSetBean.setPropertyValue(iconColor);
//                                                        devFunSetBeans.add(devFunSetBean);
//                                                        runOnUiThread(new Runnable() {
//                                                            @Override
//                                                            public void run() {
//                                                                devFunctionBeanQuickAdapter.replaceAll(devFunSetBeans);
//                                                                tv_add_mission.setVisibility(View.GONE);
//                                                                lv_missions.setVisibility(View.VISIBLE);
//                                                                LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) ll_mission.getLayoutParams();
//                                                                layoutParams.height= UIUtils.dip2PX(60+60*devFunSetBeans.size()+20);
//                                                                ll_mission.setLayoutParams(layoutParams);
//                                                            }
//                                                        });
////                                                        String actionsStr = sceneDetail.getString(Constance.actionsJson);
//                                                    }catch (Exception e){
//
//                                                    }
//
//                                                }
//                                            });


                                        }else {
                                            String identifier=params.getString(Constance.identifier);
                                            String iotId=params.getString(Constance.iotId);
                                            String localizedProductName=params.getString(Constance.localizedProductName);
                                            String productImage=params.getString(Constance.productImage);
                                            String propertyName=params.getString(Constance.propertyName);
                                            String localizedPropertyName=params.getString(Constance.localizedPropertyName);
                                            String deviceNickName=params.getString(Constance.deviceNickName);
                                            String propertyValue=params.getString(Constance.propertyValue);
                                            String localizedCompareValueName=params.getString(Constance.localizedCompareValueName);
                                            String productKey=params.getString(Constance.productKey);
                                            org.json.JSONObject propertyItems=params.getJSONObject(Constance.propertyItems);
                                            paramsObjec.put("iotId",iotId);
                                            paramsObjec.put("propertyName",identifier);
                                            String value=propertyValue;
                                            try{
                                                paramsObjec.put("propertyValue",Integer.parseInt(value));
                                            }catch (Exception e){
                                                paramsObjec.put("propertyValue",value);
                                            }
                                            devFunSetBean.setName(localizedProductName);
                                            devFunSetBean.setIotId(iotId);
                                            devFunSetBean.setIcon(productImage);
                                            devFunSetBean.setPropertyName_desc(localizedPropertyName+"："+localizedCompareValueName);
                                            devFunSetBean.setPropertyValue_dec(localizedCompareValueName);
                                            devFunSetBean.setPropertyName(propertyName);
                                            devFunSetBean.setPropertyValue(propertyValue);

                                        }
                                        devFunSetBeans.add(devFunSetBean);
                                        int status=action.getInt(Constance.status);
                                        jsonObject.put("uri",uri);
                                        jsonObject.put("params",paramsObjec);
                                        actions.add(jsonObject);
                                        DemoApplication.actions=actions;

//                                        devFunSetBean.uri=uri;


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                devFunctionBeanQuickAdapter.replaceAll(devFunSetBeans);
                                tv_add_mission.setVisibility(View.GONE);
                                lv_missions.setVisibility(View.VISIBLE);
                                LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) ll_mission.getLayoutParams();
                                layoutParams.height= UIUtils.dip2PX(60+60*devFunSetBeans.size()+20);
                                ll_mission.setLayoutParams(layoutParams);

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        if(is_auto){
            ll_condition.setVisibility(View.VISIBLE);
//            rl_effect_period.setVisibility(View.VISIBLE);
            rl_home_disappear.setVisibility(View.GONE);
        }else {
            ll_condition.setVisibility(View.GONE);
            rl_effect_period.setVisibility(View.GONE);
            rl_home_disappear.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        fullScreen(this);
        setContentView(R.layout.activity_iot_scene);
        tv_save = findViewById(R.id.tv_save);
        tv_name = findViewById(R.id.tv_name);
//        iv_img = findViewById(R.id.iv_img);
        iv_add = findViewById(R.id.iv_add);
        lv_action = findViewById(R.id.lv_action);
        rl_name = findViewById(R.id.rl_name);
        rl_style = findViewById(R.id.rl_style);
        tv_add_mission = findViewById(R.id.tv_add_mission);
        rl_home_disappear = findViewById(R.id.rl_home_disappear);
        iv_kaiguan = findViewById(R.id.iv_kaiguan);
        lv_missions = findViewById(R.id.lv_missions);
        ll_mission = findViewById(R.id.ll_mission);
        iv_color = findViewById(R.id.iv_color);
        iv_icon = findViewById(R.id.iv_icon);
        iv_pic = findViewById(R.id.iv_pic);
        tv_title = findViewById(R.id.tv_title);
        tv_delete = findViewById(R.id.tv_delete);

        ll_condition = findViewById(R.id.ll_condition);
        iv_add_condition = findViewById(R.id.iv_add_condition);
        tv_add_condition = findViewById(R.id.tv_add_condition);
        lv_condition = findViewById(R.id.lv_condition);

        rl_effect_period = findViewById(R.id.rl_effect_period);
        tv_effect = findViewById(R.id.tv_effect);

        iv_add_condition.setOnClickListener(this);
        tv_add_condition.setOnClickListener(this);
        rl_effect_period.setOnClickListener(this);

        devFunctionBeanQuickAdapter = new QuickAdapter<DevFunSetBean>(this,R.layout.item_dev_function_scene) {
            @Override
            protected void convert(BaseAdapterHelper helper, DevFunSetBean item) {
                if(item.getIcon().contains("&#")){
                    helper.setVisible(R.id.rl_img,true);
                    helper.setVisible(R.id.iv_img,false);
                    helper.setVisible(R.id.tv_desc,false);
                    TextView tv_img=helper.getView(R.id.tv_img);
                    tv_img.setText(Html.fromHtml(item.getIcon()));
//                    tv_img.setTextColor(Color.parseColor(item.getPropertyValue()));
                    helper.setBackgroundColor(R.id.iv_bg,Color.parseColor(item.getPropertyValue()));
                }else {
                    helper.setVisible(R.id.tv_desc,true);
                    helper.setVisible(R.id.rl_img,false);
                    helper.setVisible(R.id.iv_img,true);
                    ImageView iv_img=helper.getView(R.id.iv_img);
                    ImageLoader.getInstance().displayImage(item.getIcon(),iv_img);
                    helper.setText(R.id.tv_desc,item.getPropertyName_desc());
                }
                helper.setText(R.id.tv_name,item.getName());
            }
        };
        adapterCondition = new QuickAdapter<DevFunSetBean>(this,R.layout.item_dev_function_scene) {
            @Override
            protected void convert(BaseAdapterHelper helper, DevFunSetBean item) {
                    helper.setText(R.id.tv_name,item.getName());
                if(item.getIcon().contains("http")){
                    helper.setText(R.id.tv_desc,item.getPropertyName_desc()+":"+item.getPropertyValue_dec());
                    ImageView iv_img=helper.getView(R.id.iv_img);
                    ImageLoader.getInstance().displayImage(item.getIcon(),iv_img);
                    helper.setBackgroundRes(R.id.iv_img,0);
                }else {
                    helper.setImageBitmap(R.id.iv_img,null);
                    helper.setText(R.id.tv_desc, item.getPropertyName_desc());
                    helper.setBackgroundRes(R.id.iv_img,R.mipmap.icon_dingshi);
                }
            }
        };
        lv_missions.setAdapter(devFunctionBeanQuickAdapter);
        lv_condition.setAdapter(adapterCondition);

        lv_missions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(actions.getJSONObject(i).containsValue("action/scene/trigger")){
                    Intent intent=new Intent(IotSceneAddActivity.this,IotSceneActionAutoSelectActivity.class);
                    intent.putExtra(Constance.is_edit,true);
                    startActivityForResult(intent,700);
                }else {
                    Intent intent=new Intent(IotSceneAddActivity.this,IotSceneFunctionDevSetActivity.class);
                    intent.putExtra(Constance.iotId,devFunSetBeans.get(i).getIotId());
                    intent.putExtra(Constance.position,i);
                    intent.putExtra(Constance.name,devFunSetBeans.get(i).getName());
                    intent.putExtra(Constance.icon,devFunSetBeans.get(i).getIcon());
                    intent.putExtra(Constance.propertyName,devFunSetBeans.get(i).getPropertyName());
                    intent.putExtra(Constance.is_edit,true);
                    startActivityForResult(intent,600);
                }

            }
        });
        lv_missions.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                UIUtils.showSingleWordDialog(IotSceneAddActivity.this, getString(R.string.str_delete_misstion), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        actions.remove(i);
                        DemoApplication.actions=actions;
                        devFunSetBeans.remove(i);
                        devFunctionBeanQuickAdapter.replaceAll(devFunSetBeans);
                        LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) ll_mission.getLayoutParams();
                        if(devFunSetBeans.size()==0){
                            lv_missions.setVisibility(View.GONE);
                            tv_add_mission.setVisibility(View.VISIBLE);
                            layoutParams.height= UIUtils.dip2PX(140);

                        }else {
                            layoutParams.height= UIUtils.dip2PX(60+60*devFunSetBeans.size()+20);
                            lv_missions.setVisibility(View.VISIBLE);
                            tv_add_mission.setVisibility(View.GONE);
                        }
                        ll_mission.setLayoutParams(layoutParams);
                    }
                });
                return true;
            }
        });
        lv_condition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(devCondition.get(i).getName().contains(getString(R.string.str_countdown))){
                    Intent intent=new Intent(IotSceneAddActivity.this,IotAutoTriggerActivity.class);
                    intent.putExtra(Constance.hour,devCondition.get(i).hour);
                    intent.putExtra(Constance.minute,devCondition.get(i).min);
                    intent.putExtra(Constance.cron,devCondition.get(i).cron);
                    intent.putExtra(Constance.weekStr,devCondition.get(i).weeks);
                    intent.putExtra(Constance.is_edit,true);
                    intent.putExtra(Constance.position,i);
                    startActivityForResult(intent,400);
                }else if(devCondition.get(i).getName().contains(getString(R.string.str_time_range))){
                    Intent intent=new Intent(IotSceneAddActivity.this,IotAutoActionTimingActivity.class);
                    intent.putExtra(Constance.beginDate,caConditions.getJSONObject(0).getJSONObject(Constance.params).getString(Constance.beginDate));
                    intent.putExtra(Constance.endDate,caConditions.getJSONObject(0).getJSONObject(Constance.params).getString(Constance.endDate));
                    intent.putExtra(Constance.repeat,""+caConditions.getJSONObject(0).getJSONObject(Constance.params).getString(Constance.repeat));
                    startActivityForResult(intent,300);
                }
            }
        });
        lv_condition.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                UIUtils.showSingleWordDialog(IotSceneAddActivity.this, getString(R.string.str_delete_condition), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        caConditions.remove(i);
                        devCondition.remove(i);
                        DemoApplication.caConditions=caConditions;
                        adapterCondition.replaceAll(devCondition);
                        if(devCondition.size()==0){
                          lv_condition.setVisibility(View.GONE);
                          tv_add_condition.setVisibility(View.VISIBLE);
                        }else {
                            lv_condition.setVisibility(View.VISIBLE);
                            tv_add_condition.setVisibility(View.GONE);
                        }
                    }
                });
                return true;
            }
        });
        tv_name.setOnClickListener(this);
        tv_save.setOnClickListener(this);
        iv_add.setOnClickListener(this);
        rl_name.setOnClickListener(this);
        rl_style.setOnClickListener(this);
        iv_kaiguan.setOnClickListener(this);
        tv_add_mission.setOnClickListener(this);
        tv_delete.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        isEdit = getIntent().getBooleanExtra(Constance.is_edit,false);
        sceneid = getIntent().getStringExtra(Constance.scene_id);
        scenetype = getIntent().getIntExtra(Constance.scene_type,0);
        is_auto = getIntent().getBooleanExtra(Constance.is_auto,false);

    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            dialog = UIUtils.showBottomInDialog(IotSceneAddActivity.this,R.layout.dialog_scene_style,UIUtils.dip2PX(300));
            tv_color = dialog.findViewById(R.id.tv_color);
            tv_icon = dialog.findViewById(R.id.tv_icon);
            tv_pic = dialog.findViewById(R.id.tv_pic);
            gv_style = dialog.findViewById(R.id.gv_style);
            styleAdapter = new QuickAdapter<String>(IotSceneAddActivity.this,R.layout.item_scene_style) {
                @Override
                protected void convert(BaseAdapterHelper helper, String item) {
                    ImageView iv_img=helper.getView(R.id.iv_img);
                    ImageView iv_img_circle=helper.getView(R.id.iv_img_circle);
                    TextView tv_icon=helper.getView(R.id.tv_icon);
//                    ImageView iv_icon_auto=helper.getView(R.id.iv_icon_auto);

                    if(currentStyle==0){
                        iv_img_circle.setVisibility(View.VISIBLE);
                        iv_img.setVisibility(View.GONE);
                        tv_icon.setVisibility(View.GONE);
//                        iv_icon_auto.setVisibility(View.GONE);
                        ColorDrawable colorDrawable=new ColorDrawable();
                        colorDrawable.setColor(Color.parseColor(item));
                        iv_img_circle.setImageDrawable(colorDrawable);
                    }else {
                        iv_img_circle.setVisibility(View.GONE);
                        if(currentStyle==1){
//                                iv_icon_auto.setVisibility(View.VISIBLE);
                                tv_icon.setVisibility(View.VISIBLE);
                                Typeface font = Typeface.createFromAsset(context.getAssets(), "iconfont.ttf");
                                tv_icon.setTypeface(font);
                                tv_icon.setText(Html.fromHtml("&#x"+item+";"));
                                iv_img.setVisibility(View.GONE);

                        }else {
                            tv_icon.setVisibility(View.GONE);
                            iv_img.setVisibility(View.VISIBLE);
                            LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) iv_img.getLayoutParams();
                            layoutParams.height=UIUtils.dip2PX(45);
                            layoutParams.width=UIUtils.dip2PX(75);
                            iv_img.setLayoutParams(layoutParams);
                            ImageLoader.getInstance().displayImage(item,iv_img);

                        }
                    }
                }
            };
            gv_style.setAdapter(styleAdapter);
            gv_style.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if(currentStyle==0){
                        currenIconColor = color_list.get(i);
                        ColorDrawable colorDrawable=new ColorDrawable();
                        colorDrawable.setColor(Color.parseColor(currenIconColor));
                        iv_color.setImageDrawable(colorDrawable);
                    }else if(currentStyle==1){

                        currentIcon = iconList.get(i);
                        iv_icon.setText(Html.fromHtml("&#x"+currentIcon+";"));
                    }else if(currentStyle==2){
                        if(is_auto){
                            currentPic = zdhimg_list.get(i);
                        }else {
                            currentPic = imgList.get(i);
                        }
                        ImageLoader.getInstance().displayImage(currentPic,iv_pic);
                    }
                    int selectP=isAllSelect();
                    if(selectP==-1){
                    dialog.dismiss();
                    }else {
                        if(selectP==0){
                            tv_color.performClick();
                        }else if(selectP==1){
                            tv_icon.performClick();
                        }else {
                            tv_pic.performClick();
                        }
                    }
                }
            });
            tv_color.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentStyle=0;
                    refreshUI();
                }
            });
            tv_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentStyle=1;
                    refreshUI();
                }
            });
            tv_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentStyle=2;
                    refreshUI();
                }
            });
            tv_color.performClick();
        }
    };

    private int isAllSelect() {
        if(TextUtils.isEmpty(currenIconColor)){
//            MyToast.show(this,getString(R.string.str_please_choose_icon_color));
            return 0;
        }
        if(TextUtils.isEmpty(currentIcon)){
//            MyToast.show(this,getString(R.string.str_please_choose_icon_color));
            return 1;
        }

        if(TextUtils.isEmpty(currentPic)){
//            MyToast.show(this,getString(R.string.str_please_choose_pic));
            return 2;
        }
        return -1;
    }

    @Override
    public void onClick(View view) {
    switch (view.getId()){
        case R.id.tv_save:
            createScene();
            break;
        case R.id.rl_name:
            final Dialog dialog=new Dialog(this,R.style.customDialog);
            dialog.setContentView(R.layout.dialog_name_input);
            final EditText et_name=dialog.findViewById(R.id.et_name);
            TextView tv_cancel=dialog.findViewById(R.id.tv_cancel);
            TextView tv_ensure=dialog.findViewById(R.id.tv_ensure);
            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            tv_ensure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name=et_name.getText().toString();
                    if(TextUtils.isEmpty(name)){
                        MyToast.show(IotSceneAddActivity.this,getString(R.string.str_not_empty));
                        return;
                    }
                    tv_name.setText(name);
                    currentName = name;
                    dialog.dismiss();
                }
            });
            if(isEdit){
                et_name.setText(tv_name.getText().toString());
            }
            dialog.show();
            break;
        case R.id.iv_kaiguan:
            isShowInHome=!isShowInHome;
            if(isShowInHome){
                iv_kaiguan.setImageResource(R.mipmap.kg_on);
            }else {
                iv_kaiguan.setImageResource(R.mipmap.kg_off);
            }
            break;
        case R.id.iv_add:
        case R.id.tv_add_mission:
            Intent intent=new Intent(this,IotSceneActionAddActivity.class);
            intent.putExtra(Constance.is_condition,false);
            startActivityForResult(intent,200);
            break;
        case R.id.rl_style:
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    String scene=ApiClient.getSceneImg();
                    try {
                        org.json.JSONObject jsonObject=new org.json.JSONObject(scene);
                        org.json.JSONObject data=jsonObject.getJSONObject(Constance.data);
                        org.json.JSONArray icon=data.getJSONArray(Constance.icon);
                        org.json.JSONArray img=data.getJSONArray(Constance.img);
                        org.json.JSONArray color_img=data.getJSONArray(Constance.color_img);
                        org.json.JSONArray color=data.getJSONArray(Constance.color);
                        org.json.JSONArray zdhimg=data.getJSONArray(Constance.zdhimg);
                        iconList = new ArrayList<>();
                        imgList = new ArrayList<>();
                        color_img_list = new ArrayList<>();
                        color_list = new ArrayList<>();
                        zdhimg_list = new ArrayList<>();
                        for(int i=0;i<icon.length();i++){
                            iconList.add(icon.getString(i));
                        }
                        for(int i=0;i<img.length();i++){
                            imgList.add(img.getString(i));
                        }
                        for(int i=0;i<color_img.length();i++){
                            color_img_list.add(color_img.getString(i));
                        }
                        for(int i=0;i<color.length();i++){
                            color_list.add(color.getString(i));
                        }
                        for(int i=0;i<zdhimg.length();i++){
                            zdhimg_list.add(zdhimg.getString(i));
                        }
                        handler.sendEmptyMessage(0);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });


                }
            }.start();
            break;
        case R.id.tv_delete:
            UIUtils.showSingleWordDialog(this, getString(R.string.str_delete_scene), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Map<String,Object> map=new HashMap<>();
                    map.put("sceneId",sceneid);
                    ApiClientForIot.getIotClient("/scene/delete", "1.0.2", map, new IoTCallback() {
                        @Override
                        public void onFailure(IoTRequest ioTRequest, Exception e) {

                        }

                        @Override
                        public void onResponse(IoTRequest ioTRequest, final IoTResponse ioTResponse) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MyToast.show(IotSceneAddActivity.this,getString(R.string.str_delete_success));
                                    finish();
                                }
                            });

                        }
                    });

                }
            });
            break;
        case R.id.iv_add_condition:
        case R.id.tv_add_condition:
            Intent intent2=new Intent(this,IotAutoConditionActivity.class);
            intent2.putExtra(Constance.is_condition,true);
            startActivityForResult(intent2,300);
            break;
        case R.id.rl_effect_period:
            startActivityForResult(new Intent(this,IotAutoActionTimingActivity.class),500);
            break;

    }
    }

    private void refreshUI() {
        tv_color.setTextColor(getResources().getColor(R.color.tv_999999));
        tv_icon.setTextColor(getResources().getColor(R.color.tv_999999));
        tv_pic.setTextColor(getResources().getColor(R.color.tv_999999));

        switch (currentStyle){
            case 0:
                gv_style.setNumColumns(4);
                tv_color.setTextColor(getResources().getColor(R.color.theme));
                styleAdapter.replaceAll(color_list);
                break;
            case 1:
                gv_style.setNumColumns(5);
                tv_icon.setTextColor(getResources().getColor(R.color.theme));

                styleAdapter.replaceAll(iconList);
                break;
            case 2:
                gv_style.setNumColumns(4);
                tv_pic.setTextColor(getResources().getColor(R.color.theme));
                if(is_auto){
                    styleAdapter.replaceAll(zdhimg_list);
                }else {
                styleAdapter.replaceAll(imgList);
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==200&&requestCode==200&&data!=null){
            //场景/自动化——任务——设备

            String functionList=data.getStringExtra(Constance.functionList);
            List<DevFunctionBean> devFunctionBeans=new Gson().fromJson(functionList,new TypeToken<List<DevFunctionBean>>(){}.getType());

            for(int i=0;i<devFunctionBeans.size();i++){
                com.alibaba.fastjson.JSONObject jsonObject=new JSONObject();
                String uri=data.getStringExtra(Constance.uri);
                jsonObject.put("uri",uri);
                com.alibaba.fastjson.JSONObject params=new com.alibaba.fastjson.JSONObject();
                params.put("iotId",data.getStringExtra(Constance.iotId));
                if(uri.contains("invokeService")){
                    params.put("serviceName",data.getStringExtra(Constance.propertyName));
                    String value=data.getStringExtra(Constance.propertyValue);
                    params.put("serviceArgs",value);
//            params.put("propertyValue",data.getStringExtra(Constance.propertyValue));
                }else {
                    params.put("propertyName",devFunctionBeans.get(i).getIdentifier());
                    String value=devFunctionBeans.get(i).property_value;
                    try{
                        params.put("propertyValue",Integer.parseInt(value));
                    }catch (Exception e){
                        params.put("propertyValue",value);
//            params.put("propertyValue",data.getStringExtra(Constance.propertyValue));
                    }
                }
                jsonObject.put("params",params);
                String property_valueStr="";
                property_valueStr+=devFunctionBeans.get(i).getName();
                property_valueStr+=":";
                property_valueStr+=devFunctionBeans.get(i).property;
                property_valueStr+=" ";
                DevFunSetBean devFunSetBean=new DevFunSetBean();
                devFunSetBean.setIcon(data.getStringExtra(Constance.img));
                devFunSetBean.setIotId(data.getStringExtra(Constance.iotId));
                devFunSetBean.setName(data.getStringExtra(Constance.name));
                devFunSetBean.setPropertyName(devFunctionBeans.get(i).getIdentifier());
                devFunSetBean.setPropertyName_desc(property_valueStr);
                devFunSetBean.setPropertyValue(data.getStringExtra(Constance.propertyValue));
                devFunSetBean.setPropertyValue_dec("");

                devFunSetBeans.add(devFunSetBean);

                actions.add(jsonObject);
            }

            DemoApplication.actions=actions;
            if(actions!=null&&actions.size()>0){
                devFunctionBeanQuickAdapter.replaceAll(devFunSetBeans);
                tv_add_mission.setVisibility(View.GONE);
                lv_missions.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) ll_mission.getLayoutParams();
                layoutParams.height= UIUtils.dip2PX(60+60*devFunSetBeans.size()+20);
                ll_mission.setLayoutParams(layoutParams);

            }
//            createScene();
//            getAction(data.getStringExtra(Constance.iotId));
        }else if(requestCode==200&&resultCode==400&&data!=null){
            //场景/自动化——任务——自动化

            String result=data.getStringExtra(Constance.scene_id);
            String [] sceneIds=result.split(",");
            String[]icon=data.getStringExtra(Constance.icon).split(",");
            String []iconColor=data.getStringExtra(Constance.iconColor).split(",");
            String []name =data.getStringExtra(Constance.name).split(",");
            for(int i=0;i<sceneIds.length;i++){
                com.alibaba.fastjson.JSONObject jsonObject=new JSONObject();
                String uri=data.getStringExtra(Constance.uri);
                jsonObject.put("uri",uri);
                com.alibaba.fastjson.JSONObject params=new com.alibaba.fastjson.JSONObject();
                params.put("sceneId",sceneIds[i]);
                jsonObject.put("params",params);
                actions.add(jsonObject);
                DevFunSetBean devFunSetBean=new DevFunSetBean();
                devFunSetBean.setIcon("&#x"+icon[i]+";");
                devFunSetBean.setName(name[i]);
                devFunSetBean.setPropertyValue(iconColor[i]);
                devFunSetBeans.add(devFunSetBean);
            }
            DemoApplication.actions=actions;
            if(actions!=null&&actions.size()>0){
                devFunctionBeanQuickAdapter.replaceAll(devFunSetBeans);
                tv_add_mission.setVisibility(View.GONE);
                lv_missions.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) ll_mission.getLayoutParams();
                layoutParams.height= UIUtils.dip2PX(60+60*devFunSetBeans.size()+20);
                ll_mission.setLayoutParams(layoutParams);
            }
        }else if(requestCode==700&&resultCode==200&&data!=null){
            //场景/自动化——任务——自动化
            for (int i=0;i<actions.size();i++){
                if(actions.getJSONObject(i).containsValue("action/scene/trigger")){
                    actions.remove(i);
                    devFunSetBeans.remove(i);
                    i--;
                }
            }
            String result=data.getStringExtra(Constance.scene_id);
            String [] sceneIds=result.split(",");
            String[]icon=data.getStringExtra(Constance.icon).split(",");
            String []iconColor=data.getStringExtra(Constance.iconColor).split(",");
            String []name =data.getStringExtra(Constance.name).split(",");
            for(int i=0;i<sceneIds.length;i++){
                com.alibaba.fastjson.JSONObject jsonObject=new JSONObject();
                String uri=data.getStringExtra(Constance.uri);
                jsonObject.put("uri",uri);
                com.alibaba.fastjson.JSONObject params=new com.alibaba.fastjson.JSONObject();
                params.put("sceneId",sceneIds[i]);
                jsonObject.put("params",params);
                actions.add(jsonObject);
                DevFunSetBean devFunSetBean=new DevFunSetBean();
                devFunSetBean.setIcon("&#x"+icon[i]+";");
                devFunSetBean.setName(name[i]);
                devFunSetBean.setPropertyValue(iconColor[i]);
                devFunSetBeans.add(devFunSetBean);
            }
            DemoApplication.actions=actions;
            if(actions!=null&&actions.size()>0){
                devFunctionBeanQuickAdapter.replaceAll(devFunSetBeans);
                tv_add_mission.setVisibility(View.GONE);
                lv_missions.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) ll_mission.getLayoutParams();
                layoutParams.height= UIUtils.dip2PX(60+60*devFunSetBeans.size()+20);
                ll_mission.setLayoutParams(layoutParams);
            }


        }
        else if(requestCode==600&&resultCode==300&&data!=null){
            int positoin=data.getIntExtra(Constance.position,0);

            String functionList=data.getStringExtra(Constance.functionList);
            List<DevFunctionBean> devFunctionBeans=new Gson().fromJson(functionList,new TypeToken<List<DevFunctionBean>>(){}.getType());

            for(int i=0;i<devFunctionBeans.size();i++){
                com.alibaba.fastjson.JSONObject jsonObject=new JSONObject();
                String property_valueStr="";
                String uri=data.getStringExtra(Constance.uri);
                jsonObject.put("uri",uri);
                com.alibaba.fastjson.JSONObject params=new com.alibaba.fastjson.JSONObject();
                params.put("iotId",data.getStringExtra(Constance.iotId));
                if(uri.contains("invokeService")){
                    params.put("serviceName",data.getStringExtra(Constance.propertyName));
                    String value=data.getStringExtra(Constance.propertyValue);
                    params.put("serviceArgs",value);
//            params.put("propertyValue",data.getStringExtra(Constance.propertyValue));
                }else {
                    params.put("propertyName",devFunctionBeans.get(i).getIdentifier());
                    String value=devFunctionBeans.get(i).property_value;
                    try{
                        params.put("propertyValue",Integer.parseInt(value));
                    }catch (Exception e){
                        params.put("propertyValue",value);
//            params.put("propertyValue",data.getStringExtra(Constance.propertyValue));
                    }
                }
                jsonObject.put("params",params);
                property_valueStr+=devFunctionBeans.get(i).getName();
                property_valueStr+=":";
                property_valueStr+=devFunctionBeans.get(i).property;
                property_valueStr+=" ";
                jsonObject.put("params",params);
                actions.set(positoin,jsonObject);
                DevFunSetBean devFunSetBean=new DevFunSetBean();
                devFunSetBean.setIcon(data.getStringExtra(Constance.img));
                devFunSetBean.setIotId(data.getStringExtra(Constance.iotId));
                devFunSetBean.setName(data.getStringExtra(Constance.name));
                devFunSetBean.setPropertyName(devFunctionBeans.get(i).getIdentifier());
                devFunSetBean.setPropertyName_desc(property_valueStr);
                devFunSetBean.setPropertyValue(data.getStringExtra(Constance.propertyValue));
                devFunSetBean.setPropertyValue_dec("");
                devFunSetBeans.set(positoin,devFunSetBean);
            }

            DemoApplication.actions=actions;
            if(actions!=null&&actions.size()>0){
                devFunctionBeanQuickAdapter.replaceAll(devFunSetBeans);
                tv_add_mission.setVisibility(View.GONE);
                lv_missions.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) ll_mission.getLayoutParams();
                layoutParams.height= UIUtils.dip2PX(60+60*devFunSetBeans.size()+20);
                ll_mission.setLayoutParams(layoutParams);
            }
//            DemoApplication.actions=actions;
//            if(actions!=null&&actions.size()>0){
//                DevFunSetBean devFunSetBean=new DevFunSetBean();
//                devFunSetBean.setIcon(data.getStringExtra(Constance.img));
//                devFunSetBean.setIotId(data.getStringExtra(Constance.iotId));
//                devFunSetBean.setName(data.getStringExtra(Constance.name));
//                devFunSetBean.setPropertyName(data.getStringExtra(Constance.propertyName));
//                devFunSetBean.setPropertyName_desc(data.getStringExtra(Constance.propertyName_desc));
//                devFunSetBean.setPropertyValue(data.getStringExtra(Constance.propertyValue));
//                devFunSetBean.setPropertyValue_dec(data.getStringExtra(Constance.propertyValue_desc));

//                devFunSetBeans.set(positoin,devFunSetBean);
//                devFunctionBeanQuickAdapter.replaceAll(devFunSetBeans);
//                tv_add_mission.setVisibility(View.GONE);
//                lv_missions.setVisibility(View.VISIBLE);
//                LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) ll_mission.getLayoutParams();
//                layoutParams.height= UIUtils.dip2PX(60+60*devFunSetBeans.size()+20);
//                ll_mission.setLayoutParams(layoutParams);

//            }


        }else if(requestCode==300&&resultCode==200&&data!=null){
            //自动化——条件——设备
            com.alibaba.fastjson.JSONObject jsonObject=new JSONObject();
            jsonObject.put("uri",data.getStringExtra(Constance.uri));
            com.alibaba.fastjson.JSONObject params=new com.alibaba.fastjson.JSONObject();
            params.put("iotId",data.getStringExtra(Constance.iotId));
            params.put("propertyName",data.getStringExtra(Constance.propertyName));
            params.put("compareType","==");
            params.put("productKey",data.getStringExtra(Constance.productKey));
            params.put("deviceName",data.getStringExtra(Constance.deviceName));
            String value=data.getStringExtra(Constance.propertyValue);
            try{
//                params.put("compareValue",);
                params.put("compareValue",Integer.parseInt(value));
            }catch (Exception e){
//            params.put("propertyValue",data.getStringExtra(Constance.propertyValue));
            }
            jsonObject.put("params",params);
            caConditions.add(jsonObject);
            DemoApplication.caConditions=caConditions;
            if(caConditions!=null&&caConditions.size()>0){
                DevFunSetBean devFunSetBean=new DevFunSetBean();
                devFunSetBean.setIcon(data.getStringExtra(Constance.img));
                devFunSetBean.setIotId(data.getStringExtra(Constance.iotId));
                devFunSetBean.setName(data.getStringExtra(Constance.name));
                devFunSetBean.setPropertyName(data.getStringExtra(Constance.propertyName));
                devFunSetBean.setPropertyName_desc(data.getStringExtra(Constance.propertyName_desc));
                devFunSetBean.setPropertyValue(data.getStringExtra(Constance.propertyValue));
                devFunSetBean.setPropertyValue_dec(data.getStringExtra(Constance.propertyValue_desc));

                devCondition.add(devFunSetBean);
                adapterCondition.replaceAll(devCondition);
                tv_add_condition.setVisibility(View.GONE);
                lv_condition.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) ll_condition.getLayoutParams();
                layoutParams.height= UIUtils.dip2PX(60+60*devCondition.size()+20);
                ll_condition.setLayoutParams(layoutParams);
        }
    }else if(requestCode==300&&resultCode==300&&data!=null){
            //自动化——条件——定时
            String timing_time=data.getStringExtra(Constance.timing_time);
            String timing_date=data.getStringExtra(Constance.timing_date);
            String timing=data.getStringExtra(Constance.timing);
            com.alibaba.fastjson.JSONObject jsonObject=new JSONObject();
            jsonObject.put("uri",data.getStringExtra(Constance.uri));
            com.alibaba.fastjson.JSONObject params=new com.alibaba.fastjson.JSONObject();
            params.put("cron",timing);
            params.put("cronType","linux");

            jsonObject.put("params",params);
            if(caConditions.size()>0){
                caConditions.set(0,jsonObject);
            }else {
                caConditions.add(jsonObject);
            }
            DemoApplication.caConditions=caConditions;
//            triggers.put("uri",data.getStringExtra(Constance.uri));
//            triggers.put("params",params);
            if(caConditions!=null&&caConditions.size()>0){
                DevFunSetBean devFunSetBean=new DevFunSetBean();
                devFunSetBean.setIcon(R.mipmap.icon_dingshi+"");
//                devFunSetBean.setIotId(data.getStringExtra(Constance.iotId));
                devFunSetBean.setName("定时："+timing_time);
                devFunSetBean.setPropertyName(data.getStringExtra(Constance.propertyName));
                devFunSetBean.setPropertyName_desc(timing_date);
                devFunSetBean.setPropertyValue(data.getStringExtra(Constance.propertyValue));
                devFunSetBean.setPropertyValue_dec(data.getStringExtra(Constance.propertyValue_desc));
                devFunSetBean.cron=timing;
                devFunSetBean.weeks=timing_date;
                devFunSetBean.hour=data.getStringExtra(Constance.hour);
                devFunSetBean.min=data.getStringExtra(Constance.minute);

                if(devCondition.size()>0){
                devCondition.set(0,devFunSetBean);
                }else {
                devCondition.add(devFunSetBean);
                }
                adapterCondition.replaceAll(devCondition);
                tv_add_condition.setVisibility(View.GONE);
                lv_condition.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) ll_condition.getLayoutParams();
                layoutParams.height= UIUtils.dip2PX(60+60*devCondition.size()+20);
                ll_condition.setLayoutParams(layoutParams);
        }
        }if(requestCode==400&&resultCode==300&&data!=null){
            int position=data.getIntExtra(Constance.position,0);
            String timing_time=data.getStringExtra(Constance.timing_time);
            String timing_date=data.getStringExtra(Constance.timing_date);
            String timing=data.getStringExtra(Constance.timing);
            com.alibaba.fastjson.JSONObject jsonObject=new JSONObject();
            jsonObject.put("uri",devCondition.get(position).uri);
            com.alibaba.fastjson.JSONObject params=new com.alibaba.fastjson.JSONObject();
            params.put("cron",timing);
            params.put("cronType","linux");
//            params.put("iotId",data.getStringExtra(Constance.iotId));
//            params.put("propertyName",data.getStringExtra(Constance.propertyName));
//            String value=data.getStringExtra(Constance.propertyValue);
//            try{
//                params.put("propertyValue",Integer.parseInt(value));
//            }catch (Exception e){
//            }
            jsonObject.put("params",params);
            if(caConditions.size()>0){
                caConditions.set(0,jsonObject);
            }else {
                caConditions.add(jsonObject);
            }
            DemoApplication.caConditions=caConditions;
                DevFunSetBean devFunSetBean=new DevFunSetBean();
                devFunSetBean.setIcon(R.mipmap.icon_dingshi+"");
//                devFunSetBean.setIotId(data.getStringExtra(Constance.iotId));
                devFunSetBean.setName("定时："+timing_time);
                devFunSetBean.setPropertyName(data.getStringExtra(Constance.propertyName));
                devFunSetBean.setPropertyName_desc(timing_date);
                devFunSetBean.setPropertyValue(data.getStringExtra(Constance.propertyValue));
                devFunSetBean.setPropertyValue_dec(data.getStringExtra(Constance.propertyValue_desc));
                devFunSetBean.cron=timing;
                devFunSetBean.weeks=timing_date;
                devFunSetBean.hour=data.getStringExtra(Constance.hour);
                devFunSetBean.min=data.getStringExtra(Constance.minute);
                devCondition.set(position,devFunSetBean);
                adapterCondition.replaceAll(devCondition);
//                tv_add_condition.setVisibility(View.GONE);
//                lv_condition.setVisibility(View.VISIBLE);
//                LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) ll_condition.getLayoutParams();
//                layoutParams.height= UIUtils.dip2PX(60+60*devCondition.size()+20);
//                ll_condition.setLayoutParams(layoutParams);
        }else if(requestCode==300&&resultCode==500&&data!=null){
            String timeRange=data.getStringExtra(Constance.timing_time);
            String weekDay=data.getStringExtra(Constance.timing_date);
            String cron=data.getStringExtra(Constance.timing);
            String startTime=data.getStringExtra(Constance.start_time);
            String endTime=data.getStringExtra(Constance.end_time);
            tv_effect.setText(weekDay+" "+timeRange);
            LogUtils.logE("range_cron",cron);
            if(cron.split(" ").length>4){
                weekCron = cron.split(" ")[4];
            }
            com.alibaba.fastjson.JSONObject jsonObject=new JSONObject();
            jsonObject.put("uri","condition/timeRange");
            com.alibaba.fastjson.JSONObject params=new com.alibaba.fastjson.JSONObject();
//            params.put("cron",cron);
//            params.put("cronType","linux");
            params.put("beginDate",startTime);
            params.put("endDate",endTime);
            params.put("format","HH:mm");
            if(weekCron!=null&&!weekCron.equals("*")){
            params.put("repeat",weekCron);
            }
//            params.put("iotId",data.getStringExtra(Constance.iotId));
//            params.put("propertyName",data.getStringExtra(Constance.propertyName));
//            String value=data.getStringExtra(Constance.propertyValue);
//            try{
//                params.put("propertyValue",Integer.parseInt(value));
//            }catch (Exception e){
//            }
            jsonObject.put("params",params);
            if(caConditions.size()>0){
            caConditions.set(0,jsonObject);
            }else {
            caConditions.add(jsonObject);
            }
            DemoApplication.caConditions=caConditions;
                DevFunSetBean devFunSetBean=new DevFunSetBean();
                devFunSetBean.setIcon(R.mipmap.icon_dingshi+"");
//                devFunSetBean.setIotId(data.getStringExtra(Constance.iotId));
                devFunSetBean.setName(getString(R.string.str_time_range)+"："+timeRange);
                devFunSetBean.setPropertyName(data.getStringExtra(Constance.propertyName));
                devFunSetBean.setPropertyName_desc(weekDay);
                devFunSetBean.setPropertyValue(data.getStringExtra(Constance.propertyValue));
                devFunSetBean.setPropertyValue_dec(data.getStringExtra(Constance.propertyValue_desc));
                devFunSetBean.cron=cron;
                devFunSetBean.weeks=weekDay;
                devFunSetBean.hour=data.getStringExtra(Constance.hour);
                devFunSetBean.min=data.getStringExtra(Constance.minute);
                if(devCondition.size()>0){
                    devCondition.set(0,devFunSetBean);
                }else {
                    devCondition.add(devFunSetBean);
                }
                adapterCondition.replaceAll(devCondition);
                tv_add_condition.setVisibility(View.GONE);
                lv_condition.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) ll_condition.getLayoutParams();
                layoutParams.height= UIUtils.dip2PX(60+60*devCondition.size()+20);
                ll_condition.setLayoutParams(layoutParams);
        }else if(requestCode==500&&resultCode==500&&data!=null){

        }
    }

    boolean isOpen=true;
    com.alibaba.fastjson.JSONArray actions=new JSONArray();
    JSONArray caConditions=new JSONArray();
    JSONObject triggers=new JSONObject();
    String sceneType="CA";
    String mode="all";
    boolean isShowInHome=true;
    public void createScene(){
        String name=tv_name.getText().toString();
        if(TextUtils.isEmpty(name)||name.equals("编辑名称")){
            MyToast.show(this,getString(R.string.str_please_input_name));
            return;
        }
        if(actions==null||actions.size()==0){
            MyToast.show(this,getString(R.string.str_please_choose_action));
            return;
        }
        if(TextUtils.isEmpty(currentIcon)||TextUtils.isEmpty(currenIconColor)){
            MyToast.show(this,getString(R.string.str_please_choose_icon_color));
            return;
        }
        if(TextUtils.isEmpty(currentPic)){
            MyToast.show(this,getString(R.string.str_please_choose_pic));
            return;
        }

        JSONArray conditionPic=new JSONArray();
        for(int i=0;i<devCondition.size();i++){
            conditionPic.add(devCondition.get(i).getIcon());
        }
        JSONArray actionPic=new JSONArray();
        for(int i=0;i<devFunSetBeans.size();i++){
            actionPic.add(devFunSetBeans.get(i).getIcon());
        }
        Map<String,Object> map=new HashMap<>();
        map.put("groupId",is_auto?"1":"0");
        map.put("enable",isOpen);
        map.put("name",currentName);
        map.put("icon",currentIcon);
        map.put("iconColor",currenIconColor);
        map.put("actions",actions);
        map.put("sceneType",sceneType);
        map.put("mode",mode);
        JSONObject descObject=new JSONObject();
        descObject.put(Constance.bg_pic,currentPic);
        descObject.put(Constance.dev_nums,actions.size()+"");

        if(is_auto){
            if(triggers!=null){
//                map.put("triggers",triggers);
            }
//            descObject.put(Constance.conditionPic,conditionPic);
//            descObject.put(Constance.actionPic,actionPic);
            if(caConditions!=null){
            map.put("caConditions",caConditions);
            }
//            map.put("triggers",triggers);
        }
        if(!is_auto){
            if(isShowInHome){
                    descObject.put(Constance.showinhome,"1");
            }else {
                if(descObject.containsKey(Constance.showinhome)){
                    descObject.remove(Constance.showinhome);
                }
//                descObject.put(Constance.showinhome,isShowInHome?"1":"");
            }
        }
        String dec_json=descObject.toJSONString();
            map.put("description",dec_json);
        if(isEdit){
            map.put("sceneId",sceneid);
        }
        ApiClientForIot.getIotClient(isEdit?"/scene/update":"/scene/create", "1.0.5", map, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, final IoTResponse ioTResponse) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.logE("create,", String.valueOf(ioTResponse.getData()));
                        if(ioTResponse.getCode()==200){
                            if(isEdit){
                            MyToast.show(IotSceneAddActivity.this,getString(R.string.str_update_success));
                            }else {
                            MyToast.show(IotSceneAddActivity.this,getString(R.string.str_create_success));
                            }
                            finish();
                        }else {
                            MyToast.show(IotSceneAddActivity.this," "+ioTResponse.getLocalizedMsg());
                        }
                    }
                });
            }
        });
    }
    public void getAction(String iotId){
        Map<String,Object> map=new HashMap<>();
        map.put("iotId",iotId);
        map.put("flowType",2);
        ApiClientForIot.getIotClient("/iotid/scene/ability/list", "1.0.2", map, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                LogUtils.logE("action", String.valueOf(ioTResponse.getData()));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DemoApplication.actions=new JSONArray();
        DemoApplication.caConditions=new JSONArray();
    }
}
