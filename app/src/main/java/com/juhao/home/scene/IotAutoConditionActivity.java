package com.juhao.home.scene;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.BaseActivity;
import com.juhao.home.R;
import com.util.Constance;

public class IotAutoConditionActivity extends BaseActivity implements View.OnClickListener {

    private boolean isCondition;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_iot_auto_condition);
        TextView tv_device_add=findViewById(R.id.tv_device_add);
        TextView tv_countdown=findViewById(R.id.tv_countdown);
        TextView tv_time_range=findViewById(R.id.tv_time_range);
        tv_device_add.setOnClickListener(this);
        tv_countdown.setOnClickListener(this);
        tv_time_range.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        isCondition = getIntent().getBooleanExtra(Constance.is_condition,false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_device_add:
                Intent intent=new Intent(this, IotSceneDeviceAddActivity.class);
                intent.putExtra(Constance.is_auto,true);
                intent.putExtra(Constance.is_condition,true);
                startActivityForResult(intent, 300);
                break;
            case R.id.tv_countdown:
                startActivityForResult(new Intent(this,IotAutoTriggerActivity.class),300);
                break;
            case R.id.tv_time_range:
                startActivityForResult(new Intent(this,IotAutoActionTimingActivity.class),500);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==300&&resultCode==200){
            data.putExtra(Constance.uri,"condition/device/property");
            setResult(200,data);
            finish();
        }else if(requestCode==300&&resultCode==300&&data!=null){
            data.putExtra(Constance.uri,"condition/timer");
            setResult(300,data);
            finish();
        }else if(requestCode==500&&resultCode==500&&data!=null){
            data.putExtra(Constance.uri,"condition/timeRange");
            setResult(500,data);
            finish();
        }
    }
}
