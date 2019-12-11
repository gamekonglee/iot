package com.juhao.home;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.aliyun.iot.ilop.demo.DemoApplication;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


//import bc.yxdc.com.constant.Constance;
//import bc.yxdc.com.ui.activity.user.LoginActivity;
//import bc.yxdc.com.ui.view.PMSwipeRefreshLayout;
//import bc.yxdc.com.view.SystemCheckPopWindow;


/**
 * @author Jun
 * @time 2016/8/19  10:37
 * @desc ${TODD}
 */
public class
UIUtils {

    /**
     * 得到上下文
     * @return
     */
    public static Context getContext(){
        return DemoApplication.getInstance();
    }


//    public static String getDeviceId(){
//        return ((TelephonyManager) getContext().getSystemService(getContext().TELEPHONY_SERVICE))
//                .getDeviceId();
//    }

    public static void initActivityScreen(Activity addDeviceActivity) {
        setFullScreenColor(Color.TRANSPARENT,addDeviceActivity);
        setStatuTextColor(addDeviceActivity,Color.TRANSPARENT);
    }
    public static  void setStatuTextColor(Activity activity, int color) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            //取消状态栏透明
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //添加Flag把状态栏设为可绘制模式
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(color);
            //设置系统状态栏处于可见状态
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                //设置状态栏文字颜色及图标为深色
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }
    public static void setFullScreenColor(int color,Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            // 生成一个状态栏大小的矩形
//            View statusView = createStatusView(activity, color);
            // 添加 statusView 到布局中
//            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
//            decorView.addView(statusView);
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
//            rootView.setFitsSystemWindows(true);
//            rootView.setClipToPadding(true);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                try {
                    Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                    Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                    field.setAccessible(true);
                    field.setInt(activity.getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
                } catch (Exception e) {}
            }
        }
    }
    /**
     *mac
     * @param context
     * @return String
     */
    public static String getLocalMac(Context context){
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String mac=info.getMacAddress();
        if(mac.equals("02:00:00:00:00:00")){
            return getInterfaceLocalmac();
        }else {
            return mac;
        }
    }



    public static String  getInterfaceLocalmac(){
        String mac="";
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iF = interfaces.nextElement();
                byte[] addr = iF.getHardwareAddress();
                if (addr == null || addr.length == 0) {
                    continue;
                }
                StringBuilder buf = new StringBuilder();
                for (byte b : addr) {
                    buf.append(String.format("%02X:", b));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                mac = buf.toString();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return mac;
    }


    /**
     * 得到Resources对象
     * @return
     */
    public static Resources getResources(){
        return getContext().getResources();
    }

    /**
     * 得到包名
     * @return
     */
    public static String getpackageName(){
        return  getContext().getPackageName();
    }

    /**
     * 得到配置的String信息
     * @param resId
     * @return
     */
    public static String getString(int resId){
        return getResources().getString(resId);
    }

    /**
     * 得到配置的String信息
     * @param resId
     * @return
     */
    public static String getString(int resId,Object ...formatAgs){
        return getResources().getString(resId,formatAgs);
    }

    /**
     * 得到配置String数组
     * @param resId
     * @return
     */
    public static String[] getStringArr(int resId){
        return getResources().getStringArray(resId);
    }
    public static int dip2PX(int dip) {
        //拿到设备密度
        float density=getResources().getDisplayMetrics().density;
        int px= (int) (dip*density+.5f);
        return px;
    }
    public static int dip2PX(Context context,int dip) {
        //拿到设备密度
        float density=context.getResources().getDisplayMetrics().density;
        int px= (int) (dip*density+.5f);
        return px;
    }

    /**
     * 兼容状态栏透明（沉浸式）
     * @param activity
     */
    public static void setImmersionStateMode(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT != Build.VERSION_CODES.LOLLIPOP) {
            // 透明状态栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }
    public static Dialog showBottomInDialog(Activity activity, int layout_res, int height) {
        Dialog dialog = new Dialog(activity, R.style.customDialog);
        dialog.setContentView(layout_res);
        dialog.setCanceledOnTouchOutside(true);
        Window win = dialog.getWindow();
        win.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = win.getAttributes();
        WindowManager manager = activity.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        lp.width = width;
        lp.height = height;
        lp.x=0;
        win.setAttributes(lp);
        dialog.show();
        return  dialog;
    }

    public static Dialog showTopInDialog(Activity activity, int layout_res, int height) {
        Dialog dialog = new Dialog(activity, R.style.customDialog);
        dialog.setContentView(layout_res);
        dialog.setCanceledOnTouchOutside(true);
        Window win = dialog.getWindow();
        win.setGravity(Gravity.TOP);
        WindowManager.LayoutParams lp = win.getAttributes();
        WindowManager manager = activity.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        lp.width = width;
        lp.height = height;
        lp.x=0;
        win.setAttributes(lp);
        dialog.show();
        return  dialog;
    }
    public static void showSingleWordDialog(final Context activity, String tittle, final View.OnClickListener listener) {
        final Dialog dialog = new Dialog(activity, R.style.customDialog);
        dialog.setContentView(R.layout.dialog_layout);
        TextView tv_num= (TextView) dialog.findViewById(R.id.tv_num);
        tv_num.setText(tittle);

        TextView btn = (TextView) dialog.findViewById(R.id.tv_ensure);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view);
                dialog.dismiss();
            }
        });
        TextView cancel= (TextView) dialog.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
           /*
         * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window
         * 对象,这样这可以以同样的方式改变这个Activity的属性.
         */
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }
    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }
//    public static Dialog showLoginDialog(final Context acticity){
//        final Dialog dialog=new Dialog(acticity, R.style.customDialog);
//        dialog.setContentView(R.layout.dialog_login_toast);
//        TextView tv_login=dialog.findViewById(R.id.tv_login);
//        TextView tv_register=dialog.findViewById(R.id.tv_register);
//        tv_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent logoutIntent = new Intent(acticity, LoginActivity.class);
////                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                acticity.startActivity(logoutIntent);
//                dialog.dismiss();
//            }
//        });
//        tv_register.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                IntentUtil.startActivity(acticity, Regiest01Activity.class, false);
//                acticity.startActivity(new Intent(acticity, Regiest01Activity.class));
//                dialog.dismiss();
//            }
//        });
//        ImageView iv_dismiss=dialog.findViewById(R.id.iv_dismiss);
//        iv_dismiss.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//        return dialog;
//    }
public static int measureListViewHeight(final AbsListView listView) {
    final ListAdapter listAdapter = listView.getAdapter();

    if (listAdapter == null) {
        return 0;
    }
    final int[] totalHeight = {0};
    listView.post(new Runnable() {
        @Override
        public void run() {

            int count = listAdapter.getCount();
            //TODO 这里可以去获取每一列最高的一个
            View listItem = listAdapter.getView(0, null, listView);
//            listItem.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
            listItem.measure(0, 0);
            if (listView instanceof GridView) {
                int columns = ((GridView) listView).getNumColumns();
                int rows = count % columns != 0 ? 1 : 0;
                rows += count / columns;
                totalHeight[0] += listItem.getMeasuredHeight() * rows;
            } else if (listView instanceof ListView) {
                for (int i = 0; i < count; i++) {
                    listItem = listAdapter.getView(i, null, listView);
                    listItem.measure(0, 0);
                    totalHeight[0] += listItem.getMeasuredHeight() + ((ListView) listView).getDividerHeight() * (listAdapter.getCount() - 1);
                }
            }
            ViewGroup.LayoutParams params = listView.getLayoutParams();

            params.height = totalHeight[0];

            listView.setLayoutParams(params);

        }
    });
    return totalHeight[0];
}
    public static int  initListViewHeight(ListView listView) {
        if(listView==null){
            return 0;
        }
        Adapter adapter=listView.getAdapter();
        if(adapter==null){
            return 0;
        }
        int count=adapter.getCount();
        int total=0;
        for(int i=0;i<count;i++){
            View view=adapter.getView(i,null,listView);
            view.measure(0,0);
            total+=view.getMeasuredHeight();
            if(i!=count-1){
                total+=listView.getDividerHeight();
            }
        }
//        System.out.println("total:"+total);
        ViewGroup.LayoutParams layoutParams=listView.getLayoutParams();
        layoutParams.height=total;
        listView.setLayoutParams(layoutParams);
        listView.requestLayout();
        return total;
//       return measureListViewHeight(listView);
    }
    public static int  initListViewHeight(ListView listView,int height) {
        if(listView==null){
            return 0;
        }
        Adapter adapter=listView.getAdapter();
        if(adapter==null){
            return 0;
        }
        int count=adapter.getCount();
        int total=0;
        for(int i=0;i<count;i++){
            View view=adapter.getView(i,null,listView);
            view.measure(0,0);
            total+=height;
            if(i!=count-1){
                total+=listView.getDividerHeight();
            }
        }
//        System.out.println("total:"+total);
        ViewGroup.LayoutParams layoutParams=listView.getLayoutParams();
        layoutParams.height=total;
        listView.setLayoutParams(layoutParams);
        listView.requestLayout();
        return total;
//       return measureListViewHeight(listView);
    }

    public static int  initGridViewHeight(GridView listView,int numColumn) {
        if(listView==null){
            return 0;
        }
        Adapter adapter=listView.getAdapter();
        if(adapter==null){
            return 0;
        }
        int count=adapter.getCount();
        int total=0;
        if(count%numColumn!=0){
            while (count%numColumn!=0){
                count++;
            }
        }
        count=count/numColumn;
        for(int i=0;i<count;i++){
            View view=adapter.getView(i,null,listView);
            view.measure(0,0);
            total+=view.getMeasuredHeight();
            if(i!=count-1)total+=listView.getVerticalSpacing();
        }
        System.out.println("total:"+total);
        ViewGroup.LayoutParams layoutParams=listView.getLayoutParams();
        layoutParams.height=total;
        listView.setLayoutParams(layoutParams);
        listView.requestLayout();
        return total;
    }
    public static int  initGridViewHeight(GridView listView) {
        if(listView==null){
            return 0;
        }
        Adapter adapter=listView.getAdapter();
        if(adapter==null){
            return 0;
        }
        int count=adapter.getCount();
        int total=0;
        if(count%2!=0){
            count++;
        }
        count=count/2;
        for(int i=0;i<count;i++){
            View view=adapter.getView(i,null,listView);
            view.measure(0,0);
            total+=view.getMeasuredHeight();
            if(i!=count-1)total+=listView.getVerticalSpacing();
        }
//        System.out.println("total:"+total);
        ViewGroup.LayoutParams layoutParams=listView.getLayoutParams();
        layoutParams.height=total;
        listView.setLayoutParams(layoutParams);
        listView.requestLayout();
        return total;
    }
    public static void diallPhone(Context context,String mShop_mobile) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + mShop_mobile);
        intent.setData(data);
        context.startActivity(intent);
    }
    public static int getScreenWidth(Activity activity){
        if(activity==null||activity.isFinishing()||activity.isDestroyed()){
            return UIUtils.dip2PX(480);
        }
        WindowManager manager = activity.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        return width;
    }
    public static int getScreenHeight(Activity activity){
        WindowManager manager = activity.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        return height;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isValidContext (Context c) {

        Activity a = (Activity) c;

        if (a.isDestroyed() || a.isFinishing()) {
//            Log.i("YXH", "Activity is invalid." + " isDestoryed-->" + a.isDestroyed() + " isFinishing-->" + a.isFinishing());
            return false;
        } else {
            return true;
        }
    }

    public static Bitmap drawableToBitmap(int width,int height,Drawable drawable) {

        int w = width;
        int h =height;
//        System.out.println("Drawable转Bitmap");
        Bitmap.Config config = Bitmap.Config.ARGB_8888;

        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否则在View或者SurfaceView里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);

        return bitmap;
    }

    public static void showSystemStopDialog(final Activity activity, final View.OnClickListener listener) {
        final Dialog dialog = new Dialog(activity, R.style.customDialog);
        dialog.setContentView(R.layout.dialog_layout_system);
        TextView tv_num= (TextView) dialog.findViewById(R.id.tv_num);
//        tv_num.setText(string);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView btn = (TextView) dialog.findViewById(R.id.tv_ensure);
        TextView tv_cancel=dialog.findViewById(R.id.tv_cancel);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                listener.onClick(view);

            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                activity.finish();
            }
        });

           /*
         * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window
         * 对象,这样这可以以同样的方式改变这个Activity的属性.
         */
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
//        params.horizontalMargin=50;
        params.gravity=Gravity.CENTER;
        params.width = getScreenWidth(activity)-100;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

//        SystemCheckPopWindow popWindow=new SystemCheckPopWindow(activity,string,content);
//        popWindow.onShow(v);
    }

    public static void showLoginDialog(FragmentActivity activity) {
//        Intent intent=new Intent(activity, LoginActivity.class);
//        activity.startActivity(intent);
    }
//    public static Dialog showBottomInDialog(Context mContext, int resId){
//        //R.style.***一定要写，不然不能充满整个屏宽，引用R.style.AppTheme就可以
//        final AlertDialog dialog = new AlertDialog.Builder(mContext, R.style.AppTheme).create();
//        View view = View.inflate(mContext, resId, null);
//        Window window = dialog.getWindow();
//        window.setGravity(Gravity.BOTTOM);
//        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
//        window.setWindowAnimations(R.style.dialogStyle);
////        window.getDecorView().setPadding(0, 0, 0, 0);
//
//        //设置dialog弹出后会点击屏幕或物理返回键，dialog不消失
//        dialog.setCanceledOnTouchOutside(true);
//        dialog.show();
//        window.setContentView(view);
//
//        //获得window窗口的属性
//        WindowManager.LayoutParams params = window.getAttributes();
//        //设置窗口宽度为充满全屏
//        params.width = WindowManager.LayoutParams.MATCH_PARENT;//如果不设置,可能部分机型出现左右有空隙,也就是产生margin的感觉
//        //设置窗口高度为包裹内容
//        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
////        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;//显示dialog的时候,就显示软键盘
//        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;//就是这个属性导致window后所有的东西都成暗淡
//        params.dimAmount = 0.5f;//设置对话框的透明程度背景(非布局的透明度)
//        //将设置好的属性set回去
//        window.setAttributes(params);

//        Dialog dialog=new Dialog(mContext,R.style.customDialog);
//        dialog.setContentView(resId);//这行一定要写在前面
//        dialog.setCancelable(true);//点击外部不可dismiss
//        dialog.setCanceledOnTouchOutside(true);
//        Window window = dialog.getWindow();
//        window.setGravity(Gravity.BOTTOM);
//        window.setBackgroundDrawable(mContext.getResources().getDrawable(android.R.color.white));
//        window.setWindowAnimations(R.style.dialogStyle);
//        WindowManager.LayoutParams params = window.getAttributes();
//        params.width = WindowManager.LayoutParams.MATCH_PARENT;
//        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        params.y=0;
//        params.x=0;
////        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;//就是这个属性导致window后所有的东西都成暗淡
//        window.setAttributes(params);
//        dialog.show();
//        return dialog;
//    }

//    public static void clearLoginInfo(Context context) {
//        MyShare.get(context).putString(Constance.token,"");
//        MyShare.get(context).putString(Constance.user_id,"");
//    }

//    public static void initPullToRefresh(PMSwipeRefreshLayout mPullToRefreshLayout) {
//        mPullToRefreshLayout.setColorSchemeColors(Color.BLUE,Color.GREEN,Color.YELLOW,Color.RED);
//        mPullToRefreshLayout.setRefreshing(false);
//
//    }

//    public static void showShareDialog(final Activity activity, final Bitmap mBitmap, final String path, final String mLocalPath) {
//        final String title = "来自 " + UIUtils.getString(R.string.app_name) + " App的分享";
//        final Dialog dialog=UIUtils.showBottomInDialog(activity, R.layout.share_dialog,UIUtils.dip2PX(205));
//        TextView tv_cancel=dialog.findViewById(R.id.tv_cancel);
//        LinearLayout ll_wx=dialog.findViewById(R.id.ll_wx);
//        LinearLayout ll_pyq=dialog.findViewById(R.id.ll_pyq);
//        LinearLayout ll_qq=dialog.findViewById(R.id.ll_qq);
//        LinearLayout ll_link=dialog.findViewById(R.id.ll_link);
//        tv_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//        ll_wx.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ShareUtil.shareWxPic(activity,title,mBitmap,true);
//                dialog.dismiss();
//            }
//        });
//        ll_pyq.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ShareUtil.shareWxPic(activity,title,mBitmap,false);
//                dialog.dismiss();
//            }
//        });
//        ll_qq.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ShareUtil.shareQQLocalpic(activity,mLocalPath ,title);
//                dialog.dismiss();
//            }
//        });
//        ll_link.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
//                // 将文本内容放到系统剪贴板里。
//                cm.setText(path);
//                MyToast.show(activity,"链接复制成功！");
//                dialog.dismiss();
//            }
//        });
//    }

    public static Bitmap converBitmap(Bitmap bmp) {
            int w = bmp.getWidth();
            int h = bmp.getHeight();

            Matrix matrix = new Matrix();
            matrix.postScale(-1, 1); // 镜像水平翻转
            Bitmap convertBmp = Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);
            return convertBmp;
    }

    public static byte[] Bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] datas = baos.toByteArray();
        return datas;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Bitmap bytes2bitmap(byte[] bytes, BitmapFactory.Options opts, Activity context, Size size) {
        Size previewSize = size;
        YuvImage yuvimage=new YuvImage(bytes, ImageFormat.NV21, previewSize.getWidth(), previewSize.getHeight(), null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, previewSize.getWidth(), previewSize.getHeight()), 80, baos);  //这里 80 是图片质量，取值范围 0-100，100为品质最高
        byte[] jdata = baos.toByteArray();//这时候 bmp 就不为 null 了

//        YuvImage yuvimage=new YuvImage(bytes, ImageFormat.NV21, UIUtils.getScreenWidth(context),UIUtils.getScreenHeight(context), null);//20、20分别是图的宽度与高度
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        yuvimage.compressToJpeg(new Rect(0, 0, UIUtils.getScreenWidth(context),UIUtils.getScreenHeight(context)), 80, baos);//80--JPG图片的质量[0-100],100最高
//        byte[] jdata = baos.toByteArray();

        if (jdata != null)
                if (opts != null)
                    return BitmapFactory.decodeByteArray(jdata, 0, jdata.length,
                            opts);
                else
                    return BitmapFactory.decodeByteArray(jdata, 0, jdata.length);
            return null;

        }





    public static Bitmap view2Bitmap(View view) {
//        Bitmap b = Bitmap.createBitmap(UIUtils.dip2PX(200),UIUtils.dip2PX(320), Bitmap.Config.RGB_565);
//        Canvas c = new Canvas(b);
//        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
//        // Draw background
//        Drawable bgDrawable = v.getBackground();
//        if (bgDrawable != null)
//            bgDrawable.draw(c);
//        else
//            c.drawColor(Color.WHITE);
//        // Draw view to canvas
//        v.draw(c);
//        return b;
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());view.buildDrawingCache();Bitmap bitmap=view.getDrawingCache();return bitmap;
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int getTvWidth(Context context,TextView tv_temp, String text) {
        TextPaint paint = tv_temp.getPaint();
//        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
//        paint.setTextSize(scaledDensity * tv_temp.getTextSize());
//        return (int) paint.measureText(text);
        return getTextWidth(paint,text)+UIUtils.dip2PX(20);
    }
    public static int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }
}
