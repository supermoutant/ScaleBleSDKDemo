package com.example.scale_ble_sdk.Bean;

public class FatResult {
    private int userId;
    private float fat;
    private float weight;
    private int resistance;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;
    private int weekOfYear;
    private String mac;

    private boolean isKGUnit;

    public void setUnitIsKG(boolean isKGUnit) {
        this.isKGUnit = isKGUnit;
    }

    public boolean getUnitIsKG() {
        return this.isKGUnit;
    }

    /**
     * 是否疑似数据
     */
    private boolean isSuspectedData = false;

    public boolean isSuspectedData() {
        return isSuspectedData;
    }

    public void setSuspectedData(boolean isSuspectedData) {
        this.isSuspectedData = isSuspectedData;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public float getFat() {
        return fat;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getResistance() {
        return resistance;
    }

    public void setResistance(int resistance) {
        this.resistance = resistance;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getWeekOfYear() {
        return weekOfYear;
    }

    public void setWeekOfYear(int weekOfYear) {
        this.weekOfYear = weekOfYear;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("fat measure test result.");
        sb.append("test time:");
        sb.append(year).append("-").append(month).append("-").append(day);
        sb.append(" ").append(hour).append(":").append(minute).append(":").append(second);
        sb.append(", fat:").append(fat);
        sb.append(", weight:").append(weight);
        sb.append(",resistance:").append(resistance);
        sb.append(",is suspectedData:");
        sb.append(isSuspectedData);

        return sb.toString();
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
