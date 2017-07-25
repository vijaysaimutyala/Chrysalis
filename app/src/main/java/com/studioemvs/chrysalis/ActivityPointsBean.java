package com.studioemvs.chrysalis;

/**
 * Created by vijsu on 24-07-2017.
 */

public class ActivityPointsBean {
    String activity;
    int points;

    public ActivityPointsBean() {
    }

    public ActivityPointsBean(String activity, int points) {
        this.activity = activity;
        this.points = points;
    }

    public String getActivity() {
        return activity;
    }

    public int getPoints() {
        return points;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
