package com.aliyun.iot.demo.ipcview.beans;

import androidx.annotation.NonNull;

import androidx.annotation.NonNull;

/**
 * Created by guoweisong on 2018/8/16.
 */

public class VideoInfo implements Comparable<VideoInfo> {
    public int type;
    public String iotId;
    public String fileName;
    public int fileSize;
    public int streamType;
    public int recordType;
    public String beginTime;
    public String endTime;
    public long dayTime;

    public String eventTime;
    public String eventId;
    public int eventType;
    public String eventPicId;
    public String eventFileName;
    public String eventDesc;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VideoInfo videoInfo = (VideoInfo) o;

        if (type != videoInfo.type) return false;
        if (!iotId.equals(videoInfo.iotId)) return false;
        if (fileName != null ? !fileName.equals(videoInfo.fileName) : videoInfo.fileName != null)
            return false;
        return eventId != null ? eventId.equals(videoInfo.eventId) : videoInfo.eventId == null;
    }

    @Override
    public int hashCode() {
        int result = type;
        result = 31 * result + iotId.hashCode();
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        result = 31 * result + (eventId != null ? eventId.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(@NonNull VideoInfo o) {
        return (-1)*Long.compare(Long.parseLong(this.beginTime), Long.parseLong(o.beginTime));
    }
}
