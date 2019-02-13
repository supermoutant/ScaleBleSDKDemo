package com.example.scale_ble_sdk.blueutils;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.example.scale_ble_sdk.Bean.ScaleUser;
import com.example.scale_ble_sdk.bluetooths.ScaleDataConstant;
import com.example.scale_ble_sdk.interfaces.BleGattSDKCallback;
import com.example.scale_ble_sdk.interfaces.BleSDKReadCallback;
import com.example.scale_ble_sdk.interfaces.BleSDKWriteCallback;
import com.example.scale_ble_sdk.interfaces.BleScanSdkCallback;
import com.example.scale_ble_sdk.interfaces.BleSdkNotifyCallback;
import com.example.scale_ble_sdk.interfaces.IMeasureResultCallback;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ScaleBleUtils {
    private static final String TAG = ScaleBleUtils.class.getSimpleName();
    private String serviceUUID;
    private String deviceReadUUIDs;
    private String deviceWriteUUIDs;
    private BleManager mBleManager;
    private UUID uuid_chara;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public ScaleBleUtils(Application context, String aServiceUUID, String aDeviceReadUUIDs, String aDeviceWriteUUIDs) {
        this.serviceUUID = aServiceUUID;
        this.deviceReadUUIDs = aDeviceReadUUIDs;
        this.deviceWriteUUIDs = aDeviceWriteUUIDs;
        mBleManager = BleManager.getInstance();
        mBleManager.init(context);
        mBleManager.enableLog(true).setMaxConnectCount(7).setOperateTimeout(5000);
    }

    //扫描蓝牙

    /**
     * @param scanTimeOutTime 扫描时长
     */
    public void scanningBluetoothDevices(final long scanTimeOutTime, final BleScanSdkCallback bleScanSdkCallback) {
        if (BluetoothAdapter.getDefaultAdapter() == null) {
            Log.e(TAG, "Phone not support Ble");
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                BleScanCallback mBleScanCallback = new BleScanCallback() {
                    @Override
                    public void onScanStarted(boolean success) {
                        Log.e(TAG, "onScanStarted");
                        bleScanSdkCallback.onScanStarted(success);
                    }

                    @Override
                    public void onScanning(BleDevice result) {
                        Log.e(TAG, "onScanning");
                        bleScanSdkCallback.onScanning(result.getName(), result.getMac());
                    }

                    @Override
                    public void onScanFinished(List<BleDevice> scanResultList) {
                        Log.e(TAG, "onScanFinished");
                        bleScanSdkCallback.onScanFinished();
                    }
                };
                BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                        .setScanTimeOut(scanTimeOutTime)              // 扫描超时时间，可选，默认10秒；小于等于0表示不限制扫描时间
                        .build();
                mBleManager.initScanRule(scanRuleConfig);
                mBleManager.scan(mBleScanCallback);
            }
        });
    }

    /**
     * @param devicesMac 设备MAC地址
     */
    public void connectBleDevices(final String devicesMac, final BleGattSDKCallback bleGattSDKCallback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(devicesMac)) {
                    return;
                }
                final String addressStr = formatMacAddress(devicesMac, true);
                BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(addressStr);
                if (bluetoothDevice == null) {
                    return;
                }
                //连接回调
                BleGattCallback mBleGattCallback = new BleGattCallback() {
                    @Override
                    public void onStartConnect() {
                        bleGattSDKCallback.onStartConnect();
                    }

                    @Override
                    public void onConnectFail(BleDevice bleDevice, BleException exception) {
                        bleGattSDKCallback.onConnectFail();
                    }

                    @Override
                    public void onConnectSuccess(final BleDevice bleDevice, BluetoothGatt gatt, int status) {
                        getUuidServiceAndUuidChara(gatt);
                        bleGattSDKCallback.onConnectSuccess(bleDevice.getName(), bleDevice.getMac());
                    }

                    @Override
                    public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                        bleGattSDKCallback.onDisConnected(device.getName(), device.getMac());
                    }
                };
                BleDevice mBleDevice = new BleDevice(bluetoothDevice);
                mBleManager.connect(mBleDevice, mBleGattCallback);
            }
        });
    }


    //格式化MAC地址
    private String formatMacAddress(String macAddressStr, boolean shouldColonSep) {
        String colon = ":";
        //正则表达式判断mac 地址是否有使用：分隔
        String reMac = "^[A-F0-9]{2}(:[A-F0-9]{2}){5}$";
        Pattern pattern = Pattern.compile(reMac);
        Matcher matcher = pattern.matcher(macAddressStr);
        boolean matchFound = matcher.matches();
        if (matchFound && shouldColonSep) {//是冒号分隔格式并且需要逗号分隔
            return macAddressStr;
        } else if (matchFound && !shouldColonSep) {//是冒号分隔格式 需要去掉冒号
            return macAddressStr.replace(colon, "");
        } else if (!matchFound && shouldColonSep) {//非冒号分隔格式 但需要逗号分隔
            if (!macAddressStr.contains(colon) && macAddressStr.length() == 12) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < macAddressStr.length() / 2; i++) {
                    sb.append(macAddressStr.substring(i * 2, (i + 1) * 2));
                    sb.append(colon);
                }
                String macStr = sb.toString();
                return macStr.substring(0, macStr.length() - 1);
            }
        }
        return macAddressStr;
    }

    /**
     * 获取Bledevice
     *
     * @param scaleMac 设备MAC 地址
     * @return
     */
    public BleDevice getBleDevice(String scaleMac) {
        if (TextUtils.isEmpty(scaleMac)) {
            return null;
        }
        final String addressStr = formatMacAddress(scaleMac, true);
        BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(addressStr);
        BleDevice bleDevice = new BleDevice(bluetoothDevice);
        return bleDevice;
    }

    /**
     * @param scaleMac 断开蓝牙连接
     */
    public void disConnectionDevice(String scaleMac) {
        if (TextUtils.isEmpty(scaleMac)) {
            return;
        }
        BleDevice bleDevice = getBleDevice(scaleMac);
        if (mBleManager != null) {
            mBleManager.disconnect(bleDevice);
        }
    }

    /**
     * 停止扫描
     */
    public void stopScanDevice() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mBleManager != null) {
                    mBleManager.cancelScan();
                }
            }
        });
    }
    //向体脂秤端写入数据

    /**
     * @param data    写入的数据
     * @param address 秤端地址
     */
    public void writeDataToDevices(byte[] data, final String address, final BleSDKWriteCallback bleSDKWriteCallback) {
        if (data == null || data.length == 0) {
            return;
        }
        if (TextUtils.isEmpty(address)) {
            return;
        }
        final byte[] byteData = bodyFatScaleEncrypt(data);
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                BleWriteCallback mBleWriteCallback = new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        bleSDKWriteCallback.onWriteSuccess();
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        bleSDKWriteCallback.onWriteFailure();
                    }

                };
                mBleManager.write(getBleDevice(address), serviceUUID, deviceWriteUUIDs, byteData, mBleWriteCallback);
                SystemClock.sleep(300);
            }
        });
    }

    /**
     * 加密数据
     *
     * @param data
     * @return
     */
    private byte[] bodyFatScaleEncrypt(byte[] data) {
        if ((data[0] & 0xff) == (0xab & 0xff)) {
            byte[] macAddr = ScaleDataConstant.deviceAddressBytes;
            for (int i = 3, j = 0; i < data.length; i++, j++) {
                data[i] ^= macAddr[j % 6];
            }
        }
        return data;
    }

    /**
     * 从设备端读取数据
     */
    public void readDataFromDevice(String devicesMac/*, final BleSDKReadCallback bleSDKReadCallback*/) {
        if (TextUtils.isEmpty(devicesMac)) {
            return;
        }
        final BleDevice bleDevice = getBleDevice(devicesMac);
        if (bleDevice == null) {
            return;
        }
        //读取设备反回信息回调
        final BleReadCallback mBleReadCallback = new BleReadCallback() {
            @Override
            public void onReadSuccess(byte[] data) {
                Log.e(TAG, "onReadSuccess" + data);
                // bleSDKReadCallback.onReadSuccess(data);
            }

            @Override
            public void onReadFailure(BleException exception) {
                Log.e(TAG, "onReadFailure");
                // bleSDKReadCallback.onReadFailure();
            }
        };
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mBleManager != null) {
                    mBleManager.read(bleDevice, serviceUUID, deviceReadUUIDs, mBleReadCallback);
                }
            }
        });
    }

    /**
     * @param addressMac 设备MAC地址
     */
    public void notifyDataFromDevice(String addressMac, final BleSdkNotifyCallback bleSdkNotifyCallback) {
        if (TextUtils.isEmpty(addressMac)) {
            return;
        }
        final BleDevice bleDevice = getBleDevice(addressMac);
        if (bleDevice == null) {
            return;
        }
        final BleNotifyCallback mBleNotifyCallback = new BleNotifyCallback() {
            @Override
            public void onNotifySuccess() {
                Log.e(TAG, "onNotifySuccess");
                bleSdkNotifyCallback.onNotifySuccess();
            }

            @Override
            public void onNotifyFailure(BleException exception) {
                Log.e(TAG, "onNotifyFailure" + exception.getDescription());
                bleSdkNotifyCallback.onNotifyFailure();
            }

            @Override
            public void onCharacteristicChanged(byte[] data) {
                Log.e(TAG, "onCharacteristicChanged");
                bleSdkNotifyCallback.onCharacteristicChanged(data);
            }
        };
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mBleManager != null) {
                    mBleManager.notify(bleDevice, serviceUUID, uuid_chara.toString(), mBleNotifyCallback);
                }
            }
        });
    }

    /**
     * 发送心跳
     */
    public void sendHeartBeat(final String addressMac) {
        if (TextUtils.isEmpty(addressMac)) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                writeDataToDevices(ScaleDataConstant.HEARTCHECKBYTES, addressMac, new BleSDKWriteCallback() {
                    @Override
                    public void onWriteSuccess() {
                        Log.e(TAG, "心跳SUCCESS");
                    }

                    @Override
                    public void onWriteFailure() {
                        Log.e(TAG, "心跳Failure");
                    }
                });
            }
        });
    }

    /**
     * 获取notify ID。
     *
     * @param gatt
     */
    private void getUuidServiceAndUuidChara(BluetoothGatt gatt) {
        List<BluetoothGattService> serviceList = gatt.getServices();
        for (BluetoothGattService service : serviceList) {
            List<BluetoothGattCharacteristic> characteristicList = service.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic : characteristicList) {
                uuid_chara = characteristic.getUuid();
            }
        }
    }

    //下发时钟信息
    public byte[] getSystemClock() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int date = cal.get(Calendar.DATE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        //周一至周日，1至7
        int week = cal.get(Calendar.DAY_OF_WEEK);
        week = (week == 1) ? 7 : (week - 1);

        int yearLowHex = year & 0xff;
        int yearHeightHex = (year >> 8) & 0xff;
        Log.i(TAG, "year=" + year + "--yearLowHex=" + yearLowHex + "--yearHeightHex=" + yearHeightHex + "--month=" + month
                + "--date=" + date + "--hour=" + hour + "--minute=" + minute + "--second=" + second + "--week=" + week);
        byte[] bytes = {
                (byte) 0xab,
                0x09,
                (byte) 0x98,
                (byte) yearLowHex,
                (byte) yearHeightHex,
                (byte) month,
                (byte) date,
                (byte) hour,
                (byte) minute,
                (byte) second,
                (byte) week

        };
        return bytes;
    }

    /**
     * 下发时钟信息
     */
    public void syncSystemClock(final String addressMac) {
        if (TextUtils.isEmpty(addressMac)) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                writeDataToDevices(getSystemClock(), addressMac, new BleSDKWriteCallback() {
                    @Override
                    public void onWriteSuccess() {
                        Log.e(TAG, "心跳SUCCESS");
                    }

                    @Override
                    public void onWriteFailure() {
                        Log.e(TAG, "心跳Failure");
                    }
                });
            }
        });
    }

    /**
     * 下发用户信息
     */
    public void sendUserInfo(final String addressMac, ScaleUser scaleUser) {
        if (TextUtils.isEmpty(addressMac)) {
            return;
        }
        final byte[] userInfo = scaleUser.buildScaleUserData();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                writeDataToDevices(userInfo, addressMac, new BleSDKWriteCallback() {
                    @Override
                    public void onWriteSuccess() {
                        Log.e(TAG, "心跳SUCCESS");
                    }

                    @Override
                    public void onWriteFailure() {
                        Log.e(TAG, "心跳Failure");
                    }
                });
            }
        });
    }

    //请求离线数据
    public byte[] getHistoryData(String uid) {

        String[] uidArr = uid.split(":");

        if (uidArr.length != 7) {
            uidArr = new String[]{"0", "0", "0", "0", "0", "0", "0"};
        }

        byte[] bytes = {
                (byte) 0xab,
                0x07,
                (byte) 0x9b,
                (byte) Integer.parseInt(uidArr[0], 16),
                (byte) Integer.parseInt(uidArr[1], 16),
                (byte) Integer.parseInt(uidArr[2], 16),
                (byte) Integer.parseInt(uidArr[3], 16),
                (byte) Integer.parseInt(uidArr[4], 16),
                (byte) Integer.parseInt(uidArr[5], 16),
                (byte) Integer.parseInt(uidArr[6], 16),
                0x00
        };
        return bytes;
    }

    /**
     * 请求历史数据
     *
     * @param addressMac
     * @param uid
     */
    public void sendHistoryCommand(final String addressMac, final String uid) {
        if (TextUtils.isEmpty(addressMac)) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                writeDataToDevices(getHistoryData(uid), addressMac, new BleSDKWriteCallback() {
                    @Override
                    public void onWriteSuccess() {
                        Log.e(TAG, "心跳SUCCESS");
                    }

                    @Override
                    public void onWriteFailure() {
                        Log.e(TAG, "心跳Failure");
                    }
                });
            }
        });
    }

    /**
     * 存在历史数据在请求一条
     */
    public void sendHistoryRecord(final String addressMac) {
        if (TextUtils.isEmpty(addressMac)) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                writeDataToDevices(ScaleDataConstant.getHistoryDataACK, addressMac, new BleSDKWriteCallback() {
                    @Override
                    public void onWriteSuccess() {
                        Log.e(TAG, "心跳SUCCESS");
                    }

                    @Override
                    public void onWriteFailure() {
                        Log.e(TAG, "心跳Failure");
                    }
                });
            }
        });
    }
}
