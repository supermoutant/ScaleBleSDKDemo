package com.example.scale_ble_sdk.interfaces;

import com.clj.fastble.data.BleDevice;

import java.util.List;

public interface BleScanSdkCallback {
    void onScanStarted(boolean success);
    void onScanning(String devicesName,String devicesMac);
    void onScanFinished();
}
