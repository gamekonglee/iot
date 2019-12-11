package com.aliyun.iot.demo.ipcview.utils;

import android.content.Context;
import android.util.TypedValue;

public class ScreenUtil {
    static public int convertDp2Px(Context context, float count) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, count, context.getResources()
                .getDisplayMetrics());
    }

    static public int convertPx2Dp(Context context, float count) {
        return (int) (count / context.getResources().getDisplayMetrics().density);
    }

    public static int convertSp2px(Context context, float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources()
                .getDisplayMetrics());
    }

}
