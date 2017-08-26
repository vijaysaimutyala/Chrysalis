package com.studioemvs.chrysalis;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vijsu on 11-08-2017.
 */
@IgnoreExtraProperties
public class NewsForUser {
    String news;

    public NewsForUser(String news) {
        this.news = news;
    }

    public NewsForUser() {
    }

    public String getNews() {
        return news;
    }

    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("news",news);
        return result;
    }
}
