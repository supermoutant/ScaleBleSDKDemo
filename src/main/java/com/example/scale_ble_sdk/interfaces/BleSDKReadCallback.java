package com.example.scale_ble_sdk.interfaces;

public interface BleSDKReadCallback {
    void onReadSuccess(byte[] data);
    void onReadFailure();
}
