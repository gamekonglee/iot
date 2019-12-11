package com.aliyun.iot.demo.ipcview.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Carson_Ho on 16/10/31.
 */
public class NetWorkStateReceiver extends BroadcastReceiver {
    private NetWorkChangeListener netWorkChangeListener;
    private NetworkStateEnum currentNetworkState;

    public NetWorkStateReceiver(NetWorkChangeListener netWorkChangeListener) {
        this.netWorkChangeListener = netWorkChangeListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkStateEnum newNetworkStateEnum = NetworkUtil.getCurrentNetworkState(context);
        if (netWorkChangeListener == null) {
            return;
        }
        if (currentNetworkState != newNetworkStateEnum) {
            netWorkChangeListener.stateChanged(newNetworkStateEnum);
            currentNetworkState = newNetworkStateEnum;
        }
    }

}
