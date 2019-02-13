package com.example.scale_ble_sdk.interfaces;
public interface BleGattSDKCallback {
    void onStartConnect();
    void onConnectFail();
    void onConnectSuccess(String deviceName,String deviceMac);
    void onDisConnected(String deviceName,String deviceMac);
}
