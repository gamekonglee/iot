package com.aliyun.iot.demo.ipcview.beans;

import java.util.List;

/**
 * 录像计划内容
 * @author azad
 */
public class EventRecordPlanResponse {

    private String planId;
    private String name;
    private int preRecordDuration;
    private int recordDuration;
    private int allDay;
    private List<Integer> eventTypeList;
    private List<TimeSectionForPlan> timeSectionList;

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPreRecordDuration() {
        return preRecordDuration;
    }

    public void setPreRecordDuration(int preRecordDuration) {
        this.preRecordDuration = preRecordDuration;
    }

    public int getRecordDuration() {
        return recordDuration;
    }

    public void setRecordDuration(int recordDuration) {
        this.recordDuration = recordDuration;
    }

    public int getAllDay() {
        return allDay;
    }

    public void setAllDay(int allDay) {
        this.allDay = allDay;
    }

    public List<Integer> getEventTypeList() {
        return eventTypeList;
    }

    public void setEventTypeList(List<Integer> eventTypeList) {
        this.eventTypeList = eventTypeList;
    }

    public List<TimeSectionForPlan> getTimeSectionList() {
        return timeSectionList;
    }

    public void setTimeSectionList(
        List<TimeSectionForPlan> timeSectionList) {
        this.timeSectionList = timeSectionList;
    }
}
