package com.aliyun.iot.ilop.demo.utils;

import android.content.Context;
import android.util.TypedValue;


/**
 * Created by wuwang.djw on 2015/3/4.
 */
public class ScreenTools {

    static public double convertDp2Px(Context context,float count) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, count, context.getResources()
                .getDisplayMetrics());
    }

    static public double convertPx2Dp(Context context,float count) {
        return count / context.getResources().getDisplayMetrics().density;
    }


    static public double getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    static public double getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}
