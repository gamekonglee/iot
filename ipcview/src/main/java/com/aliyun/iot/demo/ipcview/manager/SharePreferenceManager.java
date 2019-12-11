package com.aliyun.iot.demo.ipcview.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.aliyun.iot.demo.ipcview.R;

import java.util.LinkedList;
import java.util.List;

/**
 *
 *
 * ！！！注意！！！
 * 注意本示例代码主要用于演示部分视频业务接口以及对应的效果
 * 代码中涉及的交互，UI以及代码框架请自行设计，示例代码仅供参考，稳定性请客户自行保证。
 *
 *
 * @author azad
 */
public class SharePreferenceManager {

    private SharePreferenceManager() {
    }

    private static class SharePreferenceManagerHolder {
        private final static SharePreferenceManager manager = new SharePreferenceManager();
    }

    public static SharePreferenceManager getInstance() {
        return SharePreferenceManagerHolder.manager;
    }

    private List<SharedPreferences.OnSharedPreferenceChangeListener> listenerList = new LinkedList<>();
    List<OnCallSetListener> setListenerList = new LinkedList<>();

    public interface OnCallSetListener {
        void onCallSet(String key);
    }

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Context context;

    public void init(Context context) {
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        editor = settings.edit();
        this.context = context;
    }

    public void registerListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);
            settings.registerOnSharedPreferenceChangeListener(listener);
        }
    }

    public void unRegisterListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        if (listenerList.contains(listener)) {
            listenerList.remove(listener);
            settings.unregisterOnSharedPreferenceChangeListener(listener);
        }
    }

    public void registerOnCallSetListener(OnCallSetListener listener) {
        if (!setListenerList.contains(listener)) {
            setListenerList.add(listener);
        }
    }

    public void unRegisterOnCallSetListener(OnCallSetListener listener) {
        if (setListenerList.contains(listener)) {
            setListenerList.remove(listener);
        }
    }

    private void notifyCalledSet(String key) {
        if (setListenerList != null && setListenerList.size() > 0) {
            for (OnCallSetListener tmp : setListenerList) {
                tmp.onCallSet(key);
            }
        }
    }

    /**
     * 麦克风开关 MicSwitch 0：关闭 	1：开启
     */
    public boolean getMicSwitch() {
        return settings.getBoolean(context.getString(R.string.mic_switch_key), true);
    }

    public void setMicSwitch(boolean micSwitch) {
        editor.putBoolean(context.getString(R.string.mic_switch_key), micSwitch).commit();
        notifyCalledSet(context.getString(R.string.mic_switch_key));
    }

    /**
     * 扬声器开关 SpeakerSwitch 0：关闭 	1：开启
     */
    public boolean getSpeakerSwitch() {
        return settings.getBoolean(context.getString(R.string.speaker_switch_key), true);
    }

    public void setSpeakerSwitch(boolean speakerSwitch) {
        editor.putBoolean(context.getString(R.string.speaker_switch_key), speakerSwitch).commit();
        notifyCalledSet(context.getString(R.string.speaker_switch_key));
    }

    /**
     * 状态灯开关  StatusLightSwitch 0：关闭	1：开启
     */
    public boolean getStatusLightSwitch() {
        return settings.getBoolean(context.getString(R.string.status_light_switch_key), true);
    }

    public void setStatusLightSwitch(boolean statusLightSwitch) {
        editor.putBoolean(context.getString(R.string.status_light_switch_key), statusLightSwitch).commit();
        notifyCalledSet(context.getString(R.string.status_light_switch_key));
    }

    /**
     * 日夜模式 DayNightMode 0：白天模式	1：夜晚模式	2：自动模式
     */
    public int getDayNightMode() {
        return Integer.valueOf(settings.getString(context.getString(R.string.day_night_mode_key), "0"));
    }

    public void setDayNightMode(int dayNightMode) {
        editor.putString(context.getString(R.string.day_night_mode_key), dayNightMode+"").commit();
        notifyCalledSet(context.getString(R.string.day_night_mode_key));
    }

    /**
     * 主码流视频质量 StreamVideoQuality 0：流畅	1：标清	2：高清
     */
    public int getStreamVideoQuality() {
        return Integer.valueOf(settings.getString(context.getString(R.string.stream_video_quality_key), "0"));
    }

    public void setStreamVideoQuality(int streamVideoQuality) {
        editor.putString(context.getString(R.string.stream_video_quality_key), streamVideoQuality+"").commit();
        notifyCalledSet(context.getString(R.string.stream_video_quality_key));
    }

    /**
     * 辅码流视频质量 SubStreamVideoQuality 0：流畅	1：标清	2：高清
     */
    public int getSubStreamVideoQuality() {
        return Integer.valueOf(settings.getString(context.getString(R.string.subStream_video_quality_key), "0"));
    }

    public void setSubStreamVideoQuality(int subStreamVideoQuality) {
        editor.putString(context.getString(R.string.subStream_video_quality_key), subStreamVideoQuality+"").commit();
        notifyCalledSet(context.getString(R.string.subStream_video_quality_key));
    }

    /**
     * 画面翻转状态 ImageFlipState  0：正常状态	1：翻转状态
     */
    public int getImageFlip() {
        return Integer.valueOf(settings.getString(context.getString(R.string.image_flip_status_key), "0"));
    }

    public void setImageFlip(int imageFlip) {
        editor.putString(context.getString(R.string.image_flip_status_key), imageFlip+"").commit();
        notifyCalledSet(context.getString(R.string.image_flip_status_key));
    }

    /**
     * 视频加密开关 EncryptSwitch  0：关闭	1：开启
     */
    public boolean getEncryptSwitch() {
        return settings.getBoolean(context.getString(R.string.encrypt_switch_key), true);
    }

    public void setEncryptSwitch(boolean encryptSwitch) {
        editor.putBoolean(context.getString(R.string.encrypt_switch_key), encryptSwitch).commit();
        notifyCalledSet(context.getString(R.string.encrypt_switch_key));
    }

    public boolean getForceIFrameSwitch() {
        return settings.getBoolean(context.getString(R.string.force_iframe_key), true);
    }

    public void setForceIFrameSwitch(boolean forceIFrame) {
        editor.putBoolean(context.getString(R.string.force_iframe_key), forceIFrame).commit();
    }

    /**
     * 报警开关 AlarmSwitch  0：关闭	1：开启
     */
    public boolean getAlarmSwitch() {
        return settings.getBoolean(context.getString(R.string.alarm_switch_key), true);
    }

    public void setAlarmSwitch(boolean alarmSwitch) {
        editor.putBoolean(context.getString(R.string.alarm_switch_key), alarmSwitch).commit();
        notifyCalledSet(context.getString(R.string.alarm_switch_key));
    }

    /**
     * 移动侦测灵敏度 MotionDetectSensitivity  0：关闭	1：最低档	2：低档	3：中档	4：高档	5：最高档
     */
    public int getMotionDetectSensitivity() {
        return Integer.valueOf(settings.getString(context.getString(R.string.motion_detect_sensitivity_key), "0"));
    }

    public void setMotionDetectSensitivity(int motionDetectSensitivity) {
        editor.putString(context.getString(R.string.motion_detect_sensitivity_key), motionDetectSensitivity+"").commit();
        notifyCalledSet(context.getString(R.string.motion_detect_sensitivity_key));
    }

    /**
     * 声音侦测灵敏度 VoiceDetectionSensitivity  0：关闭	1：最低档	2：低档	3：中档	4：高档	5：最高档
     */
    public int getVoiceDetectSensitivity() {
        return Integer.valueOf(settings.getString(context.getString(R.string.voice_detect_sensitivity_key), "0"));
    }

    public void setVoiceDetectSensitivity(int voiceDetectSensitivity) {
        editor.putString(context.getString(R.string.voice_detect_sensitivity_key), voiceDetectSensitivity+"").commit();
        notifyCalledSet(context.getString(R.string.voice_detect_sensitivity_key));
    }

    /**
     * 报警频率 AlarmFrequencyLevel  0：低频	1：中频	2：高频
     */
    public int getAlarmFrequencyLevel() {
        return Integer.valueOf(settings.getString(context.getString(R.string.alarm_frequency_level_key), "0"));
    }

    public void setAlarmFrequencyLevel(int alarmFrequencyLevel) {
        editor.putString(context.getString(R.string.alarm_frequency_level_key), alarmFrequencyLevel+"").commit();
        notifyCalledSet(context.getString(R.string.alarm_frequency_level_key));
    }

    /**
     * 存储介质状态 storageStatus 0：未插卡	1：正常	2：未格式化	3：正在格式化
     */
    public int getStorageStatus() {
        return Integer.valueOf(settings.getString(context.getString(R.string.storage_status_key), "0"));
    }

    public void setStorageStatus(int storageStatus) {
        editor.putString(context.getString(R.string.storage_status_key), storageStatus+"").commit();
        notifyCalledSet(context.getString(R.string.storage_status_key));
    }

    /**
     * 总存储空间  StorageTotalCapacity  0~2^31-1	2	0.01	MB（兆字节）
     */
    public float getStorageTotalCapacity() {
        return settings.getFloat(context.getString(R.string.storage_total_capacity_key), 0.0f);
    }

    public void setStorageTotalCapacity(float storageTotalCapacity) {
        editor.putFloat(context.getString(R.string.storage_total_capacity_key), storageTotalCapacity).commit();
        notifyCalledSet(context.getString(R.string.storage_total_capacity_key));
    }

    /**
     * 剩余存储空间  StorageRemainCapacity  0~2^31-1	2	0.01	MB（兆字节）
     */
    public float getStorageRemainingCapacity() {
        return settings.getFloat(context.getString(R.string.storage_remain_capacity_key), 0.0f);
    }

    public void setStorageRemainingCapacity(float storageRemainingCapacity) {
        editor.putFloat(context.getString(R.string.storage_remain_capacity_key), storageRemainingCapacity).commit();
        notifyCalledSet(context.getString(R.string.storage_remain_capacity_key));
    }

    /**
     * 存储介质录像模式  StorageRecordMode  0：不录像	1：事件录像	2：全天录像
     */
    public int getStorageRecordMode() {
        return Integer.valueOf(settings.getString(context.getString(R.string.storage_record_mode_key), "0"));
    }

    public void setStorageRecordMode(int storageRecordMode) {
        editor.putString(context.getString(R.string.storage_record_mode_key), storageRecordMode+"").commit();
        notifyCalledSet(context.getString(R.string.storage_record_mode_key));
    }


}
