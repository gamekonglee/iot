package com.juhao.home.intelligence;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.alink.linksdk.channel.core.base.AError;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileRequestListener;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileChannel;
import com.aliyun.alink.linksdk.tools.ALog;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialListener;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageError;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageImpl;
import com.aliyun.iot.aep.sdk.credential.data.IoTCredentialData;
import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.ilop.demo.DemoApplication;

import com.juhao.home.R;
import com.juhao.home.ui.ItHomeMainFragment;
import com.mainintelligence.ItApplicationFragment;
import com.mainintelligence.ItMineMainFragment;

/**
 * Created by gamekonglee on 2018/7/7.
 */

public class MainIntelligenceActivity extends IntelBaseActivity implements View.OnClickListener {
    private static final String TAG = "mainIntel";
    private TextView frag_top_tv;
    private TextView frag_product_tv;
    private TextView frag_mine_tv;
    private ImageView frag_top_iv;
    private ImageView frag_product_iv;
    private ImageView frag_mine_iv;
    private ItHomeMainFragment mHomeFragment;
    private Fragment currentFragmen;
    private ItApplicationFragment mProductFragment;
    private ItMineMainFragment mMineFragment;
    private Handler mH = new Handler();
    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initView() {
        setContentView(R.layout.activity_intelligence);
        setColor(this, Color.WHITE);
        frag_top_tv = (TextView) findViewById(R.id.frag_top_tv);
        frag_product_tv = (TextView) findViewById(R.id.frag_product_tv);
        frag_mine_tv = (TextView) findViewById(R.id.frag_mine_tv);
        frag_top_iv = (ImageView) findViewById(R.id.frag_top_iv);
        frag_product_iv = (ImageView) findViewById(R.id.frag_product_iv);
        frag_mine_iv = (ImageView) findViewById(R.id.frag_mine_iv);

        findViewById(R.id.frag_top_tv).setOnClickListener(this);
        findViewById(R.id.frag_product_tv).setOnClickListener(this);
        findViewById(R.id.frag_mine_tv).setOnClickListener(this);
        findViewById(R.id.frag_top_ll).setOnClickListener(this);
        findViewById(R.id.frag_product_ll).setOnClickListener(this);
        findViewById(R.id.frag_mine_ll).setOnClickListener(this);
        initTab();
        findViewById(R.id.frag_top_ll).performClick();

        if (LoginBusiness.isLogin()) {
            LoginBusiness.logout(new ILogoutCallback() {
                @Override
                public void onLogoutSuccess() {

                }

                @Override
                public void onLogoutFailed(int i, String s) {

                }
            });

//            Router.getInstance().toUrl(MainIntelligenceActivity.this, "page/ilopmain");
        } else {
            LoginBusiness.login(new ILoginCallback() {
                @Override
                public void onLoginSuccess() {
                    //登录成功后，bindaccount
                    IoTCredentialManageImpl ioTCredentialManage =  IoTCredentialManageImpl.getInstance(DemoApplication.getInstance());

                    if(TextUtils.isEmpty(ioTCredentialManage.getIoTToken())){
                        ioTCredentialManage.asyncRefreshIoTCredential(new IoTCredentialListener() {
                            @Override
                            public void onRefreshIoTCredentialSuccess(IoTCredentialData ioTCredentialData) {
                                MobileChannel.getInstance().bindAccount(ioTCredentialData.iotToken, new IMobileRequestListener() {
                                    @Override
                                    public void onSuccess(String s) {
                                        ALog.i(TAG,"mqtt bindAccount onSuccess");

                                    }

                                    @Override
                                    public void onFailure(AError aError) {
                                        ALog.i(TAG,"mqtt bindAccount onFailure aError = " + aError.getMsg());
                                    }
                                });
                            }

                            @Override
                            public void onRefreshIoTCredentialFailed(IoTCredentialManageError ioTCredentialManageError) {
                                ALog.i(TAG,"mqtt bindAccount onFailure ");

                            }
                        });
                    } else {
                        MobileChannel.getInstance().bindAccount(ioTCredentialManage.getIoTToken(), new IMobileRequestListener() {
                            @Override
                            public void onSuccess(String s) {
                                ALog.i(TAG,"mqtt bindAccount onSuccess ");
                            }

                            @Override
                            public void onFailure(AError aError) {
                                ALog.i(TAG,"mqtt bindAccount onFailure aError = " + aError.getMsg());

                            }
                        });
                    }

//                            //注册虚拟设备
//                            String[] pks = {"a1nbd4BjB3N", "a186j8fot9K", "a1XKEJTOkPL", "a1aJCduQG0p"};
//                            for (String pk : pks) {
//                                registerVirtualDevice(pk);
//                            }

                    mH.postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            Router.getInstance().toUrl( MainIntelligenceActivity.this, "page/ilopmain");
                        }
                    }, 0);

                }


                @Override
                public void onLoginFailed(int i, String s) {
                    Toast.makeText(getApplicationContext(), "登录失败 :" + s, Toast.LENGTH_SHORT).show();
                }
            });
            finish();
        }


//

      /*  LocalDeviceMgr.getInstance().startDiscovery(MainIntelligenceActivity.this, new IDiscoveryListener() {
            @Override
            public void onLocalDeviceFound(DeviceInfo deviceInfo) {
                Log.e("deviceinfo",deviceInfo.toString());
            }

            @Override
            public void onEnrolleeDeviceFound(List<DeviceInfo> list) {
                Log.e("deviceinfo",list.toString());
            }
        });*/
        // 构建请求
//        IoTRequest request = new IoTRequestBuilder()
//                .setScheme(Scheme.HTTP) // 如果是HTTPS，可以省略本设置
////                .setHost(host) // 如果是IoT官方服务，可以省略本设置
//                .setPath("/kit/debug/ping") // 参考业务API文档，设置path
//                .setApiVersion("1.0.0")  // 参考业务API文档，设置apiVersion
//                .addParam("input", "测试") // 参考业务API文档，设置params,也可使用setParams(Map<Strign,Object> params)
//                .build();
//
//        // 获取Client实例，并发送请求
//        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
//        ioTAPIClient.send(request, new IoTCallback() {
//            @Override
//            public void onFailure(IoTRequest request, Exception e) {
//                // 根据 e， 处理异常
//            }
//
//            @Override
//            public void onResponse(IoTRequest request, IoTResponse response) {
//                int code = response.getCode();
//
//                // 200 代表成功
//                if(200 != code){
//                    String mesage = response.getMessage();
//                    String localizedMsg = response.getLocalizedMsg();
//                    // 根据 mesage 和 localizedMsg，处理失败信息
//                    return;
//                }
//
//                Object data = response.getData();
//                // 解析 data
//            }
//        });

    }

    @Override
    protected void initData() {

    }

    /**
     * 初始化底部标签
     */
    private void initTab() {
        if (mHomeFragment == null) {
            mHomeFragment = new ItHomeMainFragment();
        }
        if (!mHomeFragment.isAdded()) {
            // 提交事务
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.top_bar, mHomeFragment).commit();

            // 记录当前Fragment
            currentFragmen = mHomeFragment;
        }
    }
    private int mCurrenTabId;

    /**
     * 选择指定的item
     *
     * @param currenTabId
     */
    public void selectItem(int currenTabId) {
        //	设置 如果电机的是当前的的按钮 再次点击无效
        if (mCurrenTabId != 0 && mCurrenTabId == currenTabId) {
            return;
        }
        //点击前先默认全部不被选中
        defaultTabStyle();

        mCurrenTabId = currenTabId;
        switch (currenTabId) {
            case R.id.frag_top_ll:
                frag_top_tv.setSelected(true);
                frag_top_iv.setSelected(true);
                clickTab1Layout();
                break;
            case R.id.frag_product_ll:
                frag_product_tv.setSelected(true);
                frag_product_iv.setSelected(true);
                clickTab2Layout();
                break;
//            case R.id.frag_match_ll:
////                frag_match_tv.setSelected(true);
////                frag_match_iv.setSelected(true);
//                clickTab3Layout();
//                break;
            case R.id.frag_mine_ll:
                frag_mine_tv.setSelected(true);
                frag_mine_iv.setSelected(true);
                clickTab5Layout();
                break;
            case R.id.frag_top_tv:
                frag_top_tv.setSelected(true);
                frag_top_iv.setSelected(true);
                clickTab1Layout();
                break;
            case R.id.frag_product_tv:
                frag_product_tv.setSelected(true);
                frag_product_iv.setSelected(true);
                clickTab2Layout();
                break;
            case R.id.frag_mine_tv:
                frag_mine_tv.setSelected(true);
                frag_mine_iv.setSelected(true);
                clickTab5Layout();
                break;
        }
    }

    /**
     * 点击第1个tab
     */
    public void clickTab1Layout() {
        if (mHomeFragment == null) {
            mHomeFragment = new ItHomeMainFragment();
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), mHomeFragment);
    }
    /**
     * 点击第2个tab
     */
    public void clickTab2Layout() {
        if (mProductFragment == null) {
            mProductFragment = new ItApplicationFragment();
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), mProductFragment);

    }

    /**
     * 点击第3个tab
     */
    public void clickTab3Layout() {
//        if (mMatchFragment == null) {
//            mMatchFragment = new ProgrammeFragment();
//        }
//        addOrShowFragment(getSupportFragmentManager().beginTransaction(), mMatchFragment);
//        startActivity(new Intent(this,ItDevicesActivity.class));
    }


    /**
     * 点击第5个tab
     */
    public void clickTab5Layout() {
        if (mMineFragment == null) {
            mMineFragment = new ItMineMainFragment();
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), mMineFragment);

    }

    /**
     * 默认全部不被选中
     */
    private void defaultTabStyle() {
        frag_top_tv.setSelected(false);
        frag_top_iv.setSelected(false);
        frag_product_tv.setSelected(false);
        frag_product_iv.setSelected(false);
        frag_mine_tv.setSelected(false);
        frag_mine_iv.setSelected(false);
    }

    @Override
    public void onClick(View v) {
        //	设置 如果点击的是当前的的按钮 再次点击无效
        if (mCurrenTabId != 0 && mCurrenTabId == v.getId()) {
            return;
        }

        selectItem(v.getId());
    }

    /**
     * 添加或者显示碎片
     *
     * @param transaction
     * @param fragment
     */
    private void addOrShowFragment(FragmentTransaction transaction,
                                   Fragment fragment) {
        if (currentFragmen == fragment)
            return;

        if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
            transaction.hide(currentFragmen)
                    .add(R.id.top_bar, fragment).commit();
        } else {
            transaction.hide(currentFragmen).show(fragment).commit();
        }

        currentFragmen = fragment;
    }
}
