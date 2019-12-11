package com.juhao.home.scene;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.BaseActivity;
import com.bigkoo.pickerview.TimePickerView;
import com.juhao.home.R;
import com.util.Constance;
import com.util.LogUtils;
import com.view.TextViewPlus;

import java.util.Calendar;
import java.util.Date;

public class IotSceneActionAddActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_iot_action_add);
        TextView tv_device_add=findViewById(R.id.tv_device_add);
        TextView tv_delay=findViewById(R.id.tv_delay);
        TextView tv_auto=findViewById(R.id.tv_auto);
        tv_device_add.setOnClickListener(this);
        tv_delay.setOnClickListener(this);
        tv_auto.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
    switch (view.getId()){
        case R.id.tv_device_add:
            startActivityForResult(new Intent(this,IotSceneDeviceAddActivity.class),200);
            break;
        case R.id.tv_delay:
            TimePickerView timePickerView=new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    LogUtils.logE("timer",date.getMinutes()+"min:"+date.getSeconds()+"s");
                    Intent intent=new Intent();
                    intent.putExtra(Constance.minute,date.getMinutes()+"");
                    intent.putExtra(Constance.second,date.getSeconds()+"");
                    setResult(300,intent);
                }
            })
                    .setType(new boolean[]{false,false,false,false,true,true}).build();
            timePickerView.setDate(Calendar.getInstance());
            timePickerView.show();

//            startActivityForResult(new Intent(this,CountDownActivity.class),200);
            break;
        case R.id.tv_auto:
            startActivityForResult(new Intent(this,IotSceneActionAutoSelectActivity.class),300);
            break;

    }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200&&resultCode==200){
            data.putExtra(Constance.uri,data.getStringExtra(Constance.actionsUrl));
            setResult(200,data);
            finish();
        }else if(requestCode==300&&data!=null){
            data.putExtra(Constance.uri,"action/scene/trigger");
            setResult(400,data);
            finish();
        }
    }
}
