package com.aliyun.iot.demo.ipcview.enums;

/**
 * @author azad
 */

public enum PicRequestTypeEnums {

    /**
     * 图片请求类型
     */
    THUMB(2,"缩略图"),
    ORIG(1,"原图"),
    ALL(0,"全部");


    private int code;
    private String desc;

    PicRequestTypeEnums(int code, String desc) {
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
