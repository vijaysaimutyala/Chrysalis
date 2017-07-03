package com.studioemvs.chrysalis;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vijsu on 03-07-2017.
 */
@IgnoreExtraProperties
public class RecentActivity {
    String id;
    String activity;
    int points;

    public RecentActivity() {
    }

    public RecentActivity(String id, String activity, int points) {
        this.id = id;
        this.activity = activity;
        this.points = points;
    }

    public String getId() {
        return id;
    }

    public String getActivity() {
        return activity;
    }

    public int getPoints() {
        return points;
    }
    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("id",id);
        result.put("activity",activity);
        result.put("points",points);
        return result;
    }
}

