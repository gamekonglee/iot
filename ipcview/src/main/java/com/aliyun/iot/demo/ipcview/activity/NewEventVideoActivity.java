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
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
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
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
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
import com.aliyun.iotx.linkvisual.media.video.player.ExoHlsPlayer;
import com.aliyun.iotx.linkvisual.media.video.views.ZoomableTextureView;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.savvi.rangedatepicker.CalendarCellDecorator;
import com.savvi.rangedatepicker.CalendarPickerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
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

public class NewEventVideoActivity extends Activity implements View.OnClickListener {
    private static final int VIDEO_PAGE_SIZE = 500;
    private final int REQUESTCODE_WRITE_EXTERNAL = 0x1112;
    public static final String TAG = "NewEventVideoActivity";

    /**
     * Handler标签，用于切换到主线程，通知时间尺刷新
     */
    public static final int FLAG_UI_TIMERULER_UPDATE = 203;
    /**
     * Handler标签，用于切换到主线程，定时任务更新时间尺当前刻度
     */
    public static final int FLAG_SEEKBAR_UPDATE = 204;

    /**
     * 录像数据段最大查询页数
     */
    private final int MAX_PAGE_START = 3;

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
    private PlayerView mPlayerView;
    /**
     * 视频播放器
     */
    protected ExoHlsPlayer mExoHlsPlayer;
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

    /**
     * 记录指定月份是否调用过{@link #queryMonthVideo(String)}方法，避免重复调用
     */
    SparseBooleanArray monthsRequired;
    /**
     * 记录指定月份数据，配合{@link #queryMonthVideo(String)}方法使用
     */
    SparseArray<String> mapIndexToMonthStr;
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
        setContentView(R.layout.activity_new_event_video);
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
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
            hideOtherView();//横屏
        } else {
            showOtherView();//竖屏
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseAndStopBeat();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mExoHlsPlayer.getPlayState() == PlayerState.STATE_READY) {
            pauseIv.setSelected(true);
            updateTimeline(true);
            resumeVideo();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
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
                mExoHlsPlayer.pause();
                seekBar.setEnabled(false);
            } else {
                mExoHlsPlayer.start();
                seekBar.setEnabled(true);
            }
            pauseIv.setSelected(!pauseIv.isSelected());
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
        } else if (id == R.id.tbtn_capture) {
            if (!hasWritePermission()) {
                requestWritePermission();
                return;
            }
            if (mExoHlsPlayer.getPlayState() == PlayerState.STATE_READY) {
                snapshot();
            } else {
                showToast(R.string.ipc_video_snapshot_fail);
            }

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
        resetZoomOut();
    }

    int mPlayerState = PlayerState.STATE_IDLE;

    /**
     * 初始化播放器 ExoHlsPlayer
     */
    protected void initPlayer() {
        mPlayerView = findViewById(R.id.hls_player_view);
        replaceZoomTextureView();
        mExoHlsPlayer = new ExoHlsPlayer(getApplicationContext());
        mPlayerView.setPlayer(mExoHlsPlayer.getExoPlayer());
        mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
        mExoHlsPlayer.setOnErrorListener(new OnErrorListener() {
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
        mExoHlsPlayer.setOnPlayerStateChangedListener(new OnPlayerStateChangedListener() {
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
                        onPlayerReady();
                        updateTimeline(true);
                        pauseIv.setEnabled(true);
                        seekBar.setEnabled(true);
                        pauseIv.setSelected(true);
                        allDurationTv.setText(timeLineFormatter.format(updateDateTimeLine(mExoHlsPlayer.getDuration())));
                        if (Math.abs(seekToVal) > 0.0001) {
                            mExoHlsPlayer.seekTo((long) (mExoHlsPlayer.getDuration() * seekToVal));
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

                        break;
                    default:
                        break;
                }
                mPlayerState = playerState;
            }
        });
        mExoHlsPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                mExoHlsPlayer.start();
            }
        });
        mExoHlsPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion() {
                Log.d(TAG, "onCompletion");
                dismissBuffering();
                hideCoverStateView();
            }
        });
    }

    /**
     * 将PlayerView内的播放控件替换为ZoomableTextureView，用以进行双指缩放操作
     */
    private void replaceZoomTextureView() {
        FrameLayout textureContainView = (FrameLayout) mPlayerView.findViewById(R.id.exo_content_frame);

        try {
            ZoomableTextureView zoomableTextureView = new ZoomableTextureView(getApplicationContext());
            zoomableTextureView.setMaxScale(4);//设置最大的缩放比例，可不设置，默认为4
            Field field = mPlayerView.getClass().getDeclaredField("surfaceView");
            field.setAccessible(true);
            field.set(mPlayerView, zoomableTextureView);
            field.setAccessible(false);

            textureContainView.removeAllViews();
            ViewGroup.LayoutParams params =
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            zoomableTextureView.setLayoutParams(params);
            textureContainView.addView(zoomableTextureView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重置缩放大小
     */
    private void resetZoomOut() {
        if (mPlayerView == null) {
            return;
        }
        View videoSurfaceView = mPlayerView.getVideoSurfaceView();
        if (videoSurfaceView instanceof ZoomableTextureView) {
            ((ZoomableTextureView) videoSurfaceView).zoomOut(false);
        }
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
        captureBtn = findView(R.id.tbtn_capture);

        replayVs = findView(R.id.vs_replay);
        newSeekVs = findView(R.id.vs_new_seek);

        captureBtn.setOnClickListener(this);
        pauseIv.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (mExoHlsPlayer.getDuration() > 0) {
                    int currentProgress = (int) (progress * mExoHlsPlayer.getDuration() / 100);
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

                int currentProgress = (int) (seekBar.getProgress() * mExoHlsPlayer.getDuration() / 100);
                mExoHlsPlayer.seekTo(currentProgress);
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

    private void stopTimeBeat() {
        if (timelineUpdateHandle != null) {
            timelineUpdateHandle.cancel(true);
            timelineUpdateHandle = null;
        }
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
        minTime.add(Calendar.MONTH, -1);
        minTime.set(Calendar.DAY_OF_MONTH, 1);
        calendarPickerView.setCustomDayView(new CustomeDayViewAdapter());
        decorators = new ArrayList<>(1);
        calendarDecorator = new DateDecorator();
        calendarDecorator.setDefaultClickable(false);
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

        calendarPickerView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount <= 0 || monthsRequired.get(firstVisibleItem)) {
                    return;
                }

                monthsRequired.put(firstVisibleItem, true);
                String yearMonth = mapIndexToMonthStr.get(firstVisibleItem);
                queryMonthVideo(yearMonth);
            }
        });

        initMonths(minTime, maxTime);
    }

    /**
     * 初始化日历日期是否可点击状态数据，结合{@link #queryMonthVideo(String)}方法填充数据
     *
     * @param min 最小日期
     * @param max 最大日期
     */
    private void initMonths(Calendar min, Calendar max) {
        setMidnight(min);
        setMidnight(max);
        int maxYear = max.get(Calendar.YEAR), maxMonth = max.get(Calendar.MONTH) + 1;
        int minYear = min.get(Calendar.YEAR), minMonth = min.get(Calendar.MONTH) + 1;
        int monthSpan = maxMonth - minMonth + (maxYear - minYear) * 12 + 1;
        monthsRequired = new SparseBooleanArray(monthSpan);
        mapIndexToMonthStr = new SparseArray<>(monthSpan);

        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < monthSpan; i++) {
            buffer.append(minYear);
            if (minMonth < 10) {
                buffer.append("0");
            }
            buffer.append(minMonth);
            mapIndexToMonthStr.put(i, buffer.toString());
            buffer.delete(0, buffer.length());

            min.add(Calendar.MONTH, 1);
            minYear = min.get(Calendar.YEAR);
            minMonth = min.get(Calendar.MONTH) + 1;
        }

    }

    /**
     * 设置为00：00时间
     *
     * @param cal
     */
    void setMidnight(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    /**
     * 保存指定日期是否可选择数据
     *
     * @param yearMonth 需要保存数据所属的月份，格式yyyyMM
     * @param days      指定月份数据
     */
    public void addSimpleMonthInfo(String yearMonth, char[] days) {
        calendarDecorator.add(yearMonth, days);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                calendarPickerView.setDecorators(decorators);
            }
        });
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
        if (mExoHlsPlayer.getDuration() <= 0) {
            curDurationTv.setText("-:-");
            allDurationTv.setText("-:-");
            seekBar.setProgress(0);
            stopTimeBeat();
        } else {
            int newAddSec = (int) (mExoHlsPlayer.getCurrentPosition() / 1000);

            Log.d(TAG, "updateTimeline: ---///CurrentPosition In Second = " + (mExoHlsPlayer.getCurrentPosition() / 1000) + "//Duration=" + mExoHlsPlayer.getDuration());
            Log.d(TAG, "updateTimeline: /// " + newAddSec + " // " + mExoHlsPlayer.getCurrentPosition() * 100 / mExoHlsPlayer.getDuration());
            int currentPositionTime = newAddSec;

            if (mPlayerState == PlayerState.STATE_READY || mPlayerState == PlayerState.STATE_ENDED) {
                newAddSec = (int) (curTimeSpanSpan * (mExoHlsPlayer.getCurrentPosition() * 1.0 / mExoHlsPlayer.getDuration()));
                currentPositionTime = curTimeSpanTodayStart + (
                        newAddSec > curTimeSpanSpan ? curTimeSpanSpan : newAddSec);
                Log.w(TAG, "setCurrentTime : " + currentPositionTime);
                timeRuleView.setCurrentTime(currentPositionTime);
                String realTimestampStr = TimeUtil.TimeStamp2Date("" + (beginTimeOfThisDayInMS / 1000 + currentPositionTime), "yyyy.MM.dd HH:mm:ss");
                selectTimeLabelTv.setText(realTimestampStr);
            }

            if (isSmallSeekViewShown()) {
                return;
            }

            if (mPlayerState == PlayerState.STATE_READY || mPlayerState == PlayerState.STATE_ENDED) {
                if (newAddSec == 0) {
                    seekBar.setProgress(0);
                } else {
                    seekBar.setProgress((int) (mExoHlsPlayer.getCurrentPosition() * 100 / mExoHlsPlayer.getDuration()));
                }
                curDurationTv.setText(timeLineFormatter.format(
                        updateDateTimeLine(mExoHlsPlayer.getCurrentPosition())
                ));
            }
            if (mExoHlsPlayer.getCurrentPosition() >= mExoHlsPlayer.getDuration()) {
                stopVideo();
                pauseIv.setSelected(false);
                pauseIv.setEnabled(false);
                seekBar.setEnabled(false);
                showReplay();
                stopTimeBeat();
            } else if (isReplayViewShown()) {
                hideReplay();
            }
        }
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

    /**
     * 查找该时间点是否所归属的文件
     */
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

        VideoInfo videoInfo = getFileName(secondsFromToday);
        if (videoInfo != null) {
            hideCoverStateView();
            curTimeSpanStart = Long.parseLong(videoInfo.beginTime) * 1000;
            curTimeSpanTodayStart = (int) ((curTimeSpanStart / 1000) - videoInfo.dayTime);
            curTimeSpanSpan = (int) (Long.parseLong(videoInfo.endTime) - Long.parseLong(videoInfo.beginTime));

            if (TextUtils.equals(nowPlayFileName, videoInfo.fileName)) {
                mExoHlsPlayer.seekTo((secondsFromToday == 0 ? 0 :
                        (mExoHlsPlayer.getDuration()
                                * (secondsFromToday - Long.parseLong(videoInfo.beginTime) + videoInfo.dayTime)
                                / (Long.parseLong(videoInfo.endTime) - Long.parseLong(videoInfo.beginTime)))));
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
     * 子线程通知刷新时间尺
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
        if (mExoHlsPlayer != null) {
            if (mExoHlsPlayer.getCurrentPosition() > 0) {
                mExoHlsPlayer.pause();
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
        if (mExoHlsPlayer != null) {
            if (mExoHlsPlayer.getCurrentPosition() > 0) {
                mExoHlsPlayer.start();
            }
        }

    }

    /**
     * 停止播放。若需要继续播放，则需要重走整个播放流程
     */
    protected void stopVideo() {
        if (mExoHlsPlayer != null) {
            mExoHlsPlayer.stop();
            nowPlayFileName = "";
        }

    }

    protected void showToast(String text) {
        if (isActivityFinished()) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(NewEventVideoActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });

    }

    protected void showToast(int textRes) {
        if (isActivityFinished()) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(NewEventVideoActivity.this, textRes, Toast.LENGTH_SHORT).show();
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
     * 判断fragment依附的activity是否已关闭
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

    protected void onPlayerReady() {

    }

    private static class PlayHandler extends Handler {


        WeakReference<NewEventVideoActivity> weakReference;

        public PlayHandler(NewEventVideoActivity fragment) {
            weakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            NewEventVideoActivity fragment = weakReference.get();
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
        Bitmap bitmap = ((TextureView) mPlayerView.getVideoSurfaceView()).getBitmap();
        if (bitmap == null) {
            showToast(R.string.ipc_video_snapshot_fail);
            return;
        }
        SnapshotPreviewDialog snapshotPreviewDialog = new SnapshotPreviewDialog(this);
        snapshotPreviewDialog.show();
        snapshotPreviewDialog.setImageBitmap(bitmap);
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


    protected void queryVideoForSomeday(long dateTime) {
        getEventVideoList(dateTime, (int) dateTime, (int) (dateTime + 24 * 3600 - 1), 0, VIDEO_PAGE_SIZE,
                0);
    }

    protected void setFilenameToPlayer(String fileName) {
        stopVideo();
        nowQueryFileName = fileName;
        nowPlayFileName = null;
        prePlayFileName = null;
        mExoHlsPlayer.setDataSourceByIPCRecordFileName(iotId, fileName);
        mExoHlsPlayer.prepare();
    }

    /**
     * 查询月录像数据，判断指定月份每天是否有录像
     * <br>格式"000000000000000000000000000000"，每一位代表一天.0：无数据;1：有数据
     *
     * @param yearMonth 查询月录像的年月信息
     */
    protected void queryMonthVideo(String yearMonth) {
        IPCManager.getInstance().getDevice(iotId).queryMonthVideos(yearMonth,
                new IoTCallback() {
                    @Override
                    public void onFailure(IoTRequest ioTRequest, Exception e) {

                    }

                    @Override
                    public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                        if (ioTResponse.getCode() == 200) {
                            if (ioTResponse.getData() != null && !StringUtil.isNullOrEmpty(
                                    ioTResponse.getData().toString())) {
                                String recordFlags;
                                Object data = ioTResponse.getData();
                                if (data instanceof org.json.JSONObject) {
                                    recordFlags = ((org.json.JSONObject) data).optString("recordFlags");
                                } else {
                                    recordFlags = JSON.parseObject(String.valueOf(data)).getString("recordFlags");
                                }

                                if (!StringUtil.isNullOrEmpty(recordFlags) && recordFlags.matches("[0-1]+")) {
                                    addSimpleMonthInfo(yearMonth, recordFlags.toCharArray());
                                }

                            }
                        }
                    }
                });
    }


    /**
     * 查询录像列表.
     *
     * @param streamType 码流类型(主码流0，辅码流1)
     * @param dayTime    查询的日期（X年X月X日的秒数，单位秒）
     * @param startTime  开始时间 单位s 时间戳
     * @param endTime    结束时间 单位s 时间戳
     * @param pageStart  每页开始位置
     * @param pageSize   每页个数
     */
    private void getEventVideoList(long dayTime, int startTime, int endTime, int pageStart, int pageSize,
                                   int streamType) {
        if (calendarPickerView.getSelectedDate().getTime() / 1000 != dayTime) {
            return;
        }
        getEventVideoListInter(dayTime, startTime, endTime, pageStart, pageSize, streamType, true);
    }

    private void getEventVideoListInter(long dayTime, int startTime, int endTime, int pageStart, int pageSize,
                                        int streamType, boolean oneMoreTime) {

        IPCManager.getInstance().getDevice(iotId).queryVideoLst(streamType, startTime, endTime,
                1, pageStart, pageSize, new IoTCallback() {
                    @Override
                    public void onFailure(IoTRequest ioTRequest, Exception e) {
                        Log.d(TAG, "onFailure");
                    }

                    @Override
                    public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                        processGetEventAndRecordVideoResponse(ioTResponse, dayTime, startTime,
                                endTime, pageStart, pageSize, streamType, oneMoreTime);
                    }
                });

    }


    private void processGetEventAndRecordVideoResponse(IoTResponse ioTResponse, long dayTime,
                                                       int startTime,
                                                       int endTime, int pageStart, int pageSize,
                                                       int streamType, boolean oneMoreTime) {

        if (calendarPickerView.getSelectedDate().getTime() / 1000 != dayTime) {
            return;
        }
        final int code = ioTResponse.getCode();
        if (code != 200) {
            if (oneMoreTime) {
                getEventVideoListInter(dayTime, startTime, endTime, pageStart, pageSize, streamType, false);
            } else {
                showCoverNotConnect();
            }
            return;
        }
        Object data = ioTResponse.getData();
        if (data == null) {
            if (oneMoreTime) {
                getEventVideoListInter(dayTime, startTime, endTime, pageStart, pageSize, streamType, false);
            } else {
                showCoverDataError();
            }
            return;
        }
        if (!(data instanceof JSONObject)) {
            if (oneMoreTime) {
                getEventVideoListInter(dayTime, startTime, endTime, pageStart, pageSize, streamType, false);
            } else {
                showCoverDataError();
            }
            return;
        }
        try {
            if (calendarPickerView.getSelectedDate().getTime() / 1000 != dayTime) {
                return;
            }
            JSONObject jsonObject = (JSONObject) data;
            JSONArray jsonArray = jsonObject.getJSONArray("recordFileList");
            if (jsonArray != null) {
                List<VideoInfo> tmpLst = new LinkedList<>();
                int returnDataSize = jsonArray.length();
                for (int i = 0; i < returnDataSize; i++) {
                    JSONObject videoJsonObject = jsonArray.getJSONObject(i);
                    /**
                     {
                     "fileName": "1534417242_PQYJVoAjWLEsQs6jtm6R00100e4800_0_53802d03-79d8-4933-a236
                     -6c95b400556e",
                     "streamType": 0,
                     "fileSize": 144460328,
                     "recordType": 0,
                     "endTime": "2018-08-16 19:30:44",
                     "beginTime": "2018-08-16 19:00:42"
                     }
                     **/
                    VideoInfo videoInfo = new VideoInfo();
                    videoInfo.iotId = iotId;
                    videoInfo.fileName = videoJsonObject.getString("fileName");
                    videoInfo.streamType = videoJsonObject.getInt("streamType");
                    videoInfo.fileSize = videoJsonObject.getInt("fileSize");
                    videoInfo.recordType = videoJsonObject.getInt("recordType");
                    videoInfo.beginTime = TimeUtil.Date2TimeStamp(
                            videoJsonObject.getString("beginTime"),
                            "yyyy-MM-dd HH:mm:ss");
                    videoInfo.endTime = TimeUtil.Date2TimeStamp(videoJsonObject.getString("endTime"),
                            "yyyy-MM-dd HH:mm:ss");
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


                if (videoList.size() == 0 && jsonArray.length() < pageSize) {//加载完毕，且无录像
                    showCoverNoneVideo();
                } else if (returnDataSize > 0 && returnDataSize == pageSize && pageStart + 1 < MAX_PAGE_START) {//继续加载
                    getEventVideoList(dayTime, startTime, endTime, pageStart + 1, pageSize,
                            streamType);
                } else {//加载完毕，且有录像
                    showCoverSwipToPlay();
                }

            } else {
                showCoverDataError();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            showCoverDataError();
        }
    }

}
