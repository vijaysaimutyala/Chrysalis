package com.studioemvs.chrysalis.models;

import android.support.annotation.Keep;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.util.HashMap;
import java.util.Map;

@Keep
@IgnoreExtraProperties
public class ActivitiesBean {
    public int actPoints;
    public String activity;
    public String adminid;
    public String chrysalisGroup;
    public String date;

    public ActivitiesBean(String activity, int actPoints, String chrysalisGroup, String adminid, String date) {
        this.activity = activity;
        this.actPoints = actPoints;
        this.chrysalisGroup = chrysalisGroup;
        this.adminid = adminid;
        this.date = date;
    }

    public ActivitiesBean() {
    }

    @Exclude
    public String getActivity() {
        return this.activity;
    }

    @Exclude
    public int getActPoints() {
        return this.actPoints;
    }

    @Exclude
    public String getChrysalisGroup() {
        return this.chrysalisGroup;
    }

    @Exclude
    public String getAdminid() {
        return this.adminid;
    }

    @Exclude
    public String getDate() {
        return this.date;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap();
        result.put("activity", this.activity);
        result.put("actPoints", Integer.valueOf(this.actPoints));
        result.put("adminid", this.adminid);
        result.put("chrysalisGroup", this.chrysalisGroup);
        result.put("date", this.date);
        return result;
    }
}
