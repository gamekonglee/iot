package com.aliyun.iotx.linkvisual.linkvisualapi.constants;

/**
 * 服务端API的path.
 * @author azad
 */
public class APIConstants {

    /**
     * 触发设备抓图
     */
    public static final String API_PATH_CAPTURE = "/vision/customer/picture/trigger";

    /**
     * 配置事件录像计划.
     */
    public static final String API_PATH_EVENT_RECORD_PLAN_SET = "/vision/customer/eventrecord/plan/set";

    /**
     * 更新事件录像计划.
     */
    public static final String API_PATH_EVENT_RECORD_PLAN_UPDATE = "/vision/customer/eventrecord/plan/update";

    /**
     * 删除事件录像计划.
     */
    public static final String API_PATH_EVENT_RECORD_PLAN_DELETE = "/vision/customer/eventrecord/plan/delete";

    /**
     * 获取事件录像计划详情.
     */
    public static final String API_PATH_EVENT_RECORD_PLAN_GET = "/vision/customer/eventrecord/plan/getbyid";

    /**
     * 查询事件录像计划列表.
     */
    public static final String API_PATH_EVENT_RECORD_PLAN_QUERY = "/vision/customer/eventrecord/plan/query";

    /**
     * 根据设备ID查询事件录像计划.
     */
    public static final String API_PATH_DEV_EVENT_RECORD_PLAN_GET = "/vision/customer/eventrecord/plan/getbyiotid";

    /**
     * 设备关联事件录像计划.
     */
    public static final String API_PATH_DEV_EVENT_RECORD_PLAN_ADD = "/vision/customer/eventrecord/plan/device/bind";


    /**
     * 删除事件录像计划与设备关系.
     */
    public static final String API_PATH_DEV_EVENT_RECORD_PLAN_DELETE = "/vision/customer/eventrecord/plan/unbind";


    /**
     * 查询事件联动录像计划所关联的设备列表.
     */
    public static final String API_PATH_DEV_EVENT_RECORD_PLAN_QUERY_DEVS = "/vision/customer/eventrecord/bind/device/query";

    /**
     * 查询事件列表.
     */
    public static final String API_PATH_EVENT_LIST_QUERY = "/vision/customer/event/query";

    /**
     * 批量获取图片地址.
     */
    public static final String API_PATH_PICTURE_QUERY_FILE_LIST = "/vision/customer/picture/querybytime";

    /**
     * 删除图片.
     */
    public static final String API_PATH_PICTURE_DELETE_FILE = "/vision/customer/picture/device/delete";

    /**
     * 根据主动抓图ID获取图片信息.
     */
    public static final String API_PATH_PICTURE_GET_BY_ID = "/vision/customer/picture/querybyids";

    /**
     * 批量删除图片.
     */
    public static final String API_PATH_PICTURE_DELETE_FILE_BATCH = "/vision/customer/picture/batchdelete";

    /**
     * 查询录像列表.
     */
    public static final String API_PATH_VIDEO_LIST_QUERY = "/vision/customer/record/query";

    /**
     * 查询月录像.
     */
    public static final String API_PATH_VIDEO_MONTH_QUERY = "/vision/customer/monthrecord/query";

    /**
     * 批量删除录像.
     */
    public static final String API_PATH_RECORD_DELETE_FILE_BATCH = "/vision/customer/record/batchdeleteh";

    /**
     * 查询赠送的云存储套餐详情.
     */
    public static final String API_PATH_PRESENTED_CLOUD_STORAGE_GET = "/vision/customer/cloudstorage/presented/get";

    /**
     * 领取赠送的云存储套餐.
     */
    public static final String API_PATH_PRESENTED_CLOUD_STORAGE_CONSUME = "/vision/customer/cloudstorage/presented/consume";



    /**
     * 配置录像计划.
     */
    public static final String API_PATH_RECORD_PLAN_SET = "/vision/customer/record/plan/set";

    /**
     * 更新录像计划.
     */
    public static final String API_PATH_RECORD_PLAN_UPDATE = "/vision/customer/record/plan/update";

    /**
     * 删除录像计划.
     */
    public static final String API_PATH_RECORD_PLAN_DELETE = "/vision/customer/record/plan/delete";

    /**
     * 获取录像计划详情.
     */
    public static final String API_PATH_RECORD_PLAN_GET = "/vision/customer/record/plan/get";

    /**
     * 查询录像计划列表.
     */
    public static final String API_PATH_RECORD_PLAN_QUERY = "/vision/customer/record/plan/query";

    /**
     * 根据设备ID查询录像计划.
     */
    public static final String API_PATH_DEV_RECORD_PLAN_GET = "/vision/customer/record/plan/getbyiotid";

    /**
     * 设备关联录像计划.
     */
    public static final String API_PATH_DEV_RECORD_PLAN_ADD = "/vision/customer/record/plan/bind";

    /**
     * 删除录像计划与设备关系.
     */
    public static final String API_PATH_DEV_RECORD_PLAN_DELETE = "/vision/customer/record/plan/unbind";

    /**
     * 查询联动录像计划所关联的设备列表.
     */
    public static final String API_PATH_DEV_RECORD_PLAN_QUERY_DEVS = "/vision/customer/record/plan/bind/device/query";



}
