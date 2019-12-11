package com.juhao.home.ui;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.BaseActivity;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.bean.NoticeBean;
import com.bean.ShareDeviceBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.juhao.home.adapter.BaseAdapterHelper;
import com.juhao.home.adapter.QuickAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.util.ApiClientForIot;
import com.util.Constance;
import com.util.DateUtils;
import com.util.LogUtils;
import com.view.EndOfListView;
import com.view.MyToast;
import com.view.PMSwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoticeActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private int position;
    private TextView tv_message;
//    private TextView tv_share;
    private TextView tv_notice;
    String messageType="share";
    private int page=1;
    int pageSize=20;
    private PMSwipeRefreshLayout pullToRefresh;
    private EndOfListView lv_devices;
    private QuickAdapter<ShareDeviceBean> shareDeviceBeanQuickAdapter;
    private List<ShareDeviceBean> shareDeviceBeanList;
    private List<NoticeBean> noticeBeans;
    private QuickAdapter<NoticeBean> adapterNotice;

    @Override
    protected void InitDataView() {
        getMessageRecord();
    }

    private void getMessageRecord() {
        lv_devices.setAdapter(adapterNotice);
        Map<String,Object> map=new HashMap<>();
        Map<String,Object> map2=new HashMap<>();
        map.put("messageType",messageType);
        map.put("type","NOTICE");
        map.put("pageNo",page);
        map.put("pageSize",pageSize);
        map2.put("requestDTO",map);
        ApiClientForIot.getIotClient("/message/center/record/query", "1.0.6", map2, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                LogUtils.logE("record",e.toString());

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pullToRefresh.setRefreshing(false);
                    }
                });
                if(ioTResponse.getCode()==200){
                JSONObject jsonObject= (JSONObject) ioTResponse.getData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pullToRefresh.setVisibility(View.GONE);
                    }
                });
                if(jsonObject!=null){
                    try {
                        JSONArray array=jsonObject.getJSONArray(Constance.data);
                        if(array!=null&&array.length()>0){
                            noticeBeans=new Gson().fromJson(array.toString(),new TypeToken<List<NoticeBean>>(){}.getType());
                            if(TextUtils.isEmpty(messageType)){
                                for(int i=0;i<noticeBeans.size();i++){
                                    if(noticeBeans.get(i).getMessageType().equals("announcement")){
                                        noticeBeans.remove(i);
                                        i--;
                                    }
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapterNotice.replaceAll(noticeBeans);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(noticeBeans!=null&&noticeBeans.size()>0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pullToRefresh.setVisibility(View.VISIBLE);
                        }
                    });
                }
                }
            LogUtils.logE("recordcenter",ioTResponse.getData()+","+ioTResponse.getCode()+","+ioTResponse.getMessage());
            }
        });

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_notice);
        tv_message = findViewById(R.id.tv_device);
//        tv_share = findViewById(R.id.tv_share);
        tv_notice = findViewById(R.id.tv_notice);
        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(this);
        lv_devices = findViewById(R.id.lv_devices);
        shareDeviceBeanQuickAdapter = new QuickAdapter<ShareDeviceBean>(this,R.layout.item_share_devices) {
            @Override
            protected void convert(BaseAdapterHelper helper, ShareDeviceBean item) {
            helper.setText(R.id.tv_name,item.getProductName());
            helper.setText(R.id.tv_desc,item.getDescription());
            helper.setText(R.id.tv_time, DateUtils.getStrTime(item.getGmtModified()/1000L+""));
                ImageView iv_img=helper.getView(R.id.iv_img);
                iv_img.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(item.getProductImage(),iv_img);
            }
        };
        adapterNotice = new QuickAdapter<NoticeBean>(this,R.layout.item_share_devices) {
            @Override
            protected void convert(BaseAdapterHelper helper, NoticeBean item) {
                helper.setText(R.id.tv_name,item.getTitle());
                helper.setText(R.id.tv_desc,item.getBody());
                helper.setText(R.id.tv_time, DateUtils.getStrTime(item.getGmtModified()/1000L+""));
                ImageView iv_img=helper.getView(R.id.iv_img);
                iv_img.setVisibility(View.GONE);
//                ImageLoader.getInstance().displayImage(item.getProductImage(),iv_img);
            }
        };
        lv_devices.setAdapter(shareDeviceBeanQuickAdapter);
        shareDeviceBeanList = new ArrayList<>();
        noticeBeans = new ArrayList<>();
        lv_devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if(position==1) {
                    if (shareDeviceBeanList.get(i).getStatus() == -1) {
                        if(shareDeviceBeanList.get(i).getIsReceiver()!=0) {
                            UIUtils.showSingleWordDialog(NoticeActivity.this, getString(R.string.str_agree_share), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Map<String, Object> map = new HashMap<>();
                                    List<String> list = new ArrayList<>();
                                    list.add(shareDeviceBeanList.get(i).getRecordId());
                                    map.put("recordIdList", list);
                                    map.put("agree", "1");
                                    ApiClientForIot.getIotClient("/uc/confirmShare", "1.0.2", map, new IoTCallback() {
                                        @Override
                                        public void onFailure(IoTRequest ioTRequest, Exception e) {

                                        }

                                        @Override
                                        public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                                            LogUtils.logE("confirm", ioTResponse.getData() + "" + "." + ioTResponse.getCode());
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    MyToast.show(NoticeActivity.this, getString(R.string.str_bind_success));
                                                    noticeBeans = new ArrayList<>();
                                                    shareDeviceBeanList = new ArrayList<>();
                                                    page = 1;
                                                    getMessageRecord2();
                                                }
                                            });
                                        }
                                    });
                                }
                            });

                        }else {
                            UIUtils.showSingleWordDialog(NoticeActivity.this, getString(R.string.str_cancel_share), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Map<String, Object> map = new HashMap<>();
                                    List<String> list = new ArrayList<>();
                                    list.add(shareDeviceBeanList.get(i).getRecordId());
                                    map.put("recordIdList", list);
//                                    map.put("agree", "1");
                                    ApiClientForIot.getIotClient("/uc/cancelShare", "1.0.2", map, new IoTCallback() {
                                        @Override
                                        public void onFailure(IoTRequest ioTRequest, Exception e) {

                                        }

                                        @Override
                                        public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                                            LogUtils.logE("confirm", ioTResponse.getData() + "" + "." + ioTResponse.getCode());
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    MyToast.show(NoticeActivity.this, getString(R.string.str_cancel_success));
                                                    noticeBeans = new ArrayList<>();
                                                    shareDeviceBeanList = new ArrayList<>();
                                                    page = 1;
                                                    getMessageRecord2();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    }
                }else {

                }
            }
        });


        tv_message.setOnClickListener(this);
//        tv_share.setOnClickListener(this);
        tv_notice.setOnClickListener(this);

    }

    @Override
    protected void initData() {
    }

    @Override
    public void onClick(View view) {
        page=1;
        shareDeviceBeanList=new ArrayList<>();
        noticeBeans=new ArrayList<>();
        switch (view.getId()){
            case R.id.tv_device:
                position=0;
                messageType="share";
                getMessageRecord();
                break;
            case R.id.tv_share:
                position=1;
                messageType="share";
                getMessageRecord2();
                break;
            case R.id.tv_notice:
                position=2;
                messageType="announcement";
                getMessageRecord();
                break;
        }
        pullToRefresh.setRefreshing(true);
        refreshUI();


    }

    private void getMessageRecord2() {
        lv_devices.setAdapter(shareDeviceBeanQuickAdapter);
        Map<String,Object> map=new HashMap<>();
        map.put("pageNo",page);
        map.put("pageSize",pageSize);
        ApiClientForIot.getIotClient("/uc/getShareNoticeList", "1.0.2", map, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                LogUtils.logE("shareNoticeList",ioTResponse.getData()+"");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pullToRefresh.setVisibility(View.GONE);
                    }
                });
                if(ioTResponse.getCode()==200&&ioTResponse.getData()!=null){
                    org.json.JSONObject jsonObject= (org.json.JSONObject) ioTResponse.getData();
                    org.json.JSONArray data= null;
                    try {
                        data = jsonObject.getJSONArray(Constance.data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    List<ShareDeviceBean> temp=new Gson().fromJson(data.toString(),new TypeToken<List<ShareDeviceBean>>(){}.getType());
                    if(temp!=null&temp.size()>0){
                        shareDeviceBeanList.addAll(temp);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pullToRefresh.setRefreshing(false);
                                shareDeviceBeanQuickAdapter.replaceAll(shareDeviceBeanList);
                            }
                        });
                    }
                }
                if(shareDeviceBeanList!=null&&shareDeviceBeanList.size()>0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        pullToRefresh.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }

    private void refreshUI() {
        tv_message.setBackgroundResource(0);
//        tv_share.setBackgroundResource(R.drawable.bg_theme_empty);
        tv_notice.setBackgroundResource(0);
        tv_message.setTextColor(getResources().getColor(R.color.tv_666666));
//        tv_share.setTextColor(getResources().getColor(R.color.theme));
        tv_notice.setTextColor(getResources().getColor(R.color.tv_666666));

        if(position==0){
            tv_message.setBackgroundResource(R.drawable.shape_bottom_line);
            tv_message.setTextColor(Color.BLACK);
        }else if(position==1){
//            tv_share.setBackgroundColor(getResources().getColor(R.color.theme));
//            tv_share.setTextColor(Color.WHITE);
        }else {
            tv_notice.setBackgroundResource(R.drawable.shape_bottom_line);
            tv_notice.setTextColor(Color.BLACK);
        }
    }

    @Override
    public void onRefresh() {
        page=1;
        shareDeviceBeanList=new ArrayList<>();
        if(position==1){
            getMessageRecord2();
        }else {
            getMessageRecord();
        }
    }
}
