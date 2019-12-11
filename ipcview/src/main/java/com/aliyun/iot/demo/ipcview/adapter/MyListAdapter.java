package com.aliyun.iot.demo.ipcview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aliyun.iot.demo.ipcview.R;
import com.aliyun.iot.demo.ipcview.beans.TimeSectionForPlan;

import java.util.List;

/**
 *
 *
 *
 * ！！！注意！！！
 * 注意本示例代码主要用于演示部分视频业务接口以及对应的效果
 * 代码中涉及的交互，UI以及代码框架请自行设计，示例代码仅供参考，稳定性请客户自行保证。
 *
 * @author azad
 */
public class MyListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<TimeSectionForPlan> infoList;

    public MyListAdapter(Context context, List<TimeSectionForPlan> list) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        infoList = list;
    }

    @Override
    public int getCount() {
        return infoList.size();
    }

    @Override
    public Object getItem(int position) {
        return infoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.time_item_layout, parent, false);
            holder = new ViewHolder();

            holder.tv_day = (TextView) convertView.findViewById(R.id.tv_day);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TimeSectionForPlan info = infoList.get(position);
        holder.tv_day.setText(changeDay(info.getDayOfWeek()));
        holder.tv_time.setText(changeTime(info.getBegin(),info.getEnd()));

        return convertView;
    }

    private String changeDay(int day){
        switch (day){
            case 0:
                return mContext.getString(R.string.ipc_plan_sunday);
            case 1:
                return mContext.getString(R.string.ipc_plan_monday);
            case 2:
                return mContext.getString(R.string.ipc_plan_tuesday);
            case 3:
                return mContext.getString(R.string.ipc_plan_wednesday);
            case 4:
                return mContext.getString(R.string.ipc_plan_thursday);
            case 5:
                return mContext.getString(R.string.ipc_plan_friday);
            case 6:
                return mContext.getString(R.string.ipc_plan_saturday);
            default:
                return mContext.getString(R.string.ipc_plan_unknow);

        }
    }

    private String changeTime(int begin, int end){
        if(begin > end || begin < 0 || begin > 86399 || end > 86399){
            return mContext.getString(R.string.ipc_plan_invalid_time);
        }
        return formatTime(begin)+" - "+formatTime(end);
    }

    public String formatTime(int seconds){
        StringBuilder time = new StringBuilder();
        int min = 0;
        int hour = 0;
        if (seconds >= 60) {
            min = seconds / 60;
            seconds = seconds % 60;
        }
        if (min >= 60) {
            hour = min / 60;
            min = min % 60;
        }
        //拼接
        if (hour < 10){
            time.append("0");
        }
        time.append(hour);
        time.append(":");
        if (min < 10){
            time.append("0");
        }
        time.append(min);
        return time.toString();
    }



    private class ViewHolder {
        TextView tv_day;
        TextView tv_time;
    }
}
