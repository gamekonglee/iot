package com.aliyun.iot.demo.ipcview.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.aliyun.iot.demo.ipcview.R;
import com.aliyun.iot.demo.ipcview.fragments.SettingsPreferenceFragment;

/**
 * 设置界面
 *
 *
 *
 * ！！！注意！！！
 * 注意本示例代码主要用于演示部分视频业务接口以及对应的效果
 * 代码中涉及的交互，UI以及代码框架请自行设计，示例代码仅供参考，稳定性请客户自行保证。
 *
 * @author azad
 */
public class SettingsActivity extends Activity {

    private String iotId = "";
    public String getIotId(){
        return iotId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iotId = getIntent().getStringExtra("iotId");
        SettingsPreferenceFragment fragment = new SettingsPreferenceFragment();
        fragment.setIotId(iotId);
        getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();

    }


    @Override
    protected void onResume() {
        super.onResume();

    }
    public void goBack(View v){
        onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
