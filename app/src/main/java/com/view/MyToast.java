package com.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.juhao.home.R;
import com.util.AppUtils;

public class MyToast {

    public static Toast mToast;

    public static void init(Context context){
        mToast = new Toast(context);
    }

    public static void show(Context context, String msg) {
        if (mToast == null)
            init(context);
        if (!AppUtils.isEmpty(msg)){
            View v = LayoutInflater.from(context).inflate(R.layout.toast, null);
            TextView txtMsg = (TextView) v.findViewById(R.id.txtMsg);
            txtMsg.setText(msg);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.setView(v);
            mToast.show();
        }else{
//            MyLog.e("MyToast no msg");
        }
    }

    public static void show(Context context, int resid) {
        show(context,context.getString(resid));
    }

    public static void show(Context context, View v) {
        if (mToast == null)
            init(context);
        mToast.setView(v);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(v);
        mToast.show();
    }

    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
