package com.example.scale_ble_sdk.Bean;

public class OfflineMeasureResult extends ScaleMeasureResult{
    /*****是否有疑义****/
    private boolean isSuspectedData = false;

    public boolean isSuspectedData() {
        return isSuspectedData;
    }

    public void setSuspectedData(boolean suspectedData) {
        isSuspectedData = suspectedData;
    }

    @Override
    public String toString() {
        return "OfflineMeasureResult{" +
                "isSuspectedData=" + isSuspectedData +
                "btId='" + btId + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                ", fat=" + fat +
                ", weight=" + weight +
                ", mac='" + mac + '\'' +
                ", measureTime='" + measureTime + '\'' +
                ", waterRate=" + waterRate +
                ", bmr=" + bmr +
                ", visceralFat=" + visceralFat +
                ", muscleVolume=" + muscleVolume +
                ", boneVolume=" + boneVolume +
                ", bmi=" + bmi +
                ", protein=" + protein +
                ", weightUnit='" + weightUnit + '\'' +
                ", fatUnit='" + fatUnit + '\'' +
                '}';
    }

}
