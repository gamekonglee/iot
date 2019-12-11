package com.util;

import android.content.SharedPreferences;

import com.aliyun.iot.ilop.demo.DemoApplication;



public class AppConfig {

    /**
     * 同步锁
     */
    private static Object lock = new Object();

    /**
     * app版本
     */
    public final static String APP_VERSION = "1000";

    /**
     * apptoken
     */
    public final static String APP_TOKEN = "3040ngmingch50dt";

    /**
     * API URL
     */
    public final static String API_URL = NetWorkConst.API_HOST;

    /**
     * SOURCE URL
     */
    public final static String SOURCE_URL = "https://source.3pzs.com/download.php";

    /**
     * UPDATE URL
     */
    public final static String UPDATE_URL = "https://android.3pzs.com/app_update1/android4.apk";

    /**
     * 主目录
     */
    public static String DIRECTORY = null;

    /**
     * 临时目录
     */
    public static String TEMP_DIRECTORY = null;

    /**
     * API.会话ID
     */
    public static String API_SESSIONID = null;

    /**
     * 用户令牌
     */
    private static String USER_TOKEN = null;

    /**
     * 设置用户令牌
     *
     * @param userToken
     */
    public static void setUserToken(String userToken) {
        setValue("userToken", userToken);
        USER_TOKEN = userToken;
    }

//    /**
//     * 取得用户令牌
//     */
//    public static String getUserToken() {
//        if (USER_TOKEN != null) return USER_TOKEN;
//        USER_TOKEN = getValue("TOKEN", null);
//        return USER_TOKEN != null ? USER_TOKEN : "";
//    }

    /**
     * 屏幕宽度
     */
    private static int SCREEN_WIDTH = 0;

    public static int getScreenWidth() {
        if (SCREEN_WIDTH > 0) return SCREEN_WIDTH;
        SCREEN_WIDTH = DemoApplication.getContext().getResources().getDisplayMetrics().widthPixels;
        return SCREEN_WIDTH;
    }

    /**
     * 屏幕高度
     */
    private static int SCREEN_HEIGHT = 0;

    public static int getScreenHeight() {
        if (SCREEN_HEIGHT > 0) return SCREEN_HEIGHT;
        SCREEN_HEIGHT = DemoApplication.getContext().getResources().getDisplayMetrics().heightPixels;
        return SCREEN_HEIGHT;
    }

    /**
     * 设置配置值(String)
     *
     * @param key
     * @param value
     */
    public static void setValue(String key, String value) {

        synchronized (lock) {
            SharedPreferences sp = DemoApplication.getInstance().getSharedPreferences("b7app", DemoApplication.getContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value);
            editor.commit();
        }

    }

    /**
     * 设置配置信息(int)
     *
     * @param key
     * @param value
     */
    public static void setValue(String key, int value) {

        synchronized (lock) {
            SharedPreferences sp = DemoApplication.getInstance().getSharedPreferences("b7app", DemoApplication.getContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(key, value);
            editor.commit();
        }

    }

    /**
     * 设置配置信息(float)
     *
     * @param key
     * @param value
     */
    public static void setValue(String key, float value) {

        synchronized (lock) {
            SharedPreferences sp = DemoApplication.getInstance().getSharedPreferences("b7app", DemoApplication.getContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat(key, value);
            editor.commit();
        }

    }

//    /**
//     * 取得配置值(String)
//     *
//     * @param key
//     * @param defValue
//     * @return String
//     */
//    public static String getValue(String key, String defValue) {
//
//        String ret = null;
//
//        synchronized (lock) {
////            SharedPreferences sp = DemoApplication.getInstance().getSharedPreferences("b7app", DemoApplication.getContext().MODE_PRIVATE);
//            ret=ConfigUtil.getInstance(DemoApplication.getContext()).getString(Constance.TOKEN);
//        }
//
//        //return
//        return ret;
//    }

    /**
     * 取得配置值(int)
     *
     * @param key
     * @param defValue
     * @return int
     */
    public static int getValue(String key, int defValue) {

        int ret = defValue;

        synchronized (lock) {
            SharedPreferences sp = DemoApplication.getInstance().getSharedPreferences("b7app", DemoApplication.getContext().MODE_PRIVATE);
            ret = sp.getInt(key, defValue);
        }

        //return
        return ret;
    }

    /**
     * 取得配置值(float)
     *
     * @param key
     * @param defValue
     * @return float
     */
    public static float getValue(String key, float defValue) {

        float ret = defValue;

        synchronized (lock) {
            SharedPreferences sp = DemoApplication.getInstance().getSharedPreferences("b7app", DemoApplication.getContext().MODE_PRIVATE);
            ret = sp.getFloat(key, defValue);
        }

        //return
        return ret;
    }


}
