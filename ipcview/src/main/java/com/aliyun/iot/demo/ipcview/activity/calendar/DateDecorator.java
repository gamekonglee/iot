package com.aliyun.iot.demo.ipcview.activity.calendar;

import com.savvi.rangedatepicker.CalendarCellDecorator;
import com.savvi.rangedatepicker.CalendarCellView;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DateDecorator implements CalendarCellDecorator {

    private ConcurrentMap<String, char[]> dataFilter;
    private final Calendar calendar;

    private boolean defaultClickable;

    public DateDecorator() {
        dataFilter = new ConcurrentHashMap<>(12);
        calendar = Calendar.getInstance();
    }


    @Override
    public void decorate(CalendarCellView calendarCellView, Date date) {
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        String curYearMonth = mergeIntYearMonthToStr(year, month);

        boolean showDot;
        char[] days = dataFilter.get(curYearMonth);
        if (defaultClickable) {
            showDot = true;
        } else if (days != null &&
                days.length > day) {
            showDot = '1' == days[day];
        } else {
            showDot = false;
        }

        calendarCellView.setDeactivated(!showDot);
        if (calendarCellView.isToday()) {
            calendarCellView.setClickable(true);
        } else {
            calendarCellView.setClickable(showDot);
        }

    }

    public void add(String yearMonth, char[] days) {
        dataFilter.put(yearMonth, days);
    }

    /**
     * @param year
     * @param month 0,表示1月
     * @return
     */
    public String mergeIntYearMonthToStr(int year, int month) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(year);
        if (month + 1 < 10) {
            buffer.append("0");
        }
        buffer.append(month + 1);
        return buffer.toString();
    }

    public void setDefaultClickable(boolean defaultClickable) {
        this.defaultClickable = defaultClickable;
    }
}
