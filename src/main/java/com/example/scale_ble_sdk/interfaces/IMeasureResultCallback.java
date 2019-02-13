package com.example.scale_ble_sdk.interfaces;

import com.example.scale_ble_sdk.Bean.OfflineMeasureResult;
import com.example.scale_ble_sdk.Bean.ScaleMeasureResult;

/**
 * 测量结果接口
 */
public interface IMeasureResultCallback {

    /**
     * 脂肪测量结果数据
     */
    void onReceiveMeasureResult(ScaleMeasureResult result);

    /**
     * 体重超载
     */
    void onWeightOverLoad();

    /**
     * 接收到测量历史数据
     */
    void onReceiveHistoryRecord(OfflineMeasureResult result);

    /**
     * 测脂出错
     */
    void onFatMeasureError(int type);

    /**
     * 接收到历史记录上发完毕
     */
    void onHistoryDownloadDone();


    void unitChange(String unit);

    /**
     * 获取到秤版本信息
     * @param bleVer bel固件版本
     * @param scaleVer 秤固件版本
     * @param coefficientVer 系数固件版本
     * @param arithmeticVer 算法固件版本
     */
    void onGotScaleVersion(int bleVer, int scaleVer, int coefficientVer, int arithmeticVer);

    /**
     * 升级包应答
     * @param pkgNo 第几个升级包
     */
    void onUpgradeResponse(int pkgNo, boolean result);

    /**
     * 升级结果
     * 00：OK
     * 01：超时ERR
     * 02：CS出错
     * 04：电量低
     * @param result
     */
    void onUpgradeResult(int result, int type);

    /**
     * 低电提示
     */
    void onLowPower();

    /**
     * 接收到OTA升级准备就绪命令
     */
    void onOtaUpgradeReady(boolean result);

    /**
     * 设置 用户信息成功
     */
    void setUserInfoSuccess();

    //接收到秤的时间
    void receiveTime(long time);


    void onScaleWake();

    void onScaleSleep();
    void onFatError(int type);
}
