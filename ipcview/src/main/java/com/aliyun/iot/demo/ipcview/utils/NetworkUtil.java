package com.aliyun.iot.demo.ipcview.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

public class NetworkUtil {



    /**
     * 获取设备当前网络状态
     * @param context 上下文
     * @return
     */
    public static final NetworkStateEnum getCurrentNetworkState(Context context) {
        NetworkStateEnum newNetworkStateEnum = NetworkStateEnum.NONE;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                newNetworkStateEnum = NetworkStateEnum.WIFI;
            } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
                newNetworkStateEnum = NetworkStateEnum.WIFI;
            } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                newNetworkStateEnum = NetworkStateEnum.MOBILE;
            }
        } else {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            Network[] networks = connMgr.getAllNetworks();

            //通过循环将网络信息逐个取出来
            for (int i = 0; i < networks.length; i++) {
                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    newNetworkStateEnum = NetworkStateEnum.MOBILE;
                }
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    newNetworkStateEnum = NetworkStateEnum.WIFI;
                }
            }
        }
        return newNetworkStateEnum;
    }
}
