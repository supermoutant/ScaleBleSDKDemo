package com.example.scale_ble_sdk.bluetooths;

import com.chipsea.healthscale.CsAlgoBuilderEx;
import com.example.scale_ble_sdk.Bean.FatResult;
import com.example.scale_ble_sdk.Bean.OfflineMeasureResult;
import com.example.scale_ble_sdk.Bean.ScaleMeasureResult;
import com.example.scale_ble_sdk.Bean.ScaleUser;

public class MeasureResultAnalyzer {
    private static final String TAG = "BodyInfoUtil";
    private CsAlgoBuilderEx mBuilderEx = new CsAlgoBuilderEx();
    private static MeasureResultAnalyzer instance = new MeasureResultAnalyzer();

    private MeasureResultAnalyzer() {
    }

    public static MeasureResultAnalyzer getInstance() {
        return instance;
    }


    /**
     * 分析测量结果，得到分析结果
     */
    public OfflineMeasureResult getOfflineMeasureResult(FatResult result, ScaleUser user) {
        if (result == null || user == null) {
            return null;
        }
        /*#################创建分析结果对象#################*/
        OfflineMeasureResult mResult = new OfflineMeasureResult();
        getResult(result, user, mResult);
        mResult.setSuspectedData(result.isSuspectedData());

        return mResult;
    }

    /**
     * 分析测量结果，得到分析结果
     */
    public ScaleMeasureResult getMeasureResult(FatResult result, ScaleUser user) {
        if (result == null || user == null) {
            return null;
        }
        /*#################创建分析结果对象#################*/
        ScaleMeasureResult mResult = new ScaleMeasureResult();
        getResult(result, user, mResult);
        return mResult;
    }

    /**
     * 分析测量结果，得到分析结果
     */
    private void getResult(FatResult result, ScaleUser user, ScaleMeasureResult mResult) {


        // 从测量结果中取出sdk计算基值
        final float weightResult = result.getWeight();
        final float fatResult = result.getFat();
        final float resistanceResult = result.getResistance();

        //  计算各种值
        //设置用户信息 身高，体重，性别 1-男 0-女，年龄，电阻
        int sex = user.getSex();
        int age = user.getAge();
        float height = user.getHeight();


        if (age < 18) {
            age = 18;
        } else if (age > 80) {
            age = 80;
        }
        int orSex = 1 - sex;

        try {
            // 设置sdk基值
            mBuilderEx.setUserInfo(height, weightResult
                    , (byte) orSex, age, resistanceResult);
            //设置用户角色，1为运动员，0为普通用户
            mBuilderEx.setMode(user.getRoleType());
            /*####################计算出各个指标值###############################*/
            float wr, bmr, vf, mv, sm, bv, protein, bmi = 0;
            int ba, score;
            if (fatResult > 0.0f && age >= 18) {//脂肪率大于0则，计算各个指标
                wr = mBuilderEx.getTFR();
                bmr = mBuilderEx.getBMR();
                vf = mBuilderEx.getVFR();
                //骨骼肌
                mv = mBuilderEx.getSMM();
                sm = mBuilderEx.getSLM();
                bv = mBuilderEx.getMSW();
                ba = (int) mBuilderEx.getBodyAge();
                protein = mBuilderEx.getPM();
                score = mBuilderEx.getScore();
                // 蛋白质KG  转 百分比
                protein = dbzKgToPer(protein, weightResult);

            } else {//脂肪率小于等于0，则各个指标无效
                wr = bmr = vf = mv = sm = bv = protein = -1f;
                ba = score = 0;
            }

            //BMI
            bmi = getBmi(weightResult, height);

            //测量时间
            String year = result.getYear() + "";
            String month = numberFormat(result.getMonth() + "");
            String day = numberFormat(result.getDay() + "");
            String hour = numberFormat(result.getHour() + "");
            String minute = numberFormat(result.getMinute() + "");
            String sec = numberFormat(result.getSecond() + "");

            String measureTime = new StringBuffer(year).append("-").append(month)
                    .append("-").append(day).append(" ").append(hour).append(":")
                    .append(minute).append(":").append(sec).toString();


            // 设置基本信息
            mResult.setBtId(user.getBtId());
            mResult.setSex(user.getSex());
            mResult.setAge(user.getAge());
            mResult.setWeight(weightResult);
            mResult.setFat(fatResult);
            mResult.setMeasureTime(measureTime);
            //设置指标值
            mResult.setWaterRate(wr);
            mResult.setBmr(bmr);
            mResult.setVisceralFat(vf);
            mResult.setMuscleVolume(sm);
            mResult.setSkeletalMuscle(mv);
            mResult.setBoneVolume(bv);
            mResult.setProtein(protein);
            mResult.setBmi(bmi);
            mResult.setResistance(resistanceResult);
            mResult.setBodyScore(score);
            mResult.setBodyAge(ba);
            //默认单位为kg
            mResult.setWeightUnit(result.getUnitIsKG() ? ScaleDataConstant.KG : ScaleDataConstant.LB);//zcq
        } catch (Exception e) {
        }

    }

    private String numberFormat(String number) {
        return number.length() < 2 ? "0" + number : number;
    }

    private float getBmi(float weight, float userHeight) {
        float bmi = weight / (userHeight * userHeight / 10000);
        return Math.round(bmi * 10) / 10.0f;
    }

    private float dbzKgToPer(float dbz, float weight) {
        float preF = dbz / weight * 100;
        preF = Math.round(preF * 10) / 10.0f;
        preF = Math.min(preF, 100.0f);
        return preF;
    }
}
