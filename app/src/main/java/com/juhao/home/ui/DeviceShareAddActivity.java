package com.juhao.home.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.BaseActivity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.bean.AccountDevDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.juhao.home.MyLoginActivity;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.juhao.home.adapter.BaseAdapterHelper;
import com.juhao.home.adapter.QuickAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.util.ApiClientForIot;
import com.util.Constance;
import com.util.DateUtils;
import com.util.LogUtils;
import com.util.MyShare;
import com.view.MyToast;
import com.yjn.swipelistview.swipelistviewlibrary.widget.SwipeMenu;
import com.yjn.swipelistview.swipelistviewlibrary.widget.SwipeMenuCreator;
import com.yjn.swipelistview.swipelistviewlibrary.widget.SwipeMenuItem;
import com.yjn.swipelistview.swipelistviewlibrary.widget.SwipeMenuListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceShareAddActivity extends BaseActivity {

    private TextView tv_title;
    private TextView tv_share_add;
    private SwipeMenuListView lv_devices_sharers;
    private AccountDevDTO accountDevDTO;
    private ArrayList<String> list;
    private QuickAdapter<AccountDevDTO> adapter;
    private List<AccountDevDTO> accountDevDTOS;
    private String iotId;
    private boolean isList;

    @Override
    protected void InitDataView() {
        Intent intent=getIntent();
        if(intent!=null){
            accountDevDTO = (AccountDevDTO) getIntent().getSerializableExtra(Constance.data);
            list = getIntent().getStringArrayListExtra(Constance.list);
            if(list==null||list.size()==0){
                tv_title.setText(""+ accountDevDTO.getProductName());
            }else {
                tv_title.setText("设备共享");
                isList = true;
            }

        }
    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_device_share_add);
        tv_title = findViewById(R.id.tv_title);
        tv_share_add = findViewById(R.id.tv_share_add);
        lv_devices_sharers = findViewById(R.id.lv_devices_sharers);
        tv_share_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DeviceShareAddActivity.this,DeviceShareAddUserActivity.class);
                if(!isList){
                    intent.putExtra(Constance.data,accountDevDTO);
                }else {
                    intent.putStringArrayListExtra(Constance.list,list);
                }
                startActivity(intent);
            }
        });
        adapter = new QuickAdapter<AccountDevDTO>(this,R.layout.item_share_devices_user) {
            @Override
            protected void convert(BaseAdapterHelper helper, AccountDevDTO item) {
                helper.setText(R.id.tv_name,item.getIdentityAlias());
                helper.setText(R.id.tv_desc,"86-"+item.getIdentityAlias());
                helper.setText(R.id.tv_time, DateUtils.getStrTime(Long.parseLong(item.getGmtModified())/1000L+""));
                ImageView iv_img=helper.getView(R.id.iv_img);
//                iv_img.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(item.getCategoryImage(),iv_img);
            }
        };
        lv_devices_sharers.setAdapter(adapter);
        accountDevDTOS = new ArrayList<>();

        final SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(DeviceShareAddActivity.this);
                deleteItem.setBackground(new ColorDrawable(Color.parseColor("#f3192d")));
                deleteItem.setWidth(UIUtils.dip2PX(65));
                deleteItem.setTitle("删除");
                deleteItem.setTitleColor(Color.WHITE);
                deleteItem.setTitleSize(14);
                menu.addMenuItem(deleteItem);
            }
        };
        lv_devices_sharers.setMenuCreator(creator);

        lv_devices_sharers.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                if (index == 0) {

                    UIUtils.showSingleWordDialog(DeviceShareAddActivity.this, getString(R.string.str_disconnect_devices), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Map<String ,Object> map=new HashMap<>();
                            map.put("targetIdentityId",accountDevDTOS.get(position).getIdentityId());
                            List<String > iotidList=new ArrayList<>();
                            iotidList.add(iotId);
                            map.put("iotIdList",iotidList);
                            ApiClientForIot.getIotClient("/uc/unbindByManager", "1.0.2", map, new IoTCallback() {
                                @Override
                                public void onFailure(IoTRequest ioTRequest, Exception e) {

                                }

                                @Override
                                public void onResponse(IoTRequest ioTRequest, final IoTResponse ioTResponse) {
                                    if(ioTResponse.getCode()==200){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                MyToast.show(DeviceShareAddActivity.this,getString(R.string.str_unbind_device_success));
                                                accountDevDTOS=new ArrayList<>();
                                                listByAccount();
                                            }
                                        });
                                    }else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                MyToast.show(DeviceShareAddActivity.this,ioTResponse.getLocalizedMsg());
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isList){
        listByAccount();
        }
    }

    private void listByAccount() {
        Map<String, Object> map=new HashMap<>();
        map.put("pageSize","20");
        map.put("pageNo", 1);
        map.put("iotId",iotId);
        ApiClientForIot.getIotClient("/uc/listBindingByDev", "1.0.2", map, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                final int code = response.getCode();
                final String msg = response.getMessage();
                if (code != 200){

//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(DeviceShareAddActivity.this, "code = " +code + " msg =" + msg, Toast.LENGTH_SHORT).show();
//                            if(code==401){
//                                LoginBusiness.logout(new ILogoutCallback() {
//                                    @Override
//                                    public void onLogoutSuccess() {
//                                        startActivity(new Intent(DeviceShareAddActivity.this, MyLoginActivity.class));
//                                    }
//
//                                    @Override
//                                    public void onLogoutFailed(int i, String s) {
//
//                                    }
//                                });
//
////                                getActivity().finish();
//                            }
//                        }
//                    });

                    return;
                }

                Object data = response.getData();
                if (null != data) {
                    if(data instanceof org.json.JSONObject){
                        org.json.JSONObject result= (org.json.JSONObject) data;
                        LogUtils.logE("devlist",result.toString());
                        JSONArray listData= null;
                        try {
                            listData = result.getJSONArray(Constance.data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        mDeviceList = parseDeviceListFromSever((JSONArray) data);
                        accountDevDTOS=new Gson().fromJson(((JSONArray)listData).toString(),new TypeToken<List<AccountDevDTO>>(){}.getType());
                        if(accountDevDTOS==null||accountDevDTOS.size()==0){
//                            mHandler.sendEmptyMessage(1);
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
//                        for(int i=0;i<accountDevDTOS.size();i++){
//                            for(int j=0;j<accountDevDTOS.size();j++){
//                                if(i!=j&&accountDevDTOS.get(i).getIotId().equals(accountDevDTOS.get(j).getIotId())||accountDevDTOS.get(j).getName()!=null&&accountDevDTOS.get(j).getName().contains("蓝牙")){
//                                    accountDevDTOS.remove(j);
//                                    if(j!=0)j--;
//                                }
//                            }
//                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.replaceAll(accountDevDTOS);
//                                UIUtils.initListViewHeight(lv_devices_sharers);
//                                pullToRefresh.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.initListViewHeight(lv_devices)));
//                                ll_none_device.setVisibility(View.GONE);
                            }
                        });
            }}
            }});
    }

    @Override
    protected void initData() {
        if(getIntent()!=null){
            iotId = getIntent().getStringExtra(Constance.iotId);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==200&&data!=null){
            Map<String,Object> map=new HashMap<>();
            map.put("phone",data.getStringExtra(Constance.phone));
            ApiClientForIot.getIotClient("/user/account/identity/query", "1.0.0", map, new IoTCallback() {
                @Override
                public void onFailure(IoTRequest ioTRequest, Exception e) {

                }

                @Override
                public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                    if(ioTResponse.getCode()==200){
                        JSONObject jsonObject= (JSONObject) JSON.parse(String.valueOf(ioTResponse.getData()));
                        if(jsonObject!=null){
                            String identityId=jsonObject.getString(Constance.identityId);
                            DeviceShareAddActivity.this.finish();
                        }
                    }

                }
            });
        }
    }
}
