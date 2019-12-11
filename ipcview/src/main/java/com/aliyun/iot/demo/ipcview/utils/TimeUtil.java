package com.aliyun.iot.demo.ipcview.utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * ！！！注意！！！
 * 注意本示例代码主要用于演示部分视频业务接口以及对应的效果
 * 代码中涉及的交互，UI以及代码框架请自行设计，示例代码仅供参考，稳定性请客户自行保证。
 *
 * @author azad
 */
public class TimeUtil {

    public static String TimeStamp2Date(String timestampString, String formats) {
        if (StringUtil.isNullOrEmpty(formats)) {
            formats = "yyyy-MM-dd HH:mm:ss";
        }
        Long timestamp = Long.parseLong(timestampString) * 1000;
        String date = new SimpleDateFormat(formats, Locale.CHINA).format(new Date(timestamp));
        return date;
    }

    public static String getFormatDay(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        return format.format(date.getTime());
    }

    public static String getFormatDayWithTime(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss", Locale.CHINA);
        return format.format(date.getTime());
    }

    public static String Date2TimeStamp(String dateStr, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(dateStr).getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取当天00：00点时间戳
     *
     * @return
     */
    public static long getTodayMidnightTimeStamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getMidnightTimeStamp(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        if (timestamp > 0) {
            calendar.setTimeInMillis(timestamp);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }


}
