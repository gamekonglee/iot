package com.aliyun.iotx.linkvisual.linkvisualapi.bean;

/**
 * 图片信息.
 * @author azad
 */
public class DevPictureFile {
    private String iotId;
    private Long picCreateTime;
    private String picId;

    public String getIotId() {
        return iotId;
    }

    public void setIotId(String iotId) {
        this.iotId = iotId;
    }

    public Long getPicCreateTime() {
        return picCreateTime;
    }

    public void setPicCreateTime(Long picCreateTime) {
        this.picCreateTime = picCreateTime;
    }

    public String getPicId() {
        return picId;
    }

    public void setPicId(String picId) {
        this.picId = picId;
    }
}
