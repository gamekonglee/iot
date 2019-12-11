package com.mainintelligence;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.BaseFragment;
import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.ilop.demo.page.ilopmain.AddDeviceActivity;
import com.bean.AccountDevDTO;
import com.bean.Scenes;
import com.bean.ScenesBean;
import com.google.gson.Gson;
import com.juhao.home.R;
import com.BaseFragment;
import com.juhao.home.UIUtils;
import com.juhao.home.adapter.BaseAdapterHelper;
import com.juhao.home.adapter.QuickAdapter;
import com.juhao.home.scene.DragSortSceneActivity;
import com.juhao.home.scene.IotSceneAddActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.util.ApiClientForIot;
import com.util.Constance;
import com.util.LogUtils;
import com.view.EndOfGridView;
import com.view.EndOfListView;
import com.view.FontIconView;
import com.view.MyToast;
import com.view.PMSwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alibaba.mtl.appmonitor.AppMonitor.TAG;

/**
 * Created by gamekonglee on 2018/7/7.
 */

public class ItApplicationFragment extends BaseFragment implements EndOfListView.OnEndOfListListener {

    private int currentP=0;
    private TextView btn_add_device;
    private View iv_add;
    Bundle mBundle=new Bundle();
    private List<Scenes> scenes=new ArrayList<>();
    private QuickAdapter<Scenes> adapter;
    private int page=1;
    private PMSwipeRefreshLayout pullToRefresh;
    private boolean isBottom;
    private QuickAdapter<Scenes> adapterB;
    private View rl_add;
    private GridView lv_scenes;
    private TextView tv_none_devices_tips;
    private View mViewContent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mViewContent == null) {
            mViewContent = inflater.inflate(R.layout.frag_it_main_app, container, false);
        }

        // 缓存View判断是否含有parent, 如果有需要从parent删除, 否则发生已有parent的错误.
        ViewGroup parent = (ViewGroup) mViewContent.getParent();
        if (parent != null) {
            parent.removeView(mViewContent);
        }
        return mViewContent;
    }
    @Override
    protected void initController() {

    }

    @Override
    protected void initViewData() {

    }

    @Override
    protected void initView() {
        final TextView tv_scene=getView().findViewById(R.id.tv_scene);
        final TextView tv_auto=getView().findViewById(R.id.tv_auto);
        final View view_scene=getView().findViewById(R.id.view_scene);
        final View view_auto=getView().findViewById(R.id.view_auto);
        btn_add_device = getView().findViewById(R.id.btn_add_device);
        tv_none_devices_tips = getView().findViewById(R.id.tv_none_devices_tips);
        iv_add = getView().findViewById(R.id.iv_add);
        pullToRefresh = getView().findViewById(R.id.pullToRefresh);
        rl_add = getView().findViewById(R.id.rl_add);
        pullToRefresh.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED);
        pullToRefresh.setRefreshing(false);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page=1;
                scenes=new ArrayList<>();
                getSceneList();
            }
        });
        lv_scenes = getView().findViewById(R.id.lv_scenes);
//        lv_scenes.setOnEndOfListListener(this);
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), IotSceneAddActivity.class);
                intent.putExtra(Constance.is_auto,currentP==0?false:true);
                startActivity(intent);
//                String code = "link://router/scene";
//                Bundle bundle = new Bundle();
//                bundle.putString("sceneType","ilop"); // 传入插件参数，没有参数则不需要这一行
//                Router.getInstance().toUrlForResult(getActivity(), code, 1, bundle);
            }
        });
        rl_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), DragSortSceneActivity.class);
                intent.putExtra(Constance.is_auto,currentP==1?true:false);
                startActivity(intent);
            }
        });
        final int width= (UIUtils.getScreenWidth(getActivity())-UIUtils.dip2PX(40))/2;
        adapter = new QuickAdapter<Scenes>(getActivity(),R.layout.item_scene) {
            @Override
            protected void convert(BaseAdapterHelper helper, final Scenes item) {
                View rl_root=helper.getView(R.id.rl_root);
                rl_root.setLayoutParams(new ViewGroup.LayoutParams(width,width*102/167));
                helper.setText(R.id.tv_name,item.getName());
                TextView iv_icon=helper.getView(R.id.iv_icon);
                ImageView iv_bg=helper.getView(R.id.iv_bg);
                ImageView iv_color=helper.getView(R.id.iv_color);
                Typeface font = Typeface.createFromAsset(context.getAssets(), "iconfont.ttf");
                iv_icon.setTypeface(font);
                iv_icon.setText(Html.fromHtml("&#x"+item.getIcon()+";"));
//                ImageLoader.getInstance().displayImage(item.getIcon(),iv_icon);
                String desc=item.getDescription();
                try {
                    JSONObject jsonObject=new JSONObject(desc);
                    String pic=jsonObject.optString(Constance.bg_pic);
                    String dev_nums=jsonObject.optString(Constance.dev_nums);
                    if(!TextUtils.isEmpty(dev_nums)){
                        helper.setText(R.id.tv_dev_count,dev_nums+getString(R.string.str_dev_nums));
                    }else {
                        helper.setText(R.id.tv_dev_count,"0"+getString(R.string.str_dev_nums));
                    }
                    ImageLoader.getInstance().displayImage(pic,iv_bg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                iv_color.setImageAlpha(217);
                ColorDrawable colorDrawable=new ColorDrawable();
                String iconColor=item.getIconColor();
                if(TextUtils.isEmpty(iconColor)){
                    iconColor="#fa4b00";
                }
                colorDrawable.setColor(Color.parseColor(iconColor));
                iv_color.setImageDrawable(colorDrawable);
//                helper.getView().setLayoutParams(new ViewGroup.LayoutParams(width,width*102/167));

                helper.setOnClickListener(R.id.tv_doit, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Map<String, Object> maps = new HashMap<>();
                    maps.put("sceneId",item.getId());
                    IoTRequestBuilder builder = new IoTRequestBuilder()
                            .setPath("/scene/fire")
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
                        public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {

                            final int code=ioTResponse.getCode();

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(code!=200){
                                            Toast.makeText(getActivity(), getString(R.string.str_excute_failed), Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(getActivity(), getString(R.string.str_excute_success), Toast.LENGTH_SHORT).show();
                                            page=1;
                                            scenes=new ArrayList<>();
                                            getSceneList();
                                        }
                                    }
                                });
                        }
                    });
                }
            });
//                helper.setOnClickListener(R.id.rl_item_click, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Map<String, Object> maps = new HashMap<>();
//                        maps.put("sceneId",item.getId());
//                        IoTRequestBuilder builder = new IoTRequestBuilder()
//                                .setPath("/scene/fire")
//                                .setApiVersion("1.0.2")
//                                .setAuthType("iotAuth")
//                                .setParams(maps);
//                        IoTRequest request = builder.build();
//
//                        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
//                        ioTAPIClient.send(request, new IoTCallback() {
//                            @Override
//                            public void onFailure(IoTRequest ioTRequest, Exception e) {
//
//                            }
//
//                            @Override
//                            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
//
//                                final int code=ioTResponse.getCode();
//
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if(code!=200){
//                                            Toast.makeText(getActivity(), getString(R.string.str_excute_failed), Toast.LENGTH_SHORT).show();
//                                        }else {
//                                            Toast.makeText(getActivity(), getString(R.string.str_excute_success), Toast.LENGTH_SHORT).show();
//                                            page=1;
//                                            scenes=new ArrayList<>();
//                                            getSceneList();
//                                        }
//                                    }
//                                });
//                            }
//                        });
//                    }
//                });
            helper.setOnClickListener(R.id.rl_setting, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getActivity(),IotSceneAddActivity.class);
                    intent.putExtra(Constance.is_edit,true);
                    intent.putExtra(Constance.scene_id,item.getId());
                    intent.putExtra(Constance.scene_type,0);
                    startActivity(intent);
//                    String code = "link://router/scene";
//                    Bundle bundle = new Bundle();
//                    bundle.putString("sceneType","ilop"); // 传入插件参数，没有参数则不需要这一行
//                    bundle.putString("sceneId",item.getId());
//                    Router.getInstance().toUrlForResult(getActivity(), code, 1, bundle);
                }
            });
            }
        };
        adapterB = new QuickAdapter<Scenes>(getActivity(),R.layout.item_scene_auto) {
            @Override
            protected void convert(final BaseAdapterHelper helper, final Scenes item) {
                helper.setText(R.id.tv_name,item.getName());
                ImageView iv_bg=helper.getView(R.id.iv_bg);
                ImageView iv_color=helper.getView(R.id.iv_color);
                TextView iv_img_auto=helper.getView(R.id.iv_img_auto);
//                LinearLayout ll_pic=helper.getView(R.id.ll_pic);
                String desc=item.getDescription();
                try {
                    JSONObject jsonObject=new JSONObject(desc);
                    String pic=jsonObject.optString(Constance.bg_pic);
                    String dev_nums=jsonObject.optString(Constance.dev_nums);
                    if(!TextUtils.isEmpty(dev_nums)){
                        helper.setText(R.id.tv_dev_count,dev_nums+getString(R.string.str_dev_nums));
                    }else {
                        helper.setText(R.id.tv_dev_count,"0"+getString(R.string.str_dev_nums));
                    }
//                    JSONArray conditionList=jsonObject.optJSONArray(Constance.conditionPic);
//                    JSONArray actionList=jsonObject.optJSONArray(Constance.actionPic);
//
//                    ll_pic.removeAllViews();
//                    if(conditionList!=null&&conditionList.length()>0){
//                    for(int i=0;i<conditionList.length();i++){
//                        View view=View.inflate(getActivity(),R.layout.view_scene_auto,null);
//                        ImageView iv_img=view.findViewById(R.id.iv_img);
//                        String url=conditionList.getString(i);
//                        if(url.contains("http")){
//                            ImageLoader.getInstance().displayImage(url,iv_img);
//                        }else {
//                            try {
//                            iv_img.setImageResource(R.mipmap.icon_dingshi);
//                            }catch (Exception e){
//
//                            }
//                        }
//                        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(UIUtils.dip2PX(40),UIUtils.dip2PX(40));
//                        layoutParams.setMargins(0,0,15,0);
//                        layoutParams.gravity= Gravity.CENTER;
//                        view.setLayoutParams(layoutParams);
//                        ll_pic.addView(view);
//                    }
//                        FontIconView fontIconView=new FontIconView(getActivity());
//                        fontIconView.setText(getString(R.string.icon_arrow_right));
//                        fontIconView.setTextColor(Color.WHITE);
//                        fontIconView.setTextSize(UIUtils.dip2PX(12));
//                        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(UIUtils.dip2PX(40),UIUtils.dip2PX(40));
//                        layoutParams.setMargins(0,0,15,0);
//                        layoutParams.gravity= Gravity.CENTER;
//                        fontIconView.setLayoutParams(layoutParams);
//                        ll_pic.addView(fontIconView);
//                    }
//
//                    for(int i=0;i<actionList.length();i++){
//                            View view=View.inflate(getActivity(),R.layout.view_scene_auto,null);
//                            ImageView iv_img=view.findViewById(R.id.iv_img);
//                            String url=actionList.getString(i);
//                            if(url.contains("http")){
//                                ImageLoader.getInstance().displayImage(url,iv_img);
//                            }else {
//                                try {
//                                    iv_bg.setImageResource(Integer.parseInt(url));
//                                }catch (Exception e){
//
//                                }
//                            }
//                            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(UIUtils.dip2PX(40),UIUtils.dip2PX(40));
//                            layoutParams.setMargins(0,0,15,0);
//                            layoutParams.gravity= Gravity.CENTER;
//                            view.setLayoutParams(layoutParams);
//                            ll_pic.addView(view);
//                    }
                    ImageLoader.getInstance().displayImage(pic,iv_bg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                iv_color.setImageAlpha(217);
                ColorDrawable colorDrawable=new ColorDrawable();
                if(!TextUtils.isEmpty(item.getIconColor())){
                colorDrawable.setColor(Color.parseColor(item.getIconColor()));
                iv_color.setImageDrawable(colorDrawable);
                    Typeface font = Typeface.createFromAsset(context.getAssets(), "iconfont.ttf");
                    iv_img_auto.setTypeface(font);
                    iv_img_auto.setText(Html.fromHtml("&#x"+item.getIcon()+";"));
                    iv_img_auto.setTextColor(Color.parseColor(item.getIconColor()));
                }
                if(item.getEnable()){
                    helper.setBackgroundRes(R.id.tv_doit,R.mipmap.icon_scene_auto_on);
                }else {
                    helper.setBackgroundRes(R.id.tv_doit,R.mipmap.icon_scene_auto_off);
                }
                helper.setOnClickListener(R.id.tv_doit, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, Object> maps = new HashMap<>();
                        maps.put("sceneId",item.getId());
                        maps.put("enable",!item.getEnable());
                        if(!item.getEnable()){
                            helper.setBackgroundRes(R.id.tv_doit,R.mipmap.icon_scene_auto_on);
                        }else {
                            helper.setBackgroundRes(R.id.tv_doit,R.mipmap.icon_scene_auto_off);
                        }
                        ApiClientForIot.getIotClient("/living/scene/switch", "1.0.0", maps, new IoTCallback() {
                            @Override
                            public void onFailure(IoTRequest ioTRequest, Exception e) {

                            }

                            @Override
                            public void onResponse(IoTRequest ioTRequest, final IoTResponse ioTResponse) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(ioTResponse.getCode()!=200){
                                            MyToast.show(getActivity(),getString(R.string.str_setting_failed));
                                            return;
                                        }else {
                                            MyToast.show(getActivity(),getString(R.string.str_setting_success));
                                        }
                                        page=1;
                                        scenes=new ArrayList<>();
                                        getSceneList();
                                    }
                                });
                            }
                        });
                    }
                });
                helper.setOnClickListener(R.id.rl_setting, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        String code = "link://router/scene";
//                        Bundle bundle = new Bundle();
////                        bundle.putString("sceneType","ilop"); // 传入插件参数，没有参数则不需要这一行
//                        bundle.putString("sceneId",item.getId());
//                        Router.getInstance().toUrlForResult(getActivity(), code, 1, bundle);
                        Intent intent=new Intent(getActivity(),IotSceneAddActivity.class);
                        intent.putExtra(Constance.is_auto,true);
                        intent.putExtra(Constance.is_edit,true);
                        intent.putExtra(Constance.scene_id,item.getId());
                        intent.putExtra(Constance.scene_type,1);
                        startActivity(intent);
                    }
                });
            }
        };
        lv_scenes.setAdapter(adapter);
        tv_scene.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(currentP==0){
                    return;
                }
                currentP=0;
                tv_none_devices_tips.setText(getString(R.string.str_add_scene));
                view_scene.setVisibility(View.VISIBLE);
                view_auto.setVisibility(View.INVISIBLE);
                tv_scene.setTextColor(getResources().getColor(R.color.black));
                tv_scene.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
                tv_auto.setTextColor(getResources().getColor(R.color.tv_666666));
                tv_auto.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                lv_scenes.setNumColumns(2);
                lv_scenes.setAdapter(adapter);
//                adapter.replaceAll(scenes);
                page=1;
                scenes=new ArrayList<>();
                getSceneList();
            }
        });
        tv_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentP==1){
                    return;
                }
                currentP=1;
                tv_none_devices_tips.setText(getString(R.string.str_add_auto));
                view_scene.setVisibility(View.INVISIBLE);
                view_auto.setVisibility(View.VISIBLE);
                tv_auto.setTextColor(getResources().getColor(R.color.black));
                tv_auto.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
                tv_scene.setTextColor(getResources().getColor(R.color.tv_666666));
                tv_scene.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                lv_scenes.setNumColumns(1);
                lv_scenes.setAdapter(adapterB);
                page=1;
                scenes=new ArrayList<>();

//                adapterB.replaceAll(scenes);
                getSceneList();
            }
        });
        btn_add_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), IotSceneAddActivity.class);
                intent.putExtra(Constance.is_auto,currentP==1?true:false);
                startActivity(intent);
//                String code = "link://router/scene";
//                Bundle bundle = new Bundle();
//                bundle.putString("sceneType","ilop"); // 传入插件参数，没有参数则不需要这一行
//                Router.getInstance().toUrlForResult(getActivity(), code, 1, bundle);

            }
        });
        lv_scenes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                UIUtils.showSingleWordDialog(getActivity(), getString(R.string.str_delete_scene), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Map<String,Object> map=new HashMap<>();
                        map.put("sceneId",scenes.get(i).getId());
                        ApiClientForIot.getIotClient("/scene/delete", "1.0.2", map, new IoTCallback() {
                            @Override
                            public void onFailure(IoTRequest ioTRequest, Exception e) {

                            }

                            @Override
                            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                            if(ioTResponse.getCode()==200){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyToast.show(getActivity(),getString(R.string.str_delete_success));
                                    }
                                });
                                page=1;
                                scenes=new ArrayList<>();
                                getSceneList();
                            }
                            }
                        });
                    }
                });
                return true;
            }
        });
        lv_scenes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, Object> maps = new HashMap<>();
                maps.put("sceneId",scenes.get(i).getId());
                IoTRequestBuilder builder = new IoTRequestBuilder()
                        .setPath("/scene/fire")
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
                    public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {

                        final int code=ioTResponse.getCode();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(code!=200){
                                    Toast.makeText(getActivity(), getString(R.string.str_excute_failed), Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getActivity(), getString(R.string.str_excute_success), Toast.LENGTH_SHORT).show();
                                    page=1;
                                    scenes=new ArrayList<>();
                                    getSceneList();
                                }
                            }
                        });
                    }
                });
        }});
//        iv_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////            addDevice();
//
////                String code = "link://router/scene";
////                Bundle bundle = new Bundle();
////                bundle.putString("sceneType","ilop"); // 传入插件参数，没有参数则不需要这一行
////                Router.getInstance().toUrlForResult(getActivity(), code, 1, bundle);
//
//                Intent intent=new Intent(getActivity(), IotSceneAddActivity.class);
//                startActivity(intent);
//
//            }
//        });
        page=1;
        scenes=new ArrayList<>();
        getSceneList();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//    }

    private void getSceneList() {

        Map<String, Object> maps = new HashMap<>();
        maps.put("pageNo",page);
        maps.put("pageSize","20");
        maps.put("groupId",currentP==0?"0":"1");
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pullToRefresh.setRefreshing(false);
                }
            });
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pullToRefresh.setRefreshing(false);
                    }
                });
                final int code = response.getCode();
                final String msg = response.getMessage();
                if (code != 200){
                    isBottom=true;
                    return;
                }

                Object data = response.getData();
                if(data!=null){
                ScenesBean scenesBean=new Gson().fromJson(data.toString(),ScenesBean.class);
                List<Scenes>temp=scenesBean.getScenes();
                if(temp==null||temp.size()==0){
                    isBottom = true;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(scenes==null||scenes.size()==0){
                                pullToRefresh.setVisibility(View.GONE);
                            }else {
                                pullToRefresh.setVisibility(View.VISIBLE);
                            }
//                            if(currentP==0){
//                            adapter.replaceAll(scenes);
//                            }else {
//                            adapterB.replaceAll(scenes);
//                            }
                        }
                    });
                    return;
                }
                    if(scenesBean.getPageNo()==1){
                        scenes =temp;
                    }else {
                        scenes.addAll(temp);
                    }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(scenes==null||scenes.size()==0){
                         pullToRefresh.setVisibility(View.GONE);
                        }else {
                            pullToRefresh.setVisibility(View.VISIBLE);
                        }
                        if(currentP==0){
                        adapter.replaceAll(scenes);
                        }else {
                        adapterB.replaceAll(scenes);
                        }
                    }
                });
                }else {
                    isBottom = true;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(scenes==null||scenes.size()==0){
                                pullToRefresh.setVisibility(View.GONE);
                            }else {
                                pullToRefresh.setVisibility(View.VISIBLE);
                            }
                            if(currentP==0){
                                adapter.replaceAll(scenes);
                            }else {
                                adapterB.replaceAll(scenes);
                            }
                        }
                    });
                }
//                LogUtils.logE("sceneList",""+data.toString());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void addDevice() {
        final List<AccountDevDTO>  accountDevDTOS = new ArrayList<>();
        Map<String, Object> maps = new HashMap<>();
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/uc/listByAccount")
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
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                ALog.d(TAG, "onResponse listByAccount");
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(temp!=null){
//                            temp.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    temp.setRefreshing(false);
//                                }
//                            });
//                        }
//                    }
//                });
                final int code = response.getCode();
                final String msg = response.getMessage();
                if (code != 200){
                    return;
                }

                Object data = response.getData();
                if (null != data) {
                    if(data instanceof JSONArray){
//                        List<JSONObject >mDeviceList = parseDeviceListFromSever((JSONArray) data);
                        Intent intent=new Intent(getActivity(), AddDeviceActivity.class);
                        intent.putExtra("bundle",mBundle);
                        startActivity(intent);
//                        if(mDeviceList==null||mDeviceList.size()==0){
////                            mHandler.sendEmptyMessage(1);
//                            return;
//                        }
//                        LogUtils.logE("mDevices",mDeviceList.toString());
//                        for(int i=0;i<mDeviceList.size();i++){
//                            try {
//                                if(!mDeviceList.get(i).getString(Constance.type).equals("虚拟")){
//                                    accountDevDTOS.add(new Gson().fromJson(mDeviceList.get(i).toString(), AccountDevDTO.class));
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        for(int i=0;i<accountDevDTOS.size();i++){
//                            for(int j=0;j<accountDevDTOS.size();j++){
//                                if(i!=j&&accountDevDTOS.get(i).getIotId().equals(accountDevDTOS.get(j).getIotId())){
//                                    accountDevDTOS.remove(j);
//                                    if(j!=0)j--;
//                                }
//                            }
//                        }
//
//                        mHandler.sendEmptyMessage(0);
//                        String[] pks = {"a1IjeL0MqPS", "a1AzoSi5TMc", "a1nZ7Kq7AG1", "a1XoFUJWkPr"};
//                        if (mDeviceList.size() == 0 || virturlDeviceCount < pks.length - 1){
//                            if (mRegisterCount > 0){
//                                return;
//                            }
//                            //注册虚拟设备
//
////                            for (String pk : pks) {
//////                                registerVirtualDevice(pk);
////                            }
//                        }else {
//                            mHandler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    initDevicePanel();
//                                }
//                            });
//                        }
                    }
                }

            }
        });
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
        mBundle.putStringArrayList("deviceList", deviceStrList);
        return arrayList;
    }
    @Override
    protected void initData() {

    }


    @Override
    public void onEndOfList(Object lastItem) {
        if(page==1&&scenes.size()==0||isBottom){
            return;
        }
        page++;
        getSceneList();
    }
}
