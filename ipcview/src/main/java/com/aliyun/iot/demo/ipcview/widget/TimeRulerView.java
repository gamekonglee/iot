/**
 * Copyright (c) 2018 Ralap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/


package com.aliyun.iot.demo.ipcview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.aliyun.iot.demo.ipcview.R;

import java.util.List;

public class TimeRulerView extends View {
    /**
     * 最大时间值
     */
    public static final int MAX_TIME_VALUE = 24 * 3600;

    /**
     * 绘制刻度线、中心指示线、时间段
     */
    private Paint mPaint;
    /**
     * 绘制时间数值
     */
    private TextPaint mTextPaint;
    /**
     * 时间数值字体大小，单位px
     */
    private float gradationTextSize;
    /**
     * 时间数值字体颜色
     */
    private int gradationTextColor;
    /**
     * 刻度线宽度
     */
    private int gradationWidth;
    /**
     * 刻度线颜色
     */
    private int gradationColor;

    /**
     * 控件实际宽度
     */
    private int mWidth;
    /**
     * 控件实际高度
     */
    private int mHeight;
    /**
     * 控件实际宽度的一半
     * <p>在绘制刻度线、中心指示线、时间段上都要用到改值，故将其提取出。初始0刻度是从控件中心绘制</p>
     */
    private int mHalfWidth;
    /**
     * 实际绘制的时间数值字符串一般长度，用于将时间数值字符串中心与刻度线对齐
     */
    private float mTextHalfWidth;

    /**
     * 实际绘制的时间数值字符串基线（baseline）位置高度，用于垂直居中显示
     */
    private float mTextCenterVerticalBaseLine;

    /**
     * 默认每隔4分钟显示时间数值标示
     */
    private static final int PER_TEXT_SECOND_COUNT = 60 * 4;

    /**
     * 一格代表的秒数。默认10s
     */
    private static final int UNIT_SECOND = 10;
    /**
     * 1s对应的间隔
     */
    private final float mOneSecondGap = dp2px(12) / 40f;
    /**
     * 10秒对应的物理间隔（px）
     */
    private final float mUnitGap = mOneSecondGap * UNIT_SECOND;

    /**
     * 控件内容最大长度（最大左划长度）
     */
    private final float mMaxDiatance = MAX_TIME_VALUE * mUnitGap / UNIT_SECOND;
    /**
     * 控件内容当前划动长度，即0刻度线偏离初始位置（控件中心）的距离，亦是控件getScrollX()的值
     */
    private float mCurrentDistance;

    /**
     * 当前选中的时间[0,24*60*60]
     */
    private int mCurrentTime;

    /**
     * 划动工具类
     */
    private Scroller mScroller;
    /**
     * 跟踪触摸事件滑动速度的帮助类
     */
    private VelocityTracker mVelocityTracker;

    /**
     * 划动的最小距离，小于这个值的划动距离不认为是划动操作，用于提升用户体验
     */
    private final int TOUCH_SLOP;
    /**
     * 每秒划动的最大速度
     */
    private final int MAX_VELOCITY;
    /**
     * 每秒划动的最小速度，用于判断手指离开屏幕后是否需要自动划动
     */
    private final int MIN_VELOCITY;

    /**
     * 手指按下的x轴位置
     */
    private float mPointDownX;
    /**
     * 配合{@link #TOUCH_SLOP}标记当前是否处于划动状态
     */
    private boolean isMoving;
    /**
     * 标记当前是否处于划动状态，true：{@link #setCurrentTime(int)}方法不做处理
     */
    private boolean isOnTouch;
    /**
     * 记录移动过程中的上一次{@link #onTouchEvent}回调X位置，用于计算出本次{@link #onTouchEvent}回调移动的距离
     */
    private float mLastX;

    /**
     * 秒刻度线长度
     */
    private float mSecondLen;
    /**
     * 分刻度线长度
     */
    private float mMinuteLen;
    /**
     * 小时刻度线长度
     */
    private float mHourLen;

    /**
     * 中心指示线颜色
     */
    private int indicatorColor;
    /**
     * 中心指示线宽度
     */
    private int indicatorWidth;
    /**
     * 时间段颜色
     */
    private int partColor;

    /**
     * 时间回调
     */
    private TimeCallBack mTimeCallBack;

    /**
     * 时间段数据
     */
    private List<TimePart> mTimeParts;

    /**
     * 时间回调,包含划动过程回调和选中回调
     */
    public interface TimeCallBack {
        /**
         * 选中值回调
         *
         * @param newTimeValue 选中值
         */
        void onTimeSelected(int newTimeValue);

        /**
         * 划动过程中值的回调
         *
         * @param newTimeValue 划动过程中的值
         */
        void onTimeChanged(int newTimeValue);
    }

    /**
     * 时间段bean，包含开始时间、结束时间,[0，24*60*60)
     */
    public static class TimePart {
        public int startTime;
        public int endTime;
    }

    /**
     * 设置时间块
     *
     * @param timeParts
     */
    public void setTimeParts(List<TimePart> timeParts) {
        if (timeParts != null) {
            mTimeParts = timeParts;
            postInvalidate();
        }
    }

    /**
     * 设置时间块（段）集合，并回到43200(12:00)刻度处
     *
     * @param timePartList 时间块集合
     */
    public void setTimePartListAndBackCenter(List<TimePart> timePartList) {
        this.mTimeParts = timePartList;
        resetToMiddle();
    }

    /**
     * 设置时间回调
     *
     * @param timeCallBack
     */
    public void setTimeCallBack(TimeCallBack timeCallBack) {
        this.mTimeCallBack = timeCallBack;
    }

    /**
     * 设置当前选中时间
     *
     * @param currentTime
     */
    public void setCurrentTime(int currentTime) {
        if (isOnTouch) {
            return;
        }
        //限制输入值为[0,MAX_TIME_VALUE]
        currentTime = Math.min(MAX_TIME_VALUE, Math.max(currentTime, 0));

        if (mCurrentTime == currentTime) {
            return;
        }
        mCurrentTime = currentTime;
        calculateDistance();
        postInvalidate();
    }

    /**
     * 将选中时间设置为12：00
     */
    private void resetToMiddle() {
        mCurrentTime = MAX_TIME_VALUE >> 1;
        calculateDistance();
        postInvalidate();
    }

    public TimeRulerView(Context context) {
        this(context, null);
    }

    public TimeRulerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeRulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);

        init(context);

        mTextHalfWidth = mTextPaint.measureText("00:00") * .5f;

        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        TOUCH_SLOP = viewConfiguration.getScaledTouchSlop();
        MAX_VELOCITY = viewConfiguration.getScaledMaximumFlingVelocity();
        MIN_VELOCITY = viewConfiguration.getScaledMinimumFlingVelocity();
    }

    private void init(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(gradationTextSize);
        mTextPaint.setColor(gradationTextColor);

        mScroller = new Scroller(context);
    }

    /**
     * 获取自定义属性值
     * @param context
     * @param attrs
     */
    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimeRulerView);
        gradationTextSize = typedArray.getDimension(R.styleable.TimeRulerView_ipc_gradationTextSize, sp2px(12));
        gradationTextColor = typedArray.getColor(R.styleable.TimeRulerView_ipc_gradationTextColor, Color.GRAY);
        gradationWidth = (int) typedArray.getDimension(R.styleable.TimeRulerView_ipc_gradationWidth, dp2px(1));
        gradationColor = typedArray.getColor(R.styleable.TimeRulerView_ipc_gradationColor, Color.GRAY);
        indicatorColor = typedArray.getColor(R.styleable.TimeRulerView_ipc_indicatorLineColor, Color.RED);
        indicatorWidth = (int) typedArray.getDimension(R.styleable.TimeRulerView_ipc_indicatorLineWidth, dp2px(1));
        partColor = typedArray.getColor(R.styleable.TimeRulerView_ipc_partColor, Color.parseColor("#7FFF3838"));
        mHourLen = typedArray.getDimension(R.styleable.TimeRulerView_ipc_hourLen, dp2px(30));
        mMinuteLen = typedArray.getDimension(R.styleable.TimeRulerView_ipc_minuteLen, dp2px(22));
        mSecondLen = typedArray.getDimension(R.styleable.TimeRulerView_ipc_secondLen, dp2px(16));
        mCurrentTime = typedArray.getInt(R.styleable.TimeRulerView_ipc_currentTime, 0);
        typedArray.recycle();
    }

    /**
     * 根据时间算移动距离
     */
    private void calculateDistance() {
        mCurrentDistance = mCurrentTime * mUnitGap / UNIT_SECOND;
    }

    /**
     * 根据移动距离算时间
     *
     * @param selected true：当前划动结束，需要回调选中值；false：处于划动状态，不需要回调选中值
     */
    private void calculateTime(boolean selected) {
        mCurrentDistance = Math.max(0, Math.min(mCurrentDistance, mMaxDiatance));
        mCurrentTime = (int) (mCurrentDistance / mUnitGap * UNIT_SECOND);
        if (mTimeCallBack != null) {
            mTimeCallBack.onTimeChanged(mCurrentTime);
            if (selected) {
                mTimeCallBack.onTimeSelected(mCurrentTime);
            }
        }
        if (selected) {
            isOnTouch = false;
        }
        invalidate();
    }

    /**
     * 设置文字baseline位置
     */
    private void setTextYLocation() {
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        mTextCenterVerticalBaseLine = mHeight / 2.0f - fm.descent + (fm.descent - fm.ascent) / 2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        int actionMasked = event.getActionMasked();
        float x = event.getX();
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                mPointDownX = x;
                //若之前的划动还未结束，则强制中止
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
                isMoving = false;
                isOnTouch = true;
                break;

            case MotionEvent.ACTION_MOVE:
                if (!isMoving) {
                    //判断当前划动距离是否满足TOUCH_SLOP
                    if (Math.abs(mPointDownX - x) > TOUCH_SLOP) {
                        isMoving = true;
                    } else {
                        break;
                    }
                }
                //更新当前划动距离
                mCurrentDistance -= (x - mLastX);
                //更新当前时间字段，并重绘UI
                calculateTime(false);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //根据VelocityTracker获取当前速度，从而判断当前是否需要自动划动
                mVelocityTracker.computeCurrentVelocity(1000, MAX_VELOCITY);
                int xVelocity = (int) mVelocityTracker.getXVelocity();
                if (Math.abs(xVelocity) > MIN_VELOCITY) {
                    mScroller.fling((int) mCurrentDistance, 0, -xVelocity, 0, 0, (int) mMaxDiatance, 0, 0);
                    invalidate();
                } else {
                    calculateTime(true);
                    isOnTouch = false;
                }
                break;
        }
        mLastX = x;
        return true;
    }

    /**
     * 配合Scroller实现滑动效果
     */
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mCurrentDistance = mScroller.getCurrX();
            calculateTime(mScroller.getCurrX() == mScroller.getFinalX());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (modeHeight == MeasureSpec.AT_MOST) {
            mHeight = dp2px(80);
        }
        mHalfWidth = mWidth >> 1;
        setMeasuredDimension(widthMeasureSpec, mHeight);
        setTextYLocation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制刻度线和时间文本
        drawGraduation(canvas);
        //绘制中间指针
        drawTimeIndicator(canvas);
        //绘制时间片段
        drawTimeParts(canvas);
    }

    /**
     * 绘制刻度线和时间文本
     *
     * @param canvas
     */
    private void drawGraduation(Canvas canvas) {
        mPaint.setColor(gradationColor);
        mPaint.setStrokeWidth(gradationWidth);
        int expend = 3 * UNIT_SECOND;//多绘制3格最小刻度，避免在边界范围刻度线缺失
        int start = (int) ((mCurrentDistance - mHalfWidth) / mUnitGap) * UNIT_SECOND;//计算显示的最小刻度值
        start = Math.max(0, start - expend);//0刻度未划至容器外
        int end = Math.min(MAX_TIME_VALUE, start + (int) ((mWidth / mUnitGap) * UNIT_SECOND) + expend * 2);//计算显示的最大刻度值
        float offsetX = mHalfWidth - (mCurrentDistance - start / UNIT_SECOND * mUnitGap);
        while (start <= end) {
            if (start % 3600 == 0) {//小时刻度线
                canvas.drawLine(offsetX, 0, offsetX, mHourLen, mPaint);//绘制上侧刻度线
                canvas.drawLine(offsetX, mHeight - mHourLen, offsetX, mHeight, mPaint);//绘制下侧刻度线
            } else if (start % 60 == 0) {//分刻度线
                canvas.drawLine(offsetX, 0, offsetX, mMinuteLen, mPaint);
                canvas.drawLine(offsetX, mHeight - mMinuteLen, offsetX, mHeight, mPaint);
            } else {//秒刻度线
                canvas.drawLine(offsetX, 0, offsetX, mSecondLen, mPaint);
                canvas.drawLine(offsetX, mHeight - mSecondLen, offsetX, mHeight, mPaint);
            }

            //每隔4分钟绘制时间文本提示
            if (start % PER_TEXT_SECOND_COUNT == 0) {
                String time = formatTime(start);
                canvas.drawText(time, offsetX - mTextHalfWidth, mTextCenterVerticalBaseLine, mTextPaint);
            }
            start += UNIT_SECOND;
            offsetX += mUnitGap;
        }
    }

    /**
     * 绘制中间指针
     *
     * @param canvas
     */
    private void drawTimeIndicator(Canvas canvas) {
        mPaint.setColor(indicatorColor);
        mPaint.setStrokeWidth(indicatorWidth);
        canvas.drawLine(mHalfWidth, 0, mHalfWidth, mHeight, mPaint);
    }

    /**
     * 绘制时间片段
     *
     * @param canvas
     */
    private void drawTimeParts(Canvas canvas) {
        if (mTimeParts == null) {
            return;
        }
        mPaint.setStrokeWidth(mHeight);
        mPaint.setColor(partColor);
        float start, end;
        float halfPartHeight = mHeight * .5f;
        for (int i = 0, size = mTimeParts.size(); i < size; i++) {
            TimePart timePart = mTimeParts.get(i);
            start = timePart.startTime * mOneSecondGap + mHalfWidth - mCurrentDistance;
            end = timePart.endTime * mOneSecondGap + mHalfWidth - mCurrentDistance;
            if (start < mWidth && end > 0) {//view范围外的不绘制
                canvas.drawLine(start, halfPartHeight, end, halfPartHeight, mPaint);
            }
        }
    }

    /**
     * 格式化时间为 HH：ss
     *
     * @param timeValue
     * @return
     */
    public static String formatTime(int timeValue) {
        if (timeValue < 0) {
            timeValue = 0;
        }
        int hour = timeValue / 3600;
        int minute = timeValue % 3600 / 60;
        StringBuilder sb = new StringBuilder();
        if (hour < 10) {
            sb.append('0');
        }
        sb.append(hour).append(':');
        if (minute < 10) {
            sb.append('0');
        }
        sb.append(minute);
        return sb.toString();
    }

    /**
     * dp转px
     *
     * @param dp
     * @return
     */
    private int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param sp
     * @return
     */
    private int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }
}
