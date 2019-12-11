package com.aliyun.iot.demo.ipcview.beans;

import java.util.List;

/**
 * 时间模板
 * @author azad
 */
public class TimeTemplateDTO {
    public TimeTemplateDTO(){}
    private int isAllDay;
    private int isDefault;
    private String name;
    private List<TimeSectionForPlan> timeSectionList;


    public int getIsAllDay() {
        return isAllDay;
    }

    public void setIsAllDay(int isAllDay) {
        this.isAllDay = isAllDay;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TimeSectionForPlan> getTimeSectionList() {
        return timeSectionList;
    }

    public void setTimeSectionList(List<TimeSectionForPlan> timeSectionList) {
        this.timeSectionList = timeSectionList;
    }
}
