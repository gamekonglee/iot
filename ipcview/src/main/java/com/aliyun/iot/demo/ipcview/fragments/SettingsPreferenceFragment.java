package com.aliyun.iot.demo.ipcview.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.demo.ipcview.R;
import com.aliyun.iot.demo.ipcview.activity.EasyPlanSettingsActivity;
import com.aliyun.iot.demo.ipcview.constants.Constants;
import com.aliyun.iot.demo.ipcview.manager.SettingsCtrl;
import com.aliyun.iot.demo.ipcview.manager.SharePreferenceManager;
import com.aliyun.iotx.linkvisual.IPCManager;
import com.aliyun.iotx.linkvisual.mqtt.ChannelManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Route;

import static android.app.Activity.RESULT_OK;

/**
 * 设置界面
 *
 *
 * ！！！注意！！！
 * 注意本示例代码主要用于演示部分视频业务接口以及对应的效果
 * 代码中涉及的交互，UI以及代码框架请自行设计，示例代码仅供参考，稳定性请客户自行保证。
 *
 *
 * @author azad
 */
public class SettingsPreferenceFragment extends PreferenceFragment {
    private String TAG = this.getClass().getSimpleName();

    private Activity mActivity;
    private String iotId = "";

    private SwitchPreference micSwitch, speakerSwitch, statusLightSwitch,
        encryptSwitch, iFrameSwitch, alarmSwitch;
    private ListPreference dayNightMode, streamVideoQuality,
        subStreamVideoQuality, imageFlip, motionDetectSensitivity,
        voiceDetectSensitivity, alarmFrequencyLevel, storageRecordMode;

    private Preference storageStatus, storageTotalCapacity, storageRemainingCapacity, alarmNotifyPlan,
        eventRecordTimeSettings, storageFormat, unbind, reboot;
    private PreferenceScreen storageScreen;
    private Handler uiHandler;

    private SharePreferenceManager.OnCallSetListener mOnCallListener = new SharePreferenceManager.OnCallSetListener() {
        @Override
        public void onCallSet(String key) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    refreshUI(key);
                }
            });
        }
    };

    public void setIotId(String iotId) {
        this.iotId = iotId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        addPreferencesFromResource(R.xml.settings_preferences);
        uiHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        holdAllChange();
    }

    @Override
    public void onResume() {
        super.onResume();
        //注册物模型属性修改的监听
        SharePreferenceManager.getInstance().registerOnCallSetListener(mOnCallListener);
        //获取物模型属性
        SettingsCtrl.getInstance().getProperties(iotId);
        //监听物模型属性的变化以防止其他途径对物模型属性的修改
        ChannelManager.getInstance().registerListener(iMobileMsgListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharePreferenceManager.getInstance().unRegisterOnCallSetListener(mOnCallListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        ChannelManager.getInstance().unRegisterListener(iMobileMsgListener);
    }

    private ChannelManager.IMobileMsgListener iMobileMsgListener = new ChannelManager.IMobileMsgListener() {
        @Override
        public void onCommand(String topic, String msg) {
            Log.e(TAG, "ChannelManager.IMobileMsgListener    topic:" + topic + "     msg:" + msg);
            if (topic.equals("/thing/properties")) {
                SettingsCtrl.getInstance().getProperties(iotId);
            }
        }
    };

    private void initView() {
        micSwitch = (SwitchPreference)findPreference(getString(R.string.mic_switch_key));
        speakerSwitch = (SwitchPreference)findPreference(getString(R.string.speaker_switch_key));
        statusLightSwitch = (SwitchPreference)findPreference(getString(R.string.status_light_switch_key));
        dayNightMode = (ListPreference)findPreference(getString(R.string.day_night_mode_key));
        streamVideoQuality = (ListPreference)findPreference(getString(R.string.stream_video_quality_key));
        subStreamVideoQuality = (ListPreference)findPreference(getString(R.string.subStream_video_quality_key));
        imageFlip = (ListPreference)findPreference(getString(R.string.image_flip_status_key));
        encryptSwitch = (SwitchPreference)findPreference(getString(R.string.encrypt_switch_key));
        iFrameSwitch = (SwitchPreference)findPreference(getString(R.string.force_iframe_key));

        alarmSwitch = (SwitchPreference)findPreference(getString(R.string.alarm_switch_key));
        motionDetectSensitivity = (ListPreference)findPreference(getString(R.string.motion_detect_sensitivity_key));
        voiceDetectSensitivity = (ListPreference)findPreference(getString(R.string.voice_detect_sensitivity_key));
        alarmFrequencyLevel = (ListPreference)findPreference(getString(R.string.alarm_frequency_level_key));

        storageStatus = findPreference(getString(R.string.storage_status_key));
        storageTotalCapacity = findPreference(getString(R.string.storage_total_capacity_key));
        storageRemainingCapacity = findPreference(getString(R.string.storage_remain_capacity_key));
        storageRecordMode = (ListPreference)findPreference(getString(R.string.storage_record_mode_key));

        eventRecordTimeSettings = findPreference(getString(R.string.event_record_time_settings_key));
        eventRecordTimeSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(mActivity, EasyPlanSettingsActivity.class);
                intent.putExtra("iotId", iotId);
                startActivity(intent);
                return true;
            }
        });

        alarmNotifyPlan = findPreference(getString(R.string.alarm_notify_settings_key));
        alarmNotifyPlan.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.ipc_setting_function_developing, Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }
        });

        storageScreen = (PreferenceScreen)findPreference("card");

        showStorageStatus();
        storageTotalCapacity.setSummary(SharePreferenceManager.getInstance().getStorageTotalCapacity() + " MB");
        storageRemainingCapacity.setSummary(SharePreferenceManager.getInstance().getStorageRemainingCapacity() + " MB");

        storageFormat = findPreference(getString(R.string.storage_format_key));
        storageFormat.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                IPCManager.getInstance().getDevice(iotId).formatStorageMedium(new IPanelCallback() {
                    @Override
                    public void onComplete(boolean b, Object o) {
                        Log.d(TAG, "formatStorageMedium:" + b + "       o:" + (o != null ? String.valueOf(o) : "null"));
                    }
                });
                return true;
            }
        });

        unbind = findPreference(getString(R.string.unbind_key));
        unbind.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showCheckDialog();
                return true;
            }
        });

        reboot = findPreference(getString(R.string.reboot_key));
        reboot.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                IPCManager.getInstance().getDevice(iotId).reboot(new IPanelCallback() {
                    @Override
                    public void onComplete(boolean b, Object o) {
                        Log.d(TAG, "reboot:" + b + "       o:" + (o != null ? String.valueOf(o) : "null"));
                        if(b){
                            showToast(R.string.ipc_setting_reboot_success);
                        }
                    }
                });
                return true;
            }
        });

    }

    private void showStorageStatus() {
        int tips = R.string.ipc_setting_sd_normal;
        switch (SharePreferenceManager.getInstance().getStorageStatus()) {
            case 0:
                tips = R.string.ipc_setting_sd_no;
                storageScreen.setEnabled(false);
                break;
            case 1:
                tips = R.string.ipc_setting_sd_normal;
                storageScreen.setEnabled(true);
                break;
            case 2:
                tips = R.string.ipc_setting_sd_not_format;
                storageScreen.setEnabled(true);
                break;
            case 3:
                tips = R.string.ipc_setting_sd_formatting;
                storageScreen.setEnabled(true);
                break;
            default:
                break;
        }
        storageStatus.setSummary(tips);
        storageScreen.setSummary(tips);
    }

    /**
     * 由于PreferenceFragment里所有按钮都是及时生效的，
     * 而我们设置设备属性都是网络请求，只有收到修改生效后才是设置成功，
     * 所以此处我们需要界面的点击不要直接改变按钮状态，
     * 只有发现设置成功后才改变。
     */
    private void holdAllChange() {
        micSwitch.setOnPreferenceChangeListener(listener);
        speakerSwitch.setOnPreferenceChangeListener(listener);
        statusLightSwitch.setOnPreferenceChangeListener(listener);
        dayNightMode.setOnPreferenceChangeListener(listener);
        streamVideoQuality.setOnPreferenceChangeListener(listener);
        subStreamVideoQuality.setOnPreferenceChangeListener(listener);
        imageFlip.setOnPreferenceChangeListener(listener);
        encryptSwitch.setOnPreferenceChangeListener(listener);

        alarmSwitch.setOnPreferenceChangeListener(listener);
        motionDetectSensitivity.setOnPreferenceChangeListener(listener);
        voiceDetectSensitivity.setOnPreferenceChangeListener(listener);
        alarmFrequencyLevel.setOnPreferenceChangeListener(listener);
        storageRecordMode.setOnPreferenceChangeListener(listener);
    }

    /**
     * 截获所有通过点击的设置变化，然后直接发送请求到云端,然后把这次UI修改抛掉，等待更改成功以后会再查询一下当前属性，根据查询结果修改UI
     * 此处可以加一个加载动画，以防止更改属性和获取属性过慢，导致UI想卡住了一样。
     */
    private Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Map<String, Object> param = new HashMap<>();
            boolean flag = false;

            if (preference.getKey().equals(getString(R.string.mic_switch_key))) {
                param.put(Constants.MIC_SWITCH_MODEL_NAME, (Boolean)newValue ? 1 : 0);
                flag = true;
            } else if (preference.getKey().equals(getString(R.string.speaker_switch_key))) {
                param.put(Constants.SPEAKER_SWITCH_MODEL_NAME, (Boolean)newValue ? 1 : 0);
                flag = true;
            } else if (preference.getKey().equals(getString(R.string.status_light_switch_key))) {
                param.put(Constants.STATUS_LIGHT_SWITCH_MODEL_NAME, (Boolean)newValue ? 1 : 0);
                flag = true;
            } else if (preference.getKey().equals(getString(R.string.day_night_mode_key))) {
                param.put(Constants.DAY_NIGHT_MODE_MODEL_NAME, Integer.parseInt(newValue.toString()));
                flag = true;
            } else if (preference.getKey().equals(getString(R.string.stream_video_quality_key))) {
                param.put(Constants.STREAM_VIDEO_QUALITY_MODEL_NAME, Integer.parseInt(newValue.toString()));
                flag = true;
            } else if (preference.getKey().equals(getString(R.string.subStream_video_quality_key))) {
                param.put(Constants.SUBSTREAM_VIDEO_QUALITY_MODEL_NAME, Integer.parseInt(newValue.toString()));
                flag = true;
            } else if (preference.getKey().equals(getString(R.string.image_flip_status_key))) {
                param.put(Constants.IMAGE_FLIP_STATE_MODEL_NAME, Integer.parseInt(newValue.toString()));
                flag = true;
            } else if (preference.getKey().equals(getString(R.string.encrypt_switch_key))) {
                param.put(Constants.ENCRYPT_SWITCH_MODEL_NAME, (Boolean)newValue ? 1 : 0);
                flag = true;
            } else if (preference.getKey().equals(getString(R.string.alarm_switch_key))) {
                param.put(Constants.ALARM_SWITCH_MODEL_NAME, (Boolean)newValue ? 1 : 0);
                flag = true;
            } else if (preference.getKey().equals(getString(R.string.motion_detect_sensitivity_key))) {
                param.put(Constants.MOTION_DETECT_SENSITIVITY_MODEL_NAME, Integer.parseInt(newValue.toString()));
                flag = true;
            } else if (preference.getKey().equals(getString(R.string.voice_detect_sensitivity_key))) {
                param.put(Constants.VOICE_DETECT_SENSITIVITY_MODEL_NAME, Integer.parseInt(newValue.toString()));
                flag = true;
            } else if (preference.getKey().equals(getString(R.string.alarm_frequency_level_key))) {
                param.put(Constants.ALARM_FREQUENCY_LEVEL_MODEL_NAME, Integer.parseInt(newValue.toString()));
                flag = true;
            } else if (preference.getKey().equals(getString(R.string.storage_record_mode_key))) {
                param.put(Constants.STORAGE_RECORD_MODE_MODEL_NAME, Integer.parseInt(newValue.toString()));
                flag = true;
            }

            if (flag) {
                SettingsCtrl.getInstance().updateSettings(iotId, param);
            }

            return false;
        }
    };

    private void refreshUI(String key) {
        if (key != null && !key.trim().equals("")) {
            if (key.equals(getString(R.string.mic_switch_key))) {
                micSwitch.setChecked(SharePreferenceManager.getInstance().getMicSwitch());
            } else if (key.equals(getString(R.string.speaker_switch_key))) {
                speakerSwitch.setChecked(SharePreferenceManager.getInstance().getSpeakerSwitch());
            } else if (key.equals(getString(R.string.status_light_switch_key))) {
                statusLightSwitch.setChecked(SharePreferenceManager.getInstance().getStatusLightSwitch());
            } else if (key.equals(getString(R.string.day_night_mode_key))) {
                dayNightMode.setValueIndex(SharePreferenceManager.getInstance().getDayNightMode());
            } else if (key.equals(getString(R.string.stream_video_quality_key))) {
                streamVideoQuality.setValueIndex(SharePreferenceManager.getInstance().getStreamVideoQuality());
            } else if (key.equals(getString(R.string.subStream_video_quality_key))) {
                subStreamVideoQuality.setValueIndex(SharePreferenceManager.getInstance().getSubStreamVideoQuality());
            } else if (key.equals(getString(R.string.image_flip_status_key))) {
                imageFlip.setValueIndex(SharePreferenceManager.getInstance().getImageFlip());
            } else if (key.equals(getString(R.string.encrypt_switch_key))) {
                encryptSwitch.setChecked(SharePreferenceManager.getInstance().getEncryptSwitch());
            } else if (key.equals(getString(R.string.alarm_switch_key))) {
                alarmSwitch.setChecked(SharePreferenceManager.getInstance().getAlarmSwitch());
            } else if (key.equals(getString(R.string.motion_detect_sensitivity_key))) {
                motionDetectSensitivity.setValueIndex(
                    SharePreferenceManager.getInstance().getMotionDetectSensitivity());
            } else if (key.equals(getString(R.string.voice_detect_sensitivity_key))) {
                voiceDetectSensitivity.setValueIndex(SharePreferenceManager.getInstance().getVoiceDetectSensitivity());
            } else if (key.equals(getString(R.string.alarm_frequency_level_key))) {
                alarmFrequencyLevel.setValueIndex(SharePreferenceManager.getInstance().getAlarmFrequencyLevel());
            } else if (key.equals(getString(R.string.storage_status_key))) {
                showStorageStatus();
            } else if (key.equals(getString(R.string.storage_total_capacity_key))) {
                storageTotalCapacity.setSummary(SharePreferenceManager.getInstance().getStorageTotalCapacity() + " MB");
            } else if (key.equals(getString(R.string.storage_remain_capacity_key))) {
                storageRemainingCapacity.setSummary(
                    SharePreferenceManager.getInstance().getStorageRemainingCapacity() + " MB");
            } else if (key.equals(getString(R.string.storage_record_mode_key))) {
                storageRecordMode.setValueIndex(SharePreferenceManager.getInstance().getStorageRecordMode());
            }

        }
    }

    private void showCheckDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(R.string.ipc_setting_unbind_dialog_title);
        builder.setNegativeButton(R.string.ipc_cancle, null);
        builder.setPositiveButton(R.string.ipc_setting_unbind, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestUnbind(iotId, new IoTCallback() {
                    @Override
                    public void onFailure(IoTRequest ioTRequest, Exception e) {
                        Log.e(TAG,"解绑失败：e:"+e.toString());
                        showToast(R.string.ipc_setting_unbind_err);
                    }

                    @Override
                    public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                        if(ioTResponse != null){
                            if(ioTResponse.getCode()==200){
                                showToast(R.string.ipc_setting_unbind_success);
                                //FIXME 根据用户自身跳转到对应逻辑，目前逻辑是直接跳转到飞燕DemoApp首页
                                //Router.getInstance().toUrl(getActivity(), "page/ilopmain");
                                Intent intent = new Intent("com.aliyun.iot.aep.demo.action.navigation" , Uri.parse("http://aliyun.iot.aep.demo/page/ilopmain"));

                                List<ResolveInfo> activities = getActivity().getPackageManager().queryIntentActivities(intent, 0);
                                boolean isValid = !activities.isEmpty();
                                if(isValid){
                                    startActivityForResult(intent, RESULT_OK);
                                }


                                getActivity().finish();

                            }else{
                                showToast(R.string.ipc_setting_unbind_err);
                            }
                        }
                    }
                });
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static final String UN_BIND = "/uc/unbindAccountAndDev";
    public static void requestUnbind(String iotId,IoTCallback ioTCallback) {
        Log.d("EqSettingHelp", "_______________" + iotId);
        String apiVersion = "1.0.2";
        IoTRequestBuilder builder = new IoTRequestBuilder()
            .setAuthType("iotAuth")
            .setScheme(Scheme.HTTPS)
            .setPath(UN_BIND)
            .setApiVersion(apiVersion)
            .addParam("iotId", iotId);
        IoTRequest request = builder.build();
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request,ioTCallback );
    }


    private void showToast(String msg){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showToast(int msgRes){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(),msgRes,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
