package com.aliyun.iotx.linkvisual;

import android.content.Context;

import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iotx.linkvisual.devmodel.DevManager;
import com.aliyun.iotx.linkvisual.devmodel.IPCDevice;
import com.aliyun.iotx.linkvisual.linkvisualapi.LinkVisualAPI;
import com.aliyun.iotx.linkvisual.linkvisualapi.bean.DevPictureFile;
import com.aliyun.iotx.linkvisual.linkvisualapi.bean.TimeSection;

import java.util.List;

/**
 * Link vision 服务端业务封装
 * @author azad
 */
public class IPCManager {

    private IPCManager() {
    }

    private static class IPCManagerHolder {
        private static IPCManager ipcManager = new IPCManager();
    }

    public static IPCManager getInstance() {
        return IPCManagerHolder.ipcManager;
    }


    public void init(Context context, String version) {
        DevManager.getDevManager().init(context);
        LinkVisualAPI.getInstance().init(version);
    }


    /**
     * 获取IPC.
     *
     * @param iotId    对应IPC的iotID
     * @param callback 接收结果的回调
     * @return IPC实例
     */
    public IPCDevice getDevice(String iotId, IPanelCallback callback) {
        return DevManager.getDevManager().getIPCDevice(iotId, callback);
    }

    /**
     * 获取IPC.
     *
     * @param iotId 对应IPC的iotID
     * @return IPC实例
     */
    public IPCDevice getDevice(String iotId) {
        return DevManager.getDevManager().getIPCDevice(iotId);
    }

    /**
     * 配置事件录像计划
     *
     * @param name              计划名称
     * @param eventTypeList     事件列表
     * @param preRecordDuration 预录时间
     * @param recordDuration    录像时间
     * @param isAllDay          是否全天
     * @param timeSectionsList  非全天时时间信息
     * @param callback          接收处理返回的回调
     */
    public void setEventRecordPlan(String name, List<Integer> eventTypeList, int preRecordDuration, int recordDuration,
                                   boolean isAllDay, List<TimeSection> timeSectionsList, IoTCallback callback) {
        LinkVisualAPI.getInstance().setEventRecordPlan(name, eventTypeList, preRecordDuration, recordDuration,
        isAllDay, timeSectionsList, callback);
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
        LinkVisualAPI.getInstance().updateEventRecordPlan(planId, name,  eventTypeList, preRecordDuration,
        recordDuration, isAllDay, timeSectionsList, callback);
    }

    /**
     * 删除事件录像计划
     *
     * @param planId   事件录像计划ID
     * @param callback 接收处理返回的回调
     */
    public void deleteEventRecordPlan(String planId, IoTCallback callback) {
        LinkVisualAPI.getInstance().deleteEventRecordPlan(planId, callback);
    }

    /**
     * 获取事件录像计划详情
     *
     * @param planId   事件录像计划ID
     * @param callback 接收处理返回的回调
     */
    public void getEventRecordPlan(String planId, IoTCallback callback) {
        LinkVisualAPI.getInstance().getEventRecordPlan(planId, callback);
    }

    /**
     * 查询事件录像计划列表
     *
     * @param pageStart 分页开始页数
     * @param pageSize  分页大小
     * @param callback  接收处理返回的回调
     */
    public void queryEventRecordPlan(int pageStart, int pageSize, IoTCallback callback) {
        LinkVisualAPI.getInstance().queryEventRecordPlan(pageStart, pageSize, callback);
    }

    /**
     * 批量删除图片.
     *
     * @param iotId         设备ID
     * @param pictureIdList 图片ID列表
     * @param callback 接收处理返回的回调
     */
    public void batchDeleteDevPictureFile(String iotId, List<String> pictureIdList, IoTCallback callback) {
        LinkVisualAPI.getInstance().batchDeleteDevPictureFile(iotId, pictureIdList, callback);
    }
}
