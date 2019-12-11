package com.aliyun.iot.demo.ipcview.enums;

/**
 * @author azad
 */

public enum SpeedEnum {

    /**
     * PTZ的速度类型
     */
    SLOW(0,"慢速"),
    MEDIUM(1,"中速"),
    FAST(2,"快速");

    private int code;
    private String desc;

    SpeedEnum(int code, String desc) {
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
