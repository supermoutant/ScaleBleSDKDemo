package com.example.scale_ble_sdk.Bean;

public class ScaleUser {
    private ScaleUser(){};
    private final String TAG = "ScaleUser";

    private static ScaleUser mUser;


    private int age;
    private int height;
    private float weight = 0;
    private int sex = 0;//0男1女 但是数据解析那里是0女1男 这里需要特别注意
    private float impedance;//阻抗
    private String btId;

    /**
     * 运动类型
     * 0:普通用户
     * 1:运动员
     */
    private int roleType;

    public int getRoleType() {
        return roleType;
    }

    public void setRoleType(int roleType) {
        this.roleType = roleType;
    }


    public String getBtId() {
        return btId;
    }

    public void setBtId(String btId) {
        this.btId = btId;
    }



    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getImpedance() {
        return impedance;
    }

    public void setImpedance(float impedance) {
        this.impedance = impedance;
    }

    public byte[] buildScaleUserData() {
        String[] btIdArr = null;
        if (btId != null) {
            btIdArr = btId.split(":");
        }
        if (btIdArr == null || btIdArr.length != 7) {
            btIdArr = new String[]{"0", "0", "0", "0", "0", "0", "0"};
        }

        int sexAndAge = age;
        if (sex == 1) {
            sexAndAge = sexAndAge | 128;
        }

        int weightInt = (int) (weight * 10);
        int weight1 = weightInt & 255;
        int weight2 = (weightInt >> 8) & 0xff;

        int resistance1 = 255;
        int resistance2 = 255;

        if (impedance >= 200 && impedance <= 1500) {
            resistance1 = ((int) impedance) & 255;
            resistance2 = (((int) impedance) >> 8) & 0xff;
        }


        if (height < 100) {
            height = 100;
        } else if (height > 220) {
            height = 220;
        }
        byte heightByte = (byte) height;

        // roleType
        byte roleTypeByte = 0x01;
        if (roleType == 1) {
            roleTypeByte = 0x02;
        }
        byte[] bytes = {
                (byte) 0xab,
                (byte) 0x0e,
                (byte) 0x99,
                (byte) Integer.parseInt(btIdArr[0], 16),
                (byte) Integer.parseInt(btIdArr[1], 16),
                (byte) Integer.parseInt(btIdArr[2], 16),
                (byte) Integer.parseInt(btIdArr[3], 16),
                (byte) Integer.parseInt(btIdArr[4], 16),
                (byte) Integer.parseInt(btIdArr[5], 16),
                (byte) Integer.parseInt(btIdArr[6], 16),
                (byte) sexAndAge,
                heightByte,
                roleTypeByte,
                (byte) weight1,
                (byte) weight2,
                (byte) resistance1,
                (byte) resistance2
        };
        return bytes;
    }

    //实际上就是单例模式
    public static ScaleUser getUser() {
        if (mUser == null) {
            synchronized (ScaleUser.class){
                if (mUser==null)mUser = new ScaleUser();
            }
        }
        return mUser;
    }

    //将用户信息保存



    public void clearData() {
        mUser.setAge(0);

        mUser.setHeight(0);
        mUser.setSex(-1);
        mUser.setWeight(-1);

        mUser.setImpedance(0);
    }





    /**
     * 转换成btId的方法
     * @param s 要求传入参数中为十六进制字符，即0到f，否则返回null
     */
    public synchronized static String toBtId(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder(s);
        int remainder = s.length() % 7;
        String newStr = "";
        if (remainder > 0) {
            for (int i = 0; i < 7 - remainder; i++) {
                sb.insert(0, "0");
            }
            newStr = sb.toString();
        } else {
            newStr = s;
        }
        sb.setLength(0);
        int multiple = newStr.length() / 7;
        int i = 0;
        while (i * multiple < newStr.length()) {
            //如果单个字节字符串，无法转成16进制数，则返回null
            String singleByte = newStr.substring(i * multiple, (i + 1) * multiple);
            try {
                int aaa = Integer.parseInt(singleByte, 16);
                if (aaa > 255) {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }

            sb.append(singleByte).append(":");
            i++;
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
