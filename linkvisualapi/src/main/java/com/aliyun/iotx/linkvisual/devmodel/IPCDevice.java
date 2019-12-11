package com.aliyun.iotx.linkvisual.devmodel;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.aliyun.alink.linksdk.tmp.TmpSdk;
import com.aliyun.alink.linksdk.tmp.device.panel.PanelDevice;
import com.aliyun.alink.linksdk.tmp.device.panel.data.PanelMethodExtraData;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;
import com.aliyun.alink.linksdk.tmp.utils.TmpEnum;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iotx.linkvisual.devmodel.bean.InvokeServiceRequest;
import com.aliyun.iotx.linkvisual.devmodel.constants.TMPConstants;
import com.aliyun.iotx.linkvisual.linkvisualapi.ILinkVisualAPI2Dev;
import com.aliyun.iotx.linkvisual.linkvisualapi.LinkVisualAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 真正的设备.
 * @author azad
 */
public class IPCDevice implements ILinkVisualAPI2Dev {

    private PanelDevice panelDevice;
    private String iotId = "";
    private IPanelCallback panelCallback;

    /**
     * 获取TMP原始设备.
     *
     * @return TMP原始设备
     */
    public PanelDevice getPanelDevice() {
        return panelDevice;
    }

    /**
     * 获取当前设备的iotId.
     *
     * @return 当前设备的iotId
     */
    public String getIotId() {
        return iotId;
    }

    /**
     * 判断当前设备是否有效.
     *
     * @return 返回设备有效性
     */
    public boolean isValid() {
        return panelDevice != null && panelDevice.isInit();
    }

    public IPCDevice(Context context, String iotId) {
        this.iotId = iotId;
        IPCDeviceInit(context, iotId, true);
    }

    public IPCDevice(Context context, String iotId, IPanelCallback callback) {
        this.iotId = iotId;
        panelCallback = callback;
        IPCDeviceInit(context, iotId, true);
    }

    /**
     * 重新初始化设备
     *
     * @param context 上下文
     */
    public void reInit(Context context) {
        IPCDeviceInit(context, iotId, true);
    }

    /**
     * 重新初始化设备
     *
     * @param context  上下文
     * @param callback 接收处理结果的回调
     */
    public void reInit(Context context, IPanelCallback callback) {
        panelCallback = callback;
        IPCDeviceInit(context, iotId, true);
    }

    private void IPCDeviceInit(final Context context, final String iotId, final boolean oneMoreTime) {
        try{
            panelDevice = new PanelDevice(iotId, new PanelMethodExtraData(TmpEnum.ChannelStrategy.CLOUD_CHANNEL_ONLY));
            panelDevice.init(context, new IPanelCallback() {
                @Override
                public void onComplete(boolean b, Object o) {
                    if (!b) {
                        if (oneMoreTime) {
                            IPCDeviceInit(context, iotId, false);
                        } else {
                            if (panelCallback != null) {
                                panelCallback.onComplete(b, o);
                            }
                        }
                    } else {
                        if (panelCallback != null) {
                            panelCallback.onComplete(b, o);
                        }
                    }
                }
            });
        }catch (Exception e){
            Log.e("IPCDevice","init error     e:"+e.toString());
            Log.e("IPCDevice","init error     TmpSdk.getCloudProxy:"+TmpSdk.getCloudProxy());
        }
    }


    /**
     * 获取设备状态
     */
    public void getStatus() {
        if (isValid()) {
            panelDevice.getStatus(panelCallback);
        } else {
            if (panelCallback != null) {
                panelCallback.onComplete(false, "init is wrong");
            }
        }
    }

    /**
     * 获取设备状态
     *
     * @param callback 接收处理结果的回调
     */
    public void getStatus(IPanelCallback callback) {
        if (isValid()) {
            panelDevice.getStatus(callback);
        } else {
            if (callback != null) {
                callback.onComplete(false, "init is wrong");
            }
        }
    }

    /**
     * 设置设备属性
     *
     * @param params 属性键值对
     */
    public void setProperties(Map<String, Object> params) {
        if (params == null) {
            if (panelCallback != null) {
                panelCallback.onComplete(false, "request is invalid");
            }
        }
        if (isValid()) {
            Map<String, Object> request = new HashMap<>();
            request.put("iotId", iotId);
            request.put("items", params);
            panelDevice.setProperties(JSON.toJSONString(request), panelCallback);
        } else {
            if (panelCallback != null) {
                panelCallback.onComplete(false, "init is wrong");
            }
        }
    }

    /**
     * 设置设备属性
     *
     * @param params   属性键值对
     * @param callback 接收处理结果的回调
     */
    public void setProperties(Map<String, Object> params, IPanelCallback callback) {
        if (params == null) {
            if (callback != null) {
                callback.onComplete(false, "request is invalid");
            }
        }
        if (isValid()) {
            Map<String, Object> request = new HashMap<>();
            request.put("iotId", iotId);
            request.put("items", params);
            panelDevice.setProperties(JSON.toJSONString(request), callback);
        } else {
            if (callback != null) {
                callback.onComplete(false, "init is wrong");
            }
        }
    }

    /**
     * 获取设备属性.
     */
    public void getProperties() {
        if (isValid()) {
            panelDevice.getProperties(panelCallback);
        } else {
            if (panelCallback != null) {
                panelCallback.onComplete(false, "init is wrong");
            }
        }
    }

    /**
     * 获取设备属性.
     *
     * @param callback 接收处理结果的回调
     */
    public void getProperties(IPanelCallback callback) {
        if (isValid()) {
            panelDevice.getProperties(callback);
        } else {
            if (callback != null) {
                callback.onComplete(false, "init is wrong");
            }
        }
    }

    private boolean invokeServiceRequestIsValid(InvokeServiceRequest request) {
        if (request != null) {
            if (request.getIotId() != null && !"".equals(request.getIotId())) {
                if (request.getIdentifier() != null && !"".equals(request.getIdentifier())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 调用TMP服务.
     *
     * @param request 请求参数
     */
    public void invokeService(InvokeServiceRequest request) {
        if (!invokeServiceRequestIsValid(request)) {
            if (panelCallback != null) {
                panelCallback.onComplete(false, "request is invalid");
            }
            return;
        }
        if (isValid()) {
            String paramStr = JSON.toJSONString(request);
            panelDevice.invokeService(paramStr, panelCallback);
        } else {
            if (panelCallback != null) {
                panelCallback.onComplete(false, "init is wrong");
            }
        }
    }

    /**
     * 调用TMP服务.
     *
     * @param request  请求参数
     * @param callback 接收处理结果的回调
     */
    public void invokeService(InvokeServiceRequest request, IPanelCallback callback) {
        if (!invokeServiceRequestIsValid(request)) {
            if (callback != null) {
                callback.onComplete(false, "request is invalid");
            }
            return;
        }
        if (isValid()) {
            String paramStr = JSON.toJSONString(request);
            panelDevice.invokeService(paramStr, callback);
        } else {
            if (callback != null) {
                callback.onComplete(false, "init is wrong");
            }
        }
    }

    /**
     * 进行一次PTZ.
     *
     * @param type  PTZ操作类型 0：左	1：右	2：上	3：下	4：上左	5：上右	6：下左	7：下右	8：放大	9：缩小
     * @param step    PTZ步进量，代表转动幅度：0-10；0最小；10的转动幅度最大
     */
    public void PTZActionControl(int type, int step) {
        InvokeServiceRequest request = new InvokeServiceRequest();
        request.setIotId(iotId);
        request.setIdentifier(TMPConstants.IDENTIFIER_PTZ_ACTION_CONTROL);
        Map<String, Integer> param = new HashMap<>();
        param.put("ActionType", type);
        param.put("Step", step);
        request.setArgs(param);
        invokeService(request);
    }

    /**
     * 进行一次PTZ.
     *
     * @param type     PTZ操作类型 0：左	1：右	2：上	3：下	4：上左	5：上右	6：下左	7：下右	8：放大	9：缩小
     * @param step    PTZ步进量，代表转动幅度：0-10；0最小；10的转动幅度最大
     * @param callback 接收处理结果的回调
     */
    public void PTZActionControl(int type, int step, IPanelCallback callback) {
        InvokeServiceRequest request = new InvokeServiceRequest();
        request.setIotId(iotId);
        request.setIdentifier(TMPConstants.IDENTIFIER_PTZ_ACTION_CONTROL);
        Map<String, Integer> param = new HashMap<>();
        param.put("ActionType", type);
        param.put("Step", step);
        request.setArgs(param);
        invokeService(request, callback);
    }

    /**
     * 开始PTZ.
     *
     * @param type  PTZ操作类型 0：左	1：右	2：上	3：下	4：上左	5：上右	6：下左	7：下右	8：放大	9：缩小
     * @param speed PTZ速度 0：慢速	1：中速	2：快速
     */
    public void startPTZ(int type, int speed) {
        InvokeServiceRequest request = new InvokeServiceRequest();
        request.setIotId(iotId);
        request.setIdentifier(TMPConstants.IDENTIFIER_START_PTZ);
        Map<String, Integer> param = new HashMap<>();
        param.put("ActionType", type);
        param.put("Speed", speed);
        request.setArgs(param);
        invokeService(request);
    }

    /**
     * 开始PTZ.
     *
     * @param type     PTZ操作类型 0：左	1：右	2：上	3：下	4：上左	5：上右	6：下左	7：下右	8：放大	9：缩小
     * @param speed    PTZ速度 0：慢速	1：中速	2：快速
     * @param callback 接收处理结果的回调
     */
    public void startPTZ(int type, int speed, IPanelCallback callback) {
        InvokeServiceRequest request = new InvokeServiceRequest();
        request.setIotId(iotId);
        request.setIdentifier(TMPConstants.IDENTIFIER_START_PTZ);
        Map<String, Integer> param = new HashMap<>();
        param.put("ActionType", type);
        param.put("Speed", speed);
        request.setArgs(param);
        invokeService(request, callback);
    }

    /**
     * 停止PTZ.
     */
    public void stopPTZ() {
        InvokeServiceRequest request = new InvokeServiceRequest();
        request.setIotId(iotId);
        request.setIdentifier(TMPConstants.IDENTIFIER_STOP_PTZ);
        Map<String, Object> param = new HashMap<>();
        request.setArgs(param);
        invokeService(request);
    }

    /**
     * 停止PTZ.
     *
     * @param callback 接收处理结果的回调
     */
    public void stopPTZ(IPanelCallback callback) {
        InvokeServiceRequest request = new InvokeServiceRequest();
        request.setIotId(iotId);
        request.setIdentifier(TMPConstants.IDENTIFIER_STOP_PTZ);
        Map<String, Object> param = new HashMap<>();
        request.setArgs(param);
        invokeService(request, callback);
    }

    /**
     * 设备重启.
     */
    public void reboot() {
        InvokeServiceRequest request = new InvokeServiceRequest();
        request.setIotId(iotId);
        request.setIdentifier(TMPConstants.IDENTIFIER_REBOOT);
        Map<String, Object> param = new HashMap<>();
        request.setArgs(param);
        invokeService(request);
    }

    /**
     * 设备重启.
     *
     * @param callback 接收处理结果的回调
     */
    public void reboot(IPanelCallback callback) {
        InvokeServiceRequest request = new InvokeServiceRequest();
        request.setIotId(iotId);
        request.setIdentifier(TMPConstants.IDENTIFIER_REBOOT);
        Map<String, Object> param = new HashMap<>();
        request.setArgs(param);
        invokeService(request, callback);
    }

    /**
     * 请求卡录像列表.
     *
     * @param beginTime 开始时间 单位为秒
     * @param endTime   结束时间 单位为秒
     * @param querySize 请求卡录像个数
     * @param type      录像类型 0：所有类型 1：主动录像  2：报警录像 3: 计划录像
     */
    public void queryCardRecordList(long beginTime, long endTime, int querySize, int type) {
        InvokeServiceRequest request = new InvokeServiceRequest();
        request.setIotId(iotId);
        request.setIdentifier(TMPConstants.IDENTIFIER_QUERY_RECORD_LIST);
        Map<String, Object> param = new HashMap<>();
        param.put("BeginTime", beginTime);
        param.put("EndTime", endTime);
        param.put("QuerySize", querySize);
        param.put("Type", type);
        request.setArgs(param);
        invokeService(request);
    }

    /**
     * 请求卡录像列表.
     *
     * @param beginTime 开始时间 单位为秒
     * @param endTime   结束时间 单位为秒
     * @param querySize 请求卡录像个数
     * @param type      录像类型 0：所有类型 1：主动录像  2：报警录像 3: 计划录像
     * @param callback  接收处理结果的回调
     */
    public void queryCardRecordList(long beginTime, long endTime, int querySize, int type, IPanelCallback callback) {
        InvokeServiceRequest request = new InvokeServiceRequest();
        request.setIotId(iotId);
        request.setIdentifier(TMPConstants.IDENTIFIER_QUERY_RECORD_LIST);
        Map<String, Object> param = new HashMap<>();
        param.put("BeginTime", beginTime);
        param.put("EndTime", endTime);
        param.put("QuerySize", querySize);
        param.put("Type", type);
        request.setArgs(param);
        invokeService(request, callback);
    }

    /**
     * 请求卡录像时间列表.
     *
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @param querySize 请求卡录像个数
     * @param type      录像类型 0：所有类型 1：主动录像  2：报警录像 3: 计划录像
     * @param callback  接收处理结果的回调
     */
    public void queryCardTimeList(long beginTime, long endTime, int querySize, int type, IPanelCallback callback) {
        InvokeServiceRequest request = new InvokeServiceRequest();
        request.setIotId(iotId);
        request.setIdentifier(TMPConstants.IDENTIFIER_QUERY_RECORD_TIME_LIST);
        Map<String, Object> param = new HashMap<>();
        param.put("BeginTime", beginTime);
        param.put("EndTime", endTime);
        param.put("QuerySize", querySize);
        param.put("Type", type);
        request.setArgs(param);
        invokeService(request, callback);
    }

    public void formatStorageMedium() {
        InvokeServiceRequest request = new InvokeServiceRequest();
        request.setIotId(iotId);
        request.setIdentifier(TMPConstants.IDENTIFIER_FORMAT_STORAGE_MEDIUM);
        Map<String, Object> param = new HashMap<>();
        request.setArgs(param);
        invokeService(request);
    }

    public void formatStorageMedium(IPanelCallback callback) {
        InvokeServiceRequest request = new InvokeServiceRequest();
        request.setIotId(iotId);
        request.setIdentifier(TMPConstants.IDENTIFIER_FORMAT_STORAGE_MEDIUM);
        Map<String, Object> param = new HashMap<>();
        request.setArgs(param);
        invokeService(request, callback);
    }

    //LinkVision Api begin

    @Override
    /**
     * 触发设备抓图.
     *
     * @param callback 接收处理返回的回调
     */
    public void capture(IoTCallback callback) {
        LinkVisualAPI.getInstance().capture(iotId, callback);
    }

    @Override
    /**
     * 根据设备ID查询事件录像计划.
     *
     * @param callback 接收处理返回的回调
     */
    public void getEventRecordPlan2Dev(int streamType, IoTCallback callback) {
        LinkVisualAPI.getInstance().getEventRecordPlan2Dev(iotId, streamType, callback);
    }

    @Override
    /**
     * 设备关联事件录像计划.
     *
     * @param planId     事件录像计划ID
     * @param streamType 码流类型(主码流0，辅码流1)
     * @param callback   接收处理返回的回调
     */
    public void addEventRecordPlan2Dev(String planId, int streamType, IoTCallback callback) {
        LinkVisualAPI.getInstance().addEventRecordPlan2Dev(iotId, planId, streamType, callback);
    }


    @Override
    /**
     * 删除事件录像计划与设备关系.
     *
     * @param streamType 码流类型(主码流0，辅码流1)
     * @param callback   接收处理返回的回调
     */
    public void deleteEventRecordPlan2Dev(int streamType, IoTCallback callback) {
        LinkVisualAPI.getInstance().deleteEventRecordPlan2Dev(iotId, streamType, callback);
    }


    @Override
    /**
     * 查询设备照片列表
     *
     * @param startTime 开始时间 1970年1月1日开始的毫秒数
     * @param endTime   结束时间 1970年1月1日开始的毫秒数
     * @param pageStart 图片开始
     * @param pageSize  图片数量
     * @param type      图片类型 0缩略图 1原图 2全部
     * @param source    图片来源 0全部 1报警 2主动抓图
     * @param callback  接收处理返回的回调
     */
    public void queryDevPictureFileList(long startTime, long endTime, int pageStart, int pageSize, int type, int source,
                                        IoTCallback callback) {
        LinkVisualAPI.getInstance().queryDevPictureFileList(iotId, startTime, endTime, pageStart, pageSize, type,
            source, callback);
    }

    @Override
    /**
     * 删除图片.
     *
     * @param picId         图片ID
     * @param picCreateTime 图片生成时间
     * @param callback      接收处理返回的回调
     */
    public void deleteDevPictureFile(String picId, long picCreateTime, IoTCallback callback) {
        LinkVisualAPI.getInstance().deleteDevPictureFile(iotId, picId, picCreateTime, callback);
    }

    @Override
    /**
     * 根据主动抓图ID获取图片信息.
     *
     * @param captureId 主动抓图ID
     * @param callback  接收处理返回的回调
     */
    public void getDevPictureFileById(List<String> pictureIdList, int type, IoTCallback callback) {
        LinkVisualAPI.getInstance().getDevPictureFilesById(iotId, pictureIdList, type, callback);
    }

    @Override
    /**
     * 查询事件列表.
     *
     * @param beginTime 开始时间 单位s 时间戳
     * @param endTime   结束时间 单位s 时间戳
     * @param eventType 事件类型 移动侦测:1 内部抓图事件:51
     * @param pageStart 每页开始位置
     * @param pageSize  每页个数
     * @param callback  接收处理返回的回调
     */
    public void queryEventLst(long beginTime, long endTime, Integer eventType, int pageStart, int pageSize,
                              IoTCallback callback) {
        LinkVisualAPI.getInstance().queryEventLst(iotId, beginTime, endTime, eventType, pageStart, pageSize, callback);
    }



    @Override
    /**
     * 查询录像列表.
     *
     * @param streamType 码流类型(主码流0，辅码流1)
     * @param beginTime  开始时间 单位s 时间戳
     * @param endTime    结束时间 单位s 时间戳
     * @param pageStart  每页开始位置
     * @param pageSize   每页个数
     * @param callback   接收处理返回的回调
     */
    public void queryVideoLst(int streamType, int beginTime, int endTime, int recordType, int pageStart, int pageSize,
                              IoTCallback callback) {
        LinkVisualAPI.getInstance().queryVideoLst(iotId, streamType, beginTime, endTime, recordType, pageStart,
            pageSize, callback);
    }

    @Override
    /**
     * 查询月录像.
     *
     * @param iotId    设备ID
     * @param month    格式为yyyyMM,比如201806
     * @param callback 接收处理返回的回调
     */
    public void queryMonthVideos(String month, IoTCallback callback) {
        LinkVisualAPI.getInstance().queryMonthVideos(iotId, month, callback);
    }
    //LinkVision Api end
}
