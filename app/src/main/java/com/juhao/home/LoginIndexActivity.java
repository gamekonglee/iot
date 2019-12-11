package com.juhao.home;

import android.app.Dialog;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.BaseActivity;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.model.OpenAccountSession;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIConfigs;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIService;
import com.alibaba.sdk.android.openaccount.ui.callback.EmailRegisterCallback;
import com.alibaba.sdk.android.openaccount.ui.impl.OpenAccountUIServiceImpl;
import com.alibaba.sdk.android.openaccount.ui.ui.RegisterActivity;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.aliyun.iot.ilop.demo.page.ilopmain.MainActivity;
import com.util.Constance;
import com.util.LogUtils;
import com.util.MyShare;
import com.view.MyToast;

import de.greenrobot.event.EventBus;

public class LoginIndexActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_choose_region;
    private TextView tv_register;
    private TextView tv_login;
    private boolean is_national;
    private boolean restart;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_login_index);
        fullScreen(this);
        tv_choose_region = findViewById(R.id.tv_choose_region);
        tv_register = findViewById(R.id.tv_register);
        tv_login = findViewById(R.id.tv_login);
        tv_choose_region.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        tv_login.setOnClickListener(this);
//        EventBus.getDefault().register(this);
        DemoApplication.getActivityList().add(this);
    }

    @Override
    protected void initData() {
        is_national = MyShare.get(this).getBoolean(Constance.is_national);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_choose_region:
                final Dialog dialog=new Dialog(this,R.style.customDialog);
                dialog.setContentView(R.layout.dialog_region_choose);
                TextView tv_china=dialog.findViewById(R.id.tv_china);
                TextView tv_oversea=dialog.findViewById(R.id.tv_oversea);
                tv_china.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(is_national){
                            restart = true;
                        }else {
                            restart = false;
                        }
                        MyShare.get(LoginIndexActivity.this).putBoolean(Constance.is_national,false);
                        tv_choose_region.setText("中国站");
                        dialog.dismiss();
                    }
                });
                tv_oversea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(is_national){
                            restart = false;
                        }else {
                            restart = true;
                        }
                        MyShare.get(LoginIndexActivity.this).putBoolean(Constance.is_national,true);
                        tv_choose_region.setText("国际站");
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case R.id.tv_register:
                showRegister();
//                startActivityForResult(new Intent(this, RegisterActivity.class),200);
                break;
            case R.id.tv_login:
                if(restart){
                    final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    DemoApplication.activityList.get(0).finish();
                    System.exit(0);
                    android.os.Process.killProcess(android.os.Process.myPid());
                }else {
                showLogin();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==200){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void showLogin() {
        OpenAccountUIService openAccountUIService=new OpenAccountUIServiceImpl();
        openAccountUIService.showLogin(LoginIndexActivity.this, MyLoginActivity.class, new LoginCallback() {
            @Override
            public void onSuccess(OpenAccountSession openAccountSession) {
                startActivity(new Intent(LoginIndexActivity.this, MainActivity.class));
                finish();
//                Toast.makeText(MainActivity.this, "login"+LoginBusiness.isLogin(), Toast.LENGTH_SHORT).show();
//                LogUtils.logE("login","success"+openAccountSession.getLoginId());
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtils.logE("login","failure"+s);
            }
        });
    }
    private void showRegister() {
        final Dialog dialog=new Dialog(LoginIndexActivity.this,R.style.customDialog);
        dialog.setContentView(R.layout.dialog_register_type_choose);
        TextView tv_china=dialog.findViewById(R.id.tv_china);
        TextView tv_oversea=dialog.findViewById(R.id.tv_oversea);
        tv_china.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                OpenAccountUIService openAccountUIService=new OpenAccountUIServiceImpl();
                OpenAccountUIConfigs.MobileRegisterFlow.supportForeignMobileNumbers=true;
                openAccountUIService.showRegister(LoginIndexActivity.this, new LoginCallback() {
                    @Override
                    public void onSuccess(OpenAccountSession openAccountSession) {
                        startActivity(new Intent(LoginIndexActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
            }
        });
        tv_oversea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                OpenAccountUIService openAccountUIService=new OpenAccountUIServiceImpl();
                openAccountUIService.showEmailRegister(LoginIndexActivity.this, new EmailRegisterCallback() {
                            @Override
                            public void onEmailSent(String s) {

                            }

                            @Override
                            public void onSuccess(OpenAccountSession openAccountSession) {
//                                        startActivity(new Intent(MyLoginActivity.this, MainActivity.class));
//                                        finish();
                                startActivity(new Intent(LoginIndexActivity.this, MainActivity.class));
                                finish();
                            }

                            @Override
                            public void onFailure(int i, String s) {

                            }
                        }
                );
            }
        });
        dialog.show();
    }

    private long exitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                exitTime = System.currentTimeMillis();
                MyToast.show(this,getString(R.string.str_exit_toast));
            } else {
                if(DemoApplication.activityList.size()>0){
                DemoApplication.activityList.get(0).finish();
                }
                System.exit(0);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
