package com.juhao.home.scene;

import android.view.View;
import android.widget.TimePicker;

import com.BaseActivity;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.juhao.home.R;

import java.util.Calendar;
import java.util.Date;

public class CountDownActivity extends BaseActivity {
    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_count_down_delay);
        TimePicker tp_scene=findViewById(R.id.tp_scene);
        tp_scene.setIs24HourView(false);
        TimePickerView timePickerView=new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {

            }
        })
                .setType(new boolean[]{false,false,false,false,true,true}).build();
        timePickerView.setDate(Calendar.getInstance());
        timePickerView.show();


    }

    @Override
    protected void initData() {

    }
}
