package com.example.yzz.sodemo;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import java.util.Arrays;

public class BluetoothUtil {

    private static BluetoothUtil bluetoothUtil;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    private BluetoothLeScanner mBluetoothLeScanner;
    private AdvertiseCallback mAdvertiseCallback;
    private int mManufacturerId = 0xF006;
    private static Context mContext;
    private ScanCallback mLeScanCallback;

    public static BluetoothUtil getIntstance(Context context) {
        if (bluetoothUtil == null) {
            mContext = context;
            BluetoothAdapter bluetoothAdapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE))
                    .getAdapter();
            if (bluetoothAdapter == null) {
                Toast.makeText(context, "该设备没有蓝牙", Toast.LENGTH_SHORT).show();
                return null;
            }
            if (!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                context.startActivity(intent);
            }
            if (bluetoothAdapter.isEnabled()) {
                bluetoothUtil = new BluetoothUtil(bluetoothAdapter);
            }

        }
        return bluetoothUtil;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private BluetoothUtil(BluetoothAdapter bluetoothAdapter) {
        mBluetoothAdapter = bluetoothAdapter;
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mAdvertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
//                AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE;
                Toast.makeText(mContext, "蓝牙异常，重启蓝牙" + errorCode, Toast.LENGTH_LONG).show();
            }
        };
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public synchronized void advertising(byte[] data) {
        stopAdvertising();

        int manufacturerId = ((data[1] & 0xFF) << 8) | (data[0] & 0xff);
        byte[] subData = Arrays.copyOfRange(data, 2, data.length);
        AdvertiseData.Builder advertiseData = new AdvertiseData.Builder()
                .addManufacturerData(manufacturerId, subData);
        AdvertiseSettings settings = new AdvertiseSettings.Builder().setAdvertiseMode(2)
                .setConnectable(true).setTxPowerLevel(3).build();
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.startAdvertising(settings, advertiseData.build(), mAdvertiseCallback);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public synchronized void stopAdvertising() {

        if(mBluetoothLeAdvertiser!=null)mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void scanning(ScanCallback callback) {
        mLeScanCallback = callback;
        if (mBluetoothLeScanner != null) {
            mBluetoothLeScanner.startScan(null, new ScanSettings.Builder().setScanMode(2).setReportDelay(0).build(), callback);
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void stopScan() {
        if (mBluetoothLeScanner != null && mLeScanCallback != null) {
            mBluetoothLeScanner.stopScan(mLeScanCallback);
        }
    }


}
