package com.aliyun.iot.ilop.demo.page.ilopmain;

import com.aliyun.alink.business.devicecenter.api.add.DeviceInfo;

/**
 * Created by jiangchao on 18-1-25.
 */

public class FoundDeviceListItem {
    public static final String NEED_CONNECT = "need_connect";
    public static final String NEED_BIND = "need_bind";
    public String deviceName;
    public DeviceInfo deviceInfo;
    public String deviceStatus;
    public String productKey;
}
