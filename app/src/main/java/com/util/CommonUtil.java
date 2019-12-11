package com.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * action : 工具类 开启activity
 *
 * @author : Jun
 * @version : 1.0
 * @date : 2016-11-15
 */
public class CommonUtil {
    public static void startActivity(Context context, Class clzz, boolean flag) {
        Intent intent = new Intent(context, clzz);
        ((Activity) context).startActivity(intent);
        if (flag) {
            ((Activity) context).finish();
        }
    }

    /**
     * @param lv 传入一个ListView 此时就会把ListView高度定死
     */
    public static void setListViewHeightBasedOnChildren(ListView lv) {
        ListAdapter listAdapter = lv.getAdapter();
        int listViewHeight = 0;
        int adaptCount = listAdapter.getCount();

        for (int i = 0; i < adaptCount; i++) {
            View temp = listAdapter.getView(i, null, lv);
            temp.measure(0, 0);
            listViewHeight += temp.getMeasuredHeight();
        }

        for (int i = 0; i < adaptCount - 1; i++) {
            listViewHeight += 10;
        }

        ViewGroup.LayoutParams layoutParams = lv.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = listViewHeight;
        lv.setLayoutParams(layoutParams);
    }

    public static boolean isMobileNO(String number) {
        boolean re = false;
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        if (number.length() == 11) {
            if (number.startsWith("13")) {
                re = true;
            } else if (number.startsWith("14")) {
                re = true;
            } else if (number.startsWith("15")) {
                re = true;
            } else if (number.startsWith("17")) {
                re = true;
            } else if (number.startsWith("18")) {
                re = true;
            }
        }
        return re;
    }

    /**
     * 获取app版本名称
     *
     * @return 版本名称或null
     */
    public static String localVersionName(Context ct) {
        String versionName = null;
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionName = ct.getPackageManager().getPackageInfo(ct.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static boolean isDate(String date) {
        String eL = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
        Pattern p = Pattern.compile(eL);
        Matcher m = p.matcher(date);

        return m.matches();
    }

    public static void saveImage(Bitmap bitmap) {
        File file = new File(AppConfig.TEMP_DIRECTORY + "/head.png");
        //Log.i("320it", file + "");
        try {
            FileOutputStream out = new FileOutputStream(file);
            //fileOutPutStream -> 需要保存文件的位置
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            //全部写出来
            out.flush();
            //断流
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getCompressImage(Bitmap bitmap) {
        Bitmap minBitmap = null;

        try {
            minBitmap = new AppUtils().createBitmap(bitmap, 150);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return minBitmap;
    }

    public static String decodeUnicode(String theString) {

        char aChar;
        if (AppUtils.isEmpty(theString)) {
            return "";
        }
        int len = theString.length();

        StringBuffer outBuffer = new StringBuffer(len);

        for (int x = 0; x < len; ) {

            aChar = theString.charAt(x++);

            if (aChar == '\\') {

                aChar = theString.charAt(x++);

                if (aChar == 'u') {
                    int value = 0;

                    for (int i = 0; i < 4; i++) {

                        aChar = theString.charAt(x++);

                        switch (aChar) {

                            case '0':

                            case '1':

                            case '2':

                            case '3':

                            case '4':

                            case '5':

                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }

                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';

                    else if (aChar == 'n')

                        aChar = '\n';

                    else if (aChar == 'f')

                        aChar = '\f';

                    outBuffer.append(aChar);

                }

            } else

                outBuffer.append(aChar);

        }

        return outBuffer.toString();

    }

    /**
     * 计算岁数
     *
     * @param sDate
     * @return
     */
    public static long getAge(String sDate) {
        long age = 0;
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = df.parse(df.format(new Date(System.currentTimeMillis())));
            Date date2 = df.parse(sDate);
            System.out.println("date1:" + date1.getTime());
            System.out.println("date2:" + date2.getTime());
            age = (date1.getYear() - date2.getYear());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return age;

    }

    public static int getYear() {
        int year = 0;
        try {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            year= Integer.parseInt(formatter.format(curDate)) ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return year;
    }

    private static final double EARTH_RADIUS = 6378137.0;

    /**
     * 获取两地经纬度距离
     *
     * @param longitude1
     * @param latitude1
     * @param longitude2
     * @param latitude2
     * @return
     */
    public static double getDistance(double longitude1, double latitude1,
                                     double longitude2, double latitude2) {
        double Lat1 = rad(latitude1);
        double Lat2 = rad(latitude2);
        double a = Lat1 - Lat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(Lat1) * Math.cos(Lat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000000;
        return s;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 计算时间间隔
     *
     * @param createtime
     * @return
     */
    public static String getInterval(String createtime) { //传入的时间格式必须类似于2012-8-21 17:53:20这样的格式
        String interval = null;

        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date d1 = (Date) sd.parse(createtime, pos);

        //用现在距离1970年的时间间隔new Date().getTime()减去以前的时间距离1970年的时间间隔d1.getTime()得出的就是以前的时间与现在时间的时间间隔
        long time = new Date().getTime() - d1.getTime();// 得出的时间间隔是毫秒

        if (time / 1000 < 10 && time / 1000 >= 0) {
            //如果时间间隔小于10秒则显示“刚刚”time/10得出的时间间隔的单位是秒
            interval = "刚刚";

        } else if (time / 1000 < 60 && time / 1000 > 0) {
            //如果时间间隔小于60秒则显示多少秒前
            int se = (int) ((time % 60000) / 1000);
            interval = se + "秒前";
        } else if (time / 60000 < 60 && time / 60000 > 0) {
            //如果时间间隔小于60分钟则显示多少分钟前
            int m = (int) ((time % 3600000) / 60000);//得出的时间间隔的单位是分钟
            interval = m + "分钟前";

        } else if (time / 3600000 < 24 && time / 3600000 >= 0) {
            //如果时间间隔小于24小时则显示多少小时前
            int h = (int) (time / 3600000);//得出的时间间隔的单位是小时
            interval = h + "小时前";

        } else {
            //大于24小时，则显示正常的时间，但是不显示秒
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            ParsePosition pos2 = new ParsePosition(0);
            Date d2 = (Date) sdf.parse(createtime, pos2);

            interval = sdf.format(d2);
        }
        return interval;
    }

    /**
     * 计算间隔天数
     *
     * @param startDay
     * @param backDay
     * @return
     */
    public static Long getIntervalDay(String startDay, String backDay) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date d1 = (Date) sd.parse(startDay, pos);
        SimpleDateFormat sd2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos2 = new ParsePosition(0);
        Date d2 = (Date) sd2.parse(backDay, pos2);
        return (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24);
    }


    /**
     * 计算间隔天数
     *
     * @param startDay
     * @param backDay
     * @return
     */
    public static Long getIntervalDay02(String startDay, String backDay) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date d1 = (Date) sd.parse(startDay, pos);
        SimpleDateFormat sd2 = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos2 = new ParsePosition(0);
        Date d2 = (Date) sd2.parse(backDay, pos2);
        return (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24);
    }


    /**
     * BASE64 解密
     *
     * @param str
     * @return
     */
    public static String decryptBASE64(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            byte[] encode = str.getBytes("UTF-8");
            // base64 解密
            return new String(Base64.decode(encode, 0, encode.length, Base64.DEFAULT), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 判断需不需要更新app
     *
     * @param versionLocal
     *            app本地版本名称 0.0.0
     * @param versionServer
     *            app服务器存的版本 0.0.0
     * @return true 需要更新；false 不需要更新
     */
    public static boolean isNeedUpdate(String versionLocal, String versionServer)
    {
        if(TextUtils.isEmpty(versionServer)){
            return  false;
        }
        String[] localDigits = versionLocal.split("\\.");
        String[] serverDigits = versionServer.split("\\.");

        int firstLocalDigit = 0;
        int secondLocalDigit = 0;
        int thirdLocalDigit = 0;
        int firstServerDigit = 0;
        int secondServerDigit = 0;
        int thirdServerDigit = 0;
        for (int i = 0; i < localDigits.length; i++)
        {
            if (i == 0)
            {
                firstLocalDigit = Integer.parseInt(localDigits[0]);
            } else if (i == 1)
            {
                secondLocalDigit = Integer.parseInt(localDigits[1]);
            } else if (i == 2)
            {
                thirdLocalDigit = Integer.parseInt(localDigits[2]);
            }
        }

        for (int j = 0; j < serverDigits.length; j++)
        {
            if (j == 0)
            {

                firstServerDigit = Integer.parseInt(serverDigits[0]);
                Log.v("520","firstServerDigit"+firstServerDigit);
            } else if (j == 1)
            {
                secondServerDigit = Integer.parseInt(serverDigits[1]);
            } else if (j == 2)
            {
                thirdServerDigit = Integer.parseInt(serverDigits[2]);
            }
        }

        if (firstLocalDigit < firstServerDigit)
            return true;
        else if (firstLocalDigit == firstServerDigit
                && secondLocalDigit < secondServerDigit)
            return true;
        else if (firstLocalDigit == firstServerDigit
                && secondLocalDigit == secondServerDigit
                && thirdLocalDigit < thirdServerDigit)
            return true;

        return false;
    }


}
