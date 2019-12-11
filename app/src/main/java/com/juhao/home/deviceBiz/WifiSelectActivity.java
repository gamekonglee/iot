package com.juhao.home.deviceBiz;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.BaseActivity;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.juhao.home.R;
import com.juhao.home.adapter.BaseAdapterHelper;
import com.juhao.home.adapter.QuickAdapter;
import com.view.MyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WifiSelectActivity extends BaseActivity implements View.OnClickListener {
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 452;
    private EditText et_pwd;
    private EditText et_wifi;
    private boolean isCanSee;
    private TextView tv_cansee;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_wifi_select);
        fullScreen(this);
        et_wifi = findViewById(R.id.et_wifi);
        et_pwd = findViewById(R.id.et_pwd);
        TextView tv_change_wifi=findViewById(R.id.tv_change_wifi);
        tv_cansee = findViewById(R.id.tv_cansee);
        TextView tv_ensure=findViewById(R.id.tv_ensure);

        tv_change_wifi.setOnClickListener(this);
        tv_cansee.setOnClickListener(this);
        tv_ensure.setOnClickListener(this);

        et_wifi.setText(getSSID());
//        dialog.show();
    }

    @Override
    protected void initData() {

    }

    public List<ScanResult> getWifiList() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        List<ScanResult> scanWifiList = wifiManager.getScanResults();
        final List<ScanResult> wifiList = new ArrayList<>();
        if (scanWifiList != null && scanWifiList.size() > 0) {
            HashMap<String, Integer> signalStrength = new HashMap<String, Integer>();
            for (int i = 0; i < scanWifiList.size(); i++) {
                ScanResult scanResult = scanWifiList.get(i);
                if (!scanResult.SSID.isEmpty()) {
                    String key = scanResult.SSID + " " + scanResult.capabilities;
                    if (!signalStrength.containsKey(key)) {
                        signalStrength.put(key, i);
                        wifiList.add(scanResult);
                    }
                }
            }
        }
        final Dialog dialog=new Dialog(this,R.style.customDialog);
        dialog.setContentView(R.layout.dialog_wifi_select);
        ListView lv_wifi=dialog.findViewById(R.id.lv_wifi);
        QuickAdapter<ScanResult> adapter=new QuickAdapter<ScanResult>(this,R.layout.item_wifi) {
            @Override
            protected void convert(BaseAdapterHelper helper, ScanResult item) {
                helper.setText(R.id.tv_name,item.SSID);
            }
        };
        lv_wifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                et_wifi.setText(wifiList.get(i).SSID);
                dialog.dismiss();
            }
        });
        lv_wifi.setAdapter(adapter);
        adapter.replaceAll(wifiList);
        dialog.show();
        return wifiList;
    }
    private void registerPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        } else {
            getWifiList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION) {
            getWifiList();
        }
    }

    /**
     * 获取当前连接WIFI的SSID
     */
    public String getSSID() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wm != null) {
            WifiInfo winfo = wm.getConnectionInfo();
            if (winfo != null) {
                String s = winfo.getSSID();
                if (s.length() > 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
                    return s.substring(1, s.length() - 1);
                }
            }
        }
        return "";
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_change_wifi:
                registerPermission();
                break;
            case R.id.tv_cansee:
                isCanSee = !isCanSee;
                if(isCanSee){
                    //密码可见
                    tv_cansee.setText(getString(R.string.icon_can_see));
                    et_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    //密码不可见
                    tv_cansee.setText(getString(R.string.icon_can_not_see));
                    et_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.tv_ensure:
                String ssid=et_wifi.getText().toString();
                String pwd=et_pwd.getText().toString();
                if(TextUtils.isEmpty(ssid)||TextUtils.isEmpty(pwd)){
                    MyToast.show(this,"请填写wifi账号和密码");
                    return;
                }
                DemoApplication.wifi=ssid;
                DemoApplication.pwd=pwd;
                startActivity(new Intent(this,AddDeviceTipsActivity.class));
                break;
        }
    }
}
