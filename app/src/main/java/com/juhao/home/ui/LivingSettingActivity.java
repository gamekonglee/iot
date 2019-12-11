package com.juhao.home.ui;

import com.BaseActivity;
import com.aliyun.iot.demo.ipcview.fragments.SettingsPreferenceFragment;
import com.juhao.home.R;

public class LivingSettingActivity extends BaseActivity {
    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }
    private String iotId = "";
    @Override
    protected void initView() {
        setContentView(R.layout.activity_living_setting);
        iotId = getIntent().getStringExtra("iotId");
        SettingsPreferenceFragment fragment = new SettingsPreferenceFragment();
        fragment.setIotId(iotId);
        getFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
    }

    @Override
    protected void initData() {

    }
}
