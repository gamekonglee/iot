package com.aliyun.iot.demo.ipcview.activity.calendar;

import android.view.ContextThemeWrapper;
import android.widget.TextView;

import com.aliyun.iot.demo.ipcview.R;
import com.aliyun.iot.demo.ipcview.utils.ScreenUtil;
import com.savvi.rangedatepicker.CalendarCellView;
import com.savvi.rangedatepicker.DayViewAdapter;

public class CustomeDayViewAdapter implements DayViewAdapter {
    @Override
    public void makeCellView(CalendarCellView parent) {
        TextView itemTv = new TextView(
                new ContextThemeWrapper(parent.getContext(), R.style.CalendarCell_CalendarDate));
        itemTv.setDuplicateParentStateEnabled(true);
        itemTv.setCompoundDrawablePadding(ScreenUtil.convertDp2Px(parent.getContext(), -4));
        parent.addView(itemTv);
        parent.setDayOfMonthTextView(itemTv);
    }
}
