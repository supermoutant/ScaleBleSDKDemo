package com.example.scale_ble_sdk.bluetooths;

public interface ScaleDataConstant {
    String BLUETOOTH_SCALE_SERVICE_UUID = "0000faa0-0000-1000-8000-00805f9b34fb";
    String BLUETOOTH_SCALE_READ_UUID = "0000faa2-0000-1000-8000-00805f9b34fb";
    String BLUETOOTH_SCALE_WRITE_UUID = "0000faa1-0000-1000-8000-00805f9b34fb";
    byte[] deviceAddressBytes = new byte[]{0x16, 0x15, 0x14, 0x13, 0x12, 0x11};
    byte[] HEARTCHECKBYTES = {(byte) 0xab, 0x01, (byte) 0xb0};//心跳包
     byte[] getHistoryDataACK= {(byte) 0xab, 0x02, (byte) 0x9b, 0x01};
    String KG="kg";
    String LB="lb";
}
