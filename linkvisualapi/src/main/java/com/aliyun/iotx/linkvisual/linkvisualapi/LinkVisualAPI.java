package com.aliyun.iotx.linkvisual.linkvisualapi;

import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iotx.linkvisual.linkvisualapi.bean.DevPictureFile;
import com.aliyun.iotx.linkvisual.linkvisualapi.bean.TimeSection;
import com.aliyun.iotx.linkvisual.linkvisualapi.constants.APIConstants;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 视频服务端API.
 * 详情请查看视频服务的文档
 * @author azad
 */
public class LinkVisualAPI {

    private String version = "";

    private LinkVisualAPI() {
    }

    private static class LinkVisualAPIHolder {
        private static LinkVisualAPI linkVisionAPI = new LinkVisualAPI();
    }

    public static LinkVisualAPI getInstance() {
        return LinkVisualAPIHolder.linkVisionAPI;
    }

    public void init(String version) {
        this.version = version;
    }


    public void sendRequest(Map<String, Object> param, String path, IoTCallback callback) {
        IoTRequest request = new IoTRequestBuilder()
            .setScheme(Scheme.HTTPS)
            .setHost("api.link.aliyun.com")
            .setPath(path)
            .setApiVersion(version)
            .setAuthType("iotAuth")
            .setParams(param)
            .build();
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, callback);
    }


    /**
     * 触发设备抓图.
     *
     * @param iotId    设备的id
     * @param callback 接收处理返回的回调
     */
    public void capture(String iotId, IoTCallback callback) {
        Map<String, Object> param = new HashMap<>();
        param.put("iotId", iotId);
        sendRequest(param, APIConstants.API_PATH_CAPTURE, callback);
    }

    /**
     * 配置事件录像计划
     *
     * @param name              计划名称
     * @param eventTypeList     事件列表
     * @param preRecordDuration 预录时间
     * @param recordDuration    录像时间（总时间，包含预录部分）
     * @param isAllDay          是否全天
     * @param timeSectionsList  非全天时时间信息
     * @param callback          接收处理返回的回调
     */
    public void setEventRecordPlan(String name, List<Integer> eventTypeList, int preRecordDuration, int recordDuration,
                                     boolean isAllDay, List<TimeSection> timeSectionsList, IoTCallback callback) {
        Map<String, Object> param = new HashMap<>();
        param.put("name", name);
        param.put("eventTypeList", eventTypeList);
        param.put("preRecordDuration", preRecordDuration);
        param.put("recordDuration", recordDuration);
        if (isAllDay) {
            param.put("allDay", 1);
        } else {
            param.put("allDay", 0);
        }
        List<Map<String, Object>> lst = new LinkedList<>();
        Map<String, Object> map;
        for (TimeSection ts : timeSectionsList) {
            map = new HashMap<>();
            map.put("dayOfWeek", ts.getDayOfWeek());//周日到周六，0代表周日，6代表周六
            map.put("begin", ts.getBegin());//每天开始时间，单位秒，范围0-86399
            map.put("end", ts.getEnd());//每天结束时间，单位秒，范围0-86399
            lst.add(map);
        }
        param.put("timeSectionList", lst);
        sendRequest(param, APIConstants.API_PATH_EVENT_RECORD_PLAN_SET, callback);
    }

    /**
     * 更新事件录像计划
     *
     * @param planId            事件录像计划ID
     * @param name              计划名称
     * @param eventTypeList     事件列表
     * @param preRecordDuration 预录时间
     * @param recordDuration    录像时间
     * @param isAllDay          是否是全天
     * @param timeSectionsList  非全天时时间信息
     * @param callback          接收处理返回的回调
     */
    public void updateEventRecordPlan(String planId, String name,  List<Integer> eventTypeList, int preRecordDuration,
                                        int recordDuration, boolean isAllDay, List<TimeSection> timeSectionsList, IoTCallback callback) {
        Map<String, Object> param = new HashMap<>();
        param.put("planId", planId);
        param.put("name", name);
        param.put("eventTypeList", eventTypeList);
        param.put("preRecordDuration", preRecordDuration);
        param.put("recordDuration", recordDuration);
        if (isAllDay) {
            param.put("allDay", 1);
        } else {
            param.put("allDay", 0);
        }
        List<Map<String, Object>> lst = new LinkedList<>();
        Map<String, Object> map;
        for (TimeSection ts : timeSectionsList) {
            map = new HashMap<>();
            map.put("dayOfWeek", ts.getDayOfWeek());
            map.put("begin", ts.getBegin());
            map.put("end", ts.getEnd());
            lst.add(map);
        }
        param.put("timeSectionList", lst);
        sendRequest(param, APIConstants.API_PATH_EVENT_RECORD_PLAN_UPDATE, callback);
    }

    /**
     * 删除事件录像计划
     *
     * @param planId   事件录像计划ID
     * @param callback 接收处理返回的回调
     */
    public void deleteEventRecordPlan(String planId, IoTCallback callback) {
        Map<String, Object> param = new HashMap<>();
        param.put("planId", planId);
        sendRequest(param, APIConstants.API_PATH_EVENT_RECORD_PLAN_DELETE, callback);
    }

    /**
     * 获取事件录像计划详情
     *
     * @param planId   事件录像计划ID
     * @param callback 接收处理返回的回调
     */
    public void getEventRecordPlan(String planId, IoTCallback callback) {
        Map<String, Object> param = new HashMap<>();
        param.put("planId", planId);
        sendRequest(param, APIConstants.API_PATH_EVENT_RECORD_PLAN_GET, callback);
    }

    /**
     * 查询事件录像计划列表
     *
     * @param pageStart 分页开始页数  从0开始
     * @param pageSize  分页大小  最大值是50
     * @param callback  接收处理返回的回调
     */
    public void queryEventRecordPlan(int pageStart, int pageSize, IoTCallback callback) {
        Map<String, Object> param = new HashMap<>();
        param.put("pageStart", pageStart);
        param.put("pageSize", pageSize);
        sendRequest(param, APIConstants.API_PATH_EVENT_RECORD_PLAN_QUERY, callback);
    }

    /**
     * 根据设备ID查询事件录像计划
     *
     * @param iotId      设备ID
     * @param streamType 码流信息 0主码流 1辅码流
     * @param callback   接收处理返回的回调
     */
    public void getEventRecordPlan2Dev(String iotId, int streamType, IoTCallback callback) {
        Map<String, Object> param = new HashMap<>();
        param.put("iotId", iotId);
        param.put("streamType", streamType);
        sendRequest(param, APIConstants.API_PATH_DEV_EVENT_RECORD_PLAN_GET, callback);
    }

    /**
     * 设备关联事件录像计划.
     *
     * @param iotId      设备ID
     * @param planId     事件录像计划ID
     * @param streamType 码流类型(主码流0，辅码流1)
     * @param callback   接收处理返回的回调
     */
    public void addEventRecordPlan2Dev(String iotId, String planId, int streamType, IoTCallback callback) {
        Map<String, Object> param = new HashMap<>();
        param.put("iotId", iotId);
        param.put("planId", planId);
        param.put("streamType", streamType);
        sendRequest(param, APIConstants.API_PATH_DEV_EVENT_RECORD_PLAN_ADD, callback);
    }


    /**
     * 删除事件录像计划与设备关系.
     *
     * @param iotId      设备ID
     * @param streamType 码流类型(主码流0，辅码流1)
     * @param callback   接收处理返回的回调
     */
    public void deleteEventRecordPlan2Dev(String iotId, int streamType, IoTCallback callback) {
        Map<String, Object> param = new HashMap<>();
        param.put("iotId", iotId);
        param.put("streamType", streamType);
        sendRequest(param, APIConstants.API_PATH_DEV_EVENT_RECORD_PLAN_DELETE, callback);
    }

    /**
     * 查询事件联动录像计划所关联的设备列表.
     *
     * @param planId    事件录像计划id
     * @param pageStart 起始页数，从0开始
     * @param pageSize  分页大小
     * @param callback  接收处理返回的回调
     */
    public void queryEventRecordPlanBindDeviceListForCustomer(String planId, int pageStart, int pageSize, IoTCallback callback) {
        Map<String, Object> param = new HashMap<>();
        param.put("planId", planId);
        param.put("pageStart", pageStart);
        param.put("pageSize", pageSize);
        sendRequest(param, APIConstants.API_PATH_DEV_EVENT_RECORD_PLAN_QUERY_DEVS, callback);
    }

    /**
     * 查询事件列表
     *
     * @param iotId     设备ID
     * @param beginTime 开始时间 单位s 时间戳
     * @param endTime   结束时间 单位s 时间戳
     * @param eventType 事件类型 移动侦测:1 内部抓图事件:51
     * @param pageStart 每页开始位置
     * @param pageSize  每页个数
     * @param callback  接收处理返回的回调
     */
    public void queryEventLst(String iotId, long beginTime, long endTime, Integer eventType, int pageStart,
                                int pageSize, IoTCallback callback) {
        Map<String, Object> param = new HashMap<>();
        param.put("iotId", iotId);
        param.put("beginTime", beginTime);
        param.put("endTime", endTime);
        if (eventType != null) {
            param.put("eventType", eventType);
        }
        param.put("pageStart", pageStart);
        param.put("pageSize", pageSize);
        sendRequest(param, APIConstants.API_PATH_EVENT_LIST_QUERY, callback);
    }


    /**
     * 查询设备照片列表
     *
     * @param iotId     设备的id
     * @param startTime 开始时间 1970年1月1日开始的毫秒数
     * @param endTime   结束时间 1970年1月1日开始的毫秒数
     * @param pageStart 图片开始 从0开始
     * @param pageSize  图片数量
     * @param type      图片类型 0缩略图 1原图 2全部
     * @param source    图片来源 0全部 1报警 2主动抓图 3 其他
     * @param callback  接收处理返回的回调
     */

    public void queryDevPictureFileList(String iotId, long startTime, long endTime, int pageStart, int pageSize,
                                          int type, int source, IoTCallback callback) {
        Map<String, Object> param = new HashMap<>(7);
        param.put("iotId", iotId);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        param.put("pageStart", pageStart);
        param.put("pageSize", pageSize);
        param.put("type", type);
        param.put("source", source);
        sendRequest(param, APIConstants.API_PATH_PICTURE_QUERY_FILE_LIST, callback);
    }

    /**
     * 删除图片.
     *
     * @param iotId         设备的id
     * @param picId         图片ID
     * @param picCreateTime 图片生成时间
     * @param callback      接收处理返回的回调
     */
    public void deleteDevPictureFile(String iotId, String picId, long picCreateTime, IoTCallback callback) {
        Map<String, Object> param = new HashMap<>();
        param.put("iotId", iotId);
        param.put("picId", picId);
        param.put("picCreateTime", picCreateTime);
        sendRequest(param, APIConstants.API_PATH_PICTURE_DELETE_FILE, callback);
    }

    /**
     * 根据图片ID列表获取图片地址
     *
     * @param iotId         设备的id
     * @param pictureIdList 图片ID列表
     * @param type          图片类型 0全部 1原图 2缩率图
     * @param callback      接收处理返回的回调
     */
    public void getDevPictureFilesById(String iotId, List<String> pictureIdList, int type, IoTCallback callback) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("iotId", iotId);
        param.put("pictureIdList", pictureIdList);
        param.put("type", type);
        sendRequest(param, APIConstants.API_PATH_PICTURE_GET_BY_ID, callback);
    }

    /**
     * 批量删除图片.
     *
     * @param iotId         设备ID
     * @param pictureIdList 图片ID列表
     * @param callback      接收处理返回的回调
     */
    public void batchDeleteDevPictureFile(String iotId, List<String> pictureIdList, IoTCallback callback) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("iotId", iotId);
        param.put("pictureIdList", pictureIdList);
        sendRequest(param, APIConstants.API_PATH_PICTURE_DELETE_FILE_BATCH, callback);
    }

    /**
     * 查询录像列表
     *
     * @param iotId      设备ID
     * @param streamType 主码流：0 辅码流：1
     * @param beginTime  开始时间 单位s 时间戳
     * @param endTime    结束时间 单位s 时间戳
     * @param recordType 计划录像:0 报警录像:1 主动录像:2
     * @param pageStart  每页开始位置
     * @param pageSize   每页个数
     * @param callback   接收处理返回的回调
     */
    public void queryVideoLst(String iotId, int streamType, int beginTime, int endTime, Integer recordType, int pageStart,
                                int pageSize, IoTCallback callback) {
        Map<String, Object> param = new HashMap<>();
        param.put("iotId", iotId);
        param.put("streamType", streamType);
        param.put("beginTime", beginTime);
        param.put("endTime", endTime);
        if(recordType!=null){
            param.put("recordType", recordType);
        }
        param.put("pageStart", pageStart);
        param.put("pageSize", pageSize);
        sendRequest(param, APIConstants.API_PATH_VIDEO_LIST_QUERY, callback);
    }

    /**
     * 查询月录像.
     *
     * @param iotId    设备ID
     * @param month    格式为yyyyMM,比如201806
     * @param callback 接收处理返回的回调
     */
    public void queryMonthVideos(String iotId, String month, IoTCallback callback) {
        Map<String, Object> param = new HashMap<>();
        param.put("iotId", iotId);
        param.put("month", month);
        sendRequest(param, APIConstants.API_PATH_VIDEO_MONTH_QUERY, callback);
    }

}
