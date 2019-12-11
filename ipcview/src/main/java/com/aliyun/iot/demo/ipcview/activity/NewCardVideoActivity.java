package com.aliyun.iot.demo.ipcview.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import androidx.core.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSON;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.demo.ipcview.R;
import com.aliyun.iot.demo.ipcview.activity.calendar.AnimationTools;
import com.aliyun.iot.demo.ipcview.activity.calendar.CustomeDayViewAdapter;
import com.aliyun.iot.demo.ipcview.activity.calendar.DateDecorator;
import com.aliyun.iot.demo.ipcview.beans.VideoInfo;
import com.aliyun.iot.demo.ipcview.dialog.SnapshotPreviewDialog;
import com.aliyun.iot.demo.ipcview.utils.StringUtil;
import com.aliyun.iot.demo.ipcview.utils.TimeUtil;
import com.aliyun.iot.demo.ipcview.widget.TimeRulerView;
import com.aliyun.iotx.linkvisual.IPCManager;
import com.aliyun.iotx.linkvisual.media.video.PlayerException;
import com.aliyun.iotx.linkvisual.media.video.beans.PlayerState;
import com.aliyun.iotx.linkvisual.media.video.listener.OnCompletionListener;
import com.aliyun.iotx.linkvisual.media.video.listener.OnErrorListener;
import com.aliyun.iotx.linkvisual.media.video.listener.OnPlayerStateChangedListener;
import com.aliyun.iotx.linkvisual.media.video.listener.OnPreparedListener;
import com.aliyun.iotx.linkvisual.media.video.player.VodPlayer;
import com.aliyun.iotx.linkvisual.media.video.views.ZoomableTextureView;
import com.savvi.rangedatepicker.CalendarCellDecorator;
import com.savvi.rangedatepicker.CalendarPickerView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static android.os.Environment.DIRECTORY_MOVIES;

public class NewCardVideoActivity extends Activity implements View.OnClickListener {
    private final int REQUESTCODE_WRITE_EXTERNAL = 0x1112;
    public static final String TAG = "NewCardVideoActivity";

    /**
     * 播放模式
     */
    public enum EnumPlayOrderMode {
        /**
         * 按文件播放
         */
        PLAY_ORDER_MODE_BY_FILE,
        /**
         * 按时间播放
         */
        PLAY_ORDER_MODE_BY_TIME
    }

    /**
     * 当前的播放模式
     */
    private EnumPlayOrderMode currentPlayOrderMode = EnumPlayOrderMode.PLAY_ORDER_MODE_BY_TIME;

    /**
     * Handler标签，用于切换到主线程，通知时间尺刷新
     */
    public static final int FLAG_UI_TIMERULER_UPDATE = 203;
    /**
     * Handler标签，用于切换到主线程，定时任务更新时间尺当前刻度
     */
    public static final int FLAG_SEEKBAR_UPDATE = 204;

    protected String iotId;

    private TextView titleTv;

    /**
     * 显示提示信息
     */
    private Button noVideoBtn;
    /**
     * 暂停、播放按钮
     */
    private ImageView pauseIv;
    /**
     * 进度条
     */
    private SeekBar seekBar;
    /**
     * 当前播放时间、当前视频时间
     */
    private TextView curDurationTv, allDurationTv;

    /**
     * 视频播放控件
     */
    private ZoomableTextureView playerTextureView;
    /**
     * 视频播放器
     */
    protected VodPlayer mVodPlayer;
    /**
     * 视频加载loading占位控件
     */
    private ProgressBar mVideoBufferingProgressBar;

    /**
     * 重播控件
     */
    private ViewStub replayVs;
    /**
     * 拖动进度条时显示的时间控件
     */
    private ViewStub newSeekVs;
    private View replayV;
    private View newSeekV;
    private TextView newSeekTimeTv;
    private SeekBar smallSb;

    private RelativeLayout timeControlRl;
    /**
     * 当前日期
     */
    protected TextView dateTv;
    /**
     * 选中的日期
     */
    protected TextView selectTimeLabelTv;
    /**
     * 时间尺控件
     */
    protected TimeRulerView timeRuleView;

    /**
     * 日期选择容器+背景
     */
    protected FrameLayout calendarFl;
    /**
     * 日期选择容器
     */
    protected LinearLayout calendarLl;
    private TextView titleDateTv;
    private ImageView closeIv;
    private Button okBtn;
    protected CalendarPickerView calendarPickerView;
    private Handler mHandler;

    /**
     * 视频数据
     */
    private List<VideoInfo> videoList = new CopyOnWriteArrayList<>();
    /**
     * 时间尺上需要绘制的时间片段数据，将{@link #videoList}数据进行转换得到
     */
    private List<TimeRulerView.TimePart> timeParts = new LinkedList<>();

    /**
     * 上一播放录像名，用于重播功能使用
     */
    protected String prePlayFileName = "";
    /**
     * 当前播放录像名，用于同录像内seek操作判断
     */
    private static String nowPlayFileName = "";
    /**
     * 当前正在查询的录像名
     */
    private String nowQueryFileName = "";

    /**
     * 全屏、非全屏按钮
     */
    private ToggleButton zoomBtn;
    /**
     * 录像按钮
     */
    private ToggleButton recordBtn;
    /**
     * 截图按钮
     */
    private ToggleButton captureBtn;

    SimpleDateFormat timeLineFormatter;
    SimpleDateFormat dateFormatter;
    /**
     * 用于执行定时任务
     */
    ScheduledExecutorService scheduledExecutorService;
    ScheduledFuture<?> timelineUpdateHandle;
    Runnable beeper;

    private double seekToVal;

    private Date dateTimeLine;

    /**
     * 从今天开始的毫秒数
     */
    private long beginTimeOfThisDayInMS;

    /**
     * 播放录像的开始时间，单位毫秒，（unix时间戳）
     */
    private long curTimeSpanStart;
    /**
     * 播放录像的开始时间，单位秒，（unix时间-当前0点unix时间戳）
     */
    private int curTimeSpanTodayStart;
    /**
     * 播放录像的时长，单位秒
     */
    private int curTimeSpanSpan;

    private Animation alphaAnimation;

    /**
     * 日历控件上的按钮装饰类
     */
    private DateDecorator calendarDecorator;
    private ArrayList<CalendarCellDecorator> decorators;

    /**
     * 日历上选中的日期（未点击确认按钮）
     */
    private Date curShownDate;
    /**
     * 选中的日期
     */
    private Date curSelectedDate;

    /**
     * 日历配置
     */
    private CalendarPickerView.FluentInitializer calendarInitializer;

    protected <T extends View> T findView(int viewId) {
        return (T) findViewById(viewId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_card_video);
        Intent intent = getIntent();
        if (intent != null) {
            iotId = intent.getStringExtra("iotId");
        }
        timeLineFormatter = new SimpleDateFormat("mm:ss", Locale.getDefault());
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        beeper = new Runnable() {
            @Override
            public void run() {
                updateTimelineOnUIThread();
            }
        };

        alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_loop);

        initView();
        initData();
    }


    private void initView() {
        titleTv = findView(R.id.tv_title);
        noVideoBtn = findView(R.id.tv_no_video);
        noVideoBtn.bringToFront();
        mVideoBufferingProgressBar = findView(R.id.pb_video_buffering);
        timeControlRl = findView(R.id.rl_time_control);
        dateTv = findView(R.id.tv_date);

        initVideoControl();
        initTimeRulerView();
        initCalendar();
        initZoomBtn();
        initPlayer();
    }

    protected void initData() {
        mHandler = new PlayHandler(this);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        queryVideoForSomeday(calendar.getTimeInMillis() / 1000);
        beginTimeOfThisDayInMS = calendar.getTimeInMillis();
    }

    /**
     * 横竖屏回调，处理部分UI
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
            hideOtherView();
        } else {
            showOtherView();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseAndStopBeat();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mVodPlayer.getPlayState() == PlayerState.STATE_READY) {
            pauseIv.setSelected(true);
            updateTimeline(true);
            resumeVideo();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopRecordMp4(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopVideo();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.tv_date == id) {
            AnimationTools.with().bottomMoveToViewLocation(calendarLl, calendarFl, 400);
        } else if (R.id.ll_replay == id) {
            hideReplay();

            pauseIv.setSelected(true);
            setFilenameToPlayer(prePlayFileName);
        } else if (R.id.iv_pause_resume == id) {
            if (pauseIv.isSelected()) {
                mVodPlayer.pause();
                seekBar.setEnabled(false);
            } else {
                mVodPlayer.start();
                seekBar.setEnabled(true);
            }
            pauseIv.setSelected(!pauseIv.isSelected());
        } else if (id == R.id.tbtn_record) {
            if (!hasWritePermission()) {
                requestWritePermission();
                return;
            }
            if (mVodPlayer.getPlayState() == PlayerState.STATE_READY) {
                startOrStopRecordingMp4();
            } else {
                showToast(R.string.ipc_video_record_fail);
            }

        } else if (id == R.id.tbtn_capture) {
            if (!hasWritePermission()) {
                requestWritePermission();
                return;
            }
            if (mVodPlayer.getPlayState() == PlayerState.STATE_READY) {
                snapshot();
            } else {
                showToast(R.string.ipc_video_snapshot_fail);
            }

        } else if (R.id.iv_close == id) {
            AnimationTools.with().moveToViewBottom(calendarLl, calendarFl, 400);
            if (curSelectedDate.getTime() != curShownDate.getTime()) {
                calendarInitializer.withSelectedDate(curShownDate);
            }
        } else if (R.id.btn_ok == id) {
            AnimationTools.with().moveToViewBottom(calendarLl, calendarFl, 400);
            curShownDate = curSelectedDate;
            dateTv.setText(TimeUtil.getFormatDay(curShownDate));
            timeParts.clear();
            timeRuleView.setTimePartListAndBackCenter(timeParts);
            videoList.clear();
            resetVideoView(true);
            queryVideoForSomeday(curShownDate.getTime() / 1000);
            beginTimeOfThisDayInMS = curShownDate.getTime();
        }
    }

    private void hideOtherView() {
        titleTv.setVisibility(View.GONE);
        timeControlRl.setVisibility(View.GONE);
        hideSystemUI();
    }

    private void showOtherView() {
        titleTv.setVisibility(View.VISIBLE);
        timeControlRl.setVisibility(View.VISIBLE);
        showSystemUI();
    }

    int mPlayerState = PlayerState.STATE_IDLE;

    /**
     * 初始化播放器 VodPlayer
     */
    protected void initPlayer() {
        playerTextureView = findViewById(R.id.card_player_texture_view);
        playerTextureView.setMaxScale(4);//设置最大的缩放比例，可不设置，默认为4
        mVodPlayer = new VodPlayer();
        mVodPlayer.setTextureView(playerTextureView);
        mVodPlayer.setOnErrorListener(new OnErrorListener() {
            @Override
            public void onError(PlayerException exception) {
                resetVideoView(false);
                int code = exception.getCode();
                switch (code) {
                    case PlayerException.SOURCE_ERROR:
                        showCoverOffline();
                        break;
                    case PlayerException.UNEXPECTED_ERROR:
                    case PlayerException.RENDER_ERROR:
                        showCoverNotConnect();
                        break;
                }
            }
        });
        mVodPlayer.setOnPlayerStateChangedListener(new OnPlayerStateChangedListener() {
            @Override
            public void onPlayerStateChange(int playerState) {
                switch (playerState) {
                    case PlayerState.STATE_BUFFERING:
                        hideCoverStateView();
                        showBuffering();
                        keepScreenLight();
                        Log.w(TAG, "STATE_BUFFERING");
                        break;
                    case PlayerState.STATE_IDLE:
                        //idle state
                        Log.w(TAG, "STATE_IDLE");
                        break;
                    case PlayerState.STATE_READY:
                        // dismiss your dialog here because our video is ready to play now
                        if (nowQueryFileName != null) {
                            nowPlayFileName = nowQueryFileName;
                            prePlayFileName = nowPlayFileName;
                            nowQueryFileName = null;
                        }
                        hideCoverStateView();
                        updateTimeline(true);
                        pauseIv.setEnabled(true);
                        seekBar.setEnabled(true);
                        pauseIv.setSelected(true);
                        allDurationTv.setText(timeLineFormatter.format(updateDateTimeLine(mVodPlayer.getDuration())));
                        if (Math.abs(seekToVal) > 0.0001) {
                            mVodPlayer.seekTo((long) (mVodPlayer.getDuration() * seekToVal));
                            seekToVal = 0;
                        } else {
                            dismissBuffering();
                        }

                        Log.w(TAG, "STATE_READY");
                        break;
                    case PlayerState.STATE_ENDED:
                        // do your processing after ending of video
                        Log.w(TAG, "STATE_ENDED");
                        dismissBuffering();

                        stopScreenLight();

                        file = null;
                        recordBtn.clearAnimation();
                        isRecordingMp4 = false;
                        break;
                    default:
                        break;
                }
                mPlayerState = playerState;
            }
        });
        mVodPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                mVodPlayer.start();
            }
        });
        mVodPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion() {
                Log.w(TAG, "onCompletion");
                dismissBuffering();
                hideCoverStateView();
            }
        });
    }

    private void showCoverNoneVideo() {
        showErrorCoverStateView(getString(R.string.ipc_video_no_video));
    }

    private void showCoverSwipToPlay() {
        showErrorCoverStateView(getString(R.string.ipc_video_swip_to_section));
    }

    private void showCoverNotConnect() {
        showErrorCoverStateView(getString(R.string.ipc_video_not_connect));
    }

    private void showCoverDataError() {
        showErrorCoverStateView(getString(R.string.ipc_video_data_err));
    }

    private void showCoverOffline() {
        showErrorCoverStateView(getString(R.string.ipc_video_device_offline));
    }

    /**
     * 显示异常信息
     *
     * @param msg 异常信息
     */
    private void showErrorCoverStateView(String msg) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    showErrorCoverStateView(msg);
                }
            });
            return;
        }
        if (!TextUtils.isEmpty(msg)) {
            noVideoBtn.setText(msg);
        }
        noVideoBtn.setVisibility(View.VISIBLE);
    }

    private void hideCoverStateView() {
        noVideoBtn.setVisibility(View.GONE);
    }

    public void keepScreenLight() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void stopScreenLight() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private Date updateDateTimeLine(long time) {
        if (dateTimeLine == null) {
            dateTimeLine = new Date();
        }
        dateTimeLine.setTime(time);
        return dateTimeLine;
    }

    /**
     * 初始化视频控制条 及 暂停 按钮
     */
    private void initVideoControl() {
        pauseIv = findView(R.id.iv_pause_resume);
        seekBar = findView(R.id.sb_player);
        curDurationTv = findView(R.id.tv_duration_cur);
        allDurationTv = findView(R.id.tv_duration_all);
        recordBtn = findView(R.id.tbtn_record);
        captureBtn = findView(R.id.tbtn_capture);

        replayVs = findView(R.id.vs_replay);
        newSeekVs = findView(R.id.vs_new_seek);

        pauseIv.setOnClickListener(this);
        recordBtn.setOnClickListener(this);
        captureBtn.setOnClickListener(this);

        if (isVodByTime()) {
            seekBar.setVisibility(View.GONE);
            curDurationTv.setVisibility(View.GONE);
            allDurationTv.setVisibility(View.GONE);
            recordBtn.setVisibility(View.VISIBLE);
        } else {
            seekBar.setVisibility(View.VISIBLE);
            curDurationTv.setVisibility(View.VISIBLE);
            allDurationTv.setVisibility(View.VISIBLE);
            recordBtn.setVisibility(View.GONE);
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (mVodPlayer.getDuration() > 0) {
                    int currentProgress = (int) (progress * mVodPlayer.getDuration() / 100);
                    String timeStr = timeLineFormatter.format(updateDateTimeLine(currentProgress));
                    curDurationTv.setText(timeStr);
                    updateSmallSeek(progress, timeStr);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                showSmallSeek();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                hideSmallSeek();

                int currentProgress = (int) (seekBar.getProgress() * mVodPlayer.getDuration() / 100);
                mVodPlayer.seekTo(currentProgress);
                stopTimeBeat();
                updateTimeline(true);
            }
        });
    }

    /**
     * 初始化视频全屏按钮
     */
    private void initZoomBtn() {
        zoomBtn = findView(R.id.tbtn_zoom);
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
    }

    /**
     * 初始化时间刻度尺
     */
    private void initTimeRulerView() {
        timeRuleView = findView(R.id.time_rule_view);
        selectTimeLabelTv = findView(R.id.tv_select_time_label);

        timeRuleView.setTimeCallBack(new TimeRulerView.TimeCallBack() {
            @Override
            public void onTimeChanged(int newTimeValue) {
                String realTimestampStr = TimeUtil.TimeStamp2Date("" + (beginTimeOfThisDayInMS / 1000 + newTimeValue), "yyyy.MM.dd HH:mm:ss");
                selectTimeLabelTv.setText(realTimestampStr);
            }

            @Override
            public void onTimeSelected(int newTimeValue) {
//                newTimeValue 的值为当天已经过去的秒数
                ALog.w(TAG, "onTimeSelected:" + newTimeValue);
                stopTimeBeat();
                if (!isActivityFinished()) {
                    queryAndPlayVideo(newTimeValue);
                }

            }
        });
    }

    /**
     * 初始化日历
     */
    private void initCalendar() {
        dateTv = findView(R.id.tv_date);
        dateTv.setText(TimeUtil.getFormatDay(new Date()));
        dateTv.setOnClickListener(this);


        calendarFl = findView(R.id.fl_calendar);
        calendarLl = findView(R.id.ll_calendar);
        titleDateTv = findView(R.id.tv_title_date);
        closeIv = findView(R.id.iv_close);
        okBtn = findView(R.id.btn_ok);
        calendarPickerView = findView(R.id.calendar_view);
        Calendar maxTime = Calendar.getInstance();
        maxTime.add(Calendar.DATE, 1);
        Calendar minTime = Calendar.getInstance();
        minTime.add(Calendar.MONTH, -6);
        minTime.set(Calendar.DAY_OF_MONTH, 1);
        calendarPickerView.setCustomDayView(new CustomeDayViewAdapter());
        decorators = new ArrayList<>(1);
        calendarDecorator = new DateDecorator();
        calendarDecorator.setDefaultClickable(true);
        decorators.add(calendarDecorator);
        calendarPickerView.setDecorators(decorators);


        curShownDate = curSelectedDate = new Date();

        calendarInitializer = calendarPickerView.init(minTime.getTime(), maxTime.getTime())
                .inMode(CalendarPickerView.SelectionMode.SINGLE)
                .withSelectedDate(new Date());
        titleDateTv.setText(dateFormatter.format(curSelectedDate));

        calendarPickerView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                curSelectedDate = date;
                titleDateTv.setText(dateFormatter.format(curSelectedDate));
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });

        closeIv.setOnClickListener(this);
        okBtn.setOnClickListener(this);
    }

    /* 日历操作 END*/

    private void resetVideoView(boolean showBuffering) {
        nowPlayFileName = null;
        nowQueryFileName = null;
        prePlayFileName = null;

        if (isReplayViewShown()) {
            hideReplay();
        }

        stopVideo();

        if (showBuffering) {
            showBuffering();
            hideCoverStateView();
        } else {
            dismissBuffering();
            showCoverNotConnect();
        }
    }

    private void showSmallSeek() {
        if (newSeekV == null) {
            newSeekV = newSeekVs.inflate();
            newSeekTimeTv = newSeekV.findViewById(R.id.tv_new_time);
            smallSb = newSeekV.findViewById(R.id.sb_small);
        } else {
            newSeekVs.setVisibility(View.VISIBLE);
        }
    }

    private void updateSmallSeek(int progress, String timeStr) {
        if (smallSb != null) {
            smallSb.setProgress(progress);
        }

        if (newSeekTimeTv != null) {
            newSeekTimeTv.setText(timeStr);
        }

    }

    private void hideSmallSeek() {
        newSeekVs.setVisibility(View.GONE);
    }

    private boolean isReplayViewShown() {
        return replayV != null && View.VISIBLE == replayV.getVisibility();
    }

    private boolean isSmallSeekViewShown() {
        return newSeekV != null && View.VISIBLE == newSeekV.getVisibility();
    }

    private void showReplay() {
        if (replayV == null) {
            replayV = replayVs.inflate();
            replayV.setOnClickListener(this);
        } else {
            replayVs.setVisibility(View.VISIBLE);
        }
    }

    private void hideReplay() {
        replayVs.setVisibility(View.GONE);
    }

    private void updateTimelineOnUIThread() {
        mHandler.sendEmptyMessage(FLAG_SEEKBAR_UPDATE);
    }

    /**
     * 停止定时任务
     */
    private void stopTimeBeat() {
        if (timelineUpdateHandle != null) {
            timelineUpdateHandle.cancel(true);
            timelineUpdateHandle = null;
        }
    }

    /**
     * 心跳，定时处理逻辑，用于更新时间尺和进度条等UI
     *
     * @param forceStart 是否强制启动，false: 心跳过程中断
     */
    public void updateTimeline(boolean forceStart) {

        if (forceStart && timelineUpdateHandle == null) {
            timelineUpdateHandle = scheduledExecutorService.scheduleAtFixedRate(beeper, 100, 250, TimeUnit.MILLISECONDS);
        } else if (!forceStart && timelineUpdateHandle == null) {
            return;
        }
        if (mVodPlayer.getDuration() <= 0) {
            curDurationTv.setText("-:-");
            allDurationTv.setText("-:-");
            seekBar.setProgress(0);
            stopTimeBeat();
        } else {
            int newAddSec = (int) (mVodPlayer.getCurrentPosition() / 1000);

            Log.d(TAG, "updateTimeline: ---///CurrentPosition In Second = " + (mVodPlayer.getCurrentPosition() / 1000) + "//Duration=" + mVodPlayer.getDuration());
            Log.d(TAG, "updateTimeline: /// " + newAddSec + " // " + mVodPlayer.getCurrentPosition() * 100 / mVodPlayer.getDuration());
            int currentPositionTime = newAddSec;
            if (isVodByTime()) {//按时间播放
                if (mPlayerState == PlayerState.STATE_READY) {//处理时间尺刻度跳动问题
                    timeRuleView.setCurrentTime(currentPositionTime);
                    Log.i(TAG, "timeRuleView.setCurrentTime = " + currentPositionTime);
                    Log.i(TAG, "timeRuleView.setRealTime is  = " + secondToFormatString(currentPositionTime));
                    String realTimestampStr = TimeUtil.TimeStamp2Date("" + (beginTimeOfThisDayInMS / 1000 + currentPositionTime), "yyyy.MM.dd HH:mm:ss");
                    selectTimeLabelTv.setText(realTimestampStr);
                }

            } else if (mPlayerState == PlayerState.STATE_READY || mPlayerState == PlayerState.STATE_ENDED) {//处理时间尺刻度跳动问题
                newAddSec = (int) (curTimeSpanSpan * (mVodPlayer.getCurrentPosition() * 1.0 / mVodPlayer.getDuration()));
                currentPositionTime = curTimeSpanTodayStart + (
                        newAddSec > curTimeSpanSpan ? curTimeSpanSpan : newAddSec);
                timeRuleView.setCurrentTime(currentPositionTime);
                Log.i(TAG, "timeRuleView.setCurrentTime = " + currentPositionTime);
                String realTimestampStr = TimeUtil.TimeStamp2Date("" + (beginTimeOfThisDayInMS / 1000 + currentPositionTime), "yyyy.MM.dd HH:mm:ss");
                selectTimeLabelTv.setText(realTimestampStr);
            }

            if (isSmallSeekViewShown()) {
                return;
            }

            //处理进度条跳动问题
            if (mPlayerState == PlayerState.STATE_READY || mPlayerState == PlayerState.STATE_ENDED) {
                if (newAddSec == 0) {
                    seekBar.setProgress(0);
                } else {
                    seekBar.setProgress((int) (mVodPlayer.getCurrentPosition() * 100 / mVodPlayer.getDuration()));
                }
                curDurationTv.setText(timeLineFormatter.format(
                        updateDateTimeLine(mVodPlayer.getCurrentPosition())
                ));
            }
            if (mVodPlayer.getCurrentPosition() >= mVodPlayer.getDuration()) {
                stopVideo();
                pauseIv.setSelected(false);
                pauseIv.setEnabled(false);
                seekBar.setEnabled(false);
                if (!isVodByTime()) {
                    showReplay();
                }
                stopTimeBeat();
            } else if (isReplayViewShown()) {
                hideReplay();
            }
        }
    }

    private String secondToFormatString(int seconds) {
        int second = seconds % 60;
        int minute = 0;
        int hour = 0;
        seconds -= second;
        if (seconds > 0) {
            seconds /= 60;
            minute = seconds % 60;
            seconds -= minute;
            if (seconds > 0) {
                hour = seconds / 60;
            }
        }
        return hour + ":" + minute + ":" + second;
    }

    private boolean isVodByTime() {
        return currentPlayOrderMode == EnumPlayOrderMode.PLAY_ORDER_MODE_BY_TIME;
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
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

    private boolean isSelectTimeValidForQueryVodURL(int secondsFromToday) {
        int beginTime, endTime;
        VideoInfo videoInfo;
        for (int i = 0, len = videoList.size(); i < len; i++) {
            videoInfo = videoList.get(i);
            beginTime = (int) (Long.parseLong(videoInfo.beginTime) - videoInfo.dayTime);
            endTime = (int) (Long.parseLong(videoInfo.endTime) - videoInfo.dayTime);
//            如果在文件时间段之内，或者比这个开始时间小5分钟以内，都认为是有效的时间点
            if (beginTime <= secondsFromToday && endTime > secondsFromToday || beginTime > secondsFromToday) {
                return true;
            }
        }
        return false;
    }

    //    查找该时间点是否所归属的文件
    private VideoInfo getFileName(int secondsFromToday) {
        int beginTime, endTime;
        VideoInfo videoInfo;
        for (int i = 0, len = videoList.size(); i < len; i++) {
            videoInfo = videoList.get(i);
            beginTime = (int) (Long.parseLong(videoInfo.beginTime) - videoInfo.dayTime);
            endTime = (int) (Long.parseLong(videoInfo.endTime) - videoInfo.dayTime);
            if (beginTime <= secondsFromToday && endTime > secondsFromToday) {
                return videoInfo;
            }
        }
        return null;
    }

    /**
     * 根据时间查询需要播放的录像信息并播放
     *
     * @param secondsFromToday 从今天开始的时间
     */
    private void queryAndPlayVideo(int secondsFromToday) {

        if (isReplayViewShown()) {
            hideReplay();
        }
        long realTimestamp = beginTimeOfThisDayInMS + secondsFromToday * 1000;
        Log.d(TAG, "queryAndPlayVideo select Time info : secondsFromToday = " + secondsFromToday + "， realTimestamp = " + realTimestamp + ", realTimeStr= " + TimeUtil.TimeStamp2Date("" + (realTimestamp / 1000), "yyyy.MM.dd HH:mm:ss"));
        Log.d(TAG, "curTimeSpanStart = " + curTimeSpanStart + "， curTimeSpanTodayStart = " + curTimeSpanTodayStart + ", curTimeSpanSpan= " + curTimeSpanSpan + ",dayTimeMS = " + beginTimeOfThisDayInMS);
        if (isVodByTime()) {
//                正在播放，则Seek
            if (mPlayerState == PlayerState.STATE_READY || mPlayerState == PlayerState.STATE_BUFFERING) {
//                    SeekTo的目标时间是相对于本次点播的时间点的相对偏移毫秒数；
                int millisecondFromThisVodStart = secondsFromToday * 1000;
                mVodPlayer.seekTo(millisecondFromThisVodStart);
                Log.d(TAG, "Seek To..." + millisecondFromThisVodStart + "," + secondToFormatString(millisecondFromThisVodStart / 1000));
            } else {
                if (!isSelectTimeValidForQueryVodURL(secondsFromToday)) {
                    Log.d(TAG, "SelectTime inValid " + secondsFromToday);
                    return;
                }
                mVodPlayer.stop();
//                    第一次播，建立通道
                queryAndPlayVideoByTime((int) (beginTimeOfThisDayInMS / 1000), (int)
                        (beginTimeOfThisDayInMS / 1000) + 60 * 60 * 24 - 1, secondsFromToday * 1000);
                Log.d(TAG, "queryAndPlayVideoByTime begin");
            }

        } else {
            queryAndPlayVideoByFileName(secondsFromToday);
        }

    }

    /**
     * 查找录像，并按文件方式播放
     *
     * @param secondsFromToday
     */
    private void queryAndPlayVideoByFileName(int secondsFromToday) {
        VideoInfo videoInfo = getFileName(secondsFromToday);
        if (videoInfo != null) {
            hideCoverStateView();
            curTimeSpanStart = Long.parseLong(videoInfo.beginTime) * 1000;
            curTimeSpanTodayStart = (int) ((curTimeSpanStart / 1000) - videoInfo.dayTime);
            curTimeSpanSpan = (int) (Long.parseLong(videoInfo.endTime) - Long.parseLong(videoInfo.beginTime));

            if (TextUtils.equals(nowPlayFileName, videoInfo.fileName)) {
                mVodPlayer.seekTo(mVodPlayer.getDuration()
                        * (secondsFromToday - Long.parseLong(videoInfo.beginTime) + videoInfo.dayTime)
                        / (Long.parseLong(videoInfo.endTime) - Long.parseLong(videoInfo.beginTime)));
            } else if (TextUtils.equals(nowQueryFileName, videoInfo.fileName)) {
                seekToVal = secondsFromToday == 0 ? 0 : ((secondsFromToday - Long.parseLong(videoInfo.beginTime) + videoInfo.dayTime) * 1.0
                        / (Long.parseLong(videoInfo.endTime) - Long.parseLong(videoInfo.beginTime)));
            } else {
                seekToVal = secondsFromToday == 0 ? 0 : ((secondsFromToday - Long.parseLong(videoInfo.beginTime) + videoInfo.dayTime) * 1.0
                        / (Long.parseLong(videoInfo.endTime) - Long.parseLong(videoInfo.beginTime)));
                stopVideo();
                showBuffering();
                setFilenameToPlayer(videoInfo.fileName);
            }
        } else {//未找到录像处理
            nowPlayFileName = null;
            nowQueryFileName = null;
            prePlayFileName = null;
            stopVideo();
            if (videoList.size() > 0) {
                showCoverSwipToPlay();
            } else {
                showCoverNoneVideo();
            }
        }
    }

    /**
     * 更新时间尺片段数据
     */
    private void updateTimeRulerView() {
        if (isActivityFinished()) {
            return;
        }
        timeRuleView.setTimeParts(timeParts);
    }

    /**
     * 子线程处理数据并通知刷新时间尺
     */
    protected void childUpdateTimeRulerView() {

        if (videoList != null && videoList.size() > 0) {
            List<VideoInfo> cardVideos = new ArrayList<>(videoList);
            Collections.sort(cardVideos, new Comparator<VideoInfo>() {
                @Override
                public int compare(VideoInfo o1, VideoInfo o2) {
                    return Long.compare(Long.parseLong(o1.beginTime), Long.parseLong(o2.beginTime));
                }
            });
            TimeRulerView.TimePart timePart;
            timeParts.clear();
            for (VideoInfo info : cardVideos) {
                timePart = new TimeRulerView.TimePart();
                timePart.startTime = (int) (Long.parseLong(info.beginTime) - info.dayTime);
                timePart.endTime = (int) (Long.parseLong(info.endTime) - info.dayTime);
                if (calendarPickerView.getSelectedDate().getTime() / 1000 == info.dayTime) {

                    boolean isValid = timePart.startTime <= 24 * 60 * 60 && timePart.endTime >= 0;
                    if (isValid) {
                        if (timePart.startTime < 0) {
                            timePart.startTime = 0;
                        }
                        if (timePart.endTime > 24 * 60 * 60) {
                            timePart.endTime = 24 * 60 * 60;
                        }
                        timeParts.add(timePart);
                    }
                }
            }
            mHandler.removeMessages(FLAG_UI_TIMERULER_UPDATE);
            mHandler.sendEmptyMessageDelayed(FLAG_UI_TIMERULER_UPDATE, 100);
        }
    }

    /**
     * 暂停播放
     */
    private void pauseVideo() {
        if (mVodPlayer != null) {
            if (mVodPlayer.getCurrentPosition() > 0) {
                mVodPlayer.pause();
            }
        }

    }

    /**
     * 暂停并停止心跳
     */
    private void pauseAndStopBeat() {
        pauseVideo();
        stopTimeBeat();
    }

    /**
     * 恢复播放
     */
    private void resumeVideo() {
        if (mVodPlayer != null) {
            if (mVodPlayer.getCurrentPosition() > 0) {
                mVodPlayer.start();
            }
        }

    }

    /**
     * 停止播放。若需要继续播放，则需要重走整个播放流程
     */
    protected void stopVideo() {
        if (mVodPlayer != null) {
            mVodPlayer.stop();
            nowPlayFileName = "";
        }

    }

    protected void showToast(String text) {
        if (isActivityFinished()) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(NewCardVideoActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });

    }

    protected void showToast(int textRes) {
        if (isActivityFinished()) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(NewCardVideoActivity.this, textRes, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showBuffering() {
        if (isActivityFinished()) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mVideoBufferingProgressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void dismissBuffering() {
        if (isActivityFinished()) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mVideoBufferingProgressBar.setVisibility(View.GONE);
            }
        });

    }

    /**
     * 判断fragment依附的ativity是否已关闭
     *
     * @return
     */
    private boolean isActivityFinished() {
        if (isFinishing() || isDestroyed()) {
            return true;
        }
        return false;
    }

    /**
     * 添加视频信息，并排序
     *
     * @param videoInfos
     */
    protected void addVideoInfos(List<VideoInfo> videoInfos) {
        videoList.addAll(videoInfos);
        Collections.sort(videoList, new Comparator<VideoInfo>() {
            @Override
            public int compare(VideoInfo o1, VideoInfo o2) {
                return Long.compare(Long.parseLong(o1.beginTime), Long.parseLong(o2.beginTime));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (calendarFl.getVisibility() == View.VISIBLE) {
                calendarFl.setVisibility(View.GONE);
            } else {
                stopVideo();
                super.onBackPressed();
            }
        } else {
            zoomBtn.setChecked(false);
        }
    }

    private static class PlayHandler extends Handler {


        WeakReference<NewCardVideoActivity> weakReference;

        public PlayHandler(NewCardVideoActivity fragment) {
            weakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            NewCardVideoActivity fragment = weakReference.get();
            if (fragment == null) {
                return;
            }
            switch (msg.what) {
                case FLAG_UI_TIMERULER_UPDATE:
                    fragment.updateTimeRulerView();
                    break;
                case FLAG_SEEKBAR_UPDATE:
                    fragment.updateTimeline(false);
                    break;
            }
        }
    }

    private void snapshot() {
        Bitmap bitmap = mVodPlayer.snapShot();
        if (bitmap == null) {
            showToast("无法截图，当前无画面");
            return;
        }
        SnapshotPreviewDialog snapshotPreviewDialog = new SnapshotPreviewDialog(this);
        snapshotPreviewDialog.show();
        snapshotPreviewDialog.setImageBitmap(bitmap);
    }


    private boolean isRecordingMp4 = false;
    private File file = null;

    private void startOrStopRecordingMp4() {
        if (!isRecordingMp4) {
            File appDir = new File(Environment.getExternalStorageDirectory(), "linkvision");
            if (!appDir.exists() || !appDir.isDirectory()) {
                appDir.mkdirs();
            }
            file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES),
                    System.currentTimeMillis() + ".mp4");
            try {
                if (mVodPlayer.startRecordingContent(file)) {
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
            stopRecordMp4(true);
        }
    }

    private void stopRecordMp4(boolean showInfo) {
        if (mVodPlayer.stopRecordingContent()) {
            File fileFinal = file;
            AlertDialog.Builder normalDialog =
                    new AlertDialog.Builder(NewCardVideoActivity.this);
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
                                Uri contentUri = FileProvider.getUriForFile(NewCardVideoActivity.this,
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
        } else if (showInfo) {
            showToast(R.string.ipc_main_record_save_fail);
        }
        file = null;
        recordBtn.clearAnimation();
        isRecordingMp4 = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUESTCODE_WRITE_EXTERNAL) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRationale()) {
                    jumpToPermissionActivity();
                }
            }
        }
    }

    private boolean hasWritePermission() {
        //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean shouldShowRationale() {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void requestWritePermission() {
        //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, REQUESTCODE_WRITE_EXTERNAL);
    }

    private void jumpToPermissionActivity() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.ipc_video_permission_info_about_write_sd)
                .setPositiveButton(R.string.ipc_video_permission_set, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .show();
    }


    private final int CARD_PAGE_SIZE = 10;
    private final int CARD_STREAM_TYPE = 0;

    protected void queryVideoForSomeday(long dateTime) {
        getCardVideoList(dateTime, dateTime, dateTime + 24 * 60 * 60 - 1, CARD_PAGE_SIZE, CARD_STREAM_TYPE);
    }

    protected void setFilenameToPlayer(String fileName) {
        stopVideo();
        nowQueryFileName = fileName;
        nowPlayFileName = null;
        prePlayFileName = null;
        mVodPlayer.setDataSourceByIPCRecordFileName(iotId, fileName, true, 0);
        mVodPlayer.prepare();
    }

    protected void queryAndPlayVideoByTime(int beginTimeOfOneDayInS, int endTimeOfOneDayInS, long seekToPositionInMs) {
        mVodPlayer.setDataSourceByIPCRecordTime(iotId, beginTimeOfOneDayInS, endTimeOfOneDayInS, true, 0, seekToPositionInMs);
        mVodPlayer.prepare();
    }

    /**
     * 获取录像列表
     *
     * @param dayTime   查询的日期（X年X月X日的秒数，单位秒）
     * @param startTime 查询开始时间（单位秒）
     * @param endTime   查询结束时间（单位秒）
     * @param size      请求卡录像个数
     * @param type      录像类型 0：所有类型 1：主动录像  2：报警录像 3: 计划录像
     */
    private void getCardVideoList(final long dayTime, final long startTime, final long endTime, final int size,
                                  final int type) {
        if (calendarPickerView.getSelectedDate().getTime() / 1000 == dayTime) {
            Log.d(TAG, "getCardVideoList          startTime:" + startTime + "          endTime:" + endTime);
            getCardVideoListInter(dayTime, startTime, endTime, size, type, true);
        }
    }

    private void getCardVideoListInter(long dayTime, long startTime, long endTime, int size, int type,
                                       boolean oneMoreTime) {

        if (currentPlayOrderMode == EnumPlayOrderMode.PLAY_ORDER_MODE_BY_TIME) {
            IPCManager.getInstance().getDevice(iotId).queryCardTimeList(startTime,
                    endTime, size, type, new IPanelCallback() {
                        @Override
                        public void onComplete(boolean b, Object response) {
                            processCardVideoListResponse(b, response, dayTime, startTime, endTime, size, type, oneMoreTime);
                        }
                    });
        } else {
            IPCManager.getInstance().getDevice(iotId).queryCardRecordList(startTime,
                    endTime, size, type, new IPanelCallback() {
                        @Override
                        public void onComplete(boolean b, Object response) {
                            processCardVideoListResponse(b, response, dayTime, startTime, endTime, size, type, oneMoreTime);
                        }
                    });
        }
    }

    private void processCardVideoListResponse(boolean isSucceed, Object response, long dayTime, long startTime, long endTime,
                                              int size, int type,
                                              boolean oneMoreTime) {
        Log.d(TAG,
                "getCardVideoList          startTime:" + startTime + "          endTime:" + endTime + "    "
                        + isSucceed + "  " + "     response:" + (response != null ? String
                        .valueOf(response) : "null"));
        if (isActivityFinished()) {
            return;
        }
        if (calendarPickerView.getSelectedDate().getTime() / 1000 != dayTime) {
            return;
        }
        if (isSucceed) {
            if (response != null && !StringUtil.isNullOrEmpty(String.valueOf(response))) {
                String data = response.toString();
                if (data == null || "".equals(data)) {
                    if (oneMoreTime) {
                        getCardVideoListInter(dayTime, startTime, endTime, size, type, false);
                    } else {
                        showCoverDataError();
                    }
                    return;
                }

                try {
                    com.alibaba.fastjson.JSONObject obj = JSON.parseObject(data);
                    if (!obj.containsKey("code")) {
                        if (oneMoreTime) {
                            getCardVideoListInter(dayTime, startTime, endTime, size, type, false);
                        } else {
                            showCoverDataError();
                        }
                        return;
                    } else {
                        int code = obj.getInteger("code");
                        if (code != 200) {
                            if (oneMoreTime) {
                                getCardVideoListInter(dayTime, startTime, endTime, size, type, false);
                            } else {
                                showCoverNotConnect();
                            }
                            return;
                        }
                    }
                    if (!obj.containsKey("data")) {
                        if (oneMoreTime) {
                            getCardVideoListInter(dayTime, startTime, endTime, size, type, false);
                        } else {
                            showCoverDataError();
                        }
                        return;
                    }

                    obj = obj.getJSONObject("data");
                    if (obj == null) {
                        showCoverDataError();
                        return;
                    }
                    com.alibaba.fastjson.JSONArray jsonArray = obj.getJSONArray("RecordList");
                    if (jsonArray == null) {
                        jsonArray = obj.getJSONArray("TimeList");
                    }
                    if (jsonArray != null) {
                        List<VideoInfo> tmpLst = new LinkedList<>();
                        int returnDataSize = jsonArray.size();
                        for (int i = 0; i < returnDataSize; i++) {
                            com.alibaba.fastjson.JSONObject videoJsonObject = jsonArray.getJSONObject(
                                    i);

                            VideoInfo videoInfo = new VideoInfo();
                            videoInfo.iotId = iotId;
                            if (videoJsonObject.containsKey("FileName")) {
                                videoInfo.fileName = videoJsonObject.getString("FileName");
                            }
                            if (videoJsonObject.containsKey("Size")) {
                                videoInfo.fileSize = videoJsonObject.getInteger("Size");
                            }
                            videoInfo.recordType = videoJsonObject.getInteger("Type");
                            videoInfo.beginTime = videoJsonObject.getString("BeginTime");
                            videoInfo.endTime = videoJsonObject.getString("EndTime");
                            videoInfo.dayTime = dayTime;
                            if (calendarPickerView.getSelectedDate().getTime() / 1000 != dayTime) {
                                return;
                            }

                            //过滤非指定时间内数据（保留跨天数据）
                            long vBeginTime = Long.parseLong(videoInfo.beginTime);
                            long vEndTime = Long.parseLong(videoInfo.endTime);
                            if (vBeginTime < endTime && vEndTime > startTime) {
                                tmpLst.add(videoInfo);
                            }

                        }
                        if (calendarPickerView.getSelectedDate().getTime() / 1000 != dayTime) {
                            return;
                        }
                        if (tmpLst.size() > 0) {
                            addVideoInfos(tmpLst);
                            childUpdateTimeRulerView();
                        }

                        if (videoList.size() == 0 && returnDataSize < size) {//加载完毕，且无录像
                            showCoverNoneVideo();
                            return;
                        } else if (returnDataSize > 0 && returnDataSize == size) {//继续加载
                            if (tmpLst.size() == 0) {
                                showCoverSwipToPlay();
                                return;
                            }
                            boolean isOrder =
                                    jsonArray.getJSONObject(0).getLong("BeginTime") <
                                            jsonArray.getJSONObject(1).getLong("BeginTime");
                            long time;
                            if (isOrder) {
                                time = Long.parseLong(
                                        tmpLst.get(tmpLst.size() - 1).endTime);
                                if (time >= endTime) {
                                    return;
                                } else {
                                    getCardVideoList(dayTime, time, endTime, size, type);
                                }
                            } else {
                                time = Long.parseLong(
                                        tmpLst.get(0).beginTime);
                                if (time <= startTime) {
                                    return;
                                } else {
                                    getCardVideoList(dayTime, startTime, time, size, type);
                                }
                            }
                        } else {//加载完毕，且有录像
                            showCoverSwipToPlay();
                        }

                    } else {
                        showCoverDataError();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "processCardVideoListResponse json process error " + e.toString(), e);
                    showCoverDataError();
                }
            } else {
                if (oneMoreTime) {
                    getCardVideoListInter(dayTime, startTime, endTime, size, type, false);
                } else {
                    showCoverDataError();
                }
            }
        } else {
            showCoverNotConnect();
        }
    }

}
