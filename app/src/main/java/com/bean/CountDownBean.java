package com.bean;

/**
 * Created by gamekonglee on 2018/8/7.
 */

public class CountDownBean {
    int id;
    int pid;
    String iotId;
    String items;
    int month;
    int day;
    int week;
    int hour;
    int minute;
    int status;
    String created_at;
    String updated_at;
    String weeks;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getIotid() {
        return iotId;
    }

    public void setIotid(String iotid) {
        this.iotId = iotid;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

    public CountDownBean(int id, int pid, String iotid, String items, int month, int day, int week, int hour, int minute, int status, String created_at, String updated_at, String weeks) {
        this.id = id;
        this.pid = pid;
        this.iotId = iotid;
        this.items = items;
        this.month = month;
        this.day = day;
        this.week = week;
        this.hour = hour;
        this.minute = minute;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.weeks = weeks;
    }
}
