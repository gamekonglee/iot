package com.juhao.home.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.sdk.android.openaccount.ui.ui.RegisterActivity;
import com.juhao.home.R;

public class RegisterAreaCodeActivity extends RegisterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        com.alibaba.sdk.android.openaccount.ui.ui.RegisterActivity
        LinearLayout main_input=findViewById(com.alibaba.sdk.android.openaccount.ui.R.id.main_input);
        View view=View.inflate(this, R.layout.layout_area_choose,null);
        main_input.addView(view);


    }
}
