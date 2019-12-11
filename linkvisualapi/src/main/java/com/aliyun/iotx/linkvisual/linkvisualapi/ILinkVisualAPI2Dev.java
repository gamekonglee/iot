package com.aliyun.iotx.linkvisual.linkvisualapi;

import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;

import java.util.List;

public interface ILinkVisualAPI2Dev {

    void capture(IoTCallback callback);

    void getEventRecordPlan2Dev(int streamType,IoTCallback callback);

    void addEventRecordPlan2Dev(String planId, int streamType, IoTCallback callback);

    void deleteEventRecordPlan2Dev(int streamType, IoTCallback callback);


    void queryDevPictureFileList(long startTime, long endTime, int pageStart, int pageSize, int type, int source,
                                 IoTCallback callback);

    void deleteDevPictureFile(String picId, long picCreateTime, IoTCallback callback);

    void getDevPictureFileById(List<String> pictureIdList, int type, IoTCallback callback);

    void queryEventLst(long beginTime, long endTime, Integer eventType, int pageStart, int pageSize,
                       IoTCallback callback);


    void queryVideoLst(int streamType, int beginTime, int endTime, int recordType, int pageStart, int pageSize,
                       IoTCallback callback);

    void queryMonthVideos(String month, IoTCallback callback);


}
