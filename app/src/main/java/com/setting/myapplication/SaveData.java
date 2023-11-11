package com.setting.myapplication;

import java.io.Serializable;

public class SaveData implements Serializable {
    double roll1;
    double pitch1;
    double yaw1;

    double roll2;
    double pitch2;
    double yaw2;

    double roll3;
    double pitch3;
    double yaw3;

    double roll4;
    double pitch4;
    double yaw4;

    double depth;
    double kd;

    public SaveData(double roll1, double pitch1, double yaw1, double roll2, double pitch2, double yaw2, double roll3, double pitch3, double yaw3, double roll4, double pitch4, double yaw4, double depth) {
        this.roll1 = roll1;
        this.pitch1 = pitch1;
        this.yaw1 = yaw1;
        this.roll2 = roll2;
        this.pitch2 = pitch2;
        this.yaw2 = yaw2;
        this.roll3 = roll3;
        this.pitch3 = pitch3;
        this.yaw3 = yaw3;
        this.roll4 = roll4;
        this.pitch4 = pitch4;
        this.yaw4 = yaw4;
        this.depth = depth;
        this.kd = kd;
    }

    public double getRoll1() {
        return roll1;
    }

    public void setRoll1(double roll1) {
        this.roll1 = roll1;
    }

    public double getPitch1() {
        return pitch1;
    }

    public void setPitch1(double pitch1) {
        this.pitch1 = pitch1;
    }

    public double getYaw1() {
        return yaw1;
    }

    public void setYaw1(double yaw1) {
        this.yaw1 = yaw1;
    }

    public double getRoll2() {
        return roll2;
    }

    public void setRoll2(double roll2) {
        this.roll2 = roll2;
    }

    public double getPitch2() {
        return pitch2;
    }

    public void setPitch2(double pitch2) {
        this.pitch2 = pitch2;
    }

    public double getYaw2() {
        return yaw2;
    }

    public void setYaw2(double yaw2) {
        this.yaw2 = yaw2;
    }

    public double getRoll3() {
        return roll3;
    }

    public void setRoll3(double roll3) {
        this.roll3 = roll3;
    }

    public double getPitch3() {
        return pitch3;
    }

    public void setPitch3(double pitch3) {
        this.pitch3 = pitch3;
    }

    public double getYaw3() {
        return yaw3;
    }

    public void setYaw3(double yaw3) {
        this.yaw3 = yaw3;
    }

    public double getRoll4() {
        return roll4;
    }

    public void setRoll4(double roll4) {
        this.roll4 = roll4;
    }

    public double getPitch4() {
        return pitch4;
    }

    public void setPitch4(double pitch4) {
        this.pitch4 = pitch4;
    }

    public double getYaw4() {
        return yaw4;
    }

    public void setYaw4(double yaw4) {
        this.yaw4 = yaw4;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public double getKd() {
        return kd;
    }

    public void setKd(double kd) {
        this.kd = kd;
    }
}
