package com.juhao.home.scene;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.BaseActivity;
import com.juhao.home.R;
import com.juhao.home.adapter.BaseAdapterHelper;
import com.juhao.home.adapter.QuickAdapter;
import com.util.Constance;
import com.util.LogUtils;

import org.mozilla.javascript.ConsString;

import java.util.ArrayList;
import java.util.List;

public class WeekDaySelectActivity extends BaseActivity {

    private ListView lv_week;
    private QuickAdapter<String> adapter;
    private boolean[] select;
    private TextView tv_save;
//    String timing_str="仅限一次";
//    String timing="00:00";
    String timing_day="每天";
    String timing_cron="0 0 * * *";
    private List<String> week;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    public void goBack(View v) {
        Intent intent=new Intent();
        int j=0;
        timing_day="";
        timing_cron="";
        for(int i=0;i<select.length;i++){
            if(select[i]){
                if(j>0){
                    timing_day+=week.get(i).substring(2);
                }else {
                timing_day+=week.get(i);
                }
                timing_day+=",";
                timing_cron+=(i+1);
                timing_cron+=",";
                j++;
            }
        }
        if(j==0){
            timing_day="仅限一次";
            timing_cron="";
        }else if(j==6){
            timing_day="每天";
            timing_cron="*";
        }else {
            timing_day=timing_day.substring(0,timing_day.length()-1);
            timing_cron=timing_cron.substring(0,timing_cron.length()-1);
        }
        LogUtils.logE("day",timing_day);
        LogUtils.logE("cron",timing_cron);

//        intent.putExtra(Constance.timing_str,timing_str);
//        intent.putExtra(Constance.timing,timing);
        intent.putExtra(Constance.tmiming_day,timing_day);
        intent.putExtra(Constance.timing_cron,timing_cron);
        setResult(200,intent);
        super.goBack(v);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_week_select);
        lv_week = findViewById(R.id.lv_week);
        tv_save = findViewById(R.id.tv_save);
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        adapter = new QuickAdapter<String>(this,R.layout.item_week) {
            @Override
            protected void convert(BaseAdapterHelper helper, String item) {
                helper.setText(R.id.tv_day,item);
                if(select[helper.getPosition()]){
                    helper.setText(R.id.tv_select,getString(R.string.icon_dot_select));
                    helper.setTextColor(R.id.tv_select,getResources().getColor(R.color.theme));
                }else {
                    helper.setText(R.id.tv_select,getString(R.string.icon_dot_normal));
                    helper.setTextColor(R.id.tv_select,getResources().getColor(R.color.tv_999999));
                }
            }
        };
        lv_week.setAdapter(adapter);
        week = new ArrayList<>();
        week.add(getString(R.string.str_sun));
        week.add(getString(R.string.str_mon));
        week.add(getString(R.string.str_tue));
        week.add(getString(R.string.str_wed));
        week.add(getString(R.string.str_thurs));
        week.add(getString(R.string.str_fri));
        week.add(getString(R.string.str_sat));
        adapter.replaceAll(week);
        select = new boolean[7];
        lv_week.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    select[i]=!select[i];
                    adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void initData() {

    }
}
