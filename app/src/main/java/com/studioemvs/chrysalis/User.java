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
    String personalProjects;
    Boolean admin;
    String uid;
    RecentActivity recentActivity;
    Boolean registrationApproved;
    //String chrysalisSublevel;
    int chrysalisPointsToBeApproved;
    int empid;


    public User(String emailid, String username, int chrysalisPoints, String chrysalisGroup,
                String chrysalisLevel, String personalProjects, Boolean admin, String uid,
                Boolean registrationApproved, int chrysalisPointsToBeApproved, int empid) {
        this.emailid = emailid;
        this.username = username;
        this.chrysalisPoints = chrysalisPoints;
        this.chrysalisGroup = chrysalisGroup;
        this.chrysalisLevel = chrysalisLevel;
        this.personalProjects = personalProjects;
        this.admin = admin;
        this.uid = uid;
        this.registrationApproved = registrationApproved;
        //this.chrysalisSublevel = chrysalisSublevel;
        this.chrysalisPointsToBeApproved = chrysalisPointsToBeApproved;
        this.empid = empid;
    }

    public User(String emailid, String username, int chrysalisPoints, String chrysalisGroup,
                String chrysalisLevel, String personalProjects, Boolean admin, String uid, RecentActivity recentActivity,
                Boolean registrationApproved, int chrysalisPointsToBeApproved, int empid) {
        this.emailid = emailid;
        this.username = username;
        this.chrysalisPoints = chrysalisPoints;
        this.chrysalisGroup = chrysalisGroup;
        this.chrysalisLevel = chrysalisLevel;
        this.personalProjects = personalProjects;
        this.admin = admin;
        this.uid = uid;
        this.recentActivity = recentActivity;
        this.registrationApproved = registrationApproved;
        //this.chrysalisSublevel = chrysalisSublevel;
        this.chrysalisPointsToBeApproved = chrysalisPointsToBeApproved;
        this.empid = empid;
    }

    public int getChrysalisPointsToBeApproved() {
        return chrysalisPointsToBeApproved;
    }

    public Boolean getRegistrationApproved() {
        return registrationApproved;
    }

    public int getEmpid() {
        return empid;
    }

    public RecentActivity getRecentActivity() {
        return recentActivity;
    }

    public User() {
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
        result.put("personalProjects",personalProjects);
        result.put("admin",admin);
        result.put("uid",uid);
        result.put("empid",empid);
        result.put("registrationApproved",registrationApproved);
        //result.put("chrysalisSublevel",chrysalisSublevel);
/*        result.put("userid",recentActivity.userid);
        result.put("activity",recentActivity.activity);
        result.put("points",recentActivity.points);*/
        return result;
    }
    @IgnoreExtraProperties
    public static class RecentActivity {
        String userid;
        String activity;
        int points;
        Boolean approval;
        Long id;
        String activityDate;
        String userComments;
        String activityKey;

        public RecentActivity() {
        }



        public RecentActivity(String userid, String activity, int points,
                              String activityDate, String userComments, String activityKey) {
            this.userid = userid;
            this.activity = activity;
            this.points = points;
            this.activityDate = activityDate;
            this.userComments = userComments;
            this.activityKey = activityKey;
        }

        public RecentActivity(String activity, int points, Boolean approval, Long id, String activityDate,
                              String userComments) {
            this.activity = activity;
            this.points = points;
            this.approval = approval;
            this.id = id;
            this.activityDate = activityDate;
            this.userComments = userComments;
        }


        public RecentActivity(String userid, String activity, int points, Boolean approval,
                              Long id, String activityDate, String userComments, String activityKey) {
            this.userid = userid;
            this.activity = activity;
            this.points = points;
            this.approval = approval;
            this.id = id;
            this.activityDate = activityDate;
            this.userComments = userComments;
            this.activityKey = activityKey;

        }

        public RecentActivity(String userid, String activity, int points, Boolean approval,
                              Long id, String activityDate, String userComments) {
            this.userid = userid;
            this.activity = activity;
            this.points = points;
            this.approval = approval;
            this.id = id;
            this.activityDate = activityDate;
            this.userComments = userComments;
        }

        public String getUserComments() {
            return userComments;
        }

        public String getActivityKey() {
            return activityKey;
        }

        public String getActivityDate() {
            return activityDate;
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
            result.put("activityDate",activityDate);
            result.put("userComments", userComments);
            result.put("activityKey",activityKey);
            return result;
        }
    }

}
