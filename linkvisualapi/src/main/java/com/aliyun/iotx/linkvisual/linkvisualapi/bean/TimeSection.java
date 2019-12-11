package com.aliyun.iotx.linkvisual.linkvisualapi.bean;

public class TimeSection implements Comparable<TimeSection> {
    private Integer dayOfWeek;//周日到周六，0代表周日，6代表周六
    private int begin;//每天开始时间，单位秒，范围0-86399
    private int end;//每天结束时间，单位秒，范围0-86399

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
    public int compareTo(TimeSection timeSection) {
        return this.getDayOfWeek().compareTo(timeSection.getDayOfWeek());
    }
}
