package com.aliyun.iot.demo.ipcview.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import com.aliyun.iot.demo.ipcview.R;
import com.aliyun.iot.demo.ipcview.beans.TimeSectionForPlan;

import java.util.List;

/**
 * 设置时间的弹窗
 *
 *
 * ！！！注意！！！
 * 注意本示例代码主要用于演示部分视频业务接口以及对应的效果
 * 代码中涉及的交互，UI以及代码框架请自行设计，示例代码仅供参考，稳定性请客户自行保证。
 *
 *
 * @author azad
 */
public class TimeSettingDialog {

    private TimeSettingDialog(){}

    private static class TimeSettingDialogHolder{
        private static TimeSettingDialog dialog = new TimeSettingDialog();
    }

    public static TimeSettingDialog getInstance(){
        return TimeSettingDialogHolder.dialog;
    }

    @SuppressLint("NewApi")
    public void openDialog(Context context, List<TimeSectionForPlan> timeLst, int position, DataCallBack callBack){
        View view = LayoutInflater.from(context).inflate(R.layout.time_picker_layout, null);
        final TimePicker startPicker = view.findViewById(R.id.start_picker);
        final TimePicker endPicker = view.findViewById(R.id.end_picker);
        startPicker.setIs24HourView(true);
        endPicker.setIs24HourView(true);
        int startHour = timeLst.get(position).getBegin()/3600;
        int startMinute = timeLst.get(position).getBegin()/60%60;
        int endHour = timeLst.get(position).getEnd()/3600;
        int endMinute = timeLst.get(position).getEnd()/60%60;
        startPicker.setHour(startHour);
        endPicker.setHour(endHour);
        startPicker.setMinute(startMinute);
        endPicker.setMinute(endMinute);
        AlertDialog.Builder builder = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(context.getString(R.string.ipc_dialog_select_time));
        builder.setView(view);
        builder.setPositiveButton(R.string.ipc_confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                startPicker.clearFocus();
                endPicker.clearFocus();
                try{
                    int startTime = 0;
                    int endTime = 0;
                    if(Build.VERSION.SDK_INT < 23){
                        startTime = startPicker.getCurrentHour()*3600 + startPicker.getCurrentMinute()*60;
                        endTime = endPicker.getCurrentHour()*3600 + endPicker.getCurrentMinute()*60;
                    }else{
                        startTime = startPicker.getHour()*3600 + startPicker.getMinute()*60;
                        endTime = endPicker.getHour()*3600 + endPicker.getMinute()*60;
                    }

                    timeLst.get(position).setBegin(startTime);
                    timeLst.get(position).setEnd(endTime);
                    callBack.onDataChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton(R.string.ipc_cancle, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public interface DataCallBack{
        void onDataChanged();
    }
}
