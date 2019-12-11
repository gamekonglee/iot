package com.aliyun.iot.demo.ipcview.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.demo.ipcview.R;
import com.aliyun.iot.demo.ipcview.adapter.MyListAdapter;
import com.aliyun.iot.demo.ipcview.beans.EventRecordPlanResponse;
import com.aliyun.iot.demo.ipcview.beans.TimeSectionForPlan;
import com.aliyun.iot.demo.ipcview.dialog.TimeSettingDialog;
import com.aliyun.iotx.linkvisual.IPCManager;
import com.aliyun.iotx.linkvisual.linkvisualapi.bean.TimeSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 设置设备的事件录像计划界面 事件可能是移动侦测或者声音监测触发的，当有事件发生时，如果发生的时段正好在配置的事件录像计划内则云端会通知设备把录像进行上传保存。
 * 事件录像计划对于同一个账号下的所有设备都是可见的，不过一个设备只能绑定一个事件录像计划。
 * <p>
 * <p>
 * <p>
 * ！！！注意！！！ 注意本示例代码主要用于演示部分视频业务接口以及对应的效果 代码中涉及的交互，UI以及代码框架请自行设计，示例代码仅供参考，稳定性请客户自行保证。
 *
 * @author azad
 */
public class EasyPlanSettingsActivity extends Activity {

    private String TAG = this.getClass().getSimpleName();
    private String iotId;

    private MyListAdapter myListAdapter;
    private List<TimeSectionForPlan> timeLst = new LinkedList<>();
    private ListView listView;
    private Button unbindBtn, bindBtn;
    private CheckBox isAllDayCb;
    boolean isAllDay = false;
    private boolean hasPlan = false;
    private String planId = "";
    private String planName = "";
    private List<Integer> eventTypeList = new LinkedList<>();
    Handler uiHandler;

    private final int DEFAULT_PRE_RECORD_DURATION = 5;
    private final int DEFAULT_RECORD_DURATION = 30;
    private final int DEFAULT_STREAM_TYPE = 0;

    /**
     * 重置数据
     */
    private void restoreData() {
        isAllDay = false;
        hasPlan = false;
        planId = "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHandler = new Handler(getMainLooper());
        setContentView(R.layout.activity_easy_plan_settings);
        eventTypeList.add(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        iotId = getIntent().getStringExtra("iotId");
        initView();
        refreshUI();
    }

    private void initView() {
        myListAdapter = new MyListAdapter(this, timeLst);
        listView = findViewById(R.id.time_lst);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showSetTimeDialog(position);
            }
        });
        listView.setAdapter(myListAdapter);

        unbindBtn = findViewById(R.id.plan_unbind_btn);
        bindBtn = findViewById(R.id.plan_bind_btn);

        unbindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPassive();
            }
        });
        bindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPositive();
            }
        });

        isAllDayCb = findViewById(R.id.is_all_day_cb);
        isAllDayCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    isAllDay = true;
                    listView.setEnabled(false);
                } else {
                    isAllDay = false;
                    listView.setEnabled(true);
                }
            }
        });
        initTimeMap();
        initData();
    }

    private void showToast(String s) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initTimeMap() {
        if (timeLst != null && timeLst.size() != 0) {
            timeLst.clear();
        }
        TimeSectionForPlan temp;
        /**
         * day: 0是星期天，1是星期一，2是星期二以此类推
         * begin  end：单位是秒，只是一天，即0是00：00：00，86399是23：59：59
         */
        for (int i = 0; i < 7; i++) {
            temp = new TimeSectionForPlan();
            temp.setDayOfWeek(i);
            temp.setBegin(0);
            temp.setEnd(86399);
            timeLst.add(temp);
        }
    }

    private void refreshUI() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                myListAdapter.notifyDataSetChanged();
            }
        });
    }

    private boolean isIotIdValid() {
        return iotId != null && !"".equals(iotId);
    }

    public void initData() {
        if (!isIotIdValid()) {
            return;
        }
        getEventPlan();

    }

    /**
     * 查询绑定到设备的事件录像计划
     */
    private void getEventPlan() {
        IPCManager.getInstance().getDevice(iotId)
                .getEventRecordPlan2Dev(0, new IoTCallback() {
                    @Override
                    public void onFailure(IoTRequest ioTRequest, Exception e) {
                        Log.e(TAG, "getRecordPlan2Dev   onFailure    e:" + e.toString());
                        showToast(getResources().getString(R.string.ipc_plan_query_list_err_info) + e.toString());
                    }

                    @Override
                    public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                        Log.d(TAG, "getRecordPlan2Dev     code:" + ioTResponse.getCode() + "      data:" +
                                "[" + ioTResponse.getData() + "" +
                                "]      id:" + ioTResponse.getId());

                        if (ioTResponse.getCode() == 200) {
                            if (ioTResponse.getData() != null && !"".equals(ioTResponse.getData().toString().trim())) {

                                EventRecordPlanResponse planResponse = JSON.parseObject(ioTResponse.getData().toString(),
                                        EventRecordPlanResponse.class);

                                hasPlan = true;
                                if (!isNullOrEmpty(planResponse.getPlanId())) {
                                    planId = planResponse.getPlanId();
                                }
                                if (!isNullOrEmpty(planResponse.getName())) {
                                    planName = planResponse.getName();
                                }
                                eventTypeList = planResponse.getEventTypeList();

                                if (1 == planResponse.getAllDay()) {
                                    setIsAllDayUI(true);
                                } else {
                                    setIsAllDayUI(false);
                                    List<TimeSectionForPlan> plans = planResponse.getTimeSectionList();
                                    if (plans != null && !plans.isEmpty()) {
                                        Collections.sort(plans);
                                        timeLst.clear();
                                        timeLst.addAll(plans);
                                        refreshUI();
                                    }
                                }
                                showToast(getResources().getString(R.string.ipc_plan_query_list_success));
                            } else {
                                hasPlan = false;
                                showToast(getResources().getString(R.string.ipc_plan_match_fail) + ioTResponse.getCode());
                            }
                        } else {
                            if (ioTResponse.getCode() == 9116) {
                                showToast(getResources().getString(R.string.ipc_plan_none));
                            } else {
                                showToast(getResources().getString(R.string.ipc_plan_query_list_err_code) + ioTResponse.getCode());
                            }

                        }

                    }
                });
    }

    private boolean isNullOrEmpty(String txt) {
        return txt == null || "".equals(txt.trim());
    }

    @SuppressLint("NewApi")
    private void showSetTimeDialog(final int position) {
        TimeSettingDialog.getInstance().openDialog(this, timeLst, position, new TimeSettingDialog.DataCallBack() {
            @Override
            public void onDataChanged() {
                refreshUI();
            }
        });
    }

    /**
     * 点击更新按钮
     */
    private void onPositive() {
        onPositiveEventPlan();
    }

    /**
     * 点击更新按钮
     * <p>
     * 具体的逻辑可以重新设计，这个地方只是为了简单的演示一个简单的绑定事件录像计划的流程。
     */
    private void onPositiveEventPlan() {
        List<TimeSection> beforeList = new ArrayList<>();
        /**
         * 如果不是全天计划则将时间记录下来准备传给云端
         */
        if (!isAllDay) {
            TimeSection section;
            for (int i = 0; i < 7; i++) {
                section = new TimeSection();
                section.setDayOfWeek(timeLst.get(i).getDayOfWeek());
                section.setBegin(timeLst.get(i).getBegin());
                section.setEnd(timeLst.get(i).getEnd());
                beforeList.add(section);
            }
        }
        /**
         * 如果查询到设备有绑定事件录像计划，则更新对应的事件录像计划。
         */
        if (hasPlan) {
            /**
             * 更新事件录像计划
             */
            IPCManager.getInstance().updateEventRecordPlan(planId, planName, eventTypeList, DEFAULT_PRE_RECORD_DURATION,
                    DEFAULT_RECORD_DURATION, isAllDay,
                    beforeList, new IoTCallback() {
                        @Override
                        public void onFailure(IoTRequest ioTRequest, Exception e) {
                            Log.e(TAG, "updateTimeTemplate   onFailure    e:" + e.toString());
                            showToast(getResources().getString(R.string.ipc_plan_set_err_info) + e.toString());
                        }

                        @Override
                        public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                            Log.d(TAG,
                                    "updateTimeTemplate   code:" + ioTResponse.getCode() + "      data:" + ioTResponse
                                            .getData().toString() + "      id:" + ioTResponse.getId());
                            if (ioTResponse.getCode() == 200) {
                                if (ioTResponse.getData() != null) {
                                    showToast(getResources().getString(R.string.ipc_plan_set_success));
                                } else {
                                    showToast(getResources().getString(R.string.ipc_plan_set_fail_data_null));
                                }
                            } else {
                                showToast(getResources().getString(R.string.ipc_plan_set_fail_code) + ioTResponse.getCode());
                            }

                        }
                    });
        } else {
            /**
             * 如果设备此时没有绑定事件录像计划，则直接根据用户输入的时间创建一个事件录像计划最后绑定到设备上。
             */

            /**
             * 创建事件录像计划
             */
            IPCManager.getInstance().setEventRecordPlan("T" + System.currentTimeMillis(), eventTypeList,
                    DEFAULT_PRE_RECORD_DURATION, DEFAULT_RECORD_DURATION, isAllDay, beforeList, new IoTCallback() {
                        @Override
                        public void onFailure(IoTRequest ioTRequest, Exception e) {
                            Log.e(TAG, "setRecordPlan   onFailure    e:" + e.toString());
                            showToast(getResources().getString(R.string.ipc_plan_set_err_info) + e.toString());
                        }

                        @Override
                        public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                            Log.d(TAG, "setRecordPlan     code:" + ioTResponse.getCode()
                                    + "      data:" + ioTResponse.getData() + "      id:" + ioTResponse
                                    .getId());
                            if (ioTResponse.getCode() == 200) {
                                if (ioTResponse.getData() != null) {
                                    if (!"".equals(ioTResponse.getData().toString().trim())) {
                                        JSONObject json = JSON.parseObject(ioTResponse.getData().toString());
                                        if (json != null) {
                                            if (json.containsKey("planId")) {
                                                planId = json.getString("planId");
                                                /**
                                                 * 绑定事件录像计划到设备上
                                                 */
                                                IPCManager.getInstance().getDevice(iotId)
                                                        .addEventRecordPlan2Dev(planId, DEFAULT_STREAM_TYPE, new IoTCallback() {
                                                            @Override
                                                            public void onFailure(IoTRequest ioTRequest,
                                                                                  Exception e) {
                                                                Log.e(TAG,
                                                                        "addRecordPlan2Dev   onFailure    e:"
                                                                                + e.toString());
                                                                showToast(getResources().getString(R.string.ipc_plan_set_err_info) + e.toString());
                                                            }

                                                            @Override
                                                            public void onResponse(IoTRequest ioTRequest,
                                                                                   IoTResponse ioTResponse) {
                                                                Log.d(TAG, "addRecordPlan2Dev     code:"
                                                                        + ioTResponse.getCode() + "      data:"
                                                                        + ioTResponse.getData() + "      id:"
                                                                        + ioTResponse.getId());
                                                                if (ioTResponse.getCode() == 200) {
                                                                    showToast(getResources().getString(R.string.ipc_plan_set_success));
                                                                } else {
                                                                    showToast(getResources().getString(R.string.ipc_plan_set_fail_code)
                                                                            + ioTResponse.getCode());
                                                                }
                                                            }
                                                        });
                                            }
                                        } else {

                                        }
                                    }
                                }
                            } else {
                                showToast(getResources().getString(R.string.ipc_plan_set_fail_code) + ioTResponse.getCode());
                            }
                        }
                    });
        }

    }

    private void setIsAllDayUI(boolean isAllDay) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isAllDayCb.setChecked(isAllDay);
            }
        });
    }

    private void onPassive() {
        onPassiveEventPlan();
    }

    private boolean StringIsNullOrEmpty(String s) {
        return s == null || "".equals(s.trim());
    }

    private void restoreView() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                isAllDayCb.setChecked(false);
            }
        });
        initTimeMap();
        refreshUI();
    }

    /**
     * 解绑事件录像计划
     */
    private void onPassiveEventPlan() {
        IPCManager.getInstance().getDevice(iotId).deleteEventRecordPlan2Dev(0,
                new IoTCallback() {
                    @Override
                    public void onFailure(IoTRequest ioTRequest, Exception e) {
                        Log.e(TAG, "deleteRecordPlan2Dev :" + e.toString());
                        showToast(getString(R.string.ipc_plan_unbind_err_info) + e.toString());
                    }

                    @Override
                    public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                        Log.d(TAG, "deleteRecordPlan2Dev  code:" + ioTResponse.getCode() + "      data:" + ioTResponse
                                .getData() + "      id:" + ioTResponse.getId());
                        if (ioTResponse.getCode() == 200) {
                            showToast(getString(R.string.ipc_plan_unbind_success) + ioTResponse.getData());
                            restoreView();
                            restoreData();
                        } else {
                            showToast(String.format(getString(R.string.ipc_reg_plan_unbind_fail),
                                            ioTResponse.getCode(), ioTResponse.getData()));
                        }
                    }
                });
    }
}
