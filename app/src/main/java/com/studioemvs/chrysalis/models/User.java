package com.studioemvs.chrysalis.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {
    public Boolean admin;
    public Boolean instructor;
    public String chrysalisGroup;
    public String chrysalisLevel;
    public int chrysalisPoints;
    public int chrysalisPointsToBeApproved;
    public String emailid;
    public int empid;
    public String personalProjects;
    public RecentActivity recentActivity;
    public Boolean registrationApproved;
    public String uid;
    public String username;

    @IgnoreExtraProperties
    public static class RecentActivity {
        String activity;
        String activityDate;
        String activityKey;
        String adminComments;
        String approval;
        String approvedBy;
        int empid;
        Long id;
        int points;
        String userComments;
        String userid;

        public RecentActivity() {
        }

        public RecentActivity(String activity, int points, String approval, Long id, String activityDate, String userComments, int empid, String approvedBy, String adminComments) {
            this.activity = activity;
            this.points = points;
            this.approval = approval;
            this.id = id;
            this.activityDate = activityDate;
            this.userComments = userComments;
            this.empid = empid;
            this.approvedBy = approvedBy;
            this.adminComments = adminComments;
        }

        public RecentActivity(String userid, String activity, int points, String approval, Long id, String activityDate, String userComments, String activityKey, int empid, String approvedBy, String adminComments) {
            this.userid = userid;
            this.activity = activity;
            this.points = points;
            this.approval = approval;
            this.id = id;
            this.activityDate = activityDate;
            this.userComments = userComments;
            this.activityKey = activityKey;
            this.empid = empid;
            this.approvedBy = approvedBy;
            this.adminComments = adminComments;
        }

        public String getAdminComments() {
            return this.adminComments;
        }

        public String getApprovedBy() {
            return this.approvedBy;
        }

        public int getEmpid() {
            return this.empid;
        }

        public String getUserComments() {
            return this.userComments;
        }

        public String getActivityKey() {
            return this.activityKey;
        }

        public String getActivityDate() {
            return this.activityDate;
        }

        public String getApproval() {
            return this.approval;
        }

        public String getActivity() {
            return this.activity;
        }

        public int getPoints() {
            return this.points;
        }

        public String getUserid() {
            return this.userid;
        }

        public Long getId() {
            return this.id;
        }

        @Exclude
        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap();
            result.put("userid", this.userid);
            result.put("activity", this.activity);
            result.put("actPoints", Integer.valueOf(this.points));
            result.put("approval", this.approval);
            result.put("id", this.id);
            result.put("empid", Integer.valueOf(this.empid));
            result.put("activityDate", this.activityDate);
            result.put("userComments", this.userComments);
            result.put("activityKey", this.activityKey);
            result.put("approvedBy", this.approvedBy);
            result.put("adminComments", this.adminComments);
            return result;
        }
    }

    public User(String emailid, String username, int chrysalisPoints, String chrysalisGroup, String chrysalisLevel, String personalProjects, Boolean admin, Boolean instructor, String uid, Boolean registrationApproved, int chrysalisPointsToBeApproved, int empid) {
        this.emailid = emailid;
        this.username = username;
        this.chrysalisPoints = chrysalisPoints;
        this.chrysalisGroup = chrysalisGroup;
        this.chrysalisLevel = chrysalisLevel;
        this.personalProjects = personalProjects;
        this.admin = admin;
        this.instructor = instructor;
        this.uid = uid;
        this.registrationApproved = registrationApproved;
        this.chrysalisPointsToBeApproved = chrysalisPointsToBeApproved;
        this.empid = empid;
    }

    public User(String emailid, String username, int chrysalisPoints, String chrysalisGroup, String chrysalisLevel, String personalProjects, Boolean admin,Boolean instructor, String uid, RecentActivity recentActivity, Boolean registrationApproved, int chrysalisPointsToBeApproved, int empid) {
        this.emailid = emailid;
        this.username = username;
        this.chrysalisPoints = chrysalisPoints;
        this.chrysalisGroup = chrysalisGroup;
        this.chrysalisLevel = chrysalisLevel;
        this.personalProjects = personalProjects;
        this.admin = admin;
        this.instructor = instructor;
        this.uid = uid;
        this.recentActivity = recentActivity;
        this.registrationApproved = registrationApproved;
        this.chrysalisPointsToBeApproved = chrysalisPointsToBeApproved;
        this.empid = empid;
    }

    public User() {
    }

    public Boolean getInstructor() {
        return instructor;
    }

    public void setInstructor(Boolean instructor) {
        this.instructor = instructor;
    }

    public int getChrysalisPointsToBeApproved() {
        return this.chrysalisPointsToBeApproved;
    }

    public Boolean getRegistrationApproved() {
        return this.registrationApproved;
    }

    public int getEmpid() {
        return this.empid;
    }

    public RecentActivity getRecentActivity() {
        return this.recentActivity;
    }

    public String getPersonalProjects() {
        return this.personalProjects;
    }

    public Boolean getAdmin() {
        return this.admin;
    }

    public String getUid() {
        return this.uid;
    }

    public String getEmailid() {
        return this.emailid;
    }

    public String getUsername() {
        return this.username;
    }

    public int getChrysalisPoints() {
        return this.chrysalisPoints;
    }

    public String getChrysalisGroup() {
        return this.chrysalisGroup;
    }

    public String getChrysalisLevel() {
        return this.chrysalisLevel;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap();
        result.put("emailid", this.emailid);
        result.put("username", this.username);
        result.put("chrysalisPoints", Integer.valueOf(this.chrysalisPoints));
        result.put("chrysalisGroup", this.chrysalisGroup);
        result.put("chrysalisLevel", this.chrysalisLevel);
        result.put("personalProjects", this.personalProjects);
        result.put("admin", this.admin);
        result.put("instructor",this.instructor);
        result.put("uid", this.uid);
        result.put("empid", Integer.valueOf(this.empid));
        result.put("registrationApproved", this.registrationApproved);
        return result;
    }
}
