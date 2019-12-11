package com.example.yzz.sodemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.UUID;

public class DeviceUtil {

    @SuppressLint("MissingPermission")
    public static String getUUID(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, tmPhone, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | ((long) tmSerial.hashCode()));
        return deviceUuid.toString();
    }


    public static byte[] getMasterControlAddr2(String uuid) {
        int sum = 0;
        String[] strings = uuid.split("-");
        for (int i = 0; i < strings.length; i++) {
            for (int j = 0; j < strings[i].length(); j = j + 4) {
                String temp = strings[i].substring(j, j + 4);
                sum += Integer.parseInt(temp, 16);
            }
        }
        int a1 = (sum >> 8) & 0xff;
        int a2 = sum & 0x0f;
        Log.e("-----a1---- ", Integer.toHexString(a1) + " ");
        Log.e("-----a2---- ", Integer.toHexString(a2) + " ");
        String addrStr1;
        if (a1 < 16) {
            addrStr1 = "0" + Integer.toHexString(a1) + " ";
        } else {
            addrStr1 = Integer.toHexString(a1) + " ";
        }
        String addrStr2 = Integer.toHexString(a2) + "0";

        String addr = addrStr1 + addrStr2;
        Log.e("---addr1---- ", addr);
        return CHexConver.hexStr2Bytes(addr);
    }

    public static int getDeviceSrc(String uuid) {
        String[] ids = uuid.split("-");
        String addr = ids[ids.length - 1].substring(0, 2);
        StringBuffer buffers = new StringBuffer(addr);
        buffers.insert(2, " ");
        return Integer.parseInt(addr, 16);
    }

    public static int getMasterControlAddr(String uuid) {
        int sum = 0;
        String[] strings = uuid.split("-");
        for (int i = 0; i < strings.length; i++) {
            for (int j = 0; j < strings[i].length(); j = j + 4) {
                String temp = strings[i].substring(j, j + 4);
                sum += Integer.parseInt(temp, 16);
            }
        }
        return sum;
    }
}
