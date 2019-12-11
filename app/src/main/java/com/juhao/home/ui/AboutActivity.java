package com.juhao.home.ui;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.BaseActivity;
import com.aliyun.iot.ilop.demo.page.ilopmain.MainActivity;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.util.AppUtils;
import com.util.CommonUtil;
import com.util.Constance;
import com.util.NetWorkConst;
import com.util.NetWorkUtils;
import com.util.json.JSONObject;
import com.view.MyToast;

public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_last_version;
    private TextView tv_version_tips;
    private boolean needUpgrade;
    private JSONObject jsonObject;
    private String mAppVersion;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_about);
        TextView tv_version=findViewById(R.id.tv_version);
        TextView tv_pingfen=findViewById(R.id.tv_pingfen);
        TextView tv_privacy=findViewById(R.id.tv_privacy);
        TextView tv_privacy_2=findViewById(R.id.tv_privacy_2);
        tv_last_version = findViewById(R.id.tv_last_version);
        tv_version_tips = findViewById(R.id.tv_version_tips);
        findViewById(R.id.tv_update).setOnClickListener(this);
        tv_privacy.setOnClickListener(this);
        tv_privacy_2.setOnClickListener(this);
        tv_pingfen.setOnClickListener(this);
        tv_version.setText(getString(R.string.application_name)+"v"+UIUtils.getVerName(this));
        sendVersion();
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_pingfen:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("http://a.app.qq.com/o/simple.jsp?pkgname=com.juhao.home");
                intent.setData(content_url);
                startActivity(intent);
                break;
            case R.id.tv_privacy:
            case R.id.tv_privacy_2:
                UIUtils.showSystemStopDialog(this, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                break;
            case R.id.tv_update:
                if(needUpgrade){
                showUpdateDialog();
                }else {
                    MyToast.show(this,getString(R.string.str_current_version_last));
                }
                break;
        }
    }

    /**
     * 获取版本号
     */
    private void sendVersion(){
//        mNetWork.sendVersion(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ans = NetWorkUtils.doGet(NetWorkConst.VERSION_URL_CONTENT);
                jsonObject = new JSONObject(ans);
                mAppVersion = jsonObject.getString(Constance.version);
                final String currentVersion=UIUtils.getVerName(AboutActivity.this);
                if(AppUtils.isEmpty(mAppVersion)) return;
                String localVersion = CommonUtil.localVersionName(AboutActivity.this);
                if ("-1".equals(mAppVersion)) {
                    needUpgrade=false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_last_version.setText(""+currentVersion);
                            tv_version_tips.setVisibility(View.GONE);
                        }
                    });
                } else {
                    needUpgrade=true;
                    boolean isNeedUpdate = CommonUtil.isNeedUpdate(localVersion, mAppVersion);
                    if (isNeedUpdate){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                tv_last_version.setText(""+ mAppVersion);
                                tv_version_tips.setVisibility(View.VISIBLE);


//                                UIUtils.showSingleWordDialog(MainActivity.this, "发现有新版本，是否更新？", new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Intent intent = new Intent();
//                                        intent.setAction("android.intent.action.VIEW");
//                                        Uri content_url = Uri.parse("http://app.08138.com/txdc.apk");
//                                        intent.setData(content_url);
//                                        startActivity(intent);
//                                    }
//                                });

                            }
                        });


                    }
                }

            }
        }).start();

    }
    private void showUpdateDialog(){
        final Dialog dialog=new Dialog(AboutActivity.this,R.style.customDialog);
        dialog.setContentView(R.layout.dialog_update);
        TextView tv_version=dialog.findViewById(R.id.tv_content);
        TextView tv_upgrate=dialog.findViewById(R.id.tv_update);
        TextView iv_close=dialog.findViewById(R.id.tv_cancel);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_version.setText("V"+mAppVersion+"\n "+jsonObject.getString(Constance.text));
        tv_upgrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("https://a.app.qq.com/o/simple.jsp?pkgname=com.juhao.home");
                intent.setData(content_url);
                startActivity(intent);
            }
        });
        dialog.show();

    }
}
