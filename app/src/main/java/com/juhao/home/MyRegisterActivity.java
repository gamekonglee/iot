package com.juhao.home;

import android.graphics.Color;
import android.os.Bundle;

import com.BaseActivity;
import com.alibaba.sdk.android.openaccount.ui.ui.RegisterActivity;

public class MyRegisterActivity extends RegisterActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        next.setBackgroundResource(R.drawable.bg_corner_full_orange_15);
        next.setTextColor(Color.WHITE);
    }
}
