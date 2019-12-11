package com.aliyun.iot.ilop.demo.view;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import com.juhao.home.R;

/**
 * Created by xingwei on 2017/11/16.
 */

public class IndexItemView extends LinearLayout{

    FlexboxLayout mActionLayout;
    TextView mTitleTv;

    View mStatusLayout;
    TextView mStatusTv;

    public IndexItemView(Context context) {
       this(context, null);
    }

    public IndexItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        LayoutInflater.from(context).inflate(R.layout.index_item_layout, this);
//        mActionLayout =  findViewById(R.id.action_ly);
//        mTitleTv = findViewById(R.id.title);
//        mStatusLayout = findViewById(R.id.status_ly);
//        mStatusTv = findViewById(R.id.status);
        setOrientation(LinearLayout.VERTICAL);
    }

    public void setTitle(String title) {
        mTitleTv.setText(title);
    }

    public void setStatus(String status) {
        mStatusLayout.setVisibility(View.VISIBLE);
        mStatusTv.setText(status);
    }

//    public void addAction(String actionText, View.OnClickListener action) {
//        Button button = (Button)LayoutInflater.from(getContext())
//                .inflate(R.layout.index_item_action_btn, this, false);
//        button.setText(actionText);
//        button.setOnClickListener(action);
//        if (actionText.contains("调试")) {
//            button.setTextColor(Color.parseColor("#35B34A"));
//            button.setBackgroundResource(R.drawable.action_green_bg);
//        }
//        mActionLayout.addView(button);
//    }
}
