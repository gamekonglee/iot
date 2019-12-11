package com.aliyun.iot.demo.ipcview.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;
import com.aliyun.iot.demo.ipcview.R;
import com.aliyun.iot.demo.ipcview.constants.Constants;
import com.aliyun.iot.demo.ipcview.dialog.SnapshotPreviewDialog;
import com.aliyun.iot.demo.ipcview.enums.ActionTypeEnum;
import com.aliyun.iot.demo.ipcview.manager.SettingsCtrl;
import com.aliyun.iot.demo.ipcview.manager.SharePreferenceManager;
import com.aliyun.iot.demo.ipcview.utils.NetWorkChangeListener;
import com.aliyun.iot.demo.ipcview.utils.NetWorkStateReceiver;
import com.aliyun.iot.demo.ipcview.utils.NetworkStateEnum;
import com.aliyun.iot.demo.ipcview.utils.NetworkUtil;
import com.aliyun.iotx.linkvisual.IPCManager;
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
import com.aliyun.iotx.linkvisual.media.video.listener.OnRenderedFirstFrameListener;
import com.aliyun.iotx.linkvisual.media.video.player.LivePlayer;
import com.aliyun.iotx.linkvisual.media.video.views.ZoomableTextureView;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static android.os.Environment.DIRECTORY_MOVIES;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 摄像头主界面
 * <p>
 * <p>
 * <p>
 * <p>
 * ！！！注意！！！
 * 注意本示例代码主要用于演示部分视频业务接口以及对应的效果
 * 代码中涉及的交互，UI以及代码框架请自行设计，示例代码仅供参考，稳定性请客户自行保证。
 *
 * @author azad
 */
public class IPCameraActivity extends Activity {
    private final String TAG = this.getClass().getSimpleName();
    private String iotId = "";
    private String appKey = "";
    private View videoPlayBtn, videoPauseBtn;
    private Button settingsBtn, captureBtn, recordBtn, speakBtn, videoBtn,
            pictureBtn, cardVideoBtn, fourVideoBtn;
    private Button zoomInBtn, zoomOutBtn, upRightBtn, upBtn, upLeftBtn, rightBtn, leftBtn, downRightBtn,
            downBtn, downLeftBtn;
    private Button highDefinitionBtn, norDefinitionBtn, lowDefinitionBtn;
    private TextView playInfoTv, streamLoadingTime, playerMobileNetworkTips;
    private ToggleButton zoomBtn;
    private ProgressBar videoBufferingProgressBar;
    private LivePlayer player;
    private ZoomableTextureView playerTextureView;
    private RelativeLayout containerRl;
    private LinearLayout recordLl;
    private Handler uiHandler;

    private SimpleAudioRecord audioRecord;
    private ILiveIntercom liveIntercom;
    private SimpleStreamAudioTrack simpleStreamAudioTrack;
    private BlockingQueue<byte[]> audioTrackQueue = new LinkedBlockingQueue();
    private AcousticEchoCanceler acousticEchoCanceler;
    private NoiseSuppressor noiseSuppressor;
    private boolean isLiveIntercoming = false;
    private Animation alphaAnimation;

    ScheduledExecutorService scheduledExecutorService = newScheduledThreadPool(1);
    ScheduledFuture<?> updatePlayInfoHandle;

    private NetWorkStateReceiver netWorkStateReceiver;
    /**
     * defaultDefinition 默认清晰度
     */
    private int defaultDefinition = 1;

    private final int DEFAULT_STEP = 5;

    private int retryCount;
    private int maxRetryCount = 10;
    private boolean mResumed;

    public String getIotId() {
        return iotId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipcamera);
        uiHandler = new Handler(getMainLooper());
        iotId = getIntent().getStringExtra("iotId");
        appKey = getIntent().getStringExtra("appKey");
        Log.e(TAG, "iotId:" + iotId + "    appKey:" + appKey);
        initView();
        initPlayer();
        initLiveIntercom();
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
        defaultDefinition = SharePreferenceManager.getInstance().getStreamVideoQuality();
        changeDefinitionView(defaultDefinition);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(netWorkStateReceiver);
        super.onPause();
        mResumed = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLive();
        audioRecord.stop();
        isRecordingMp4 = false;
        speakBtn.clearAnimation();
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
    protected void onDestroy() {
        super.onDestroy();
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
            hideOtherView();
        } else {
            showOtherView();
        }
        super.onConfigurationChanged(newConfig);
    }

    private void getProperties() {
        SettingsCtrl.getInstance().getProperties(iotId);
    }

    private SharePreferenceManager.OnCallSetListener definitionChangeListener
            = new SharePreferenceManager.OnCallSetListener() {

        @Override
        public void onCallSet(String key) {
            //监听主码流的视频清晰度变化
            if (key.equals(Constants.STREAM_VIDEO_QUALITY_MODEL_NAME)) {
                defaultDefinition = SharePreferenceManager.getInstance().getStreamVideoQuality();
                changeDefinitionView(defaultDefinition);
            }
        }
    };

    private void initView() {
        videoBufferingProgressBar = findViewById(R.id.video_buffering_bar);
        videoPlayBtn = findViewById(R.id.video_play_ibtn);
        videoPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissPlayButton();
                playLive();
            }
        });
        videoPauseBtn = findViewById(R.id.video_pause_ibtn);
        videoPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissPauseButton();
                showPlayButton();
                stopLive();
            }
        });

        zoomBtn = findViewById(R.id.exo_zoom_tbtn);
        //播放器全屏按钮
        zoomBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                if (isChecked) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });

        settingsBtn = findViewById(R.id.settings_btn);
        captureBtn = findViewById(R.id.capture_btn);
        recordBtn = findViewById(R.id.record_btn);
        speakBtn = findViewById(R.id.speaker_btn);
        videoBtn = findViewById(R.id.video_btn);
        pictureBtn = findViewById(R.id.picture_btn);
        cardVideoBtn = findViewById(R.id.card_videos_btn);
        fourVideoBtn = findViewById(R.id.four_videos_btn);

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IPCameraActivity.this, SettingsActivity.class);
                intent.putExtra("iotId", iotId);
                startActivity(intent);
            }
        });
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snapshot();
            }
        });
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startOrStopRecordingMp4();
            }
        });
        speakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startOrStopLiveIntercom();
            }
        });
        videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IPCameraActivity.this, NewEventVideoActivity.class);
                intent.putExtra("iotId", iotId);
                startActivity(intent);
            }
        });
        videoBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(IPCameraActivity.this, EventVideoActivity.class);
                intent.putExtra("iotId", iotId);
                startActivity(intent);
                return true;
            }
        });
        pictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IPCameraActivity.this, PictureTestActivity.class);
                intent.putExtra("iotId", iotId);
                startActivity(intent);
            }
        });
        cardVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IPCameraActivity.this, NewCardVideoActivity.class);
                intent.putExtra("iotId", iotId);
                startActivity(intent);
            }
        });
        cardVideoBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(IPCameraActivity.this, CardVideoActivity.class);
                intent.putExtra("iotId", iotId);
                startActivity(intent);
                return true;
            }
        });
        fourVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IPCameraActivity.this, MultiPlayerActivity.class);
                intent.putExtra("iotId", iotId);
                startActivity(intent);
            }
        });

        playInfoTv = findViewById(R.id.player_info_tv);
        streamLoadingTime = findViewById(R.id.player_stream_loading_time_tv);
        playerMobileNetworkTips = findViewById(R.id.player_mobile_network_tips_tv);

        recordLl = findViewById(R.id.record_rl);
        containerRl = findViewById(R.id.container);
        addPtzBtn();

        highDefinitionBtn = findViewById(R.id.high_definition_btn);
        norDefinitionBtn = findViewById(R.id.nor_definition_btn);
        lowDefinitionBtn = findViewById(R.id.low_definition_btn);
        highDefinitionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isDefinitionChooseMode()) {
                    showDefinitionView();
                    return;
                }
                changeDefinitionView(2);
                changeDefinition(2);
            }
        });
        norDefinitionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isDefinitionChooseMode()) {
                    showDefinitionView();
                    return;
                }
                changeDefinitionView(1);
                changeDefinition(1);
            }
        });
        lowDefinitionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isDefinitionChooseMode()) {
                    showDefinitionView();
                    return;
                }
                changeDefinitionView(0);
                changeDefinition(0);
            }
        });
        defaultDefinition = SharePreferenceManager.getInstance().getStreamVideoQuality();
        changeDefinitionView(defaultDefinition);

        alphaAnimation = AnimationUtils.loadAnimation(IPCameraActivity.this, R.anim.alpha_loop);
    }

    private void addPtzBtn() {
        zoomInBtn = findViewById(R.id.zoom_in_btn);
        zoomOutBtn = findViewById(R.id.zoom_out_btn);
        upRightBtn = findViewById(R.id.up_right_btn);
        upBtn = findViewById(R.id.up_btn);
        upLeftBtn = findViewById(R.id.up_left_btn);
        rightBtn = findViewById(R.id.right_btn);
        leftBtn = findViewById(R.id.left_btn);
        downRightBtn = findViewById(R.id.down_right_btn);
        downBtn = findViewById(R.id.down_btn);
        downLeftBtn = findViewById(R.id.down_left_btn);

        zoomInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PTZActionControl(ActionTypeEnum.ZOOM_IN.getCode(), DEFAULT_STEP);
            }
        });
        zoomOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PTZActionControl(ActionTypeEnum.ZOOM_OUT.getCode(), DEFAULT_STEP);
            }
        });
        upRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PTZActionControl(ActionTypeEnum.UP_RIGHT.getCode(), DEFAULT_STEP);
            }
        });
        upBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PTZActionControl(ActionTypeEnum.UP.getCode(), DEFAULT_STEP);
            }
        });
        upLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PTZActionControl(ActionTypeEnum.UP_LEFT.getCode(), DEFAULT_STEP);
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PTZActionControl(ActionTypeEnum.RIGHT.getCode(), DEFAULT_STEP);
            }
        });
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PTZActionControl(ActionTypeEnum.LEFT.getCode(), DEFAULT_STEP);
            }
        });
        downRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PTZActionControl(ActionTypeEnum.DOWN_RIGHT.getCode(), DEFAULT_STEP);
            }
        });
        downBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PTZActionControl(ActionTypeEnum.DOWN.getCode(), DEFAULT_STEP);
            }
        });
        downLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PTZActionControl(ActionTypeEnum.DOWN_LEFT.getCode(), DEFAULT_STEP);
            }
        });
    }

    /**
     * 截获back键，当处于全屏则退出全屏
     */
    @Override
    public void onBackPressed() {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            super.onBackPressed();
        } else {
            zoomBtn.setChecked(false);
        }
    }

    private boolean isDefinitionChooseMode() {
        return View.VISIBLE == highDefinitionBtn.getVisibility()
                && View.VISIBLE == norDefinitionBtn.getVisibility()
                && View.VISIBLE == lowDefinitionBtn.getVisibility();
    }

    private void showDefinitionView() {
        highDefinitionBtn.setVisibility(View.VISIBLE);
        norDefinitionBtn.setVisibility(View.VISIBLE);
        lowDefinitionBtn.setVisibility(View.VISIBLE);
    }

    private void changeDefinitionView(int choose) {
        switch (choose) {
            case 0:
                highDefinitionBtn.setVisibility(View.GONE);
                norDefinitionBtn.setVisibility(View.GONE);
                lowDefinitionBtn.setVisibility(View.VISIBLE);
                break;
            case 1:
                highDefinitionBtn.setVisibility(View.GONE);
                norDefinitionBtn.setVisibility(View.VISIBLE);
                lowDefinitionBtn.setVisibility(View.GONE);
                break;
            case 2:
                highDefinitionBtn.setVisibility(View.VISIBLE);
                norDefinitionBtn.setVisibility(View.GONE);
                lowDefinitionBtn.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void changeDefinition(int definition) {
        if (definition < 0 || definition > 3) {
            return;
        }
        Map<String, Object> param = new HashMap<>();
        param.put(Constants.STREAM_VIDEO_QUALITY_MODEL_NAME, definition);
        SettingsCtrl.getInstance().updateSettings(iotId, param);
    }

    //进行一次ptz操作
    public void PTZActionControl(int action, int speed) {
        IPCManager.getInstance().getDevice(iotId).PTZActionControl(action, speed,
                new IPanelCallback() {
                    @Override
                    public void onComplete(boolean b, Object o) {
                        Log.e(TAG, "PTZActionControl:" + b + "       o:" + (o != null ? String.valueOf(o) : "null"));
                        if (b) {
                            if (o != null) {
                                JSONObject result = JSON.parseObject(o.toString());
                                int code = result.getInteger("code");
                                if (code == 200) {
                                    showToast(R.string.ipc_main_ptz_success);
                                } else {
                                    showToast(getResources().getString(R.string.ipc_main_ptz_fail) + o);
                                }
                            }
                        } else {
                            // showToast(getResources().getString(R.string.ipc_main_ptz_fail) + o);
                        }
                    }
                });
    }


    /**
     * 初始化播放器
     */
    private void initPlayer() {
        playerTextureView = findViewById(R.id.player_textureview);
        playerTextureView.setMaxScale(4);//设置最大的缩放比例，可不设置，默认为4
        player = new LivePlayer();
        player.setTextureView(playerTextureView);
        playerTextureView.setOnZoomableTextureListener(new ZoomableTextureView.OnZoomableTextureListener() {
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
                    uiHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dismissPauseButton();
                        }
                    }, 2000);
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
                showToast("errorcode: " + exception.getCode() + "\n" + exception.getMessage());

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
                        dismissPlayButton();
                        showBuffering();
                        Log.i(TAG, "STATE_BUFFERING");
                        break;
                    case PlayerState.STATE_IDLE:
                        //idle state
                        Log.i(TAG, "STATE_IDLE");
                        break;
                    case PlayerState.STATE_READY:
                        // dismiss your dialog here because our video is ready to play now
                        resetRetryCount();
                        dismissBuffering();
                        showPlayInfo();
                        Log.i(TAG, "STATE_READY");
                        break;
                    case PlayerState.STATE_ENDED:
                        // do your processing after ending of video
                        Log.i(TAG, "STATE_ENDED");
                        dismissPlayInfo();
                        showPlayButton();
                        recordBtn.clearAnimation();
                        break;
                    default:
                        break;
                }
            }
        });
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
                        showToast("开启回声消除");
                        acousticEchoCanceler.setEnabled(true);
                    }
                }
                simpleStreamAudioTrack.start();
            }
        });
        liveIntercom.setOnErrorListener(new com.aliyun.iotx.linkvisual.media.audio.listener.OnErrorListener() {
            @Override
            public void onError(LiveIntercomException error) {
                showToast(error.getMessage());
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
                        speakBtn.setEnabled(true);
                        speakBtn.setSelected(true);
                        speakBtn.startAnimation(alphaAnimation);
                    }
                });
                showToast(R.string.ipc_main_can_intercom);
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
                        speakBtn.setEnabled(false);
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
                        speakBtn.setEnabled(true);
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
                showToast(getString(R.string.ipc_main_intercom_redio_err) + error + message);
                handleLiveIntercomError();
                isLiveIntercoming = false;
            }
        });
    }

    private void handleLiveIntercomError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                speakBtn.clearAnimation();
                speakBtn.setSelected(false);
                speakBtn.setEnabled(true);
                showToast(R.string.ipc_main_intercom_close);
            }
        });
        audioRecord.stop();
        liveIntercom.stop();
        if (simpleStreamAudioTrack != null) {
            simpleStreamAudioTrack.stop();
        }
    }

    private void startOrStopLiveIntercom() {
        if (!isLiveIntercoming) {
            audioRecord.start();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    speakBtn.clearAnimation();
                }
            });
            audioRecord.stop();
        }
    }

    private boolean isRecordingMp4 = false;
    private File file = null;

    private void startOrStopRecordingMp4() {
        verifyPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!isRecordingMp4) {
            file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES), System.currentTimeMillis() + ".mp4");
            try {
                if (player.startRecordingContent(file)) {
                    recordBtn.startAnimation(alphaAnimation);
                    isRecordingMp4 = true;
                } else {
                    showToast(R.string.ipc_main_record_fail);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast(file.getAbsolutePath() + getString(R.string.ipc_main_record_err_io));
            }
        } else {
            if (player.stopRecordingContent()) {
                File fileFinal = file;
                AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(IPCameraActivity.this);
                normalDialog.setTitle(R.string.ipc_main_record_success);
                normalDialog.setMessage(getString(R.string.ipc_main_record_save_path) + fileFinal.getAbsolutePath());
                normalDialog.setPositiveButton(getString(R.string.ipc_main_record_play),
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
                                    Uri contentUri = FileProvider.getUriForFile(IPCameraActivity.this,
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
                normalDialog.setNegativeButton(R.string.ipc_close,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                // 显示
                normalDialog.show();
            } else {
                showToast(R.string.ipc_main_record_save_fail);
            }
            recordBtn.clearAnimation();
            isRecordingMp4 = false;
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

    private void showPlayButton() {
        videoBufferingProgressBar.setVisibility(View.GONE);
        videoPlayBtn.setVisibility(View.VISIBLE);
    }

    private void showPauseButton() {
        videoBufferingProgressBar.setVisibility(View.GONE);
        videoPauseBtn.setVisibility(View.VISIBLE);
    }

    private void dismissPlayButton() {
        videoPlayBtn.setVisibility(View.GONE);
    }

    private void dismissPauseButton() {
        videoPauseBtn.setVisibility(View.GONE);
    }

    private void showBuffering() {
        videoBufferingProgressBar.setVisibility(View.VISIBLE);
    }

    private void dismissBuffering() {
        videoBufferingProgressBar.setVisibility(View.GONE);
    }

    private void showPlayInfo() {
        playInfoTv.setVisibility(View.VISIBLE);
        updatePlayInfo();
    }

    private void dismissPlayInfo() {
        playInfoTv.setVisibility(View.GONE);
        if (updatePlayInfoHandle != null) {
            updatePlayInfoHandle.cancel(true);
            updatePlayInfoHandle = null;
        }
    }

    private void showToast(String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(IPCameraActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showToast(int msgRes) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(IPCameraActivity.this, msgRes, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updatePlayInfo() {
        if (updatePlayInfoHandle == null) {
            updatePlayInfoHandle = scheduledExecutorService.scheduleAtFixedRate(updatePlayInfoTimerTask, 1, 1, SECONDS);
        }
        PlayInfo playInfo = player.getCurrentPlayInfo();
        playInfoTv.setText(playInfo.frameRate + "fps\n" + (playInfo.bitRate / 1024 / 8) + "KBps");
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

    private final Runnable delayAutoRetryRunnable = new Runnable() {
        @Override
        public void run() {
            playLive(false);
        }
    };


    private void autoRetry(boolean needDelay) {
        if (needAutoReconnect()) {
            if (needDelay) {
                Log.e(TAG, "autoRetry   retryCount:" + retryCount);
                playLive(false);
            } else {
                uiHandler.postDelayed(delayAutoRetryRunnable, 300);
            }
        } else {
            showPlayButton();
            dismissBuffering();
        }
    }

    private void playLive(boolean showInfo) {
        Log.i(TAG, "playLive");

        if (!mResumed) {
            retryCount = maxRetryCount;
            return;
        }
        retryCount++;

        boolean forceIFrame = SharePreferenceManager.getInstance().getForceIFrameSwitch();
        player.setIPCLiveDataSource(iotId, 0, false, 0, forceIFrame);
        if (showInfo) {
            keepScreenLight();
            showBuffering();
            showMobileDataTips();
        }

        player.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                player.start();
            }
        });
        player.prepare();
    }

    private void playLive() {
        playLive(true);
    }

    private void stopLive() {
        player.stop();
        stopScreenLight();
    }

    private void resetRetryCount() {
        retryCount = 0;
    }

    private boolean needAutoReconnect() {
        return mResumed && retryCount < maxRetryCount;
    }

    private void snapshot() {
        Bitmap bitmap = player.snapShot();
        if (bitmap == null) {
            showToast(R.string.ipc_main_snapshot_fail);
            return;
        }
        SnapshotPreviewDialog snapshotPreviewDialog = new SnapshotPreviewDialog(this);
        snapshotPreviewDialog.show();
        snapshotPreviewDialog.setImageBitmap(bitmap);

    }

    private void showStreamLoadingTime() {
        long playStartTimeMillis = System.currentTimeMillis();
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                player.setOnRenderedFirstFrameListener(new OnRenderedFirstFrameListener() {
                    @Override
                    public void onRenderedFirstFrame() {
                        long useTime = (System.currentTimeMillis() - playStartTimeMillis);
                        if (useTime < 800) {
                            streamLoadingTime.setTextColor(Color.GREEN);
                        } else if (useTime < 1500) {
                            streamLoadingTime.setTextColor(Color.YELLOW);
                        } else {
                            streamLoadingTime.setTextColor(Color.RED);
                        }
                        streamLoadingTime.setText(String.format(Locale.getDefault(),
                                getResources().getString(R.string.ipc_reg_main_first_frame_time), useTime));
                    }
                });
            }
        });
    }

    private void hideOtherView() {
        containerRl.setVisibility(View.GONE);
        recordLl.setVisibility(View.GONE);
        hideSystemUI();
    }

    private void showOtherView() {
        containerRl.setVisibility(View.VISIBLE);
        recordLl.setVisibility(View.VISIBLE);
        if(playerTextureView != null){
            playerTextureView.zoomOut(false);
        }
        showSystemUI();
    }

    private View decorView;

    private void hideSystemUI() {
        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void showSystemUI() {
        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_VISIBLE
        );
    }

    private void keepScreenLight() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void stopScreenLight() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

    private void showMobileDataTips() {
        NetworkStateEnum netWorkType = NetworkUtil.getCurrentNetworkState(this);
        if (NetworkStateEnum.MOBILE == netWorkType) {
            playerMobileNetworkTips.setVisibility(View.VISIBLE);
            uiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    playerMobileNetworkTips.setVisibility(View.GONE);
                }
            }, 2000);

        }
    }

}
