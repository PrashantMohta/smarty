package com.mtk.data;

import java.util.ArrayList;

public class Sport_Data {
    public static final ArrayList<String> mSport_Data_Fiels = new C05611();
    private int mDistance;
    private int mKcal;
    private int mSteps;
    private String mTime;

    static class C05611 extends ArrayList<String> {
        C05611() {
            add("steps");
            add("distance");
            add("kcal");
            add("time");
        }
    }

    public int getSteps() {
        return this.mSteps;
    }

    public void setSteps(int steps) {
        this.mSteps = steps;
    }

    public int getDistance() {
        return this.mDistance;
    }

    public void setDistance(int distance) {
        this.mDistance = distance;
    }

    public int getKcal() {
        return this.mKcal;
    }

    public void setKcal(int kcal) {
        this.mKcal = kcal;
    }

    public String getTime() {
        return this.mTime;
    }

    public void setTime(String time) {
        this.mTime = time;
    }
}
