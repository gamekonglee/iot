package com.aliyun.iot.demo.ipcview.manager;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;
import com.aliyun.iot.demo.ipcview.constants.Constants;
import com.aliyun.iotx.linkvisual.IPCManager;

import java.util.Map;

/**
 * 查询修改物模型的处理类
 *
 *
 * ！！！注意！！！
 * 注意本示例代码主要用于演示部分视频业务接口以及对应的效果
 * 代码中涉及的交互，UI以及代码框架请自行设计，示例代码仅供参考，稳定性请客户自行保证。
 *
 *
 * @author azad
 */
public class SettingsCtrl {

    private SettingsCtrl() {
    }

    private static class SettingsCtrlHolder {
        public final static SettingsCtrl INSTANCE = new SettingsCtrl();
    }

    public static SettingsCtrl getInstance() {
        return SettingsCtrlHolder.INSTANCE;
    }

    /**
     * 获取设备的物模型属性.
     *
     * @param iotId 设备id
     */
    public void getProperties(String iotId) {
        IPCManager.getInstance().getDevice(iotId).getProperties(new IPanelCallback() {
            @Override
            public void onComplete(boolean b, Object o) {
                if (b) {
                    if (o != null && !"".equals(String.valueOf(o))) {
                        JSONObject jsonObject = JSONObject.parseObject(String.valueOf(o));
                        if (jsonObject.containsKey("code")) {
                            int code = jsonObject.getInteger("code");
                            if (code != 200) {
                                return;
                            }
                        }
                        if (jsonObject.containsKey("data")) {
                            try {
                                JSONObject data = jsonObject.getJSONObject("data");
                                JSONObject tmp;
                                boolean booleanValue;
                                int intValue;
                                float floatValue;
                                if (data.containsKey(Constants.MIC_SWITCH_MODEL_NAME)) {
                                    tmp = data.getJSONObject(Constants.MIC_SWITCH_MODEL_NAME);
                                    if (tmp.containsKey("value")) {
                                        booleanValue = tmp.getInteger("value") == 1;
                                        //异或判断本地和云是否一致，不一致则修改本地
                                        if (booleanValue ^ SharePreferenceManager.getInstance().getMicSwitch()) {
                                            SharePreferenceManager.getInstance().setMicSwitch(booleanValue);
                                        }
                                    }
                                }
                                if (data.containsKey(Constants.SPEAKER_SWITCH_MODEL_NAME)) {
                                    tmp = data.getJSONObject(Constants.SPEAKER_SWITCH_MODEL_NAME);
                                    if (tmp.containsKey("value")) {
                                        booleanValue = tmp.getInteger("value") == 1;
                                        //异或判断本地和云是否一致，不一致则修改本地
                                        if (booleanValue ^ SharePreferenceManager.getInstance().getSpeakerSwitch()) {
                                            SharePreferenceManager.getInstance().setSpeakerSwitch(booleanValue);
                                        }
                                    }
                                }
                                if (data.containsKey(Constants.STATUS_LIGHT_SWITCH_MODEL_NAME)) {
                                    tmp = data.getJSONObject(Constants.STATUS_LIGHT_SWITCH_MODEL_NAME);
                                    if (tmp.containsKey("value")) {
                                        booleanValue = tmp.getInteger("value") == 1;
                                        //异或判断本地和云是否一致，不一致则修改本地
                                        if (booleanValue ^ SharePreferenceManager.getInstance().getStatusLightSwitch()) {
                                            SharePreferenceManager.getInstance().setStatusLightSwitch(booleanValue);
                                        }
                                    }
                                }
                                if (data.containsKey(Constants.DAY_NIGHT_MODE_MODEL_NAME)) {
                                    tmp = data.getJSONObject(Constants.DAY_NIGHT_MODE_MODEL_NAME);
                                    if (tmp.containsKey("value")) {
                                        intValue = tmp.getInteger("value");
                                        if (intValue != SharePreferenceManager.getInstance().getDayNightMode()) {
                                            SharePreferenceManager.getInstance().setDayNightMode(intValue);
                                        }
                                    }
                                }
                                if (data.containsKey(Constants.STREAM_VIDEO_QUALITY_MODEL_NAME)) {
                                    tmp = data.getJSONObject(Constants.STREAM_VIDEO_QUALITY_MODEL_NAME);
                                    if (tmp.containsKey("value")) {
                                        intValue = tmp.getInteger("value");
                                        if (intValue != SharePreferenceManager.getInstance().getStreamVideoQuality()) {
                                            SharePreferenceManager.getInstance().setStreamVideoQuality(intValue);
                                        }
                                    }
                                }
                                if (data.containsKey(Constants.SUBSTREAM_VIDEO_QUALITY_MODEL_NAME)) {
                                    tmp = data.getJSONObject(Constants.SUBSTREAM_VIDEO_QUALITY_MODEL_NAME);
                                    if (tmp.containsKey("value")) {
                                        intValue = tmp.getInteger("value");
                                        if (intValue != SharePreferenceManager.getInstance().getSubStreamVideoQuality()) {
                                            SharePreferenceManager.getInstance().setSubStreamVideoQuality(intValue);
                                        }
                                    }
                                }
                                if (data.containsKey(Constants.IMAGE_FLIP_STATE_MODEL_NAME)) {
                                    tmp = data.getJSONObject(Constants.IMAGE_FLIP_STATE_MODEL_NAME);
                                    if (tmp.containsKey("value")) {
                                        intValue = tmp.getInteger("value");
                                        if (intValue != SharePreferenceManager.getInstance().getImageFlip()) {
                                            SharePreferenceManager.getInstance().setImageFlip(intValue);
                                        }
                                    }
                                }
                                if (data.containsKey(Constants.ENCRYPT_SWITCH_MODEL_NAME)) {
                                    tmp = data.getJSONObject(Constants.ENCRYPT_SWITCH_MODEL_NAME);
                                    if (tmp.containsKey("value")) {
                                        booleanValue = tmp.getInteger("value") == 1;
                                        //异或判断本地和云是否一致，不一致则修改本地
                                        if (booleanValue ^ SharePreferenceManager.getInstance().getEncryptSwitch()) {
                                            SharePreferenceManager.getInstance().setEncryptSwitch(booleanValue);
                                        }
                                    }
                                }
                                if (data.containsKey(Constants.ALARM_SWITCH_MODEL_NAME)) {
                                    tmp = data.getJSONObject(Constants.ALARM_SWITCH_MODEL_NAME);
                                    if (tmp.containsKey("value")) {
                                        booleanValue = tmp.getInteger("value") == 1;
                                        //异或判断本地和云是否一致，不一致则修改本地
                                        if (booleanValue ^ SharePreferenceManager.getInstance().getAlarmSwitch()) {
                                            SharePreferenceManager.getInstance().setAlarmSwitch(booleanValue);
                                        }
                                    }
                                }
                                if (data.containsKey(Constants.MOTION_DETECT_SENSITIVITY_MODEL_NAME)) {
                                    tmp = data.getJSONObject(Constants.MOTION_DETECT_SENSITIVITY_MODEL_NAME);
                                    if (tmp.containsKey("value")) {
                                        intValue = tmp.getInteger("value");
                                        if (intValue != SharePreferenceManager.getInstance().getMotionDetectSensitivity()) {
                                            SharePreferenceManager.getInstance().setMotionDetectSensitivity(intValue);
                                        }
                                    }
                                }
                                if (data.containsKey(Constants.VOICE_DETECT_SENSITIVITY_MODEL_NAME)) {
                                    tmp = data.getJSONObject(Constants.VOICE_DETECT_SENSITIVITY_MODEL_NAME);
                                    if (tmp.containsKey("value")) {
                                        intValue = tmp.getInteger("value");
                                        if (intValue != SharePreferenceManager.getInstance().getVoiceDetectSensitivity()) {
                                            SharePreferenceManager.getInstance().setVoiceDetectSensitivity(intValue);
                                        }
                                    }
                                }
                                if (data.containsKey(Constants.ALARM_FREQUENCY_LEVEL_MODEL_NAME)) {
                                    tmp = data.getJSONObject(Constants.ALARM_FREQUENCY_LEVEL_MODEL_NAME);
                                    if (tmp.containsKey("value")) {
                                        intValue = tmp.getInteger("value");
                                        if (intValue != SharePreferenceManager.getInstance().getAlarmFrequencyLevel()) {
                                            SharePreferenceManager.getInstance().setAlarmFrequencyLevel(intValue);
                                        }
                                    }
                                }
                                if (data.containsKey(Constants.STORAGE_STATUS_MODEL_NAME)) {
                                    tmp = data.getJSONObject(Constants.STORAGE_STATUS_MODEL_NAME);
                                    if (tmp.containsKey("value")) {
                                        intValue = tmp.getInteger("value");
                                        if (intValue != SharePreferenceManager.getInstance().getStorageStatus()) {
                                            SharePreferenceManager.getInstance().setStorageStatus(intValue);
                                        }
                                    }
                                }
                                if (data.containsKey(Constants.STORAGE_TOTAL_CAPACITY_MODEL_NAME)) {
                                    tmp = data.getJSONObject(Constants.STORAGE_TOTAL_CAPACITY_MODEL_NAME);
                                    if (tmp.containsKey("value")) {
                                        floatValue = tmp.getFloatValue("value");
                                        if (floatValue != SharePreferenceManager.getInstance().getStorageTotalCapacity()) {
                                            SharePreferenceManager.getInstance().setStorageTotalCapacity(floatValue);
                                        }
                                    }
                                }
                                if (data.containsKey(Constants.STORAGE_REMAIN_CAPACITY_MODEL_NAME)) {
                                    tmp = data.getJSONObject(Constants.STORAGE_REMAIN_CAPACITY_MODEL_NAME);
                                    if (tmp.containsKey("value")) {
                                        floatValue = tmp.getFloatValue("value");
                                        if (floatValue != SharePreferenceManager.getInstance().getStorageRemainingCapacity()) {
                                            SharePreferenceManager.getInstance().setStorageRemainingCapacity(floatValue);
                                        }
                                    }
                                }
                                if (data.containsKey(Constants.STORAGE_RECORD_MODE_MODEL_NAME)) {
                                    tmp = data.getJSONObject(Constants.STORAGE_RECORD_MODE_MODEL_NAME);
                                    if (tmp.containsKey("value")) {
                                        intValue = tmp.getInteger("value");
                                        if (intValue != SharePreferenceManager.getInstance().getStorageRecordMode()) {
                                            SharePreferenceManager.getInstance().setStorageRecordMode(intValue);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            }
        });

    }

    public void updateSettings(String iotId, Map<String, Object> param) {

        IPCManager.getInstance().getDevice(iotId).setProperties(param, new IPanelCallback() {
            @Override
            public void onComplete(boolean b, Object o) {
                if (b) {
                    if (o != null && !"".equals(String.valueOf(o))) {
                        JSONObject jsonObject = JSONObject.parseObject(String.valueOf(o));
                        if (jsonObject.containsKey("code")) {
                            int code = jsonObject.getInteger("code");
                            if (code != 200) {
                                return;
                            } else {
                                getProperties(iotId);
                            }
                        }
                    }
                }
            }
        });
    }
}
