package com.example.scale_ble_sdk.bluetooths;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import com.example.scale_ble_sdk.interfaces.IBlueStationListener;
import java.util.ArrayList;
import java.util.List;

import static com.example.scale_ble_sdk.blueutils.BlueComUtils.makeBlueFilters;

/**
 * 监听手机蓝牙状态
 */
public class BlueManager {
    private static final String TAG="BlueManager";
    protected Context appContext;
    private static BlueManager instance;
    private BluetoothAdapter mBluetoothAdapter;
    private List<IBlueStationListener> mIBlueStationListeners;//蓝牙状态监听接口集合
    private BluetoothStationReceiver mBluetoothStationReceiver;

    private BlueManager(){}

    public static BlueManager getInstance() {
        if (instance==null){
            synchronized (BlueManager.class){
                if (instance==null) instance = new BlueManager();
            }
        }
        return instance;
    }
    public void init(Context context){
        appContext=context.getApplicationContext();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mIBlueStationListeners=new ArrayList<>();
    }



    //添加一个蓝牙状态监听器
    public void addBluetoothStationListener(IBlueStationListener listener){
        if (listener==null){
            return;
        }
        mIBlueStationListeners.add(listener);
        //说明添加的第一个回调接口
        if (mIBlueStationListeners.size()==1){
            registerBluetoothStationReceiver();
        }

    }


    //移除所有的蓝牙状态监听器
    public void clearAllBluetoothStationListener(){
        int count = mIBlueStationListeners.size();
        mIBlueStationListeners.clear();
        //说明移除成功 并且移除以后总的接口已经为0了 可以反注册一下
        if (count>0){
            unRegisterBluetoothStationReceiver();
        }
    }


    //用广播监听蓝牙状态接口
    private void registerBluetoothStationReceiver() {
        mBluetoothStationReceiver=new BluetoothStationReceiver();
        appContext.registerReceiver(mBluetoothStationReceiver,makeBlueFilters());
    }
    private void unRegisterBluetoothStationReceiver() {
        appContext.unregisterReceiver(mBluetoothStationReceiver);
        mBluetoothStationReceiver=null;
    }


    private class BluetoothStationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)){
                return;
            }

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.e(TAG, "--------STATE_OFF 手机蓝牙关闭");

                        for (IBlueStationListener listener:mIBlueStationListeners){
                            listener.STATE_OFF();
                        }

                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.e(TAG, "--------STATE_TURNING_OFF 手机蓝牙正在关闭");
                        for (IBlueStationListener listener:mIBlueStationListeners){
                            listener.STATE_TURNING_OFF();
                        }

                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.e(TAG, "--------STATE_ON 手机蓝牙开启");
                        for (IBlueStationListener listener:mIBlueStationListeners){
                            listener.STATE_ON();
                        }

                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.e(TAG, "--------STATE_TURNING_ON 手机蓝牙正在开启");
                        for (IBlueStationListener listener:mIBlueStationListeners){
                            listener.STATE_TURNING_ON();
                        }
                        break;
                }
            }
        }
    }


    /**
     * 判断bluetooth是否开启
     */
    public boolean isBluetoothOpen() {
        return mBluetoothAdapter != null && mBluetoothAdapter.getState() != BluetoothAdapter.STATE_OFF;
    }

    /**
     * 打开蓝牙
     */
    public void openBluetooth() {
        if (mBluetoothAdapter != null&&!isBluetoothOpen()) {
            mBluetoothAdapter.enable();
        }
    }
    /**
     * 关闭蓝牙
     */
    public void closeBluetooth() {
        if (mBluetoothAdapter != null&&isBluetoothOpen()) {
            mBluetoothAdapter.disable();
        }
    }
    //判断设备是否支持蓝牙
    public boolean isSupportBluetooth(){
        return mBluetoothAdapter!=null;
    }
}
