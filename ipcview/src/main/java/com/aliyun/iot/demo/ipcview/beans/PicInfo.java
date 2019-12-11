package com.aliyun.iot.demo.ipcview.beans;

/**
 * 照片信息
 * @author azad
 */
public class PicInfo {
    private String iotId;
    private String thumbUrl;
    private String pictureId;
    private String pictureUrl;
    private String pictureTime;

    public String getIotId() {
        return iotId;
    }

    public void setIotId(String iotId) {
        this.iotId = iotId;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getPictureTime() {
        return pictureTime;
    }

    public void setPictureTime(String pictureTime) {
        this.pictureTime = pictureTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        PicInfo info = (PicInfo)o;

        if (iotId != null ? !iotId.equals(info.iotId) : info.iotId != null) { return false; }
        if (thumbUrl != null ? !thumbUrl.equals(info.thumbUrl) : info.thumbUrl != null) { return false; }
        if (pictureId != null ? !pictureId.equals(info.pictureId) : info.pictureId != null) { return false; }
        if (pictureUrl != null ? !pictureUrl.equals(info.pictureUrl) : info.pictureUrl != null) { return false; }
        return pictureTime != null ? pictureTime.equals(info.pictureTime) : info.pictureTime == null;
    }

    @Override
    public int hashCode() {
        int result = iotId != null ? iotId.hashCode() : 0;
        result = 31 * result + (thumbUrl != null ? thumbUrl.hashCode() : 0);
        result = 31 * result + (pictureId != null ? pictureId.hashCode() : 0);
        result = 31 * result + (pictureUrl != null ? pictureUrl.hashCode() : 0);
        result = 31 * result + (pictureTime != null ? pictureTime.hashCode() : 0);
        return result;
    }


    private boolean stringIsSame(String strOne, String strTwo){
        if(strOne == null && strTwo == null){
            return true;
        } else if(strOne != null && strTwo != null){
            if(strOne.equals(strTwo)){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

}
