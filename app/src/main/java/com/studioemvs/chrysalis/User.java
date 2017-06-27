package com.studioemvs.chrysalis;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vijsu on 24-06-2017.
 */

public class User {
    String emailid;
    String username;
    int chrysalisPoints;
    String chrysalisGroup;
    String chrysalisLevel;

    public User() {
    }


    public User(String emailid, String username, int chrysalisPoints, String chrysalisGroup, String chrysalisLevel) {
        this.emailid = emailid;
        this.username = username;
        this.chrysalisPoints = chrysalisPoints;
        this.chrysalisGroup = chrysalisGroup;
        this.chrysalisLevel = chrysalisLevel;
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
        return result;
    }
}
