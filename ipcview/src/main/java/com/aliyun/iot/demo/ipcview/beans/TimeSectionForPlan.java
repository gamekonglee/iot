package com.aliyun.iot.demo.ipcview.beans;

import androidx.annotation.NonNull;

/**
 * 时间模板单元
 * @author azad
 */
public class TimeSectionForPlan implements Comparable<TimeSectionForPlan>{

    Integer dayOfWeek;
    int begin;
    int end;

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }


    @Override
    public int compareTo(@NonNull TimeSectionForPlan timeSectionForPlan) {
        return this.getDayOfWeek().compareTo(timeSectionForPlan.getDayOfWeek());
    }
}
