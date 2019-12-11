package com.aliyun.iot.demo.ipcview.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.aliyun.alink.linksdk.tools.ALog;
import com.aliyun.iot.demo.ipcview.R;
import com.aliyun.iotx.linkvisual.media.video.PlayerException;
import com.aliyun.iotx.linkvisual.media.video.beans.PlayerState;
import com.aliyun.iotx.linkvisual.media.video.listener.OnErrorListener;
import com.aliyun.iotx.linkvisual.media.video.listener.OnPlayerStateChangedListener;
import com.aliyun.iotx.linkvisual.media.video.listener.OnPreparedListener;
import com.aliyun.iotx.linkvisual.media.video.player.LivePlayer;
import com.aliyun.iotx.linkvisual.media.video.views.ZoomableTextureView;


import java.text.SimpleDateFormat;
import java.util.Date;

import static android.media.MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT;

public class MultiPlayerActivity extends Activity {

    private static final String TAG = "MultiPlayerActivity";

    ConstraintLayout playerContainer;

    private LivePlayer[] players = new LivePlayer[4];
    private ZoomableTextureView[] zoomableTextureViews = new ZoomableTextureView[4];

    private String iotId;

    private int currentFocusPlayerIndex = -1;

    private int playerContainerHeight;
    private int playerContainerWidth;

    private TextView logTv;
    private ScrollView logScroll;
    private ToggleButton fullScreenSwitch;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iotId = getIntent().getStringExtra("iotId");

        setContentView(R.layout.activity_multi_player);
        hideSystemUI();

        logTv = findViewById(R.id.log_tv);
        logScroll = findViewById(R.id.log_scroll);
        fullScreenSwitch = findViewById(R.id.full_screen_switch);
        fullScreenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });

        playerContainer = findViewById(R.id.player_container);

        playerContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                playerContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                // get player container size
                playerContainerHeight = playerContainer.getLayoutParams().height;
                playerContainerWidth = getWindowManager().getDefaultDisplay().getWidth();

                initTextureViews();
                setFourTextureViews();
            }
        });
        initPlayers();
    }

    /**
     * 初始化4个播放器
     */
    private void initPlayers() {
        ALog.setLevel(ALog.LEVEL_DEBUG);

        // create 4 player
        for (int i = 0; i < players.length; i++) {
            players[i] = new LivePlayer();
            players[i].setOnPlayerStateChangedListener(new OnPlayerStateChangedListener() {
                @Override
                public void onPlayerStateChange(int playerState) {
                    switch (playerState) {
                        case PlayerState.STATE_BUFFERING:
                            appendLog("play state= 正在加载");
                            break;
                        case PlayerState.STATE_READY:
                            appendLog("play state= 出画面");
                            break;
                        case PlayerState.STATE_ENDED:
                            appendLog("play state= 播放结束");
                            break;
                        default:
                            break;
                    }
                }
            });
            players[i].setOnErrorListener(new OnErrorListener() {
                @Override
                public void onError(PlayerException exception) {
                    appendLog("errorcode: " + exception.getCode() + "\n" + exception.getMessage());
                }
            });
        }
    }

    /**
     * 初始化4个textureview
     */
    private void initTextureViews() {
        for (int i = 0; i < players.length; i++) {
            zoomableTextureViews[i] = new ZoomableTextureView(this);
            players[i].setTextureView(zoomableTextureViews[i]);
            // 设置画面强制拉伸
            players[i].setVideoScalingMode(VIDEO_SCALING_MODE_SCALE_TO_FIT);
            zoomableTextureViews[i].setTag(i);
            zoomableTextureViews[i].setOnZoomableTextureListener(new ZoomableTextureView.OnZoomableTextureListener() {
                /**
                 * 当画面缩放比例发生变化时回调
                 *
                 * @param zoomableTextureView
                 * @param scale               画面缩放比例
                 */
                @Override
                public void onScaleChanged(ZoomableTextureView zoomableTextureView, float scale) {
                }

                /**
                 * view双击事件回调
                 *
                 * @param zoomableTextureView
                 * @param e                   MotionEvent
                 * @return 事件是否被处理，如果返回false，则会启用内部缩放逻辑
                 */
                @Override
                public boolean onDoubleTap(ZoomableTextureView zoomableTextureView, MotionEvent e) {
                    int effectPlayerIndex = (int) zoomableTextureView.getTag();
                    // 当前是单屏显示
                    if (currentFocusPlayerIndex >= 0) {
                        // 如果当前窗口被放缩，则先将播放器的缩放比重置为1.0f
                        if (zoomableTextureView.getScale() > 1.0f) {
                            zoomableTextureView.zoomOut(true);
                        } else {
                            // 当前处于全屏模式，不去切回到四分屏
                            if(fullScreenSwitch.isChecked()){
                                return true;
                            }
                            // 当前窗口未放缩，则切回到四分屏
                            appendLog("缩小" + (currentFocusPlayerIndex + 1) + "号窗口，显示4分屏");
                            // TODO 通过物模型指令向所有设备发送切换到子码流的指令
                            displayFourScreen(currentFocusPlayerIndex);
                        }
                    } else {
                        // 当前为四分屏，需要放大选中的播放器
                        appendLog("放大" + (effectPlayerIndex + 1) + "号窗口");
                        // TODO 通过物模型指令向设备发送切换到主码流的指令
                        displayOneScreen(effectPlayerIndex);
                    }
                    // 返回true，ZoomableTextureView不去处理双击放缩
                    return true;
                }

                /**
                 * view单击事件回调
                 *
                 * @param zoomableTextureView
                 * @param e                   MotionEvent
                 * @return 事件是否被处理
                 */
                @Override
                public boolean onSingleTapConfirmed(ZoomableTextureView zoomableTextureView, MotionEvent e) {
                    return false;
                }

                /**
                 * view长按事件回调
                 *
                 * @param zoomableTextureView
                 * @param e                   MotionEvent
                 */
                @Override
                public void onLongPress(ZoomableTextureView zoomableTextureView, MotionEvent e) {
                }
            });

            zoomableTextureViews[i].setId(View.generateViewId());
            playerContainer.addView(zoomableTextureViews[i]);
        }
    }

    /**
     * 设置四分屏
     */
    private void setFourTextureViews() {
        for (int i = 0; i < players.length; i++) {
            ALog.e(TAG, "h=" + playerContainerHeight + ", w=" + playerContainerWidth);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(playerContainer);
            switch (i) {
                case 0:
                    constraintSet.connect(zoomableTextureViews[i].getId(), ConstraintSet.LEFT, playerContainer.getId(), ConstraintSet.LEFT, 0);
                    constraintSet.connect(zoomableTextureViews[i].getId(), ConstraintSet.TOP, playerContainer.getId(), ConstraintSet.TOP, 0);
                    break;
                case 1:
                    constraintSet.connect(zoomableTextureViews[i].getId(), ConstraintSet.RIGHT, playerContainer.getId(), ConstraintSet.RIGHT, 0);
                    constraintSet.connect(zoomableTextureViews[i].getId(), ConstraintSet.TOP, playerContainer.getId(), ConstraintSet.TOP, 0);
                    break;
                case 2:
                    constraintSet.connect(zoomableTextureViews[i].getId(), ConstraintSet.LEFT, playerContainer.getId(), ConstraintSet.LEFT, 0);
                    constraintSet.connect(zoomableTextureViews[i].getId(), ConstraintSet.BOTTOM, playerContainer.getId(), ConstraintSet.BOTTOM, 0);
                    break;
                case 3:
                    constraintSet.connect(zoomableTextureViews[i].getId(), ConstraintSet.RIGHT, playerContainer.getId(), ConstraintSet.RIGHT, 0);
                    constraintSet.connect(zoomableTextureViews[i].getId(), ConstraintSet.BOTTOM, playerContainer.getId(), ConstraintSet.BOTTOM, 0);
                    break;
                default:
                    break;
            }
            constraintSet.constrainHeight(zoomableTextureViews[i].getId(), playerContainerHeight / 2);
            constraintSet.constrainWidth(zoomableTextureViews[i].getId(), playerContainerWidth / 2);
            constraintSet.applyTo(playerContainer);
            // 将播放器的缩放比重置为1.0f
            zoomableTextureViews[i].zoomOut(false);
            // 显示所有播放器的窗口
            zoomableTextureViews[i].setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示单屏
     *
     * @param effectPlayerIndex
     */
    private void displayOneScreen(int effectPlayerIndex) {
        currentFocusPlayerIndex = effectPlayerIndex;
        setOneTextureView(effectPlayerIndex);
        stopOthers(effectPlayerIndex);
        fullScreenSwitch.setVisibility(View.VISIBLE);
    }

    /**
     * 移除其他三屏的textuerview，并将选中的播放器textureview放大
     *
     * @param effectPlayerIndex
     */
    private void setOneTextureView(int effectPlayerIndex) {
        for (int i = 0; i < players.length; i++) {
            if (i == effectPlayerIndex) {
                continue;
            }
            zoomableTextureViews[i].setVisibility(View.INVISIBLE);
        }
        // resize the one textureview
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(playerContainerWidth, playerContainerHeight);
        zoomableTextureViews[effectPlayerIndex].setLayoutParams(layoutParams);
    }

    /**
     * 关闭除指定的播放器之外的其他所有播放器的播放
     *
     * @param excludePlayerIndex 例外的播放器index
     */
    private void stopOthers(int excludePlayerIndex) {
        for (int i = 0; i < players.length; i++) {
            if (i == excludePlayerIndex) {
                continue;
            }
            players[i].stop();
        }
    }

    /**
     * 显示四分屏
     *
     * @param effectPlayerIndex
     */
    private void displayFourScreen(int effectPlayerIndex) {
        currentFocusPlayerIndex = -1;
        setFourTextureViews();
        playOthers(effectPlayerIndex);
        fullScreenSwitch.setVisibility(View.INVISIBLE);
    }

    /**
     * 播放除了指定播放器之外的其他播放器
     *
     * @param excludePlayerIndex 例外的播放器index
     */
    private void playOthers(int excludePlayerIndex) {
        for (int i = 0; i < players.length; i++) {
            if (excludePlayerIndex == i) {
                // 忽略当前已经在播放的播放器
                continue;
            }
            players[i].setIPCLiveDataSource(iotId, 0, true, 0, true);
            final int finalI = i;
            players[i].setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared() {
                    players[finalI].start();
                }
            });
            players[i].prepare();
        }
    }

    /**
     * 释放4路播放器资源
     */
    private void releasePlayers() {
        for (int i = 0; i < players.length; i++) {
            players[i].release();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
            LinearLayout.LayoutParams playerContainerLayoutParams = (LinearLayout.LayoutParams) playerContainer.getLayoutParams();
            playerContainerLayoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
            playerContainerLayoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            playerContainer.setLayoutParams(playerContainerLayoutParams);

            ConstraintLayout.LayoutParams playerLayoutParams = (ConstraintLayout.LayoutParams) zoomableTextureViews[currentFocusPlayerIndex].getLayoutParams();
            playerLayoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
            playerLayoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
            zoomableTextureViews[currentFocusPlayerIndex].setLayoutParams(playerLayoutParams);
        } else {
            LinearLayout.LayoutParams playerContainerLayoutParams = (LinearLayout.LayoutParams) playerContainer.getLayoutParams();
            playerContainerLayoutParams.height = (int) (300 * getResources().getDisplayMetrics().density);
            playerContainerLayoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            playerContainer.setLayoutParams(playerContainerLayoutParams);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        playOthers(currentFocusPlayerIndex);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void makeToast(String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MultiPlayerActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private void appendLog(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logTv.append(format.format(new Date()) + "\t" + msg);
                logTv.append("\n");
                logScroll.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


}
