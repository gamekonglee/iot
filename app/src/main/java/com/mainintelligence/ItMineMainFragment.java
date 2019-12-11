package com.mainintelligence;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.BaseFragment;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.aliyun.alink.linksdk.channel.core.base.AError;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileRequestListener;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileChannel;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelEventCallback;
import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.connectchannel.log.ALog;
import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.login.data.UserInfo;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.juhao.home.MyLoginActivity;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.juhao.home.suggestion.SuggestionHomeActivity;
import com.juhao.home.ui.AboutActivity;
import com.juhao.home.ui.AlexaActivity;
import com.juhao.home.ui.DeviceShareActivity;
import com.juhao.home.ui.NoticeActivity;
import com.juhao.home.ui.SettingActivity;
import com.juhao.home.ui.TmActivity;
import com.juhao.home.ui.UserInfoActivity;
import com.juhao.home.ui.WebViewActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.util.Constance;
import com.util.LogUtils;
import com.util.MyShare;
import com.view.MyToast;

import java.util.Map;

import static com.alibaba.sdk.android.ams.common.global.AmsGlobalHolder.getPackageName;


/**
 * Created by gamekonglee on 2018/7/7.
 */

public class ItMineMainFragment extends BaseFragment implements View.OnClickListener {

    private TextView tv_name;
    private String code;
    private TextView tv_3th;
    private TextView tv_click_to_setnick;
    private ImageView iv_head;
    private View mViewContent;

    @Override
    protected void initController() {

    }

    @Override
    protected void initViewData() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mViewContent == null) {
            mViewContent = inflater.inflate(R.layout.frag_it_main_mine, container, false);
        }

        // 缓存View判断是否含有parent, 如果有需要从parent删除, 否则发生已有parent的错误.
        ViewGroup parent = (ViewGroup) mViewContent.getParent();
        if (parent != null) {
            parent.removeView(mViewContent);
        }

        return mViewContent;
    }
    static final String BIND = "/uc/bindPushChannel";
    static final String UN_BIND = "/uc/unbindPushChannel";
    @Override
    protected void initView() {
        TextView tv_componet=getView().findViewById(R.id.tv_component);
        TextView tv_notice=getView().findViewById(R.id.tv_notice);
        TextView tv_share=getView().findViewById(R.id.tv_share);
        tv_3th = getView().findViewById(R.id.tv_3th);
        TextView tv_suggestion=getView().findViewById(R.id.tv_suggestion);
        TextView tv_setting=getView().findViewById(R.id.tv_setting);
        TextView tv_about=getView().findViewById(R.id.tv_about);
        View iv_scale=getView().findViewById(R.id.iv_scale);
        tv_name = getView().findViewById(R.id.tv_name);
        iv_head = getView().findViewById(R.id.iv_head);
        TextView tv_change_version=getView().findViewById(R.id.tv_change_version);
        tv_click_to_setnick = getView().findViewById(R.id.tv_click_to_setnick);

        tv_componet.setOnClickListener(this);
        tv_notice.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        tv_3th.setOnClickListener(this);
        tv_suggestion.setOnClickListener(this);
        tv_setting.setOnClickListener(this);
        tv_about.setOnClickListener(this);
        iv_scale.setOnClickListener(this);
        tv_change_version.setOnClickListener(this);
        tv_click_to_setnick.setOnClickListener(this);
        getView().findViewById(R.id.rl_user_center).setOnClickListener(this);

//        PushServiceFactory.init(DemoApplication.getContext());
//        CloudPushService pushService = PushServiceFactory.getCloudPushService();
//        pushService.setSecurityGuardAuthCode(DemoApplication.securityIndex);
//
//
//        pushService.register(DemoApplication.getContext(), new CommonCallback() {
//            @Override
//            public void onSuccess(String response) {
//                Log.d("aep-demo", "init cloudchannel success");
////                String path = LoginBusiness.isLogin() ? BIND : UN_BIND;
////                request(path);
//
//                // set device id
//                String deviceId = PushServiceFactory.getCloudPushService().getDeviceId();
//                if (TextUtils.isEmpty(deviceId)) {
//                    deviceId = "没有获取到";
//                }
////                EnvConfigure.putEnvArg(EnvConfigure.KEY_DEVICE_ID, deviceId);
//                LogUtils.logE("deviceid",deviceId);
//                if (LoginBusiness.isLogin()) {
//                    request(BIND);
//                }
//            }
//
//            @Override
//            public void onFailed(String errorCode, String errorMessage) {
//                Log.d("aep-demo", "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
//            }
//        });
        if(DemoApplication.is_national){
            tv_change_version.setText(getString(R.string.str_change_version_2));
//            tv_3th.setVisibility(View.GONE);
        }else {
            tv_change_version.setText(getString(R.string.str_change_version));
        }
//        String devicesid=pushService.getDeviceId();
//        LogUtils.logE("deviceid,",devicesid);
    }

    @Override
    public void onResume() {
        super.onResume();
        UserInfo userInfo=LoginBusiness.getUserInfo();
        if(userInfo!=null){
            String phone=userInfo.userPhone;
            if(TextUtils.isEmpty(phone)){
                phone=userInfo.userEmail;
            }
            if(phone.length()>10){
                phone=phone.substring(0,3)+"******"+phone.substring(phone.length()-3);
            }
            if(!TextUtils.isEmpty(phone)){
                tv_name.setText(phone);
            }

            String nickname=userInfo.userNick;
            if(!TextUtils.isEmpty(nickname)){
                tv_click_to_setnick.setText(nickname);
            }
            String award=userInfo.userAvatarUrl;
            if(!TextUtils.isEmpty(award)){
                ImageLoader.getInstance().displayImage(award,iv_head);
            }

        }
    }

    @Override
    protected void initData() {

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_scale:
                if(Build.VERSION.SDK_INT>=23){
                    checkPermission();
                }else {
                    camear();
                }
                break;
            case R.id.tv_suggestion:
                String code = "link://router/feedback";
                Bundle bundle = new Bundle();
//                bundle.putString("sceneType","ilop"); // 传入插件参数，没有参数则不需要这一行
//                Router.getInstance().toUrlForResult(getActivity(), code, 1, bundle);
                startActivity(new Intent(getActivity(), SuggestionHomeActivity.class));
                break;
            case R.id.tv_setting:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.tv_about:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case R.id.tv_notice:
                code = "link://router/devicenotices";
                startActivity(new Intent(getActivity(), NoticeActivity.class));
                break;
            case R.id.tv_share:
                startActivity(new Intent(getActivity(), DeviceShareActivity.class));
                break;
            case R.id.tv_change_version:
                UIUtils.showSingleWordDialog(getActivity(), "确定要切换吗？", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyShare.get(getActivity()).putBoolean(Constance.is_not_first,false);
                        boolean isNational= MyShare.get(getActivity()).getBoolean(Constance.is_national);
                        isNational=!isNational;
                        MyShare.get(getActivity()).putBoolean(Constance.is_national,isNational);
                        MobileChannel.getInstance().unBindAccount(new IMobileRequestListener() {
                            @Override
                            public void onSuccess(String jsonData) {
                            }
                            @Override
                            public void onFailure(AError error) {
                            }
                        });


                        LoginBusiness.logout(new ILogoutCallback() {
                            @Override
                            public void onLogoutSuccess() {
//                                MyToast.show(getActivity(),getString(R.string.str_logout_success));

//                                  startActivity(new Intent(getActivity(), MyLoginActivity.class));
//                                  getActivity().finish();
//                                Log.i(TAG,"登出成功");
                                final Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName());
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                getActivity().finish();
                                DemoApplication.activityList.get(0).finish();
                                System.exit(0);
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                            @Override
                            public void onLogoutFailed(int code, String error) {
                                final Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName());
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        });
                    }
                });
                break;
            case R.id.tv_3th:
                if(DemoApplication.is_national){
                startActivity(new Intent(getActivity(), AlexaActivity.class));
                }else {
                startActivityForResult(new Intent(getActivity(), TmActivity.class),222);
                }
                break;
            case R.id.rl_user_center:
                startActivity(new Intent(getActivity(), UserInfoActivity.class));
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==222&&resultCode==222&&data!=null&&data.getStringExtra("AuthCode")!=null){
            bindAccount(data.getStringExtra("AuthCode"));
        }
    }
    public void bindAccount(String authCode) {

        JSONObject params = new JSONObject();
        if (null != authCode) {
            params.put("authCode", authCode);
        }
        Map<String, Object> requestMap = params.getInnerMap();

        IoTRequest ioTRequest = new IoTRequestBuilder()
                .setAuthType("iotAuth")
                .setApiVersion("1.0.5")
                .setPath("/account/taobao/bind")
                .setParams(requestMap)
                .setScheme(Scheme.HTTPS)
                .build();
        new IoTAPIClientFactory().getClient().send(ioTRequest, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {

            }
        });
    }

    public void checkPermission(){
        if(ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            //权限发生了改变 true  //  false 小米
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.CAMERA)){

                new AlertDialog.Builder(getActivity()).setTitle("title")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 请求授权
                                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA},1);

                            }
                        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();



            }else {
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA},1);

            }

        }else{
            camear();
        }
    }

    private void camear() {
        Router.getInstance().toUrl(getActivity(), "page/scan");
    }

    /**
     *
     * @param requestCode
     * @param permissions 请求的权限
     * @param grantResults 请求权限返回的结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            // camear 权限回调

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                // 表示用户授权
//                Toast.makeText(getActivity(), " user Permission" , Toast.LENGTH_SHORT).show();

                camear();


            } else {

                //用户拒绝权限
//                Toast.makeText(getActivity(), " no Permission" , Toast.LENGTH_SHORT).show();

            }



        }
}

    void request(String path) {
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        String deviceId = pushService.getDeviceId();
        if (TextUtils.isEmpty(deviceId)) {
            return;
        }
        String apiVersion = "1.0.2";
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setAuthType("iotAuth")
                .setScheme(Scheme.HTTPS)
                .setPath(path)
                .setApiVersion(apiVersion)
                .addParam("deviceType", "ANDROID")
                .addParam("deviceId", deviceId);
        IoTRequest request = builder.build();
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                e.printStackTrace();
             LogUtils.logE("binddevice","onfailure");
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                LogUtils.logE("binddevice","onresponse");

            }
        });
    }

}
