package com.aliyun.iotx.linkvisual.devmodel;

import android.content.Context;

import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * 设备管理类
 * @author azad
 */
public class DevManager {

    private DevManager() {
    }

    private static class PanelDeviceHolder {
        private static final DevManager devManager = new DevManager();
    }

    public static DevManager getDevManager() {
        return PanelDeviceHolder.devManager;
    }

    private Context context;

    public void init(Context context) {
        this.context = context;
    }


    private Map<String, IPCDevice> deviceList = new HashMap<>();

    public IPCDevice getIPCDevice(String iotId) {
        IPCDevice device;
        if (deviceList != null) {
            if (!deviceList.isEmpty()) {
                if (deviceList.containsKey(iotId)) {
                    device = deviceList.get(iotId);
                    if (!device.isValid()) {
                        deviceList.remove(iotId);
                        device = new IPCDevice(context, iotId);
                        deviceList.put(iotId, device);
                    }
                    return device;
                }
            }
        } else {
            deviceList = new HashMap<>();
        }

        device = new IPCDevice(context, iotId);
        deviceList.put(iotId, device);
        return device;
    }

    public IPCDevice getIPCDevice(String iotId, IPanelCallback callback) {
        IPCDevice device;
        if (deviceList != null) {
            if (!deviceList.isEmpty()) {
                if (deviceList.containsKey(iotId)) {
                    device = deviceList.get(iotId);
                    if (!device.isValid()) {
                        deviceList.remove(iotId);
                        device = new IPCDevice(context, iotId, callback);
                        deviceList.put(iotId, device);
                    }else{
                        if(callback != null){
                            callback.onComplete(true,null);
                        }
                    }
                    return device;
                }
            }
        } else {
            deviceList = new HashMap<>();
        }

        device = new IPCDevice(context, iotId, callback);
        deviceList.put(iotId, device);
        return device;
    }


}
