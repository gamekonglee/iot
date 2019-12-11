package com.aliyun.iot.demo.ipcview.enums;

/**
 * @author azad
 */

public enum ActionTypeEnum {
    /**
     * PTZ的类型
     */
    LEFT(0,"向左"),
    RIGHT(1,"向右"),
    UP(2,"向上"),
    DOWN(3,"向下"),
    UP_LEFT(4,"向上向左"),
    UP_RIGHT(5,"向上向右"),
    DOWN_LEFT(6,"向下向左"),
    DOWN_RIGHT(7,"向下向右"),
    ZOOM_IN(8,"放大"),
    ZOOM_OUT(9,"缩小");

    private int code;
    private String desc;

    ActionTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
