package com.studioemvs.chrysalis;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

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
        result.put("userid",recentActivity.userid);
        result.put("activity",recentActivity.activity);
        result.put("points",recentActivity.points);
        return result;
    }
    @IgnoreExtraProperties
    public static class RecentActivity {
        String userid;
        String activity;
        int points;
        Boolean approval;
        Long id;

        public RecentActivity() {
        }


        public RecentActivity(String userid, String activity, int points) {
            this.userid = userid;
            this.activity = activity;
            this.points = points;
        }

        public RecentActivity(String activity, int points, Boolean approval, Long id) {
            this.activity = activity;
            this.points = points;
            this.approval = approval;
            this.id = id;
        }

        public RecentActivity(String userid, String activity, int points, Boolean approval,  Long id) {
            this.userid = userid;
            this.activity = activity;
            this.points = points;
            this.approval = approval;
            this.id = id;
        }


        public Boolean getApproval() {
            return approval;
        }


        public String getActivity() {
            return activity;
        }

        public int getPoints() {
            return points;
        }

        public String getUserid() {
            return userid;
        }

        public Long getId() {
            return id;
        }

        @Exclude
        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("userid", userid);
            result.put("activity", activity);
            result.put("points", points);
            result.put("approval",approval);
            result.put("id", id);
            return result;
        }
    }

}
