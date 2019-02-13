package com.example.scale_ble_sdk.interfaces;


/**
 * 蓝牙状态监听接口
 */
public interface IBlueStationListener {
    void STATE_OFF();
    void STATE_TURNING_OFF();
    void STATE_ON();
    void STATE_TURNING_ON();
}
