package com.aliyun.iot.demo.ipcview.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSON;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;
import com.aliyun.iot.demo.ipcview.R;
import com.aliyun.iot.demo.ipcview.adapter.IpcHistoryListRecycleAdapter;
import com.aliyun.iot.demo.ipcview.beans.VideoInfo;
import com.aliyun.iot.demo.ipcview.constants.Constants;
import com.aliyun.iot.demo.ipcview.manager.SharePreferenceManager;
import com.aliyun.iotx.linkvisual.IPCManager;
import com.aliyun.iotx.linkvisual.media.video.PlayerException;
import com.aliyun.iotx.linkvisual.media.video.beans.PlayerState;
import com.aliyun.iotx.linkvisual.media.video.listener.OnErrorListener;
import com.aliyun.iotx.linkvisual.media.video.listener.OnPlayerStateChangedListener;
import com.aliyun.iotx.linkvisual.media.video.listener.OnPreparedListener;
import com.aliyun.iotx.linkvisual.media.video.player.VodPlayer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * 查看卡录像界面。
 *
 *
 * ！！！注意！！！
 * 注意本示例代码主要用于演示部分视频业务接口以及对应的效果
 * 代码中涉及的交互，UI以及代码框架请自行设计，示例代码仅供参考，稳定性请客户自行保证。
 *
 * @author azad
 */
public class CardVideoActivity extends Activity {
    private String TAG = this.getClass().getSimpleName();

    private RecyclerView recyclerView;
    private IpcHistoryListRecycleAdapter ipcHistoryListRecycleAdapter;
    private String iotId;
    private List<VideoInfo> videoInfoList = new ArrayList<>();

    private ToggleButton pauseResumeBtn, fullScreenBtn;
    private ProgressBar videoBufferingProgressBar;
    private VodPlayer vodPlayer;
    private GLSurfaceView playerGLSurfaceView;
    private SeekBar cardSeekBar;
    private TextView durationTv, titleTv;

    ScheduledExecutorService scheduledExecutorService = newScheduledThreadPool(1);
    ScheduledFuture<?> timelineUpdateHandle;
    final SimpleDateFormat timeLineFormatter = new SimpleDateFormat("mm:ss");

    private boolean hasMoreCardVideo = true;
    private long lastTime;
    private long cardEndTime;
    private int pageSize = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_video);
        iotId = getIntent().getStringExtra("iotId");
        initView();
        initVodPlayer();
        cardEndTime = new Long(System.currentTimeMillis() / 1000).intValue();
        lastTime = cardEndTime;
        /**
         * 当发现有存储卡的时候才会查看卡录像列表
         */
        if (SharePreferenceManager.getInstance().getStorageStatus() == 1) {
            getCardVideoList();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        playerGLSurfaceView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopVideo();
        cancelUpdateTimeline();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vodPlayer.release();
    }

    private void initView() {
        recyclerView = this.findViewById(R.id.ipc_history_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ipcHistoryListRecycleAdapter = new IpcHistoryListRecycleAdapter(videoInfoList);
        ipcHistoryListRecycleAdapter.setContext(this);
        ipcHistoryListRecycleAdapter.setVideoChangeListener(new IpcHistoryListRecycleAdapter.OnHistoryRecordItemClickedListener() {

            @Override
            public void scrollBottom() {
                if (hasMoreCardVideo) {
                    getCardVideoList();
                } else {
                    showToast(getResources().getString(R.string.ipc_video_no_more));
                    setFooterView(recyclerView);
                    ipcHistoryListRecycleAdapter.revertFooterText();
                }
            }

            @Override
            public void onItemClick(String fileName) {
                playVideo(fileName, false, 0);
                showToast(getResources().getString(R.string.ipc_video_play_not_encrypt));
            }

            @Override
            public void onItemLongClick(String fileName) {
                playVideo(fileName, true, 0);
                showToast(getResources().getString(R.string.ipc_video_play_encrypt));
            }
        });
        recyclerView.setAdapter(ipcHistoryListRecycleAdapter);
        videoBufferingProgressBar = findViewById(R.id.card_video_buffering_bar);

        cardSeekBar = findViewById(R.id.card_player_seek_bar);
        cardSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (vodPlayer.getDuration() > 0) {
                    int currentProgress = (int) (progress * vodPlayer.getDuration() / cardSeekBar.getMax());
                    durationTv.setText(timeLineFormatter.format(currentProgress) + "/" + timeLineFormatter
                            .format(vodPlayer.getDuration()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int currentProgress = (int) (seekBar.getProgress() * vodPlayer.getDuration() / cardSeekBar.getMax());
                vodPlayer.seekTo(currentProgress);
                updateTimeline();
            }
        });

        titleTv = findViewById(R.id.title_tv);
        durationTv = findViewById(R.id.card_duration_tv);
        pauseResumeBtn = findViewById(R.id.card_pause_resume_tbtn);
        pauseResumeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    vodPlayer.start();
                } else {
                    vodPlayer.pause();
                }
            }
        });

        fullScreenBtn = findViewById(R.id.card_video_full_screen_tbtn);
        fullScreenBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                if (isChecked) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            super.onBackPressed();
            stopVideo();
        } else {
            fullScreenBtn.setChecked(false);
        }
    }

    private void setFooterView(RecyclerView view) {
        if (ipcHistoryListRecycleAdapter.getFooterView() == null) {
            View footer = LayoutInflater.from(this).inflate(R.layout.ipc_history_video_footer, view, false);
            ipcHistoryListRecycleAdapter.setFooterView(footer);
        }
    }

    private void initVodPlayer() {
        playerGLSurfaceView = findViewById(R.id.card_player_surface_view);
        vodPlayer = new VodPlayer();
        vodPlayer.setSurfaceView(playerGLSurfaceView);
        vodPlayer.setOnErrorListener(new OnErrorListener() {
            @Override
            public void onError(PlayerException exception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissBuffering();
                        cancelUpdateTimeline();
                        showToast("errorcode: " + exception.getCode() + "\n" + exception.getMessage());
                    }
                });
            }
        });
        vodPlayer.setOnPlayerStateChangedListener(new OnPlayerStateChangedListener() {
            @Override
            public void onPlayerStateChange(int playerState) {
                switch (playerState) {
                    case PlayerState.STATE_BUFFERING:
                        showBuffering();
                        Log.w(TAG, "STATE_BUFFERING");
                        break;
                    case PlayerState.STATE_IDLE:
                        //idle state
                        Log.w(TAG, "STATE_IDLE");
                        break;
                    case PlayerState.STATE_READY:
                        // dismiss your dialog here because our video is ready to play now
                        dismissBuffering();
                        updateTimeline();
                        pauseResumeBtn.setChecked(true);
                        Log.w(TAG, "STATE_READY");
                        break;
                    case PlayerState.STATE_ENDED:
                        // do your processing after ending of video
                        Log.w(TAG, "STATE_ENDED");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void getCardVideoList() {
        IPCManager.getInstance().getDevice(iotId).queryCardRecordList(10,
                cardEndTime - 1,
                pageSize, 0, new IPanelCallback() {
                    @Override
                    public void onComplete(boolean b, Object o) {
                        Log.e(TAG,
                                "queryCardRecordList:" + b + "  " + "     O:" + (o != null ? String.valueOf(o) : "null"));

                        if (b) {
                            if (o != null && !String.valueOf(o).equals("")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        initCardVideoListView(o);
                                        setFooterView(recyclerView);
                                        ipcHistoryListRecycleAdapter.revertFooterText();
                                    }
                                });
                            }
                        }
                    }
                });
    }

    private void initCardVideoListView(Object response) {

        String data = response.toString();
        if (TextUtils.isEmpty(data)) {
            return;
        }

        try {
            com.alibaba.fastjson.JSONObject obj = JSON.parseObject(data);
            obj = obj.getJSONObject("data");
            if (obj == null) {
                return;
            }
            com.alibaba.fastjson.JSONArray jsonArray = obj.getJSONArray("RecordList");
            if (jsonArray != null) {
                if (videoInfoList == null) {
                    videoInfoList = new ArrayList<>();
                }
                for (int i = 0; i < jsonArray.size(); i++) {
                    com.alibaba.fastjson.JSONObject videoJsonObject = jsonArray.getJSONObject(i);

                    VideoInfo videoInfo = new VideoInfo();
                    videoInfo.iotId = iotId;
                    videoInfo.type = Constants.CARD_VIDEO;
                    videoInfo.fileName = videoJsonObject.getString("FileName");
                    videoInfo.fileSize = videoJsonObject.getInteger("Size");
                    videoInfo.recordType = videoJsonObject.getInteger("Type");
                    videoInfo.beginTime = videoJsonObject.getString("BeginTime");
                    videoInfo.endTime = videoJsonObject.getString("EndTime");
                    lastTime = Long.parseLong(videoJsonObject.getString("BeginTime"));
                    if (!videoInfoList.contains(videoInfo)) {
                        videoInfoList.add(videoInfo);
                    }
                    if (cardEndTime > lastTime) {
                        cardEndTime = lastTime;
                    }
                }
                if (jsonArray.size() < pageSize) {
                    hasMoreCardVideo = false;
                }
                Collections.sort(videoInfoList);
                ipcHistoryListRecycleAdapter.setVideoList(videoInfoList);
                ipcHistoryListRecycleAdapter.notifyDataSetChanged();
                recyclerView.requestFocus();
                recyclerView.bringToFront();
                recyclerView.invalidate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playVideo(String fileName, boolean encrypted, int encryptType) {
        stopVideo();
        vodPlayer.setDataSourceByIPCRecordFileName(iotId, fileName, encrypted, encryptType);
        vodPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                vodPlayer.start();
            }
        });
        vodPlayer.prepare();
    }

    private void stopVideo() {
        vodPlayer.stop();
    }

    private void showBuffering() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                videoBufferingProgressBar.setVisibility(View.VISIBLE);
            }
        });

    }

    private void dismissBuffering() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                videoBufferingProgressBar.setVisibility(View.GONE);
            }
        });

    }

    private void showToast(String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CardVideoActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });

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

    private void hideOtherView() {
        recyclerView.setVisibility(View.GONE);
        titleTv.setVisibility(View.GONE);
        hideSystemUI();
    }

    private void showOtherView() {
        recyclerView.setVisibility(View.VISIBLE);
        titleTv.setVisibility(View.VISIBLE);
        showSystemUI();
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );

    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_VISIBLE
        );
    }

    private void cancelUpdateTimeline() {
        if (timelineUpdateHandle != null) {
            timelineUpdateHandle.cancel(true);
            timelineUpdateHandle = null;
        }
    }

    private void updateTimeline() {
        if (timelineUpdateHandle == null) {
            timelineUpdateHandle = scheduledExecutorService.scheduleAtFixedRate(timelineUpdateTask, 100, 100, MILLISECONDS);
        }
        if (vodPlayer != null) {
            long duration = vodPlayer.getDuration();
            if (duration <= 0) {
                durationTv.setText("-/-");
                cardSeekBar.setProgress(0);
                timelineUpdateHandle.cancel(true);
                timelineUpdateHandle = null;
            } else {
                cardSeekBar.setProgress((int) (vodPlayer.getCurrentPosition() * cardSeekBar.getMax() / duration));
                durationTv.setText(timeLineFormatter.format(vodPlayer.getCurrentPosition()) + "/" + timeLineFormatter
                        .format(duration));
            }
        }
    }

    final Runnable timelineUpdateTask = new Runnable() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateTimeline();
                }
            });

        }
    };

}
