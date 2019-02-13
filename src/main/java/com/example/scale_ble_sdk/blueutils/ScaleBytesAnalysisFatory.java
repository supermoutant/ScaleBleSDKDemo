package com.example.scale_ble_sdk.blueutils;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.example.scale_ble_sdk.Bean.FatResult;
import com.example.scale_ble_sdk.Bean.OfflineMeasureResult;
import com.example.scale_ble_sdk.Bean.ScaleMeasureResult;
import com.example.scale_ble_sdk.Bean.ScaleUser;
import com.example.scale_ble_sdk.bluetooths.MeasureResultAnalyzer;
import com.example.scale_ble_sdk.bluetooths.ScaleDataConstant;
import com.example.scale_ble_sdk.interfaces.IMeasureResultCallback;

import java.util.Arrays;
import java.util.Calendar;

public class ScaleBytesAnalysisFatory {
    private static final String TAG = "ScaleBytesAnalysis";
    private Handler myMainHandler;
    private IMeasureResultCallback mIMeasureResultCallback;
    public ScaleBytesAnalysisFatory() {
        this.myMainHandler = new Handler(Looper.getMainLooper());
    }

    public void setmIMeasureResultCallback(IMeasureResultCallback mIMeasureResultCallback) {
        this.mIMeasureResultCallback = mIMeasureResultCallback;
    }
    //返回信息分析处理
    public void resultAnalysis(byte[] data) {
        if (data == null || data.length <= 3) {
            Log.e(TAG, "ScaleBytesAnalysisFatory-不能处理错误数据");
            return;
        }

        //验证包头
        if (data[0] != (byte) 0x8d) {
            Log.i(TAG, "ScaleBytesAnalysisFatory-数据包头不正确");
            return;
        }
        //验证数据长度
        if (data.length != (data[1] & 0xff) + 3) {
            Log.i(TAG, "ScaleBytesAnalysisFatory-数据长度不正确");
            return;
        }
        Log.i(TAG, "ScaleBytesAnalysisFatory-解析的数据=" + Arrays.toString(data));
        int cmdCode = data[2] & 0xff;
        Log.i(TAG, "ScaleBytesAnalysisFatory-cmd = " + cmdCode);
        switch (cmdCode) {
            case 0x00:
                break;
            case 0x90: //秤已唤醒
                Log.i(TAG, "ScaleBytesAnalysisFatory-秤已唤醒");
                myMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mIMeasureResultCallback != null) mIMeasureResultCallback.onScaleWake();
                    }
                });
                break;
            case 0x91: //秤已休眠
                Log.i(TAG, "ScaleBytesAnalysisFatory-秤已休眠");
                myMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mIMeasureResultCallback != null) mIMeasureResultCallback.onScaleSleep();
                    }
                });
                break;
            case 0x92:        //称重单位已经改变
                Log.i(TAG, "ScaleBytesAnalysisFatory-称重单位已经改变");
                onScaleUnitChanged(data);
                break;
            case 0x93:  //接收到第n组闹钟
                Log.i(TAG, "ScaleBytesAnalysisFatory-接收到第n组闹钟");
                break;
            case 0x98: //接收到当前时钟
                Log.i(TAG, "ScaleBytesAnalysisFatory-接收到当前时钟");
                onReceiveTime(data);
                break;
            case 0x9c: //接收到版本信息
                Log.i(TAG, "ScaleBytesAnalysisFatory-接收到版本信息");
                onReceiveScaleVersion(data);
                break;
            case 0x9e: //接收到体脂肪测量结果
                Log.i(TAG, "ScaleBytesAnalysisFatory-接收到体脂肪测量结果");
                onReceiveMeasureResult(data);
                break;
            case 0xa0: //接收到历史测量数据
                Log.i(TAG, "ScaleBytesAnalysisFatory-接收到历史测量数据");
                onReceiveHistoryMeasureResult(data);
                break;
            case 0xa1:    //接收到升级包应答
                Log.i(TAG, "ScaleBytesAnalysisFatory-接收到升级包应答");
                break;
            case 0xa2:    //升级结果
                Log.i(TAG, "ScaleBytesAnalysisFatory-升级结果");

                break;
            case 0xa3:    //体重数据
                Log.i(TAG, "ScaleBytesAnalysisFatory-体重数据");
                break;
            case 0xa4:    //低电提示
                Log.i(TAG, "ScaleBytesAnalysisFatory-低电提示");
                break;
            case 0xa5:    //测脂出错
                Log.i(TAG, "ScaleBytesAnalysisFatory-测脂出错");
                Log.e(TAG, "onFatMeasureError~~~~");
                final int type = onFatErrorResult(data);
                myMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mIMeasureResultCallback != null) mIMeasureResultCallback.onFatError(type);
                    }
                });
                break;
            case 0xa6:    //接收到修改闹钟ACK
                Log.i(TAG, "ScaleBytesAnalysisFatory-接收到修改闹钟ACK");
                break;
            case 0xa7:    //接收到OTA升级就绪消息
                Log.i(TAG, "ScaleBytesAnalysisFatory-接收到OTA升级就绪消息");
                break;
            case 0xa8:    //接收到用户列表标记值响应

                break;
            case 0xa9:    //秤历史记录上传完毕消息
                Log.i(TAG, "ScaleBytesAnalysisFatory-秤历史记录上传完毕消息");
                onHistoryDownloadDone(data);
                break;
            case 0xb0: //秤响应app的列表更新指令
                Log.i(TAG, "ScaleBytesAnalysisFatory-用户信息设置成功");
                onUserInfoSettingSucceeded();
                break;
            case 0xb1://秤响应无连接前测量结果响应
                Log.i(TAG, "ScaleBytesAnalysisFatory-秤响应无连接前测量结果响应");
                break;
            case 0xb6: //上传秤是否与手机绑定过
                Log.i(TAG, "ScaleBytesAnalysisFatory-上传秤是否与手机绑定过");
                break;
            case 0xb7:  //绑定确认指令
                Log.i(TAG, "ScaleBytesAnalysisFatory-绑定确认指令");
                break;
            case 0xb8:                //OTA sha256校验码下发响应
                Log.i(TAG, "ScaleBytesAnalysisFatory-OTA sha256校验码下发响应");
                break;
        }
    }
    /**
     * 用户信息设置成功
     */
    private void onUserInfoSettingSucceeded() {
        myMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mIMeasureResultCallback != null) {
                    mIMeasureResultCallback.setUserInfoSuccess();
                }
            }
        });
    }
    /**
     * 获取历史数据完成
     */
    private void onHistoryDownloadDone(byte[] data) {
        myMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mIMeasureResultCallback != null) {
                    mIMeasureResultCallback.onHistoryDownloadDone();
                }
            }
        });
    }
    /**
     * 测脂出错
     * @param data
     * @return
     */
    public int onFatErrorResult(byte[] data) {
        int type = data[3] & 0xff;
        return type;
    }
    /**
     * 接收到历史测量数据
     */
    private void onReceiveHistoryMeasureResult(byte[] data) {
        FatResult fatResultBean = getFatResult(data);
        if (fatResultBean == null) {
            return;
        }
        //是否疑似数据
        int suspectedData = data[18];
        //是否疑似数据
        fatResultBean.setSuspectedData((suspectedData & 0xff) == 170);
        final OfflineMeasureResult result = MeasureResultAnalyzer.getInstance().getOfflineMeasureResult(fatResultBean, ScaleUser.getUser());

        myMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mIMeasureResultCallback != null) {
                    mIMeasureResultCallback.onReceiveHistoryRecord(result);
                }
            }
        });
    }

    /**
     * 测脂结果
     * @param data
     */
    private void onReceiveMeasureResult(byte[] data) {
        FatResult fatResultBean = getFatResult(data);
        if (fatResultBean == null) {
            return;
        }
        final ScaleMeasureResult result = MeasureResultAnalyzer.getInstance().getMeasureResult(fatResultBean, ScaleUser.getUser());

        myMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mIMeasureResultCallback != null) {
                    mIMeasureResultCallback.onReceiveMeasureResult(result);
                }
            }
        });
    }
    private FatResult getFatResult(byte[] data) {
        if (data == null || data.length < 18) {
            return null;
        }
        int userId = data[3] & 0xff;
        float weight = get2ByteValue(data[5], data[4]) / 10.0f;
        float fat = get2ByteValue(data[7], data[6]) / 10.0f;
        //时间
        int year = get2ByteValue(data[9], data[8]);
        int month = data[10] & 0xff;
        int day = data[11] & 0xff;
        int hour = data[12] & 0xff;
        int minute = data[13] & 0xff;
        int second = data[14] & 0xff;
        int weekOfYear = data[15] & 0xff;
        //"testDate":"2018-03-15 12:11:12"
        //电阻
        int resistance = get2ByteValue(data[17], data[16]);

        FatResult fatResultBean = new FatResult();
        fatResultBean.setUserId(userId);
        fatResultBean.setYear(year);
        fatResultBean.setMonth(month);
        fatResultBean.setDay(day);
        fatResultBean.setHour(hour);
        fatResultBean.setMinute(minute);
        fatResultBean.setSecond(second);
        fatResultBean.setWeekOfYear(weekOfYear);
        fatResultBean.setResistance(resistance);
        fatResultBean.setWeight(weight);
        fatResultBean.setFat(fat);
        boolean isUnitKg;
        int unitByte = (data[18] & 0xff);
        isUnitKg = 0x01 != unitByte;//判断传过来的单位类型
        fatResultBean.setUnitIsKG(isUnitKg);
        return fatResultBean;
    }
    /**
     * 接收版本信息
     */
    private void onReceiveScaleVersion(byte[] data) {
        //ble固件
        int ble0 = data[3] & 0xff;
        int ble1 = data[4] & 0xff;
        //秤固件
        int scale0 = data[5] & 0xff;
        int scale1 = data[6] & 0xff;
        //系数固件
        final int coefficientVer = get2ByteValue(data[7], data[8]);

        //算法固件
        final int arithmeticVer = get2ByteValue(data[9], data[10]);

        final int bleVer = (ble1 << 8) | ble0;
        final int scaleVer = (scale1 << 8) | scale0;


        Log.e(TAG, "receive scale version info." +
                "bleVer:" + bleVer +
                "scaleVer:" + scaleVer +
                "coefficientVer:" + coefficientVer +
                "arithmeticVer:" + arithmeticVer);

        //接收版本信息回调
        myMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mIMeasureResultCallback != null) {
                    mIMeasureResultCallback.onGotScaleVersion(bleVer, scaleVer, coefficientVer, arithmeticVer);
                }
            }
        });

    }
    /**
     * 接收当前时间
     */
    private void onReceiveTime(byte[] data) {
        int year = get2ByteValue(data[3], data[4]);
        int month = data[5] & 0xff;
        int day = data[6] & 0xff;
        int hour = data[7] & 0xff;
        int minute = data[8] & 0xff;
        int second = data[9] & 0xff;
        int weekOfYear = data[10] & 0xff;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DATE, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        final long scaleDateLong = cal.getTime().getTime();
        myMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mIMeasureResultCallback != null) {
                    mIMeasureResultCallback.receiveTime(scaleDateLong);
                }
            }
        });

    }
    /**
     * 高8位低8位拼接
     *
     * @param high 高8位字节
     * @param low  低8位字节
     */
    private int get2ByteValue(byte high, byte low) {
        return ((high & 0xff) << 8) | (low & 0xff);
    }

    /**
     * 单位称重改变
     * @param data
     */
    private void onScaleUnitChanged(byte[] data) {
        final String scaleUnit = data[3] == 1 ? ScaleDataConstant.KG : ScaleDataConstant.LB;
        myMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mIMeasureResultCallback != null) {
                    mIMeasureResultCallback.unitChange(scaleUnit);
                }
            }
        });
    }
}
