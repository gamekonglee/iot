package com.aliyun.iot.ilop.demo.utils;

import android.app.Application;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.aliyun.iot.aep.component.router.Router;

import com.juhao.home.R;

/**
 * Created by feijie.xfj on 17/11/19.
 */

public class FloatWindowHelper {
    private WindowManager windowManager;

    private Application application;

    private View windowView;

    private WindowManager.LayoutParams params;

    private boolean needShowFloatWindowFlag = false;

    private volatile boolean isInit = false;

    private volatile boolean isAddWindow = false;

    private static FloatWindowHelper floatWindowHelper;

    private FloatWindowHelper(Application application) {
        this.application = application;
        initWindow();
    }

    public static FloatWindowHelper getInstance(Application app) {
        if (floatWindowHelper == null) {
            synchronized (FloatWindowHelper.class) {
                if (floatWindowHelper == null) {
                    floatWindowHelper = new FloatWindowHelper(app);
                }
            }
        }
        return floatWindowHelper;
    }


    private void initWindow() {
        windowManager = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflater = LayoutInflater.from(application);
        params = getWindowParams();
        windowView = inflater.inflate(R.layout.float_window, null);
        windowView.setOnTouchListener(new WindowTouchListener());

        windowView.findViewById(R.id.window_home_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.getInstance().toUrl(application, "page/main");
            }
        });

        windowView.findViewById(R.id.window_about_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.getInstance().toUrl(application, "page/about");
            }
        });

        windowView.findViewById(R.id.window_log_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.getInstance().toUrl(application, "page/alog");
            }
        });

        windowView.findViewById(R.id.window_scan_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.getInstance().toUrl(application, "page/scan");
            }
        });
        isInit = true;
    }

    public void setNeedShowFloatWindowFlag(boolean showFloatWindowFlag) {
        this.needShowFloatWindowFlag = showFloatWindowFlag;
        showFloatWindow();
    }

    public void showFloatWindow() {
        if (!isInit) {
            initWindow();
        }
        if (!needShowFloatWindowFlag) {
            if (isAddWindow) {
                removeFloatWindow();
            } else {
                return;
            }
            return;
        }
        if (isAddWindow) {
            return;
        } else {
            isAddWindow = true;
            windowManager.addView(windowView, params);
        }
    }


    public void removeFloatWindow() {
        if (isAddWindow) {
            windowManager.removeView(windowView);
        }
        isAddWindow = false;
    }

    private WindowManager.LayoutParams getWindowParams() {

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.format = PixelFormat.RGBA_8888;

        params.width = (int) ScreenTools.convertDp2Px(application, 50);
        params.height = (int) ScreenTools.convertDp2Px(application, 180);

        //兼容7.1系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT > 24) {//7.1系统走这里
                params.type = WindowManager.LayoutParams.TYPE_PHONE;
            } else {//不知道为什么 MIUI9.7.11.9开发板系统 不支持用Toast
                params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = (int) (ScreenTools.getScreenWidth(application) - ScreenTools.convertDp2Px(application, 18 + 50));
        params.y = (int) (ScreenTools.getScreenHeight(application) - ScreenTools.convertDp2Px(application, 27 + 180));
        return params;
    }

    private int mode = -1;

    private static final int DOWN = 0;

    //开始触控的坐标，相对于屏幕左上角
    private float mTouchStartX, mTouchStartY;

    private class WindowTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    mode = DOWN;
                    mTouchStartX = event.getRawX();
                    mTouchStartY = event.getRawY();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (mode == DOWN) {
                        float mTouchCurrentX = event.getRawX();
                        float mTouchCurrentY = event.getRawY();

                        params.x += mTouchCurrentX - mTouchStartX;
                        params.y += mTouchCurrentY - mTouchStartY;

                        windowManager.updateViewLayout(windowView, params);

                        mTouchStartX = mTouchCurrentX;
                        mTouchStartY = mTouchCurrentY;
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    mode = -1;
                    break;
                }
            }
            return false;
        }
    }

}
