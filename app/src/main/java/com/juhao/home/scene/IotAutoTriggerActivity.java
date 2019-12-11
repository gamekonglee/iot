package com.juhao.home.scene;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.BaseActivity;
import com.bigkoo.pickerview.TimePickerView;
import com.juhao.home.R;
import com.util.Constance;

import java.util.Calendar;
import java.util.Date;

public class IotAutoTriggerActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_timing;
    private TextView tv_trigger;
    private String currentH="0";
    private String currentM="0";
    private boolean is_edit;
    private String hour;
    private String minute;
    private String cron;
    private int position;

    @Override
    protected void InitDataView() {
        if(is_edit){
            tv_timing.setText(timing_day);
            tv_trigger.setText(currentH+":"+currentM);
        }

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_iot_auto_trigger);
        View rl_timing=findViewById(R.id.rl_timing);
        tv_timing = findViewById(R.id.tv_timing);
        View rl_trigger=findViewById(R.id.rl_trigger);
        tv_trigger = findViewById(R.id.tv_trigger);
        rl_timing.setOnClickListener(this);
        rl_trigger.setOnClickListener(this);
        if(!is_edit){
        Date date=new Date();
        currentH=date.getHours()+"";
        currentM=date.getMinutes()+"";
        tv_trigger.setText(currentH+":"+currentM);}
    }

    @Override
    protected void initData() {
        is_edit = getIntent().getBooleanExtra(Constance.is_edit,false);
        currentH = getIntent().getStringExtra(Constance.hour);
        currentM = getIntent().getStringExtra(Constance.minute);
        String cron=getIntent().getStringExtra(Constance.cron);
        timing_day = getIntent().getStringExtra(Constance.weekStr);
        if(timing_day!=null&&(timing_day.contains("月")||timing_day.contains("每天"))){

        }else {
            if(cron!=null){
                timing_cron = cron.split(" ")[4];
                timing_day="";
                if(timing_cron.contains("1")){
                    timing_day+=getString(R.string.str_mon)+",";
                }
                if(timing_cron.contains("2")){
                    if(timing_day.length()>2){
                        timing_day+=getString(R.string.str_tuesday)+",";
                    }else {
                        timing_day+=getString(R.string.str_tue)+",";
                    }
                }
                if(timing_cron.contains("3")){
                    if(timing_day.length()>2){
                        timing_day+=getString(R.string.str_wednesday)+",";
                    }else {
                        timing_day+=getString(R.string.str_wed)+",";
                    }
                }
                if(timing_cron.contains("4")){
                    if(timing_day.length()>2){
                        timing_day+=getString(R.string.str_thursday)+",";
                    }else {
                        timing_day+=getString(R.string.str_thurs)+",";
                    }
                }
                if(timing_cron.contains("5")){
                    if(timing_day.length()>2){
                        timing_day+=getString(R.string.str_friday)+",";
                    }else {
                        timing_day+=getString(R.string.str_fri)+",";
                    }
                }
                if(timing_cron.contains("6")){
                    if(timing_day.length()>2){
                        timing_day+=getString(R.string.str_saturday)+",";
                    }else {
                        timing_day+=getString(R.string.str_sat)+",";
                    }
                }
                if(timing_cron.contains("7")||timing_cron.contains("0")){
                    if(timing_day.length()>2){
                        timing_day+=getString(R.string.str_sunday)+",";
                    }else {
                        timing_day+=getString(R.string.str_sun)+",";
                    }
                }

            }
        }


        position = getIntent().getIntExtra(Constance.position,0);

    }

    @Override
    public void onClick(View view) {
    switch (view.getId()){
        case R.id.rl_timing:
            startActivityForResult(new Intent(this,WeekDaySelectActivity.class),200);
            break;
        case R.id.rl_trigger:
            TimePickerView timePickerView=new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    currentH = date.getHours()+"";
                    currentM = date.getMinutes()+"";
                    tv_trigger.setText(date.getHours()+":"+date.getMinutes());
                }
            })
                    .setType(new boolean[]{false,false,false,true,true,false}).build();
            timePickerView.setDate(Calendar.getInstance());
            timePickerView.show();
            break;

    }
    }
    String timing_str="仅限一次 00:00";
    String timing="0 0 * * *";
    String timing_day="每天";
    String timing_cron="";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200&&resultCode==200&&data!=null){
            Intent intent=data;
            timing_day=intent.getStringExtra(Constance.tmiming_day);
            timing_cron= intent.getStringExtra(Constance.timing_cron);
            tv_timing.setText(timing_day);
//            setResult(200,intent);
        }
    }

    @Override
    public void save(View v) {
        Intent intent=new Intent();
        intent.putExtra(Constance.timing_time,tv_trigger.getText().toString());
        if(TextUtils.isEmpty(timing_cron)){
            Date date=new Date();
            intent.putExtra(Constance.timing_date,(date.getMonth()+1)+"月"+date.getDate()+"号");
//            intent.putExtra(Constance.timing,currentM+" "+currentH+" "+date.getDate()+" "+(date.getMonth()+1)+" *");
            intent.putExtra(Constance.timing,currentM+" "+currentH+" * * *");
        }else {
            intent.putExtra(Constance.timing_date,timing_day);
            intent.putExtra(Constance.timing,currentM+" "+currentH+" * * "+timing_cron);
        }
        if(is_edit){
            intent.putExtra(Constance.position,position);
        }
        intent.putExtra(Constance.hour,currentH);
        intent.putExtra(Constance.minute,currentM);
        intent.putExtra(Constance.uri,"condition/timer");
        setResult(300,intent);
        finish();
    }
}
