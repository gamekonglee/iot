package com.juhao.home;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.model.OpenAccountSession;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIConfigs;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIService;
import com.alibaba.sdk.android.openaccount.ui.callback.EmailRegisterCallback;
import com.alibaba.sdk.android.openaccount.ui.impl.OpenAccountUIServiceImpl;
import com.alibaba.sdk.android.openaccount.ui.ui.LoginActivity;
import com.aliyun.iot.ilop.demo.page.ilopmain.MainActivity;
import com.juhao.home.ui.RegisterAreaCodeActivity;
import com.util.Constance;
import com.util.LogUtils;
import com.util.MyShare;

public class MyLoginActivity extends LoginActivity {

    private CheckBox cb_agree;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
//        setContentView(R.layout.activity_login);
        LinearLayout main=findViewById(R.id.main);
        main.setBackgroundColor(Color.WHITE);
        oauthWidget.setVisibility(View.GONE);

        ((LinearLayout)main.getChildAt(0)).getChildAt(0).setBackground(null);
        LinearLayout child1=((LinearLayout)main.getChildAt(0));
        LinearLayout.LayoutParams temp= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        temp.setMargins(15,15,15,15);
        child1.setLayoutParams(temp);
        LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) (main.getChildAt(0)).getLayoutParams();
        layoutParams.topMargin=UIUtils.dip2PX(100);
        View view=View.inflate(this,R.layout.view_line,null);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,UIUtils.dip2PX(1));
        ((LinearLayout)((LinearLayout)main.getChildAt(0)).getChildAt(0)).addView(view,lp);


        View view_bottom=View.inflate(this,R.layout.view_login_bottom,null);
        View viewTemp=new View(this);
        LinearLayout.LayoutParams pl=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0);
        LinearLayout.LayoutParams pl2=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,UIUtils.dip2PX(50));
        pl.weight=1;
        main.addView(viewTemp,pl);
        main.addView(view_bottom,pl2);
        cb_agree = view_bottom.findViewById(R.id.cb_agree);
        TextView tv_bottom_1=view_bottom.findViewById(R.id.tv_bottom_1);
        TextView tv_bottom_2=view_bottom.findViewById(R.id.tv_bottom_2);
        tv_bottom_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.showSystemStopDialog(MyLoginActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cb_agree.setChecked(true);
                    }
                });
            }
        });
        tv_bottom_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.showSystemStopDialog(MyLoginActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cb_agree.setChecked(true);
                    }
                });
            }
        });
        Button var3 = (Button)this.findViewById("next");
        var3.setBackgroundResource(R.drawable.bg_corner_full_orange_15);
        var3.setTextColor(Color.WHITE);
        var3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb_agree.isChecked()){
                  login(view);
                }else {
                    UIUtils.showSystemStopDialog(MyLoginActivity.this, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            cb_agree.setChecked(true);
                        }
                    });
                }
            }
        });
        this.findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog=new Dialog(MyLoginActivity.this,R.style.customDialog);
                dialog.setContentView(R.layout.dialog_register_type_choose);
                TextView tv_china=dialog.findViewById(R.id.tv_china);
                TextView tv_oversea=dialog.findViewById(R.id.tv_oversea);
                tv_china.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();

                        OpenAccountUIService openAccountUIService=new OpenAccountUIServiceImpl();
//                        OpenAccountUIConfigs.AccountPasswordLoginFlow.supportForeignMobileNumbers=true;
//                        OpenAccountUIConfigs.MobileResetPasswordLoginFlow.supportForeignMobileNumbers=true;
                        OpenAccountUIConfigs.MobileRegisterFlow.supportForeignMobileNumbers=true;
                        openAccountUIService.showRegister(MyLoginActivity.this,MyRegisterActivity.class,
                        new LoginCallback() {
                            @Override
                            public void onSuccess(OpenAccountSession openAccountSession) {

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
                        openAccountUIService.showEmailRegister(MyLoginActivity.this, MyRegisterActivity.class,
                                new EmailRegisterCallback() {
                                    @Override
                                    public void onEmailSent(String s) {

                                    }

                                    @Override
                                    public void onSuccess(OpenAccountSession openAccountSession) {
//                                        startActivity(new Intent(MyLoginActivity.this, MainActivity.class));
//                                        finish();
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
        });


//        R.layout.ali_sdk_openaccount_login
    }



}
