package com.juhao.home.scene;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.BaseActivity;
import com.bigkoo.pickerview.TimePickerView;
import com.juhao.home.R;
import com.util.Constance;
import com.view.CustomDatePicker;

import java.util.Calendar;
import java.util.Date;

public class IotAutoActionTimingActivity extends BaseActivity implements View.OnClickListener {
    String timing="*";
    String startTime="00:00";
    String endTime="23:59";
    private RelativeLayout rl_all_day;
    private RelativeLayout rl_day_time;
    private RelativeLayout rl_night_time;
    private RelativeLayout rl_custom_time;
    private RelativeLayout rl_timing;
    private TextView tv_all_day;
    private TextView tv_day_time;
    private TextView tv_night_time;
    private TextView tv_custom_time;
    private int currentP;
    private TimePickerView timePickerView;
    private int count;
    private TextView tv_time_range;
    String start_min="0";
    String start_hour="0";
    String end_min="0";
    String end_hour="0";
    String timing_day="每天";
    String timing_cron="";
    private TextView tv_timing;
    private String repeat;
    private boolean isEdit;

    @Override
    protected void InitDataView() {
        if(isEdit){
            if(TextUtils.isEmpty(repeat)){
                tv_timing.setText(getString(R.string.str_onetime));
            }else {
                String weekStr="";
                if(repeat.contains("1")){
                        weekStr+=getString(R.string.str_mon)+",";
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
                tv_timing.setText(weekStr);
            }
        }
    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_iot_auto_action_timing);
        rl_all_day = findViewById(R.id.rl_all_day);
        rl_day_time = findViewById(R.id.rl_day_time);
        rl_night_time = findViewById(R.id.rl_night_time);
        rl_custom_time = findViewById(R.id.rl_custom);
        rl_timing = findViewById(R.id.rl_timing);
        tv_all_day = findViewById(R.id.tv_select_all_day);
        tv_day_time = findViewById(R.id.tv_select_day_time);
        tv_night_time = findViewById(R.id.tv_select_night);
        tv_custom_time = findViewById(R.id.tv_custom);
        tv_time_range = findViewById(R.id.tv_time_range);
        tv_timing = findViewById(R.id.tv_timing);

        rl_all_day.setOnClickListener(this);
        rl_day_time.setOnClickListener(this);
        rl_night_time.setOnClickListener(this);
        rl_custom_time.setOnClickListener(this);
        rl_timing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(IotAutoActionTimingActivity.this,WeekDaySelectActivity.class),200);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent=data;
        timing_day = intent.getStringExtra(Constance.tmiming_day);
        timing_cron = intent.getStringExtra(Constance.timing_cron);
        if(timing_day!=null){
        tv_timing.setText(timing_day);
        }
    }

    @Override
    protected void initData() {
        isEdit = getIntent().getBooleanExtra(Constance.is_edit,false);
        if(isEdit){
            startTime=getIntent().getStringExtra(Constance.beginDate);
            endTime=getIntent().getStringExtra(Constance.endDate);
            repeat = getIntent().getStringExtra(Constance.repeat);
        }
    }

    @Override
    public void save(View v) {
        super.save(v);
        Intent intent=new Intent();
        intent.putExtra(Constance.timing_time,startTime+"-"+endTime);
        if(TextUtils.isEmpty(timing_cron)){
            Date date=new Date();
            intent.putExtra(Constance.timing_date,getString(R.string.str_onetime));
            intent.putExtra(Constance.timing,start_min+"-"+end_min+" "+start_hour+"-"+end_hour+" * * *");
        }else {
            intent.putExtra(Constance.timing_date,timing_day);
            intent.putExtra(Constance.timing,start_min+"-"+end_min+" "+start_hour+"-"+end_hour+" * * "+timing_cron);
        }
        intent.putExtra(Constance.start_time,startTime);
        intent.putExtra(Constance.end_time,endTime);
//        if(is_edit){
//            intent.putExtra(Constance.position,position);
//        }
//        intent.putExtra(Constance.hour,currentH);
//        intent.putExtra(Constance.minute,currentM);
        setResult(500,intent);
        finish();

    }

    @Override
    public void onClick(View view) {
    switch (view.getId()) {
        case R.id.rl_all_day:
            currentP = 0;
            start_min="00";
            start_hour="00";
            end_min="59";
            end_hour="23";
            break;
        case R.id.rl_day_time:
            currentP = 1;
            start_min="00";
            start_hour="06";
            end_min="00";
            end_hour="18";

            break;
        case R.id.rl_night_time:
            currentP = 2;
            start_min="00";
            start_hour="18";
            end_min="00";
            end_hour="06";
            break;
        case R.id.rl_custom:
            currentP = 3;
            break;
    }
        startTime=start_hour+":"+start_min;
        endTime=end_hour+":"+end_min;
        refreshUI();
    }
    public void refreshUI(){
        tv_all_day.setTextColor(getResources().getColor(R.color.tv_cccccc));
        tv_day_time.setTextColor(getResources().getColor(R.color.tv_cccccc));
        tv_night_time.setTextColor(getResources().getColor(R.color.tv_cccccc));
        tv_custom_time.setTextColor(getResources().getColor(R.color.tv_cccccc));
        tv_all_day.setText(getString(R.string.icon_dot_normal));
        tv_day_time.setText(getString(R.string.icon_dot_normal));
        tv_night_time.setText(getString(R.string.icon_dot_normal));
        tv_custom_time.setText(getString(R.string.icon_dot_normal));

        switch (currentP){
            case 0:
                tv_all_day.setTextColor(getResources().getColor(R.color.theme));
                tv_all_day.setText(getString(R.string.icon_dot_select));
                break;
            case 1:
                tv_day_time.setTextColor(getResources().getColor(R.color.theme));
                tv_day_time.setText(getString(R.string.icon_dot_select));
                break;
            case 2:
                tv_night_time.setTextColor(getResources().getColor(R.color.theme));
                tv_night_time.setText(getString(R.string.icon_dot_select));
                break;
            case 3:
                tv_custom_time.setTextColor(getResources().getColor(R.color.theme));
                tv_custom_time.setText(getString(R.string.icon_dot_select));
                count = 0;
                timePickerView = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        count++;
                        if(count==2){
                            endTime= date.getHours()+":"+date.getMinutes();
                            tv_time_range.setText(startTime+"-"+endTime+"");
                            end_hour=date.getHours()+"";
                            end_min=date.getMinutes()+"";
                        }else {
                            handler.sendEmptyMessageDelayed(0,500);
                            startTime= date.getHours()+":"+date.getMinutes();
                            tv_time_range.setText(startTime+"-");

                            start_min=date.getMinutes()+"";
                            start_hour=date.getHours()+"";
                        }

                    }
                })
                        .setType(new boolean[]{false,false,false,true,true,false})
                        .build();
                timePickerView.setDate(Calendar.getInstance());
                timePickerView.show();
                break;
        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            timePickerView.show();
        }
    };
}
