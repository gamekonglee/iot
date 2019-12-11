package com.aliyun.iot.ilop.demo.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliyun.iot.ilop.demo.utils.ScreenTools;

import com.juhao.home.R;

/**
 * Created by wb-zyl208210 on 2016/10/20.
 */
public class ALoadView2 extends FrameLayout {

    private LinearLayout loadViewLL;
    private ImageView iconIV;
    private TextView tipTV;
    private TextView retryTV;

    private Drawable loadBackground = null;
    private Drawable loadingIcon = null;
    private int loadingIconSize = 0;
    private String loadingTipText = null;
    private int loadingTipTextSize = 0;
    private int loadingTipTextColor = 0xFF9AC3BF;
    private Drawable errorBackground = null;
    private Drawable errorIcon = null;
    private int errorIconSize = 0;
    private String errorTipText = null;
    private int errorTipTextSize = 0;
    private int errorTipTextColor = 0xFF9AC3BF;
    private Drawable retryBackground = null;
    private String retryText = null;
    private int retryTextSize = 0;
    private int retryTextColor = 0xFF00C7B2;


    private OnClickListener listener;
    private ObjectAnimator mRotation;

    public ALoadView2(Context context) {
        super(context);
        initView(context, null);
    }

    public ALoadView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ALoadView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public void initView(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ALoadView2);
        loadBackground = ta.getDrawable(R.styleable.ALoadView2_aloadview2_loadBackground);
        if (null == loadBackground) {
            loadBackground = new ColorDrawable(0x00000000);
        }
        loadingIcon = ta.getDrawable(R.styleable.ALoadView2_aloadview2_loadingIcon);
        if (null == loadingIcon) {
            loadingIcon = context.getResources().getDrawable(R.drawable.aloadview2_loading);
        }
        loadingIconSize = ta.getDimensionPixelSize(R.styleable.ALoadView2_aloadview2_loadingIconSize, (int) ScreenTools.convertDp2Px(context, 29f));
        loadingTipText = ta.getString(R.styleable.ALoadView2_aloadview2_loadingTipText);
        loadingTipTextSize = ta.getDimensionPixelSize(R.styleable.ALoadView2_aloadview2_loadingTipTextSize, (int) ScreenTools.convertDp2Px(context, 16f));
        loadingTipTextColor = ta.getColor(R.styleable.ALoadView2_aloadview2_loadingTipTextColor, 0xFF9AC3BF);
        errorBackground = ta.getDrawable(R.styleable.ALoadView2_aloadview2_errorBackground);
        if (null == errorBackground) {
            errorBackground = new ColorDrawable(0x00000000);
        }
        errorIcon = ta.getDrawable(R.styleable.ALoadView2_aloadview2_errorIcon);
        if (null == errorIcon) {
            errorIcon = context.getResources().getDrawable(R.drawable.aloadview2_loadingerror_light);
        }
        errorIconSize = ta.getDimensionPixelSize(R.styleable.ALoadView2_aloadview2_errorIconSize, (int) ScreenTools.convertDp2Px(context, 44f));
        errorTipText = ta.getString(R.styleable.ALoadView2_aloadview2_errorTipText);
        if (TextUtils.isEmpty(errorTipText)) {
            errorTipText = context.getString(R.string.aloadview2_loadingerror);
        }
        errorTipTextSize = ta.getDimensionPixelSize(R.styleable.ALoadView2_aloadview2_errorTipTextSize, (int) ScreenTools.convertDp2Px(context, 16f));
        errorTipTextColor = ta.getColor(R.styleable.ALoadView2_aloadview2_errorTipTextColor, 0xFF9AC3BF);
        retryBackground = ta.getDrawable(R.styleable.ALoadView2_aloadview2_retryBackground);
        if (null == retryBackground) {
            retryBackground = context.getResources().getDrawable(R.drawable.aloadview2_retry_background_green);
        }
        retryText = ta.getString(R.styleable.ALoadView2_aloadview2_retryText);
        retryTextSize = ta.getDimensionPixelSize(R.styleable.ALoadView2_aloadview2_retryTextSize, (int) ScreenTools.convertDp2Px(context, 14f));
        retryTextColor = ta.getColor(R.styleable.ALoadView2_aloadview2_retryTextColor, 0xFF00C7B2);
        ta.recycle();

        this.setClickable(true);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.aloadview2, this, true);
        loadViewLL = (LinearLayout) this.findViewById(R.id.aloadview2_linearlayout_root);
        iconIV = (ImageView) this.findViewById(R.id.aloadview2_imageview_icon);
        tipTV = (TextView) this.findViewById(R.id.aloadview2_textview_tip);
        retryTV = (TextView) this.findViewById(R.id.aloadview2_textview_retry);
    }

    public void showLoading() {
        if (this.getVisibility() == View.VISIBLE) {
            hide();
        }
        this.setVisibility(View.VISIBLE);

        loadViewLL.setBackground(loadBackground);
        iconIV.setBackground(loadingIcon);
        MarginLayoutParams layoutParams = (MarginLayoutParams) iconIV.getLayoutParams();
        layoutParams.width = loadingIconSize;
        layoutParams.height = loadingIconSize;
        iconIV.setLayoutParams(layoutParams);
        if (TextUtils.isEmpty(loadingTipText)) {
            tipTV.setVisibility(View.GONE);
        } else {
            tipTV.setVisibility(View.VISIBLE);
            tipTV.setTextColor(loadingTipTextColor);
            tipTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, loadingTipTextSize);
            tipTV.setText(loadingTipText);
        }
        retryTV.setVisibility(View.GONE);
        startAnimator();
    }


    public void showError(OnClickListener listener) {
        if (this.getVisibility() == View.VISIBLE) {
            hide();
        }
        this.setVisibility(View.VISIBLE);

        loadViewLL.setBackground(errorBackground);
        iconIV.setRotation(0);
        iconIV.setBackground(errorIcon);
        MarginLayoutParams layoutParams = (MarginLayoutParams) iconIV.getLayoutParams();
        layoutParams.width = errorIconSize;
        layoutParams.height = errorIconSize;
        iconIV.setLayoutParams(layoutParams);

        if (TextUtils.isEmpty(errorTipText)) {
            tipTV.setVisibility(View.GONE);
        } else {
            tipTV.setVisibility(View.VISIBLE);
            tipTV.setText(errorTipText);
            tipTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, errorTipTextSize);
            tipTV.setTextColor(errorTipTextColor);
        }

        if (null == listener) {
            retryTV.setVisibility(View.GONE);
        } else {
            retryTV.setVisibility(View.VISIBLE);
            retryTV.setBackground(retryBackground);
            retryTV.setTextColor(retryTextColor);
            retryTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, retryTextSize);
            retryTV.setText(TextUtils.isEmpty(retryText) ? getResources().getString(R.string.aloadview2_clickreload) : retryText);
            retryTV.setOnClickListener(listener);
        }
    }

    public void startAnimator() {
        try {
            if (null != iconIV.getBackground() && iconIV.getBackground() instanceof AnimationDrawable) {
                AnimationDrawable animationDrawable = (AnimationDrawable) iconIV.getBackground();
                animationDrawable.start();
            } else {
                mRotation = ObjectAnimator.ofFloat(iconIV, "rotation", 0, 360);
                mRotation.setInterpolator(new LinearInterpolator());
                mRotation.setDuration(1000);
                mRotation.setRepeatCount(ValueAnimator.INFINITE);
                mRotation.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hide() {
        if (null != mRotation) {
            mRotation.cancel();
            mRotation = null;
        }
        iconIV.clearAnimation();
        listener = null;
        this.setVisibility(View.GONE);
    }
}
