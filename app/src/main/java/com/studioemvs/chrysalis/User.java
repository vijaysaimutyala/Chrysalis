package com.studioemvs.chrysalis;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vijsu on 24-06-2017.
 */
@IgnoreExtraProperties
public  class User {
    String emailid;
    String username;
    int chrysalisPoints;
    String chrysalisGroup;
    String chrysalisLevel;
    String currentWork;
    String personalProjects;
    Boolean admin;
    String uid;
    RecentActivity recentActivity;



    public User(String emailid, String username, int chrysalisPoints, String chrysalisGroup, String chrysalisLevel,
                String currentWork, String personalProjects, Boolean admin, String uid) {
        this.emailid = emailid;
        this.username = username;
        this.chrysalisPoints = chrysalisPoints;
        this.chrysalisGroup = chrysalisGroup;
        this.chrysalisLevel = chrysalisLevel;
        this.currentWork = currentWork;
        this.personalProjects = personalProjects;
        this.admin = admin;
        this.uid = uid;
    }

    public User(String emailid, String username, int chrysalisPoints, String chrysalisGroup,
                String chrysalisLevel, String currentWork, String personalProjects, Boolean admin, String uid,
                RecentActivity recentActivity) {
        this.emailid = emailid;
        this.username = username;
        this.chrysalisPoints = chrysalisPoints;
        this.chrysalisGroup = chrysalisGroup;
        this.chrysalisLevel = chrysalisLevel;
        this.currentWork = currentWork;
        this.personalProjects = personalProjects;
        this.admin = admin;
        this.uid = uid;
        this.recentActivity = recentActivity;
    }

    public RecentActivity getRecentActivity() {
        return recentActivity;
    }

    public User() {
    }

    public String getCurrentWork() {
        return currentWork;
    }

    public String getPersonalProjects() {
        return personalProjects;
    }



    public Boolean getAdmin() {
        return admin;
    }

    public String getUid() {
        return uid;
    }

    public String getEmailid() {
        return emailid;
    }

    public String getUsername() {
        return username;
    }

    public int getChrysalisPoints() {
        return chrysalisPoints;
    }

    public String getChrysalisGroup() {
        return chrysalisGroup;
    }

    public String getChrysalisLevel() {
        return chrysalisLevel;
    }


    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("emailid",emailid);
        result.put("username",username);
        result.put("chrysalisPoints",chrysalisPoints);
        result.put("chrysalisGroup",chrysalisGroup);
        result.put("chrysalisLevel",chrysalisLevel);
        result.put("currentWork",currentWork);
        result.put("personalProjects",personalProjects);
        result.put("admin",admin);
        result.put("uid",uid);
        result.put("id",recentActivity.id);
        result.put("activity",recentActivity.activity);
        result.put("points",recentActivity.points);
        return result;
    }
    @IgnoreExtraProperties
    public static class RecentActivity {
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
        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("id", id);
            result.put("activity", activity);
            result.put("points", points);
            return result;
        }
    }

}
