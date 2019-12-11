package com.aliyun.iot.demo.ipcview.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.demo.ipcview.R;
import com.aliyun.iot.demo.ipcview.adapter.IpcHistoryListRecycleAdapter;
import com.aliyun.iot.demo.ipcview.beans.VideoInfo;
import com.aliyun.iot.demo.ipcview.constants.Constants;
import com.aliyun.iotx.linkvisual.IPCManager;
import com.aliyun.iotx.linkvisual.media.video.PlayerException;
import com.aliyun.iotx.linkvisual.media.video.beans.PlayerState;
import com.aliyun.iotx.linkvisual.media.video.listener.OnErrorListener;
import com.aliyun.iotx.linkvisual.media.video.listener.OnPlayerStateChangedListener;
import com.aliyun.iotx.linkvisual.media.video.listener.OnPreparedListener;
import com.aliyun.iotx.linkvisual.media.video.player.ExoHlsPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM;

/**
 * 事件录像页面.
 *
 *
 * ！！！注意！！！
 * 注意本示例代码主要用于演示部分视频业务接口以及对应的效果
 * 代码中涉及的交互，UI以及代码框架请自行设计，示例代码仅供参考，稳定性请客户自行保证。
 **/
public class EventVideoActivity extends Activity {

    private String TAG = this.getClass().getSimpleName();

    private RecyclerView recyclerView;
    private IpcHistoryListRecycleAdapter adapter;
    private String iotId;
    private List<VideoInfo> videoInfoList = new ArrayList<>();

    private ToggleButton fullScreenBtn;
    private TextView titleTv;
    private ProgressBar videoBufferingProgressBar;
    private ExoHlsPlayer exoHlsPlayer;
    private PlayerView playerView;

    private int pageSize = 10;
    private int pageStart = 0;
    private int eventEndTime;
    private boolean hasMoreEventVideo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_video);
        iotId = getIntent().getStringExtra("iotId");
        initView();
        initExoPlayer();
        eventEndTime = new Long(System.currentTimeMillis() / 1000).intValue();
    }

    private void initView() {
        recyclerView = this.findViewById(R.id.ipc_history_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new IpcHistoryListRecycleAdapter(videoInfoList);
        adapter.setContext(this);
        adapter.setVideoChangeListener(new IpcHistoryListRecycleAdapter.OnHistoryRecordItemClickedListener() {

            @Override
            public void scrollBottom() {
                if (hasMoreEventVideo) {
                    getEventVideoList();
                } else {
                    showToast(getResources().getString(R.string.ipc_video_no_more));
                    setFooterView(recyclerView);
                    adapter.revertFooterText();
                }
            }

            @Override
            public void onItemClick(String fileName) {
                playVideo(fileName);
            }

            @Override
            public void onItemLongClick(String fileName) {
                playVideo(fileName);
            }
        });
        recyclerView.setAdapter(adapter);
        videoBufferingProgressBar = findViewById(R.id.event_video_buffering_bar);

        fullScreenBtn = findViewById(R.id.event_video_full_screen_tbtn);
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
        titleTv = findViewById(R.id.title_tv);
    }


    private void initExoPlayer() {
        playerView = findViewById(R.id.hls_player_view);
        exoHlsPlayer = new ExoHlsPlayer(getApplicationContext());
        playerView.setPlayer(exoHlsPlayer.getExoPlayer());
        playerView.setResizeMode(RESIZE_MODE_ZOOM);
        exoHlsPlayer.setOnErrorListener(new OnErrorListener() {
            @Override
            public void onError(PlayerException exception) {
                dismissBuffering();
                showToast("errorcode: " + exception.getCode() + "\n" + exception.getMessage());
            }
        });
        exoHlsPlayer.setOnPlayerStateChangedListener(new OnPlayerStateChangedListener() {
            @Override
            public void onPlayerStateChange(int playerState) {
                switch (playerState) {
                    case PlayerState.STATE_BUFFERING:
                        Log.i(TAG, "STATE_BUFFERING");
                        showBuffering();
                        break;
                    case PlayerState.STATE_IDLE:
                        //idle state
                        Log.i(TAG, "STATE_IDLE");
                        break;
                    case PlayerState.STATE_READY:
                        // dismiss your dialog here because our video is ready to play now
                        dismissBuffering();
                        Log.i(TAG, "STATE_READY");
                        break;
                    case PlayerState.STATE_ENDED:
                        // do your processing after ending of video
                        Log.i(TAG, "STATE_ENDED");
                        dismissBuffering();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getEventVideoList();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exoHlsPlayer.release();
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
        if (adapter.getFooterView() == null) {
            View footer = LayoutInflater.from(this).inflate(R.layout.ipc_history_video_footer, view, false);
            adapter.setFooterView(footer);
        }
    }

    private void getEventVideoList() {
        IPCManager.getInstance().getDevice(iotId).queryVideoLst(0, 10,
                eventEndTime, 1, pageStart, pageSize, new IoTCallback() {
                    @Override
                    public void onFailure(IoTRequest ioTRequest, Exception e) {
                        Log.d(TAG, "onFailure");
                    }

                    @Override
                    public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                        Log.d(TAG, "onResponse");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initEventVideoListView(ioTResponse);
                                setFooterView(recyclerView);
                                adapter.revertFooterText();
                            }
                        });
                    }
                });
    }


    private void initEventVideoListView(IoTResponse response) {
        final int code = response.getCode();
        final String localizeMsg = response.getLocalizedMsg();
        if (code != 200) {
            showToast(localizeMsg);
            return;
        }

        Object data = response.getData();
        if (data == null) {
            return;
        }

        try {
            JSONObject jsonObject = (JSONObject) data;
            JSONArray jsonArray = jsonObject.getJSONArray("recordFileList");
            if (jsonArray != null) {
                pageStart = pageStart + 1;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject videoJsonObject = jsonArray.getJSONObject(i);


                    VideoInfo videoInfo = new VideoInfo();
                    videoInfo.iotId = iotId;
                    videoInfo.type = Constants.EVENT_VIDEO;
                    videoInfo.fileName = videoJsonObject.getString("fileName");
                    videoInfo.streamType = videoJsonObject.getInt("streamType");
                    videoInfo.fileSize = videoJsonObject.getInt("fileSize");
                    videoInfo.recordType = videoJsonObject.getInt("recordType");
                    videoInfo.beginTime = videoJsonObject.getString("beginTime");
                    videoInfo.endTime = videoJsonObject.getString("endTime");
                    videoInfoList.add(videoInfo);

                }
                if (jsonArray.length() < pageSize) {
                    hasMoreEventVideo = false;
                }
                adapter.setVideoList(videoInfoList);
                adapter.notifyDataSetChanged();
                recyclerView.requestFocus();
                recyclerView.bringToFront();
                recyclerView.invalidate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playVideo(String fileName) {
        stopVideo();
        Log.e(TAG,"playVideo:"+fileName);
        exoHlsPlayer.setDataSourceByIPCRecordFileName(iotId, fileName);
        exoHlsPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                exoHlsPlayer.start();
            }
        });
        exoHlsPlayer.prepare();
    }

    private void stopVideo() {
        exoHlsPlayer.stop();
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
                Toast.makeText(EventVideoActivity.this, s, Toast.LENGTH_SHORT).show();
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
}
