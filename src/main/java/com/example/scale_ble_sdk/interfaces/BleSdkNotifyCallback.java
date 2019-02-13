package com.example.scale_ble_sdk.interfaces;

public interface BleSdkNotifyCallback {
    void onNotifySuccess();

    void onNotifyFailure();

    void onCharacteristicChanged(byte[] data);
}
