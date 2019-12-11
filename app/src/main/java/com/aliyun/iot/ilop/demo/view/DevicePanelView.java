package com.aliyun.iot.ilop.demo.view;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.juhao.home.R;


public class DevicePanelView extends FrameLayout{

    TextView mNameTv;
    TextView mTypeTv;
    TextView mStatusTv;

    public DevicePanelView(Context context) {
       this(context, null);
    }

    public DevicePanelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.device_panel_layout, this);
        mNameTv = findViewById(R.id.device_panel_name);
        mTypeTv = findViewById(R.id.device_panel_type);
        mStatusTv = findViewById(R.id.device_panel_status);
    }

    public void setName(String name){
        mNameTv.setText(name);
    }

    public void setType(String type){
        mTypeTv.setText(type);
    }

    public void setStatus(String status){
        mStatusTv.setText(status);
    }

    @Override
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        mNameTv.setAlpha(alpha);
        mTypeTv.setAlpha(alpha);
        mStatusTv.setAlpha(alpha);
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        return true;
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        Log.d("dd", "onTouchEvent");
//        return super.onTouchEvent(event);
//    }
}
