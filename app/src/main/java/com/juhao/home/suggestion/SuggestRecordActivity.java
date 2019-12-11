package com.juhao.home.suggestion;

import android.widget.ListView;

import com.BaseActivity;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.bean.FeedbackBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.juhao.home.R;
import com.juhao.home.adapter.BaseAdapterHelper;
import com.juhao.home.adapter.QuickAdapter;
import com.util.ApiClientForIot;
import com.util.Constance;
import com.util.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuggestRecordActivity extends BaseActivity {

    private QuickAdapter<FeedbackBean> adapter;
    private List<FeedbackBean> feedbackBeans;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_suggest_record);
        ListView lv_record=findViewById(R.id.lv_record);
        adapter = new QuickAdapter<FeedbackBean>(this,R.layout.item_feedback_record) {
            @Override
            protected void convert(BaseAdapterHelper helper, FeedbackBean item) {
                helper.setText(R.id.tv_name,item.getContent());
                helper.setText(R.id.tv_desc,item.getTopic()+"/"+item.getDevicename());
                helper.setText(R.id.tv_time, DateUtils.getStrTime02(String.valueOf(item.getGmtModified()/1000L)));

            }
        };
        lv_record.setAdapter(adapter);
        feedbackBeans = new ArrayList<>();
        getFeedbackList();
    }
    int page=1;
    private void getFeedbackList() {
        Map<String,Object> map=new HashMap<>();
        map.put("pageSize",100);
        map.put("pageNo",page);
        ApiClientForIot.getIotClient("/feedbacklist/querybyuid", "1.0.1", map, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                if(ioTResponse.getCode()==200){
                    JSONObject data= (JSONObject) ioTResponse.getData();
                    JSONArray array= null;
                    try {
                        array = data.getJSONArray(Constance.data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(array!=null&&array.length()>0){
                        List<FeedbackBean> temp=new Gson().fromJson(array.toString(),new TypeToken<List<FeedbackBean>>(){}.getType());
                        if(page==1){
                            feedbackBeans=temp;
                        }else {
                            feedbackBeans.addAll(temp);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.replaceAll(feedbackBeans);
                            }
                        });


                    }
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void initData() {

    }
}
