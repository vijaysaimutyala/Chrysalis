package com.studioemvs.chrysalis.models;

public class ActivityPointsBean {
    public String activity;
    public int points;

    public ActivityPointsBean(String activity, int points) {
        this.activity = activity;
        this.points = points;
    }

    public ActivityPointsBean() {
    }

    public String getActivity() {
        return this.activity;
    }

    public int getPoints() {
        return this.points;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
