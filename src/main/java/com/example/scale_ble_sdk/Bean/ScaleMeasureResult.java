package com.example.scale_ble_sdk.Bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ScaleMeasureResult implements Parcelable {
    /*用户标识id，必须保证唯一性*/
    String btId;
    /*年龄*/
    int age;
    /*性别*/
    int sex;
    /*测量蓝牙设备的mac地址*/
    String mac;
    /*测量时间*/
    String measureTime;
    /*阻抗*/
    private float resistance;

    /*体脂率*/
    float fat;
    /*体重*/
    float weight;
    /*水分率*/
    float waterRate;
    /*基础代谢率率*/
    float bmr;
    /*内脏脂肪等级*/
    float visceralFat;
    /*肌肉量*/
    float muscleVolume;
    /*骨骼肌*/
    private float skeletalMuscle;
    /*骨量*/
    float boneVolume;
    /*BMI*/
    float bmi;
    /*蛋白质*/
    float protein;
    /*身体得分*/
    private float bodyScore;
    /*身体年龄*/
    private float bodyAge;

    String weightUnit ="kg";
    String fatUnit = "%";


    @Override
    public int describeContents() {
        return 0;
    }

    public static Parcelable.Creator<ScaleMeasureResult> getCREATOR() {
        return CREATOR;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(btId);
        dest.writeInt(age);
        dest.writeInt(sex);
        dest.writeString(mac);
        dest.writeString(measureTime);
        dest.writeFloat(resistance);

        dest.writeFloat(fat);
        dest.writeFloat(weight);
        dest.writeFloat(waterRate);
        dest.writeFloat(bmr);
        dest.writeFloat(visceralFat);
        dest.writeFloat(muscleVolume);
        dest.writeFloat(skeletalMuscle);
        dest.writeFloat(boneVolume);
        dest.writeFloat(bmi);

        dest.writeFloat(protein);
        dest.writeFloat(bodyScore);
        dest.writeFloat(bodyAge);

        dest.writeString(weightUnit);
        dest.writeString(fatUnit);


    }

    // 用来创建自定义的Parcelable的对象
    public static final Parcelable.Creator<ScaleMeasureResult > CREATOR = new Parcelable.Creator<ScaleMeasureResult >() {
        public ScaleMeasureResult  createFromParcel(Parcel in) {
            return new ScaleMeasureResult (in);
        }

        public ScaleMeasureResult [] newArray(int size) {
            return new ScaleMeasureResult [size];
        }
    };

    // 读数据进行恢复
    protected ScaleMeasureResult(Parcel in) {
        btId = in.readString();
        age = in.readInt();
        sex = in.readInt();
        mac = in.readString();
        measureTime = in.readString();
        resistance = in.readFloat();

        fat = in.readFloat();
        weight = in.readFloat();
        waterRate = in.readFloat();
        bmr = in.readFloat();
        visceralFat = in.readFloat();
        muscleVolume = in.readFloat();
        skeletalMuscle = in.readFloat();
        boneVolume = in.readFloat();
        bmi = in.readFloat();
        protein = in.readFloat();
        bodyScore = in.readFloat();
        bodyAge = in.readFloat();

        weightUnit = in.readString();
        fatUnit = in.readString();

    }

    public ScaleMeasureResult() {
    }

    public String getBtId() {
        return btId;
    }

    public void setBtId(String btId) {
        this.btId = btId;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMeasureTime() {
        return measureTime;
    }

    public void setMeasureTime(String measureTime) {
        this.measureTime = measureTime;
    }

    public float getResistance() {
        return resistance;
    }

    public void setResistance(float resistance) {
        this.resistance = resistance;
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

    public float getWaterRate() {
        return waterRate;
    }

    public void setWaterRate(float waterRate) {
        this.waterRate = waterRate;
    }

    public float getBmr() {
        return bmr;
    }

    public void setBmr(float bmr) {
        this.bmr = bmr;
    }

    public float getVisceralFat() {
        return visceralFat;
    }

    public void setVisceralFat(float visceralFat) {
        this.visceralFat = visceralFat;
    }

    public float getMuscleVolume() {
        return muscleVolume;
    }

    public void setMuscleVolume(float muscleVolume) {
        this.muscleVolume = muscleVolume;
    }

    public float getBoneVolume() {
        return boneVolume;
    }

    public void setBoneVolume(float boneVolume) {
        this.boneVolume = boneVolume;
    }

    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    public String getFatUnit() {
        return fatUnit;
    }

    public void setFatUnit(String fatUnit) {
        this.fatUnit = fatUnit;
    }

    public float getSkeletalMuscle() {
        return skeletalMuscle;
    }

    public void setSkeletalMuscle(float skeletalMuscle) {
        this.skeletalMuscle = skeletalMuscle;
    }

    public float getBodyScore() {
        return bodyScore;
    }

    public void setBodyScore(float bodyScore) {
        this.bodyScore = bodyScore;
    }

    public float getBodyAge() {
        return bodyAge;
    }

    public void setBodyAge(float bodyAge) {
        this.bodyAge = bodyAge;
    }

    @Override
    public String toString() {
        return "MeasureResult{" +
                "btId='" + btId + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                ", mac='" + mac + '\'' +
                ", measureTime='" + measureTime + '\'' +
                ", resistance=" + resistance +
                ", fat=" + fat +
                ", weight=" + weight +
                ", waterRate=" + waterRate +
                ", bmr=" + bmr +
                ", visceralFat=" + visceralFat +
                ", muscleVolume=" + muscleVolume +
                ", skeletalMuscle=" + skeletalMuscle +
                ", boneVolume=" + boneVolume +
                ", bmi=" + bmi +
                ", protein=" + protein +
                ", bodyScore=" + bodyScore +
                ", bodyAge=" + bodyAge +
                ", weightUnit='" + weightUnit + '\'' +
                ", fatUnit='" + fatUnit + '\'' +
                '}';
    }
}
