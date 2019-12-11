package com.juhao.home.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.BaseActivity;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.demo.ipcview.activity.IPCameraActivity;
import com.aliyun.iot.demo.ipcview.activity.NewCardVideoActivity;
import com.aliyun.iot.demo.ipcview.activity.SettingsActivity;
import com.aliyun.iot.demo.ipcview.constants.Constants;
import com.aliyun.iot.demo.ipcview.manager.SettingsCtrl;
import com.aliyun.iot.demo.ipcview.manager.SharePreferenceManager;
import com.aliyun.iot.demo.ipcview.utils.NetWorkChangeListener;
import com.aliyun.iot.demo.ipcview.utils.NetWorkStateReceiver;
import com.aliyun.iot.demo.ipcview.utils.NetworkStateEnum;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.aliyun.iotx.linkvisual.media.audio.AudioParams;
import com.aliyun.iotx.linkvisual.media.audio.ILiveIntercom;
import com.aliyun.iotx.linkvisual.media.audio.LiveIntercom;
import com.aliyun.iotx.linkvisual.media.audio.LiveIntercomException;
import com.aliyun.iotx.linkvisual.media.audio.audiotrack.SimpleStreamAudioTrack;
import com.aliyun.iotx.linkvisual.media.audio.listener.OnAudioBufferReceiveListener;
import com.aliyun.iotx.linkvisual.media.audio.listener.OnAudioParamsChangeListener;
import com.aliyun.iotx.linkvisual.media.audio.listener.OnTalkReadyListener;
import com.aliyun.iotx.linkvisual.media.audio.record.AudioRecordListener;
import com.aliyun.iotx.linkvisual.media.audio.record.SimpleAudioRecord;
import com.aliyun.iotx.linkvisual.media.video.PlayerException;
import com.aliyun.iotx.linkvisual.media.video.beans.PlayInfo;
import com.aliyun.iotx.linkvisual.media.video.beans.PlayerState;
import com.aliyun.iotx.linkvisual.media.video.listener.OnErrorListener;
import com.aliyun.iotx.linkvisual.media.video.listener.OnPlayerStateChangedListener;
import com.aliyun.iotx.linkvisual.media.video.listener.OnPreparedListener;
import com.aliyun.iotx.linkvisual.media.video.player.LivePlayer;
import com.aliyun.iotx.linkvisual.media.video.player.VodPlayer;
import com.aliyun.iotx.linkvisual.media.video.views.ZoomableTextureView;
import com.bean.ImageBean;
import com.google.android.exoplayer2.Player;
import com.juhao.home.IssApplication;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.util.ApiClientForIot;
import com.util.Constance;
import com.util.DateUtils;
import com.util.LogUtils;
import com.util.ScannerUtils;
import com.view.FreeView;
import com.view.MyToast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static android.os.Environment.DIRECTORY_MOVIES;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;

public class LivingPlayerActivity extends BaseActivity implements View.OnClickListener, MediaScannerConnection.MediaScannerConnectionClient {

    private static final String TAG = "livingplayer";
    private static final int REQUEST_EXTNERNO = 4232;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 353;
    private  String SERVICE_IDENTIFY = "PTZActionControl";
    private ZoomableTextureView surface_view;
    private LivePlayer player;
    private String iotId;
    private TextView tv_title;
    private FreeView freeView;
    private TextView tv_stream_quailty;
    private boolean isControlling;
    private View rl_control;
    private ImageView iv_shengyin;
    private ImageView iv_luxiang;
    private ImageView iv_luyin;
    private ImageView iv_jietu;
    private ImageView iv_suofang;
    private int ori;
    private boolean isOpenSoud;
    private boolean isOpenYuyin;
    private boolean isRecording;
    private View ll_bottom;
    private View rl_top;
    private View ll_mid;
    private boolean isFullScreen;
    private String currentTIme;
    private File fileName;
    private TextView tv_klx;
    private Dialog showImageDialog;
    private TextView tv_pic;
    private AcousticEchoCanceler acousticEchoCanceler;
    private NoiseSuppressor noiseSuppressor;
    private boolean isLiveIntercoming = false;
    private ILiveIntercom liveIntercom;
    private Animation alphaAnimation;
    private int defaultDefinition;
    private boolean isRecordingMp4;
    private File file;
    private Handler uiHandler;
    private ImageView iv_setting;

    @Override
    protected void InitDataView() {
        String title=getIntent().getStringExtra(Constance.title);
        if(TextUtils.isEmpty(title)){
            title="摄像头";
        }
        tv_title.setText(title);
    }

    @Override
    protected void initController() {

    }
//
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
            IssApplication.ori = newConfig.orientation;
        if(IssApplication.ori == Configuration.ORIENTATION_LANDSCAPE){
            rl_control.setVisibility(View.GONE);
            ll_bottom.setVisibility(View.GONE);
            rl_top.setVisibility(View.GONE);
            ll_mid.setVisibility(View.GONE);
            //横屏
        }else{
            //竖屏
            rl_control.setVisibility(View.VISIBLE);
            ll_bottom.setVisibility(View.VISIBLE);
            rl_top.setVisibility(View.VISIBLE);
            ll_mid.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_living_player);
        fullScreen(this);
        uiHandler = new Handler(getMainLooper());
        surface_view = findViewById(R.id.surface_view);
        // 构造播放器实例
        player = new LivePlayer();
        tv_title = findViewById(R.id.tv_title);
        freeView = findViewById(R.id.freeview);
        tv_stream_quailty = findViewById(R.id.tv_stream_quailty);
        rl_control = findViewById(R.id.rl_control);
        iv_shengyin = findViewById(R.id.iv_shengyin);
        iv_luxiang = findViewById(R.id.iv_luxiang);
        iv_luyin = findViewById(R.id.iv_yuyin);
        iv_jietu = findViewById(R.id.iv_jietu);
        iv_suofang = findViewById(R.id.iv_suofang);
        ll_bottom = findViewById(R.id.ll_bottom);
        rl_top = findViewById(R.id.rl_top);
        ll_mid = findViewById(R.id.ll_mid);
        tv_klx = findViewById(R.id.tv_klx);
        tv_pic = findViewById(R.id.tv_pic);
        iv_setting = findViewById(R.id.iv_setting);

        iv_shengyin.setOnClickListener(this);
        iv_luxiang.setOnClickListener(this);
        iv_luyin.setOnClickListener(this);
        iv_jietu.setOnClickListener(this);
        iv_suofang.setOnClickListener(this);
        tv_klx.setOnClickListener(this);
        tv_pic.setOnClickListener(this);
        iv_setting.setOnClickListener(this);
        if(IssApplication.ori == Configuration.ORIENTATION_LANDSCAPE){
            rl_control.setVisibility(View.GONE);
            ll_bottom.setVisibility(View.GONE);
            rl_top.setVisibility(View.GONE);
            ll_mid.setVisibility(View.GONE);
            //横屏
        }else{
            //竖屏
            rl_control.setVisibility(View.VISIBLE);
            ll_bottom.setVisibility(View.VISIBLE);
            rl_top.setVisibility(View.VISIBLE);
            ll_mid.setVisibility(View.VISIBLE);
        }
        freeView.setBackground(getResources().getDrawable(R.drawable.shape_blue_corner));
//        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }


        tv_stream_quailty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View popup_layout=View.inflate(LivingPlayerActivity.this,R.layout.popup_stream_quality,null);
                final PopupWindow popupWindow=new PopupWindow(popup_layout, UIUtils.dip2PX(50),UIUtils.dip2PX(90));
//                popupWindow.setContentView(popup_layout);
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(true);
                popupWindow.getContentView().findViewById(R.id.tv_stream_quailty_0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tv_stream_quailty.setText("流畅");
                        popupWindow.dismiss();
                        setProperties("StreamVideoQuality",0);
                    }
                });
                popupWindow.getContentView().findViewById(R.id.tv_stream_quailty_1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tv_stream_quailty.setText("标清");
                        popupWindow.dismiss();
                        setProperties("StreamVideoQuality",1);
                    }
                });
                popupWindow.getContentView().findViewById(R.id.tv_stream_quailty_2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tv_stream_quailty.setText("高清");
                        popupWindow.dismiss();
                        setProperties("StreamVideoQuality",2);
                    }
                });

                int[] location=new int[2];
                tv_stream_quailty.getLocationOnScreen(location);
                LogUtils.logE("tv_quaility",location[0]+","+location[1]);
                if(IssApplication.ori==Configuration.ORIENTATION_LANDSCAPE){
                    popupWindow.showAtLocation(tv_stream_quailty, Gravity.LEFT|Gravity.TOP,location[0],location[1]-UIUtils.dip2PX(100));
                }else {
                popupWindow.showAtLocation(tv_stream_quailty, Gravity.LEFT|Gravity.TOP,location[0],location[1]-UIUtils.dip2PX(100));
                }
            }
        });
        freeView.setMoveListener(new FreeView.OnSlidingDirectionListener() {
            @Override
            public void up() {
                invokeService("ActionType",2);
            }

            @Override
            public void up_left() {
                invokeService("ActionType",4);
            }

            @Override
            public void up_right() {
                invokeService("ActionType",5);
            }

            @Override
            public void down() {
                invokeService("ActionType",3);
            }

            @Override
            public void down_left() {
                invokeService("ActionType",6);
            }

            @Override
            public void down_right() {
                invokeService("ActionType",7);
            }

            @Override
            public void left() {
                invokeService("ActionType",0);
            }

            @Override
            public void right() {
                invokeService("ActionType",1);
            }

            @Override
            public void ok() {

            }
        });


        alphaAnimation = AnimationUtils.loadAnimation(this, com.aliyun.iot.demo.ipcview.R.anim.alpha_loop);
        initPlayer();
        initLiveIntercom();
        SERVICE_IDENTIFY="PTZCalibrate";
        invokeService("PTZCalibrate",0);
        SERVICE_IDENTIFY="PTZActionControl";
    }

    private void initPlayer() {

        surface_view.setMaxScale(4);//设置最大的缩放比例，可不设置，默认为4
// 设置surfaceview, 必须为GLSurfaceView
// 注意:GLSurfaceView必须在Activity的onResume和onPause回调方法中调用GLSurfaceView的onResume和onPause方法
        player.setTextureView(surface_view);
        surface_view.setOnZoomableTextureListener(new ZoomableTextureView.OnZoomableTextureListener() {
            @Override
            public void onScaleChanged(ZoomableTextureView zoomableTextureView, float v) {

            }

            @Override
            public boolean onDoubleTap(ZoomableTextureView zoomableTextureView, MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onSingleTapConfirmed(ZoomableTextureView zoomableTextureView, MotionEvent motionEvent) {
                if (player.getPlayState() == PlayerState.STATE_READY) {
                    showPauseButton();
//                    uiuiHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            dismissPauseButton();
//                        }
//                    }, 2000);
                }
                return true;
            }

            @Override
            public void onLongPress(ZoomableTextureView zoomableTextureView, MotionEvent motionEvent) {

            }
        });
        player.setOnErrorListener(new OnErrorListener() {
            @Override
            public void onError(PlayerException exception) {
//                showToast("errorcode: " + exception.getCode() + "\n" + exception.getMessage());

                switch (exception.getSubCode()) {

                    case PlayerException.SUB_CODE_SOURCE_INVALID_DECRYPTE_KEY: //无效的解密密钥
                    case PlayerException.SUB_CODE_SOURCE_INVALID_RTMP_URL: //无效的播放地址
                    case PlayerException.SUB_CODE_SOURCE_PARAMETER_ERROR: //错误的数据源参数
                    case PlayerException.SUB_CODE_SOURCE_QUERY_URL_FAILED: //请求播放地址失败
                        autoRetry(true);
                        break;
                    case PlayerException.SUB_CODE_RENDER_DECODE_ERROR: //解码错误
                    case PlayerException.SUB_CODE_UNEXPECTED_PULL_STREAM_ERROR: //拉流失败，8S未拉取到流或连接被异常断开
                    case PlayerException.SUB_CODE_SOURCE_STREAM_CONNECT_ERROR: //与数据源建立连接失败
                        autoRetry(false);
                        break;

                    default:
                }
            }
        });
        player.setOnPlayerStateChangedListener(new OnPlayerStateChangedListener() {
            @Override
            public void onPlayerStateChange(int playerState) {
                switch (playerState) {
                    case PlayerState.STATE_BUFFERING:
//                        dismissPlayButton();
//                        showBuffering();
                        Log.i(TAG, "STATE_BUFFERING");
                        break;
                    case PlayerState.STATE_IDLE:
                        //idle state
                        Log.i(TAG, "STATE_IDLE");
                        break;
                    case PlayerState.STATE_READY:
                        // dismiss your dialog here because our video is ready to play now
                        resetRetryCount();
//                        dismissBuffering();
                        showPlayInfo();
                        Log.i(TAG, "STATE_READY");
                        break;
                    case PlayerState.STATE_ENDED:
                        // do your processing after ending of video
                        Log.i(TAG, "STATE_ENDED");
                        dismissPlayInfo();
//                        showPlayButton();
//                        recordBtn.clearAnimation();
                        break;
                    default:
                        break;
                }
            }
        });

        surface_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(IssApplication.ori==Configuration.ORIENTATION_LANDSCAPE){
                    isFullScreen=!isFullScreen;
                    if(isFullScreen){
                        rl_control.setVisibility(View.GONE);
                        ll_bottom.setVisibility(View.GONE);
                        rl_top.setVisibility(View.GONE);
                        ll_mid.setVisibility(View.GONE);
                    }else {
//                        rl_control.setVisibility(View.VISIBLE);
//                        ll_bottom.setVisibility(View.VISIBLE);
                        rl_top.setVisibility(View.VISIBLE);
                        ll_mid.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
// 设置rtmp地址.
//        player.setDataSource("rtmp://live.hkstv.hk.lxdns.com/live/hks2");

    }

    private void dismissPlayInfo() {
        if (updatePlayInfoHandle != null) {
            updatePlayInfoHandle.cancel(true);
            updatePlayInfoHandle = null;
        }
    }

    private void showPlayInfo() {
        updatePlayInfo();
    }

    private boolean mResumed;
    private int retryCount;
    private int maxRetryCount = 10;
    private void autoRetry(boolean needDelay) {
        if (needAutoReconnect()) {
            if (needDelay) {
                Log.e(TAG, "autoRetry   retryCount:" + retryCount);
                playLive(false);
            } else {
                uiHandler.postDelayed(delayAutoRetryRunnable, 300);
            }
        } else {
//            showPlayButton();
//            dismissBuffering();
        }
    }
    private final Runnable delayAutoRetryRunnable = new Runnable() {
        @Override
        public void run() {
            playLive(false);
        }
    };

    private void showPauseButton() {
//        videoBufferingProgressBar.setVisibility(View.GONE);
//        videoPauseBtn.setVisibility(View.VISIBLE);
    }
    private boolean needAutoReconnect() {
        return mResumed && retryCount < maxRetryCount;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止播放
        player.stop();
// 释放播放器资源
        player.release();
        if (simpleStreamAudioTrack != null) {
            simpleStreamAudioTrack.release();
            audioTrackQueue.clear();
        }
        if (acousticEchoCanceler != null) {
            acousticEchoCanceler.setEnabled(false);
            acousticEchoCanceler.release();
            acousticEchoCanceler = null;
        }
        if (noiseSuppressor != null) {
            noiseSuppressor.setEnabled(false);
            noiseSuppressor.release();
            noiseSuppressor = null;
        }

    }
    private NetWorkStateReceiver netWorkStateReceiver;
    @Override
    protected void onPause() {
        unregisterReceiver(netWorkStateReceiver);
        super.onPause();
        mResumed = false;
    }
    private void resetRetryCount() {
        retryCount = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mResumed = true;
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver(netWorkChangeListener);
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);

        //注册监听物模型属性变化监听器，主要是处理清晰度的属性变化
        SharePreferenceManager.getInstance().registerOnCallSetListener(definitionChangeListener);

        //获取设备最新属性状态
        getProperties();
        //开启直播
        playLive();

        //获取当前直播清晰度
//        defaultDefinition = SharePreferenceManager.getInstance().getStreamVideoQuality();
//        changeDefinitionView(defaultDefinition);
    }

    private void getProperties() {
        SettingsCtrl.getInstance().getProperties(iotId);
    }
    private void playLive(boolean showInfo) {
        Log.i(TAG, "playLive");

        if (!mResumed) {
            retryCount = maxRetryCount;
            return;
        }
        retryCount++;

//        boolean forceIFrame = SharePreferenceManager.getInstance().getForceIFrameSwitch();
        player.setIPCLiveDataSource(iotId, 0, false, 0, true);
        if (showInfo) {
            keepScreenLight();
//            showBuffering();
//            showMobileDataTips();
        }

        player.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                player.start();
            }
        });
        player.prepare();
    }
    NetWorkChangeListener netWorkChangeListener = new NetWorkChangeListener() {
        @Override
        public void stateChanged(NetworkStateEnum newNetWorkState) {
            //网络状态变化，恢复播放
            if (newNetWorkState == NetworkStateEnum.NONE) {
                return;
            }
            if (player.getPlayState() == PlayerState.STATE_BUFFERING
                    || player.getPlayState() == PlayerState.STATE_READY) {
                player.stop();
                uiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        playLive();
                    }
                }, 500);

            }
        }
    };

    private void playLive() {
        playLive(true);
    }

    private void stopLive() {
        player.stop();
        stopScreenLight();
    }

    private void keepScreenLight() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void stopScreenLight() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private SharePreferenceManager.OnCallSetListener definitionChangeListener
            = new SharePreferenceManager.OnCallSetListener() {

        @Override
        public void onCallSet(String key) {
            //监听主码流的视频清晰度变化
            if (key.equals(Constants.STREAM_VIDEO_QUALITY_MODEL_NAME)) {
                defaultDefinition = SharePreferenceManager.getInstance().getStreamVideoQuality();
//                changeDefinitionView(defaultDefinition);
            }
        }
    };

    ScheduledExecutorService scheduledExecutorService = newScheduledThreadPool(1);
    ScheduledFuture<?> updatePlayInfoHandle;

    public void updatePlayInfo() {
        if (updatePlayInfoHandle == null) {
            updatePlayInfoHandle = scheduledExecutorService.scheduleAtFixedRate(updatePlayInfoTimerTask, 1, 1, SECONDS);
        }
        PlayInfo playInfo = player.getCurrentPlayInfo();
//        playInfoTv.setText(playInfo.frameRate + "fps\n" + (playInfo.bitRate / 1024 / 8) + "KBps");
    }
    final Runnable updatePlayInfoTimerTask = new Runnable() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updatePlayInfo();
                }
            });
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        stopLive();
        audioRecord.stop();
        isRecordingMp4 = false;
        iv_luyin.clearAnimation();
        dismissPlayInfo();
        //注销监听
        SharePreferenceManager.getInstance().unRegisterOnCallSetListener(definitionChangeListener);
        try {
            liveIntercom.stop();
        } catch (Exception e) {
            Log.e(TAG, "liveIntercom.stop() error", e);
        }
    }



    @Override
    protected void initData() {
        iotId = getIntent().getStringExtra(Constance.iotId);
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            isControlling=false;
        }
    };
    private void invokeService(String identify, Object value) {
        if(isControlling){
            return;
        }

        handler.sendEmptyMessageDelayed(1,100);


        isControlling=true;

        Map<String, Object> maps = new HashMap<>();
        maps.put("iotId", iotId);
//        value="{\""+identify+"\":"+value+"}";
        com.alibaba.fastjson.JSONObject jsonObject=new com.alibaba.fastjson.JSONObject();
//        JSONObject identifyObj=new JSONObject();
//        identifyObj.put(identify,value);
//        JSONObject step=new JSONObject();
//        step.put("Step","1");
//        JSONObject starptzObj=new JSONObject();
//        starptzObj.put("")
        LogUtils.logE("startptz",value.toString());
        jsonObject.put(identify,value);
        jsonObject.put("Step",2);
        maps.put("identifier",SERVICE_IDENTIFY);
        maps.put("args",jsonObject);
        IoTRequestBuilder ioTRequestBuilder = new IoTRequestBuilder()
                .setPath("/thing/service/invoke")
                .setApiVersion("1.0.2")
                .setAuthType("iotAuth")
                .setParams(maps);
        IoTRequest request = ioTRequestBuilder.build();
        IoTAPIClient client = new IoTAPIClientFactory().getClient();
        client.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                LogUtils.logE("onResponse", ioTResponse.getCode() + "," + ioTResponse.getMessage() + "," + ioTResponse.getData());
                if(ioTResponse.getCode()==200){
//                    isOpen=!isOpen;
//                    uiHandler.sendEmptyMessage(1);
                }
//                final ProgressDialog progressDialog=ProgressDialog.show(DevicesControlActivity.this,"请求中","");
//                new Thread(){
//                    @Override
//                    public void run() {
//                        super.run();
//                        SystemClock.sleep(1000);
//                        initStatus();
//                        progressDialog.dismiss();
//
//                    }
//                }.start();

            }
        });
    }
    private void recordTrigger(Map<String,Object> maps) {


        IoTRequestBuilder ioTRequestBuilder = new IoTRequestBuilder()
                .setPath("/vision/customer/record/trigger")
                .setApiVersion("2.1.0")
                .setAuthType("iotAuth")
                .setParams(maps);
        IoTRequest request = ioTRequestBuilder.build();
        IoTAPIClient client = new IoTAPIClientFactory().getClient();
        client.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                LogUtils.logE("onResponse", ioTResponse.getCode() + "," + ioTResponse.getMessage() + "," + ioTResponse.getData());
                if(ioTResponse.getCode()==200){
                }

            }
        });
    }
    private void invokeService(Map<String,Object> maps) {


        IoTRequestBuilder ioTRequestBuilder = new IoTRequestBuilder()
                .setPath("/thing/service/invoke")
                .setApiVersion("1.0.2")
                .setAuthType("iotAuth")
                .setParams(maps);
        IoTRequest request = ioTRequestBuilder.build();
        IoTAPIClient client = new IoTAPIClientFactory().getClient();
        client.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                LogUtils.logE("onResponse", ioTResponse.getCode() + "," + ioTResponse.getMessage() + "," + ioTResponse.getData());
                if(ioTResponse.getCode()==200){
                }

            }
        });
    }
    private void setProperties(String identify, Object value) {
        Map<String, Object> maps = new HashMap<>();
        maps.put("iotId", iotId);
        com.alibaba.fastjson.JSONObject jsonObject=new com.alibaba.fastjson.JSONObject();
        jsonObject.put(identify,value);
        maps.put("items",jsonObject);
        IoTRequestBuilder ioTRequestBuilder = new IoTRequestBuilder()
                .setPath("/thing/properties/set")
                .setApiVersion("1.0.2")
                .setAuthType("iotAuth")
                .setParams(maps);
        IoTRequest request = ioTRequestBuilder.build();
        IoTAPIClient client = new IoTAPIClientFactory().getClient();
        client.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                LogUtils.logE("onResponse", ioTResponse.getCode() + "," + ioTResponse.getMessage() + "," + ioTResponse.getData());
                if(ioTResponse.getCode()==200){
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_shengyin:
                isOpenSoud = !isOpenSoud;
                setProperties("SpeakerSwitch",isOpenSoud?1:0);
                setProperties("micSwitch",isOpenSoud?1:0);
                if(isOpenSoud){
                    player.setVolume(1);
                    iv_shengyin.setImageResource(R.mipmap.icon_sy);
                }else {
                    player.setVolume(0);
                    iv_shengyin.setImageResource(R.mipmap.icon_jy);
                }
                break;
            case R.id.iv_luxiang:
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    int code=checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if(code!=0){
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_EXTNERNO);
                        return;
                    }
                }
                isRecording = !isRecording;

                Map<String,Object> maps=new HashMap();
                maps.put("iotId", iotId);
                com.alibaba.fastjson.JSONObject jsonObject=new com.alibaba.fastjson.JSONObject();
                if(isRecording){
                    fileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/iotjuhaoVideo/");
                    if(!fileName.exists()){
                        fileName.mkdirs();
                    }
                    if(PlayerState.STATE_READY==player.getPlayState()){
                        currentTIme = System.currentTimeMillis()+"";
//                        boolean isSuccess=player.startRecordingContent(new File(fileName.getAbsolutePath()+"/"+currentTIme+".mp4"));
//                        LogUtils.logE("isSuccess",isSuccess+"player");
                        iv_luxiang.setImageResource(R.mipmap.icon_lx_sel);
                        MyToast.show(LivingPlayerActivity.this,"开始录像");
                    }

//                    jsonObject.put("RecordType",1);
//                    jsonObject.put("RecordDuration",100);
//                    jsonObject.put("PreRecordDuration",5);
//                    jsonObject.put("UploadUrl","");
//                    maps.put("identifier","StartRecord");
//                    maps.put("args",jsonObject);
                    maps.put("recordDuration",60);
                }else {
                    MyToast.show(LivingPlayerActivity.this,"录像完成");
                    iv_luxiang.setImageResource(R.mipmap.icon_lx_nor);
                    maps.put("identifier","StopRecord");
//                    player.stopRecordingContent();
//                    File file=new File(fileName.getAbsolutePath()+"/"+currentTIme+".mp4");
                    if(file.exists()){
                        LogUtils.logE("file","existes");
                    }

                }
                startOrStopRecordingMp4();
//                recordTrigger(maps);


                break;
            case R.id.iv_yuyin:
                isOpenYuyin = !isOpenYuyin;
//                setProperties("MicSwitch",isOpenYuyin?1:0);
                if (!isLiveIntercoming) {
                    audioRecord.start();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv_luyin.clearAnimation();
                        }
                    });
                    audioRecord.stop();
                }
                if(isOpenYuyin){
                    iv_luyin.setImageResource(R.mipmap.icon_th_sel);
                }else {
                    iv_luyin.setImageResource(R.mipmap.icon_th_nor);
                }
                break;
            case R.id.iv_jietu:
//                getPic();
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        return;
                    }
                }
                final Bitmap bitmap=player.snapShot();
                showImageDialog = new Dialog(this,R.style.customDialog);
                showImageDialog.setContentView(R.layout.dialog_share_image);
                ImageView iv_img= showImageDialog.findViewById(R.id.iv_img);
                showImageDialog.setCancelable(true);
                showImageDialog.setCanceledOnTouchOutside(true);
                iv_img.setImageBitmap(bitmap);

                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        uiHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(showImageDialog!=null&&showImageDialog.isShowing()){
                                    showImageDialog.dismiss();
                                }
                                if(bitmap!=null&&!bitmap.isRecycled()){
                                ScannerUtils.saveImageToGallery02(LivingPlayerActivity.this,bitmap, ScannerUtils.ScannerType.MEDIA);
                                }
                            }
                        },2000);
                    }
                }.start();
                iv_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                showImageDialog.show();
                MyToast.show(this,"截屏成功！");


                break;
            case R.id.iv_suofang:
                        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                            rl_control.setVisibility(View.GONE);
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            //横屏
                }else {
                            rl_control.setVisibility(View.VISIBLE);
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            //横屏
                        }
                break;
            case R.id.tv_klx:
                Intent intent2 = new Intent(LivingPlayerActivity.this, NewCardVideoActivity.class);
                intent2.putExtra("iotId", iotId);
                startActivity(intent2);
//                Map<String,Object>map=new HashMap<>();
//                map.put("iotId",iotId);
//                map.put("month","201909");
//                map.put("beginTime", DateUtils.getTimeStamp("2019-09-01","yyyy-MM-dd"));
//                map.put("endTime", DateUtils.getTimeStamp("2019-09-30","yyyy-MM-dd"));
//                ApiClientForIot.getIotClient("/vision/customer/record/query", "2.0.0", map, new IoTCallback() {
//                    @Override
//                    public void onFailure(IoTRequest ioTRequest, Exception e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
//                        LogUtils.logE("record_query",ioTResponse.getCode()+","+ioTResponse.getData());
//                    }
//                });
//                getTimeList();
//                getRecordList();

//                Intent intent=new Intent(this,VodplayerActivity.class);
//                intent.putExtra(Constance.iotId,iotId);
//                startActivity(intent);
                break;
            case R.id.tv_pic:
//                String selection = MediaStore.Images.Media.DATA + " like %?";
//                String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/iotjuhaoImage";
//                String[] selectionArgs = {path+"%"};
//                Cursor cursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,selection, selectionArgs, null);
//                while (cursor.moveToNext()){
//                    String[] names=cursor.getColumnNames();
//                    int count=cursor.getColumnCount();
//                    LogUtils.logE("column",names.toString());
//                }
                String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/iotjuhaoImage";
//                File folder = new File("/sdcard/Photo/");
//                allFiles = folder.list();
//                //   uriAllFiles= new Uri[allFiles.length];
//                for(int i=0;i<allFiles.length;i++)
//                {
//                    Log.d("all file path"+i, allFiles[i]+allFiles.length);
//                }
                //  Uri uri= Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString()+"/yourfoldername/"+allFiles[0]));
                Intent intent=new Intent(this,ImageStoreActivity.class);
                intent.putExtra(Constance.iotId,iotId);
                startActivity(intent);
//                startScan();
                break;
            case R.id.iv_setting:
                intent = new Intent(this, LivingSettingActivity.class);
                SharePreferenceManager.getInstance().init(LivingPlayerActivity.this);
                intent.putExtra("iotId", iotId);
                startActivity(intent);
                break;
        }
    }

    private void startOrStopRecordingMp4() {
        verifyPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!isRecordingMp4) {
            File path=Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES);
            if(!path.exists()){
                path.mkdirs();
            }
            file = new File(path, System.currentTimeMillis() + ".mp4");

            try {
                if (player.startRecordingContent(file)) {
                    iv_luxiang.startAnimation(alphaAnimation);
                    isRecordingMp4 = true;
                } else {
                    MyToast.show(this,getString(com.aliyun.iot.demo.ipcview.R.string.ipc_main_record_fail));
                }
            } catch (Exception e) {
                e.printStackTrace();
                MyToast.show(this,file.getAbsolutePath() +getString(com.aliyun.iot.demo.ipcview.R.string.ipc_main_record_err_io));
            }
        } else {
            if (player.stopRecordingContent()) {
                File fileFinal = file;
                AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(LivingPlayerActivity.this);
                normalDialog.setTitle(com.aliyun.iot.demo.ipcview.R.string.ipc_main_record_success);
                normalDialog.setMessage(getString(com.aliyun.iot.demo.ipcview.R.string.ipc_main_record_save_path) + fileFinal.getAbsolutePath());
                normalDialog.setPositiveButton(getString(com.aliyun.iot.demo.ipcview.R.string.ipc_main_record_play),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent;

                                if (fileFinal == null || !fileFinal.exists()) {
                                    return;
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                                    intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    Uri contentUri = FileProvider.getUriForFile(LivingPlayerActivity.this,
                                            getApplicationContext().getPackageName() + ".provider", fileFinal);
                                    intent.setDataAndType(contentUri, "video/*");
                                } else {
                                    Uri uri = Uri.fromFile(fileFinal);
                                    //调用系统自带的播放器
                                    intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(uri, "video/*");
                                }
                                startActivity(intent);
                            }
                        });
                normalDialog.setNegativeButton(com.aliyun.iot.demo.ipcview.R.string.ipc_close,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                // 显示
                normalDialog.show();
            } else {
                MyToast.show(LivingPlayerActivity.this,getString(com.aliyun.iot.demo.ipcview.R.string.ipc_main_record_save_fail));

            }
            iv_luxiang.clearAnimation();
            isRecordingMp4 = false;
        }
    }

    private void getRecordList() {
        Map<String,Object> map=new HashMap<>();
        map.put("identifier","QueryRecordList");
        map.put("iotId",iotId);
        JSONObject jsonObject=new JSONObject();
//        String startTime=DateUtils.getTimeStamp("2019-09-26 00:00","yyyy-MM-dd hh:mm")/1000+"";
//        String endTime=DateUtils.getTimeStamp("2019-09-27 00:00","yyyy-MM-dd hh:mm")/1000+"";
//        LogUtils.logE("BeginTime",""+1569463165);
//        LogUtils.logE("EndTime",""+1569463408);
        jsonObject.put("BeginTime",1569463165);
        jsonObject.put("EndTime",1569463408);
        jsonObject.put("QuerySize",128);
        jsonObject.put("Type",2);
        map.put("args",jsonObject);
        invokeService(map);
    }

    private void getTimeList() {
        Map<String,Object> map=new HashMap<>();
        map.put("identifier","QueryRecordTimeList");
        map.put("iotId",iotId);
        JSONObject jsonObject=new JSONObject();
        String startTime=DateUtils.getTimeStamp("2019-09-26 00:00","yyyy-MM-dd hh:mm")/1000+"";
        String endTime=DateUtils.getTimeStamp("2019-09-27 00:00","yyyy-MM-dd hh:mm")/1000+"";
        LogUtils.logE("startTime",""+startTime);
        LogUtils.logE("endTime",""+endTime);
        jsonObject.put("BeginTime",Integer.parseInt(startTime));
        jsonObject.put("EndTime",Integer.parseInt(endTime));
        jsonObject.put("QuerySize",128);
        jsonObject.put("Type",2);
        map.put("args",jsonObject);
        invokeService(map);
    }

    public String[] allFiles;
    private String SCAN_PATH ;
    private static final String FILE_TYPE="image/*";

    private MediaScannerConnection conn;
    /** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//
//        System.out.println(" SCAN_PATH  " +SCAN_PATH);
//
//        Log.d("SCAN PATH", "Scan Path " + SCAN_PATH);
//        Button scanBtn = (Button)findViewById(R.id.scanBtn);
//        scanBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                startScan();
//            }});
//    }

    private SimpleAudioRecord audioRecord;
    private SimpleStreamAudioTrack simpleStreamAudioTrack;
    private BlockingQueue<byte[]> audioTrackQueue = new LinkedBlockingQueue();
    private void handleLiveIntercomError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iv_luyin.clearAnimation();
                iv_luyin.setSelected(false);
                iv_luyin.setEnabled(true);
                MyToast.show(LivingPlayerActivity.this,getString(com.aliyun.iot.demo.ipcview.R.string.ipc_main_intercom_close));
            }
        });
        audioRecord.stop();
        liveIntercom.stop();
        if (simpleStreamAudioTrack != null) {
            simpleStreamAudioTrack.stop();
        }
    }
    private void initLiveIntercom() {
        verifyPermissions(this, Manifest.permission.RECORD_AUDIO);
        liveIntercom = new LiveIntercom();
        liveIntercom.setOnAudioParamsChangeListener(new OnAudioParamsChangeListener() {
            @Override
            public void onAudioParamsChange(AudioParams audioParams) {
                if (simpleStreamAudioTrack != null) {
                    simpleStreamAudioTrack.release();
                    audioTrackQueue.clear();
                }
                if (acousticEchoCanceler != null) {
                    acousticEchoCanceler.release();
                }
                if (noiseSuppressor != null) {
                    noiseSuppressor.release();
                }
                int audioSessionId = audioRecord.getAudioSessionId();
                noiseSuppressor = NoiseSuppressor.create(audioSessionId);
                if (noiseSuppressor != null) {
                    noiseSuppressor.setEnabled(true);
                }
                simpleStreamAudioTrack = new SimpleStreamAudioTrack(audioParams, AudioManager.STREAM_MUSIC, audioTrackQueue, audioRecord.getAudioSessionId());
                if (AcousticEchoCanceler.isAvailable()) {
                    acousticEchoCanceler = AcousticEchoCanceler.create(audioRecord.getAudioSessionId());
                    if (acousticEchoCanceler != null) {
//                        showToast("开启回声消除");
                        acousticEchoCanceler.setEnabled(true);
                    }
                }
                simpleStreamAudioTrack.start();
            }
        });
        liveIntercom.setOnErrorListener(new com.aliyun.iotx.linkvisual.media.audio.listener.OnErrorListener() {
            @Override
            public void onError(LiveIntercomException error) {
//                showToast(error.getMessage());
                error.printStackTrace();
                handleLiveIntercomError();
            }
        });
        liveIntercom.setOnAudioBufferReceiveListener(new OnAudioBufferReceiveListener() {
            @Override
            public void onAudioBufferRecevie(byte[] bytes, int i) {
                audioTrackQueue.add(bytes);
            }
        });
        liveIntercom.setOnTalkReadyListener(new OnTalkReadyListener() {
            @Override
            public void onTalkReady() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv_luyin.setEnabled(true);
                        iv_luyin.setSelected(true);
                        iv_luyin.startAnimation(alphaAnimation);
                    }
                });
//                showToast(com.aliyun.iot.demo.ipcview.R.string.ipc_main_can_intercom);
            }
        });

        audioRecord = new SimpleAudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, AudioParams.AUDIOPARAM_MONO_8K_PCM);
        audioRecord.setAudioRecordListener(new AudioRecordListener() {
            @Override
            public void onRecordStart() {
                Log.d(TAG, "onRecordStart");
                player.setVolume(0f);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv_luyin.setEnabled(false);
                    }
                });
                liveIntercom.start(iotId, AudioParams.AUDIOPARAM_MONO_8K_G711A);
                isLiveIntercoming = true;
            }

            @Override
            public void onRecordEnd() {
                Log.d(TAG, "onRecordEnd");
                player.setVolume(1f);
                liveIntercom.stop();
                isLiveIntercoming = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv_luyin.setEnabled(true);
                    }
                });
            }

            @Override
            public void onBufferReceived(byte[] buffer, int offset, int size) {
                Log.d(TAG, "onBufferReceived:" + size);
                liveIntercom.sendAudioBuffer(buffer, offset, size);
            }

            @Override
            public void onError(int error, String message) {
                Log.d(TAG, "onError:" + error + message);
//                showToast(getString(com.aliyun.iot.demo.ipcview.R.string.ipc_main_intercom_redio_err) + error + message);
                handleLiveIntercomError();
                isLiveIntercoming = false;
            }
        });
    }
    private void startScan()
    {
        Log.d("Connected","success"+conn);
        if(conn!=null)
        {
            conn.disconnect();
        }
        conn = new MediaScannerConnection(this,this);
        conn.connect();
    }
    @Override
    public void onMediaScannerConnected() {
        Log.d("onMediaScannerConnected","success"+conn);
        conn.scanFile(SCAN_PATH, FILE_TYPE);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        try {
            Log.d("onScanCompleted",uri + "success"+conn);
            System.out.println("URI " + uri);
            if (uri != null)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
            }
        } finally
        {
            conn.disconnect();
            conn = null;
        }
    }

    private static void verifyPermissions(Activity activity, String permission) {

        try {
            int granted = ActivityCompat.checkSelfPermission(activity,
                    permission);
            if (granted != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{permission},
                        1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 截获back键，当处于全屏则退出全屏
     */
    @Override
    public void onBackPressed() {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            super.onBackPressed();
        } else {
            iv_suofang.performClick();
        }
    }
//
//    private void getPic() {
//        Map<String, Object> maps = new HashMap<>();
//        maps.put("iotId", iotId);
////        com.alibaba.fastjson.JSONObject jsonObject=new com.alibaba.fastjson.JSONObject();
////        jsonObject.put(identify,value);
////        maps.put("items",jsonObject);
//        IoTRequestBuilder ioTRequestBuilder = new IoTRequestBuilder()
//                .setPath("/vision/customer/picture/trigger")
//                .setApiVersion("2.0.0")
//                .setAuthType("iotAuth")
//                .setParams(maps);
//        IoTRequest request = ioTRequestBuilder.build();
//        IoTAPIClient client = new IoTAPIClientFactory().getClient();
//        client.send(request, new IoTCallback() {
//            @Override
//            public void onFailure(IoTRequest ioTRequest, Exception e) {
//
//            }
//
//            @Override
//            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
//                LogUtils.logE("onResponse", ioTResponse.getCode() + "," + ioTResponse.getMessage() + "," + ioTResponse.getData());
//                if(ioTResponse.getCode()==200){
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            MyToast.show(LivingPlayerActivity.this,"截图成功！");
//                        }
//                    });
//                }
//            }
//        });
//    }
}
