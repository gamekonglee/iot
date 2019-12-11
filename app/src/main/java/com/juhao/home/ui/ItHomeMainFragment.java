package com.juhao.home.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.BaseFragment;
import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.login.data.UserInfo;
import com.aliyun.iot.demo.ipcview.activity.IPCameraActivity;
import com.aliyun.iot.demo.ipcview.manager.SharePreferenceManager;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.aliyun.iot.ilop.demo.page.ilopmain.AddDeviceActivity;
import com.aliyun.iot.ilop.demo.page.ilopmain.MainActivity;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bean.AccountDevDTO;
import com.bean.Scenes;
import com.bean.ScenesBean;
import com.bean.WeatherBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.juhao.home.DragSortDevActivity;
import com.juhao.home.IssApplication;
import com.juhao.home.LoginIndexActivity;
import com.juhao.home.MyLoginActivity;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.juhao.home.adapter.BaseAdapterHelper;
import com.juhao.home.adapter.QuickAdapter;
import com.juhao.home.intelligence.DevicesControlActivity;
import com.net.ApiClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.util.ApiClientForIot;
import com.util.Constance;
import com.util.LogUtils;
import com.util.MyShare;
import com.view.EndOfGridView;
import com.view.EndOfListView;
import com.view.MyToast;
import com.view.PMSwipeRefreshLayout;
import com.view.TextViewPlus;
import com.zhy.http.okhttp.callback.Callback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import astuetz.MyPagerSlidingTabStrip;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/7/7.
 */

public class ItHomeMainFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, EndOfListView.OnEndOfListListener {

    private static final String TAG = "ItHomeMainFragment";
    private MyPagerSlidingTabStrip tabs;
    private ViewPager vp_it;
    private String[] titles;
    private int virturlDeviceCount;
//    private ArrayList<JSONObject> mDeviceList;
    int mRegisterCount = 0;
    private QuickAdapter<AccountDevDTO> accountDevDTOQuickAdapter;
    private List<AccountDevDTO> accountDevDTOS;
    private ListView lv_it;
    private List<QuickAdapter> adapters;
    private ImageView iv_add;
    private List<ListView> listViews;
    private int currentPosition;
    private LinearLayout ll_none_device;
    private List<View> views;
    private LinearLayout ll_cmd;
    private TextView tv_temp_outside;
    private TextView tv_pm_outside;
//    private List<PMSwipeRefreshLayout> pms;
    private RelativeLayout rl_hj;
    private RelativeLayout rl_lj;
    private RelativeLayout rl_qc;
    private RelativeLayout rl_sj;
    private RelativeLayout rl_xx;
    private RelativeLayout rl_jc;
    private int status;
    private String iotId;
    private TextView tv_home;
    private int page=1;
    private EndOfGridView lv_devices;
    private PMSwipeRefreshLayout pullToRefresh;
    private View ll_weather;
    private View ll_weather_2;
    private RelativeLayout rl_setting;
    private int column=1;
    private LinearLayout ll_listview;
    private HorizontalScrollView hsv_scene_home;
    private LinearLayout ll_scene_home;
    private MainActivity mainActivity;
    private Mybc mybc;
    private View mViewContent;
    private boolean hasData;

    @Override
    protected void initController() {

    }

    @Override
    protected void initViewData() {

        int  currenColumn=MyShare.get(getActivity()).getInt(Constance.column);
        lv_devices.setNumColumns(currenColumn);
//        accountDevDTOQuickAdapter.notifyDataSetChanged();
//        setListviewHeight();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mViewContent == null) {
            mViewContent = inflater.inflate(R.layout.frag_it_main_home, container, false);
        }

        // 缓存View判断是否含有parent, 如果有需要从parent删除, 否则发生已有parent的错误.
        ViewGroup parent = (ViewGroup) mViewContent.getParent();
        if (parent != null) {
            parent.removeView(mViewContent);
        }

        if(!DemoApplication.is_created_fragment){
        mybc = new Mybc();
        IntentFilter intentFilter=new IntentFilter("recevie.login.status");
        getActivity().getApplicationContext().registerReceiver(mybc,intentFilter);
        DemoApplication.is_created_fragment=true;
        }
        return mViewContent;
    }
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            if(!hasData){
                listByAccount();
                mHandler.postDelayed(runnable,2000);
            }
        }
    };
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        LogUtils.logE("home","onViewStateRestored");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        LogUtils.logE("home","onSaveInstanceState");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtils.logE("home","onDetach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.logE("home","onDestroy");
        getActivity().getApplicationContext().unregisterReceiver(mybc);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    LogUtils.logE("home","onActivityCreated");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.logE("home","onDestroyView");
//        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING,sticky = true)
    public void listAccount(Message event) {
        if(event!=null){
            int userNick=event.arg1;
            LogUtils.logE("usernick",userNick+",");
        }
        LogUtils.logE("onevent_list","listbyaccount");
        //处理逻辑
        listByAccount();
    }
    class Mybc extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getStringExtra(Constance.msg) != null) {
//                getActivity().getApplicationContext().unregisterReceiver(mybc);
                LogUtils.logE("onevent_list", "listbyaccount");
//                DemoApplication.has_got=true;
                //处理逻辑
                mHandler.postDelayed(runnable,1000);
            }
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        mainActivity = (MainActivity) context;
//        mainActivity.setHandler(handler);
//    }

//    Handler handler=new Handler(){
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            super.handleMessage(msg);
//            LogUtils.logE("handleMessage","listbyaccount");
//            //处理逻辑
//            listByAccount();
//        }
//    };
    @Override
    protected void initView() {
        tabs = getView().findViewById(R.id.tabs);
        vp_it = getView().findViewById(R.id.vp_it);
        iv_add = getView().findViewById(R.id.iv_add);
        ll_cmd = getView().findViewById(R.id.ll_cmd);
        tv_temp_outside = getView().findViewById(R.id.tv_temp_outside);
        tv_pm_outside = getView().findViewById(R.id.tv_pm_outside);
        rl_hj = getView().findViewById(R.id.rl_hj);
        rl_lj = getView().findViewById(R.id.rl_lj);
        rl_qc = getView().findViewById(R.id.rl_qc);
        rl_sj = getView().findViewById(R.id.rl_sj);
        rl_xx = getView().findViewById(R.id.rl_xx);
        rl_jc = getView().findViewById(R.id.rl_jc);
        tv_home = getView().findViewById(R.id.tv_home);
        lv_devices = getView().findViewById(R.id.lv_devices);
        pullToRefresh = getView().findViewById(R.id.pullToRefresh);
        ll_none_device=getView().findViewById(R.id.ll_none_device);
        ll_weather = getView().findViewById(R.id.ll_weather);
        ll_weather_2 = getView().findViewById(R.id.ll_weather_2);
        rl_setting = getView().findViewById(R.id.rl_setting);
        ll_listview = getView().findViewById(R.id.ll_listview);
        hsv_scene_home = getView().findViewById(R.id.hsv_scene_home);
        ll_scene_home = getView().findViewById(R.id.ll_scene_home);

        hsv_scene_home.setVisibility(View.VISIBLE);
//        ll_scene_home.removeAllViews();
//        for (int i=0;i<4;i++){
//                View view=View.inflate(getActivity(),R.layout.view_scene_home,null);
//                ImageView iv_bg=view.findViewById(R.id.iv_bg);
//                ImageView iv_bg_color=view.findViewById(R.id.iv_bg_color);
//                TextView tv_icon=view.findViewById(R.id.tv_icon);
//                TextView tv_name=view.findViewById(R.id.tv_name);
//                String des="{\"bg_pic\":\"d2ed\",\""+Constance.showinhome+"\":1}";
//                try {
//                    JSONObject jsonObject=new JSONObject(des);
//                    String pic=jsonObject.getString(Constance.bg_pic);
//                    if(!TextUtils.isEmpty(pic)){
//                        ImageLoader.getInstance().displayImage(pic,iv_bg);
//                    }
//                    String iconColor="#e32a2b";
//                    ColorDrawable colorDrawable=new ColorDrawable();
//                    colorDrawable.setColor(Color.parseColor(iconColor));
//                    iv_bg_color.setImageDrawable(colorDrawable);
//                    Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "iconfont.ttf");
//                    tv_icon.setTypeface(font);
//                    tv_icon.setText(Html.fromHtml(getString(R.string.icon_grid_type)));
//                    tv_name.setText("钜豪智能");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    MyToast.show(getActivity(),""+e.getMessage());
//                }
//                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(UIUtils.dip2PX(160),UIUtils.dip2PX(65));
//                layoutParams.setMargins(UIUtils.dip2PX(0),0,15,0);
//                view.setLayoutParams(layoutParams);
//                ll_scene_home.addView(view);
//        }

        getView().findViewById(R.id.rl_add_device).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddDeviceActivity.class));
            }
        });

        pullToRefresh.setColorSchemeResources(R.color.theme,android.R.color.holo_green_light,android.R.color.holo_orange_light,android.R.color.holo_red_dark);
        pullToRefresh.setRefreshing(false);
        Button btn_add_device= ll_none_device.findViewById(R.id.btn_add_device);
        btn_add_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), AddDeviceActivity.class);
                intent.putExtra("bundle",mBundle);
                startActivity(intent);
            }
        });

        rl_hj.setOnClickListener(this);
        rl_lj.setOnClickListener(this);
        rl_qc.setOnClickListener(this);
        rl_sj.setOnClickListener(this);
        rl_xx.setOnClickListener(this);
        rl_jc.setOnClickListener(this);
        rl_setting.setOnClickListener(this);
        tv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog=UIUtils.showTopInDialog(getActivity(),R.layout.dialog_home,UIUtils.dip2PX(110));
                TextViewPlus tv_mate_setting =dialog.findViewById(R.id.tv_mate_setting);
                ListView lv_home_mates=dialog.findViewById(R.id.lv_home_mates);
                QuickAdapter<String > adapter=new QuickAdapter<String>(getActivity(),R.layout.item_home_mate) {
                    @Override
                    protected void convert(BaseAdapterHelper helper, String item) {

                    }
                };
                tv_mate_setting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    startActivity(new Intent(getActivity(),HomeMateListActivity.class));
                    }
                });
                lv_home_mates.setAdapter(adapter);
                List<String> homeMate=new ArrayList<>();
                homeMate.add(getString(R.string.str_my_home));
                adapter.replaceAll(homeMate);
            }
        });
        accountDevDTOQuickAdapter = new QuickAdapter<AccountDevDTO>(getActivity(), R.layout.item_home_dev) {
            @Override
            protected void convert(BaseAdapterHelper helper, AccountDevDTO item) {
                View lv=helper.getView(R.id.lv);
                View gv=helper.getView(R.id.gv);
                if(lv_devices.getNumColumns()==1){
                    helper.getView().setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,UIUtils.dip2PX(80)));
                    lv.setVisibility(View.VISIBLE);
                    gv.setVisibility(View.GONE);
                    View bg_view=helper.getView(R.id.rl_bg);
                    View view_2= helper.getView(R.id.view_2);
                    View rl_offline=helper.getView(R.id.rl_offline);

                    if(item.getStatus().equals("1")){
//                if(helper.getPosition()==0){
                        bg_view.setBackgroundResource(R.mipmap.bg_home_device);
                        view_2.setVisibility(View.GONE);
                        rl_offline.setVisibility(View.GONE);
                    }else {
                        rl_offline.setVisibility(View.VISIBLE);
                        bg_view.setBackgroundResource(0);
                        view_2.setVisibility(View.VISIBLE);
                        RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) bg_view.getLayoutParams();
                        layoutParams.leftMargin=UIUtils.dip2PX(5);
                        layoutParams.rightMargin=UIUtils.dip2PX(5);
//                    layoutParams.bottomMargin=UIUtils.dip2PX(5);
//                        layoutParams.topMargin=UIUtils.dip2PX(5);
                        bg_view.setLayoutParams(layoutParams);
                    }

                    helper.setText(R.id.tv_scene,getString(R.string.str_keting));
                    helper.setText(R.id.tv_status,item.getStatus().equals("1")?"在线":"离线");
                    int resId=R.mipmap.home_kg;

                    String productName=item.getNickName();
                    if(productName==null)productName=item.getProductName();
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
//                helper.setImageResource(R.id.iv_img,resId);
                    ImageView iv_img=helper.getView(R.id.iv_img);
                    ImageLoader.getInstance().displayImage(item.getCategoryImage(),iv_img);
                }else {
                    helper.getView().setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,UIUtils.dip2PX(105)));
                    //宫格显示
                    lv.setVisibility(View.GONE);
                    gv.setVisibility(View.VISIBLE);
                    View bg_view=helper.getView(R.id.ll_bg);
                    View view_2= helper.getView(R.id.view_22);
                    if(item.getStatus().equals("1")){
//                if(helper.getPosition()==0){

                        bg_view.setBackgroundResource(R.mipmap.bg_home_device);
                        view_2.setVisibility(View.GONE);
                    }else {
                        bg_view.setBackgroundResource(R.drawable.bg_corner_white_8);
                        view_2.setVisibility(View.VISIBLE);
                        RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) bg_view.getLayoutParams();
//                        layoutParams.leftMargin=UIUtils.dip2PX(5);
//                        layoutParams.rightMargin=UIUtils.dip2PX(5);
                        layoutParams.bottomMargin=UIUtils.dip2PX(5);
                        layoutParams.topMargin=UIUtils.dip2PX(5);
                        bg_view.setLayoutParams(layoutParams);
                    }

//                    helper.setText(R.id.tv_scene,getString(R.string.str_keting));
//                    helper.setText(R.id.tv_status,item.getStatus().equals("1")?"在线":"离线");
                    int resId=R.mipmap.home_kg;

                    String productName=item.getNickName();
                    if(productName==null)productName=item.getProductName();
                    if(productName==null)productName=item.getName();
                    helper.setText(R.id.tv_name2,productName);
//                    if(helper.getPosition()==0){
//                        helper.setText(R.id.tv_name2,"c测试测试测试测试测试测试测试测试");
//                    }
//                    if(productName!=null){
//                        if(productName.contains(getString(R.string.str_socket))){
//                            resId=R.mipmap.home_cz;
//                        }else if(productName.contains(getString(R.string.str_kaiguan))){
//                            resId=R.mipmap.home_kg;
//                        }else if(productName.contains(getString(R.string.str_light))){
//                            resId=R.mipmap.home_zm;
//                        }
//                    }
//                helper.setImageResource(R.id.iv_img,resId);
                    ImageView iv_img=helper.getView(R.id.iv_img2);
                    ImageLoader.getInstance().displayImage(item.getCategoryImage(),iv_img);

                }

            }
        };
//        ListView listView;
//        listView.addHeaderView();

        lv_devices.setOnCanRefreshListener(new EndOfGridView.OnCanRefreshListener() {
            @Override
            public void canRefresh(boolean refesh) {
                if(!refesh){
                    pullToRefresh.setEnabled(false);
                }else {
                    pullToRefresh.setEnabled(true);
                }
            }
        });
        lv_devices.setAdapter(accountDevDTOQuickAdapter);
        lv_devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(), DevicesControlActivity.class);
                if(accountDevDTOS==null||accountDevDTOS.size()<=position){
                    return;
                }
                Map<String,Object> map=new HashMap<>();
                map.put("iotId",accountDevDTOS.get(position).getIotId());
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
                intent.putExtra(Constance.iotId,accountDevDTOS.get(position).getIotId());
                String type=Constance.night;
                boolean isBlueTooth=false;
                if(accountDevDTOS.get(position).getProductName().contains("蓝牙")){
                    isBlueTooth=true;
                }else {
                    if(accountDevDTOS.get(position).getProductName().contains("开关")){
                        type=Constance.nightswitch;
                    }else if(accountDevDTOS.get(position).getProductName().contains("插座")){
                        type=Constance.socket;
                    }else if(accountDevDTOS.get(position).getProductName().contains("锁")){
                        String iot=accountDevDTOS.get(position).getIotId();
                        intent=new Intent(getActivity(),DeviceLockControlActivity.class);
                        IssApplication.ori= Configuration.ORIENTATION_PORTRAIT;
                        intent.putExtra(Constance.iotId,iot);
                        intent.putExtra(Constance.title,accountDevDTOS.get(position).getProductName());
                        startActivity(intent);
                        return;
                    }else if(accountDevDTOS.get(position).getProductName().contains("摄像头")){
                        String iot=accountDevDTOS.get(position).getIotId();
                        intent=new Intent(getActivity(),LivingPlayerActivity.class);
                        IssApplication.ori= Configuration.ORIENTATION_PORTRAIT;
                        intent.putExtra(Constance.iotId,iot);
                        intent.putExtra(Constance.title,accountDevDTOS.get(position).getProductName());
                        startActivity(intent);
//                        SharePreferenceManager.getInstance().init(getActivity());
//                        intent = new Intent(getActivity(), IPCameraActivity.class);
//                        intent.putExtra("iotId", iot);
//                        intent.putExtra("appKey", DemoApplication.app_key);
                        startActivity(intent);

                        return;
                    }
                }
                String code = "link://router/"+accountDevDTOS.get(position).getProductKey();
                Bundle bundle = new Bundle();
                bundle.putString("iotId", accountDevDTOS.get(position).getIotId());
                Router.getInstance().toUrlForResult(getActivity(), code,1,bundle);
            }
        });
        lv_devices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                UIUtils.showSingleWordDialog(getActivity(), getString(R.string.str_unbind_device), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, Object> maps = new HashMap<>();
                        maps.put("iotId",accountDevDTOS.get(position).getIotId());
                        IoTRequestBuilder builder = new IoTRequestBuilder()
                                .setPath("/uc/unbindAccountAndDev")
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
                                Log.e("unbindAccountAndDev",ioTResponse.toString());
                                listByAccount();
                            }
                        });
                    }
                });

                return true;
            }
        });
        pullToRefresh.setOnRefreshListener(this);
        lv_devices.setOnEndOfListListener(this);
//        iv_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        vp_it.setAdapter(new MyPagerAdapter());
        tabs.defaultColor=getActivity().getResources().getColor(R.color.txt_black);
        tabs.selectColor=getActivity().getResources().getColor(R.color.txt_black);
        tabs.setUnderlineColor(Color.TRANSPARENT);
        tabs.setIndicatorColor(getActivity().getResources().getColor(R.color.theme));
        tabs.setDividerColor(Color.TRANSPARENT);
        tabs.setViewPager(vp_it);
        accountDevDTOS=new ArrayList<>();
//        adapters = new ArrayList<>();
//        listViews = new ArrayList<>();
//        views = new ArrayList<>();
//        pms = new ArrayList<>();
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                currentPosition = position;
//                if(position<adapters.size()){
//                    adapters.get(position).replaceAll(accountDevDTOS);
//                }
                if(accountDevDTOS==null||accountDevDTOS.size()==0){
                    mHandler.sendEmptyMessage(1);
                }else {
                    mHandler.sendEmptyMessage(0);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if(!EventBus.getDefault().isRegistered(this)){
        EventBus.getDefault().register(this);
        }
//        listByAccount();
//        if (LoginBusiness.isLogin()){
//            LogUtils.logE("listByAccount","login");
////            PgyCrashManager.reportCaughtException(getActivity(),new Exception("listbyaccount"));
////            bindmqtt();
//        }
        //打开Log 输出
        ALog.setLevel(ALog.LEVEL_DEBUG);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},100);
        }else {
            initWeather();
        }
    }
//    private void closeAndroidPDialog() {
//        try {
//            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
//            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
//            declaredConstructor.setAccessible(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            Class cls = Class.forName("android.app.ActivityThread");
//            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
//            declaredMethod.setAccessible(true);
//            Object activityThread = declaredMethod.invoke(null);
//            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
//            mHiddenApiWarningShown.setAccessible(true);
//            mHiddenApiWarningShown.setBoolean(activityThread, true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100&&grantResults!=null&&grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
            initWeather();
        }
    }
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private void initWeather() {
//BDAbstractLocationListener为7.2版本新增的Abstract类型的监听接口
//原有BDLocationListener接口暂时同步保留。具体介绍请参考后文中的说明
        mLocationClient = new LocationClient(getActivity());
            //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
            //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
//可选，是否需要地址信息，默认为不需要，即参数为false
//如果开发者需要获得当前点的地址信息，此处必须为true
        mLocationClient.setLocOption(option);
//mLocationClient为第二步初始化过的LocationClient对象
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
//更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        mLocationClient.start();
    }


    @Override
    protected void initData() {
        titles = new String[]{getString(R.string.str_devices_all)};
    }
    private Bundle mBundle = new Bundle();


    @Override
    public void onClick(View v) {
//        if(status!=1){
//            MyToast.show(getActivity(),getString(R.string.str_device_offline));
//            return;
//        }
        switch (v.getId()){

            case R.id.rl_hj:
                changeNight(Constance.ColorTemperature,4000);
                break;
            case R.id.rl_lj:
                changeNight(Constance.LightSwitch,0);
                break;
            case R.id.rl_qc:
                changeNight(Constance.ColorTemperature,5000);
                break;
            case R.id.rl_sj:
                changeNight(Constance.LightSwitch,0);
                break;
            case R.id.rl_xx:
                changeNight(Constance.ColorTemperature,1000);
                break;
            case R.id.rl_jc:
                changeNight(Constance.ColorTemperature,7000);
                break;
            case R.id.rl_setting:
                View popup_layout=View.inflate(getActivity(),R.layout.popup_home_list_type,null);
                final PopupWindow popupWindow=new PopupWindow(popup_layout, UIUtils.dip2PX(145),UIUtils.dip2PX(95));
//                popupWindow.setContentView(popup_layout);
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(true);
                View contentView=popupWindow.getContentView();
                final TextView tv_grid=contentView.findViewById(R.id.tv_grid);
                TextView tv_list=contentView.findViewById(R.id.tv_list);
//                TextView tv_icon_grid=contentView.findViewById(R.id.tv_icon_grid);
//                Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "iconfont.ttf");
//                tv_icon_grid.setTypeface(font);
//                tv_icon_grid.setText(getResources().getString(R.string.icon_grid_type));
                int  currenColumn=lv_devices.getNumColumns();
                if(currenColumn==1){
                    tv_grid.setText(getString(R.string.icon_grid_type));
                }else {
                    tv_grid.setText(getString(R.string.icon_listview_type));
                }
                tv_grid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int  currenColumn=lv_devices.getNumColumns();
                        if(currenColumn==2){
                            column=  1;
//                            tv_grid.setText(getString(R.string.icon_grid_type));
                        }else {
                            column = 2;
//                            tv_grid.setText(getString(R.string.icon_listview_type));
                        }
                        popupWindow.dismiss();

                        lv_devices.setNumColumns(column);
                        accountDevDTOQuickAdapter.notifyDataSetChanged();
                        MyShare.get(getActivity()).putInt(Constance.column,column);
//                        UIUtils.initGridViewHeight(lv_devices,column);
//                        ll_listview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,UIUtils.initGridViewHeight(lv_devices,column)+100));
                        setListviewHeight();
                    }
                });
                tv_list.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                        startActivity(new Intent(getActivity(), DragSortDevActivity.class));
//                        lv_devices.setNumColumns(column);
//                        accountDevDTOQuickAdapter.notifyDataSetChanged();
////                        UIUtils.initGridViewHeight(lv_devices,column);
////                        ll_listview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,UIUtils.initGridViewHeight(lv_devices,column)+100));
//                        setListviewHeight();
//                        pullToRefresh.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,UIUtils.initGridViewHeight(lv_devices,column)+100));
                    }
                });
//                popupWindow.getContentView().findViewById(R.id.tv_stream_quailty_0).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        tv_stream_quailty.setText("流畅");
//                        popupWindow.dismiss();
//                        setProperties("StreamVideoQuality",0);
//                    }
//                });
//                popupWindow.getContentView().findViewById(R.id.tv_stream_quailty_1).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        tv_stream_quailty.setText("标清");
//                        popupWindow.dismiss();
//                        setProperties("StreamVideoQuality",1);
//                    }
//                });
//                popupWindow.getContentView().findViewById(R.id.tv_stream_quailty_2).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        tv_stream_quailty.setText("高清");
//                        popupWindow.dismiss();
//                        setProperties("StreamVideoQuality",2);
//                    }
//                });
//
                int[] location=new int[2];
                rl_setting.getLocationOnScreen(location);
                popupWindow.showAtLocation(rl_setting, Gravity.RIGHT|Gravity.TOP,UIUtils.dip2PX(15),location[1]+UIUtils.dip2PX(50));
                break;
        }
    }

    private void setListviewHeight() {
        int[] locations=new int[2];
        if(column==1){
            ll_listview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,UIUtils.dip2PX(80)*(accountDevDTOS.size()<5?5:accountDevDTOS.size())+30));
        }else {
            ll_listview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,UIUtils.dip2PX(105)*((accountDevDTOS.size()+1)<6?6:(accountDevDTOS.size()+1))/2));
        }
    }

    @Override
    public void onRefresh() {
            page=1;
            accountDevDTOS=new ArrayList<>();
            listByAccount();
    }

    @Override
    public void onEndOfList(Object lastItem) {

    }

    class MyPagerAdapter extends PagerAdapter {

        private View view;

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if(view==null)
            view = View.inflate(getActivity(), R.layout.view_it_home,null);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container,position,object);
            container.removeView((View) object);
        }
    }

    private void listByAccount(){
        getSceneList();
        accountDevDTOS = new ArrayList<>();
        Map<String, Object> maps = new HashMap<>();
        maps.put("pageSize","20");
        maps.put("pageNo", page);
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/uc/listBindingByAccount")
                .setApiVersion("1.0.2")
                .setAuthType("iotAuth")
                .setParams(maps);
//        IoTRequestBuilder builder = new IoTRequestBuilder()
//                .setPath("/uc/listByAccount")
//                .setApiVersion("1.0.0")
//                .setAuthType("iotAuth")
//                .setParams(maps);
        IoTRequest request = builder.build();

        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                ALog.d(TAG, "onFailure");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(pullToRefresh!=null){
                            pullToRefresh.post(new Runnable() {
                                @Override
                                public void run() {
                                    pullToRefresh.setRefreshing(false);
                                }
                            });
                        }
                    }
                });

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                ALog.d(TAG, "onResponse listByAccount");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(pullToRefresh!=null){
                            pullToRefresh.post(new Runnable() {
                                @Override
                                public void run() {
                                    pullToRefresh.setRefreshing(false);
                                }
                            });
                        }
                    }
                });
                final int code = response.getCode();
                if (code != 200){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    Toast.makeText(getActivity(), "code = " +code + " msg =" + msg, Toast.LENGTH_SHORT).show();
                                    if(code==401){
                                        LoginBusiness.logout(new ILogoutCallback() {
                                            @Override
                                            public void onLogoutSuccess() {
                                                startActivity(new Intent(getActivity(), LoginIndexActivity.class));
                                            }

                                            @Override
                                            public void onLogoutFailed(int i, String s) {

                                            }
                                        });
//                                getActivity().finish();
                                    }
                                }
                            });
                    return;
                }
                hasData = true;
                mHandler.removeCallbacksAndMessages(null);
                Object data = response.getData();
                if (null != data) {
                    if(data instanceof JSONObject){
                        JSONObject result= (JSONObject) data;
                        JSONArray listData= null;
                        try {
                            listData = result.getJSONArray(Constance.data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        mDeviceList = parseDeviceListFromSever((JSONArray) data);
                        accountDevDTOS=new Gson().fromJson(((JSONArray)listData).toString(),new TypeToken<List<AccountDevDTO>>(){}.getType());
                        if(accountDevDTOS==null||accountDevDTOS.size()==0){
                        mHandler.sendEmptyMessage(1);
                        return;
                        }
                        LogUtils.logE("mDevices",accountDevDTOS.get(0).toString());
//                        for(int i=0;i<mDeviceList.size();i++){
//                            try {
//                                if(!mDeviceList.get(i).getString(Constance.type).equals("虚拟")){
//                                accountDevDTOS.add(new Gson().fromJson(mDeviceList.get(i).toString(),AccountDevDTO.class));
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
                        for(int i=0;i<accountDevDTOS.size();i++){
                            for(int j=0;j<accountDevDTOS.size();j++){
                            if(i!=j&&accountDevDTOS.get(i).getIotId().equals(accountDevDTOS.get(j).getIotId())||accountDevDTOS.get(j).getName()!=null&&accountDevDTOS.get(j).getName().contains("蓝牙")){
                                accountDevDTOS.remove(j);
                                if(j!=0)j--;
                            }
                            }
                        }
//                        for (int i=0;i<20;i++){
//                            AccountDevDTO accountDevDTO=new AccountDevDTO();
//                            accountDevDTO.setName(getString(R.string.str_blue_light));
//                            accountDevDTO.setDeviceName(getString(R.string.str_blue_light));
//                            accountDevDTO.setIotId("0");
//                            accountDevDTO.setStatus("1");
//                            accountDevDTO.setProductKey("aaa123");
//                            accountDevDTOS.add(accountDevDTO);
//                        }
//                        int bluetoot_light_count=MyShare.get(getActivity()).getInt(Constance.BLUETOOTH_LIGHT_COUNT);
//                        if(bluetoot_light_count>0){
//                            AccountDevDTO accountDevDTO=new AccountDevDTO();
//                            accountDevDTO.setName(getString(R.string.str_blue_light));
//                            accountDevDTO.setDeviceName(getString(R.string.str_blue_light));
//                            accountDevDTO.setIotId("0");
//                            accountDevDTO.setStatus("1");
//                            accountDevDTO.setProductKey("aaa123");
//                            accountDevDTOS.add(accountDevDTO);
//                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lv_devices.setVisibility(View.VISIBLE);
                                pullToRefresh.setVisibility(View.VISIBLE);
                                accountDevDTOQuickAdapter.replaceAll(accountDevDTOS);

//                                pullToRefresh.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,UIUtils.dip2PX(90*accountDevDTOS.size())));

//                                pullToRefresh.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,UIUtils.initGridViewHeight(lv_devices,lv_devices.getNumColumns())+100));
//                                UIUtils.initGridViewHeight(lv_devices,column);
//                                ll_listview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,UIUtils.initGridViewHeight(lv_devices,column)+100));
                                setListviewHeight();
                                ll_none_device.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }
        });
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
                    return;
                }

                Object data = response.getData();
                if(data!=null){
                    ScenesBean scenesBean=new Gson().fromJson(data.toString(),ScenesBean.class);

                    final List<Scenes>temp=scenesBean.getScenes();
                    LogUtils.logE("scenes",data.toString());
                    if(!data.toString().contains("showinhome:1")&&!data.toString().contains("\\\"showinhome\\\":\\\"1\\\"")){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hsv_scene_home.setVisibility(View.GONE);
                            }
                        });
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hsv_scene_home.setVisibility(View.VISIBLE);
                            ll_scene_home.removeAllViews();
                            for (int i=0;i<temp.size();i++){
//                                LogUtils.logE("desc",temp.get(i).getDescription());
                                if(temp.get(i).getDescription().contains("\"showinhome\":\"1\"")){
                                    View view=View.inflate(getActivity(),R.layout.view_scene_home,null);
                                    ImageView iv_bg=view.findViewById(R.id.iv_bg);
                                    ImageView iv_bg_color=view.findViewById(R.id.iv_bg_color);
                                    TextView tv_icon=view.findViewById(R.id.tv_icon);
                                    TextView tv_name=view.findViewById(R.id.tv_name);
                                    String des=temp.get(i).getDescription();
                                    try {
                                        JSONObject jsonObject=new JSONObject(des);
                                        String pic=jsonObject.getString(Constance.bg_pic);
                                        if(!TextUtils.isEmpty(pic)){
                                            ImageLoader.getInstance().displayImage(pic,iv_bg);
                                        }
                                        String iconColor=temp.get(i).getIconColor();
                                        ColorDrawable colorDrawable=new ColorDrawable();
                                        colorDrawable.setColor(Color.parseColor(iconColor));
                                        iv_bg_color.setImageDrawable(colorDrawable);
                                        iv_bg_color.setImageAlpha(217);
                                        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "iconfont.ttf");
                                        tv_icon.setTypeface(font);
                                        tv_icon.setText(Html.fromHtml("&#x"+temp.get(i).getIcon()+";"));
                                        tv_icon.setTextColor(Color.WHITE);
                                        tv_name.setText(temp.get(i).getName());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(UIUtils.dip2PX(160),UIUtils.dip2PX(65));
                                    layoutParams.setMargins(UIUtils.dip2PX(0),0,15,0);
                                    view.setLayoutParams(layoutParams);
                                    ll_scene_home.addView(view);
                                    final int finalI = i;
                                    view.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Map<String, Object> maps = new HashMap<>();
                                            maps.put("sceneId",temp.get(finalI).getId()+"");
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
//                                                                scenes=new ArrayList<>();
//                                                                getSceneList();
                                                            }
                                                        }
                                                    });
                                                }
                                            });

                                        }
                                    });
                                }
                            }

                        }
                    });
                }else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hsv_scene_home.setVisibility(View.GONE);
                        }
                    });
                }
//                LogUtils.logE("sceneList",""+data.toString());
            }
        });

    }


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
             lv_devices.setVisibility(View.GONE);
//             pullToRefresh.setVisibility(View.GONE);
             ll_none_device.setVisibility(View.VISIBLE);
             ll_cmd.setVisibility(View.GONE);
            }else if(msg.what==0){
            lv_devices.setVisibility(View.VISIBLE);
//            pullToRefresh.setVisibility(View.VISIBLE);
                accountDevDTOQuickAdapter.replaceAll(accountDevDTOS);
//                pullToRefresh.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,UIUtils.dip2PX(90*accountDevDTOS.size())));
//                pullToRefresh.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,UIUtils.initGridViewHeight(lv_devices,lv_devices.getNumColumns())+100));
//                ll_listview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,UIUtils.initGridViewHeight(lv_devices,column)+100));
                setListviewHeight();
//                UIUtils.initGridViewHeight(lv_devices,column);
            ll_none_device.setVisibility(View.GONE);
            }else if(msg.what==3){
                MyToast.show(getActivity(),getString(R.string.str_setting_success));
            }
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        accountDevDTOS=new ArrayList<>();
        page=1;
        listByAccount();
//        try {
//            InputStream configureInputStream = getResources().getAssets().open("sdk_config.json");
//            readFile(configureInputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取地址相关的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
            Log.e("district",district);
            mLocationClient.stop();
            ApiClient.getWeather(district,new Callback<String>() {
                @Override
                public String parseNetworkResponse(Response response, int id) throws Exception {
                    return null;
                }

                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public String onResponse(String response, int id) {
                    Log.e("onWeather_response",response);
                    try {

                        WeatherBean weatherBean=new Gson().fromJson(response,WeatherBean.class);
                        String temp=weatherBean.getResults().get(0).getWeather_data().get(0).getDate();
                        int index=temp.indexOf("实时")+3;
                        temp=temp.substring(index,temp.length()-1);
                        String pm25=weatherBean.getResults().get(0).getPm25();
                        tv_temp_outside.setText(temp+"");
                        tv_pm_outside.setText(""+pm25);
                        ll_weather.setVisibility(View.VISIBLE);
                        ll_weather_2.setVisibility(View.VISIBLE);
                    }catch (Exception e){
                    }
                    return null;
                }
            });

        }
    }

//    /**
//     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
//     */
//    public static int dip2px(Context context, float dpValue) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (dpValue * scale+0.5f);
//    }
    private void changeNight(String identify, Object value) {
        Map<String, Object> maps = new HashMap<>();
        if(TextUtils.isEmpty(iotId))return;
        maps.put("iotId", iotId);
        com.alibaba.fastjson.JSONObject jsonObject=new com.alibaba.fastjson.JSONObject();
        jsonObject.put(identify,value);
        maps.put("items",jsonObject);
        IoTRequestBuilder ioTRequestBuilder = new IoTRequestBuilder()
                .setPath("/thing/properties/set")
                .setApiVersion("1.0.2")
                .setAuthType("iotAuth")
                .setParams(maps);
        IoTRequest request = ioTRequestBuilder.build();
        IoTAPIClient client = new IoTAPIClientFactory().getClient();
        client.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                LogUtils.logE("onResponse", ioTResponse.getCode() + "," + ioTResponse.getMessage() + "," + ioTResponse.getData());
                if(ioTResponse.getCode()==200){

                    mHandler.sendEmptyMessage(3);
//                    isOpen=!isOpen;
//                    handler.sendEmptyMessage(1);
                }
//                final ProgressDialog progressDialog=ProgressDialog.show(DevicesControlActivity.this,"请求中","");
//                new Thread(){
//                    @Override
//                    public void run() {
//                        super.run();
//                        SystemClock.sleep(1000);
//                        initStatus();
//                        progressDialog.dismiss();
//
//                    }
//                }.start();

            }
        });
    }
}
