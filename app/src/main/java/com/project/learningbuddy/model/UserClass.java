package com.project.learningbuddy.model;

import com.google.firebase.Timestamp;

public class UserClass {
    private String userID, userType, title;
    private Timestamp timestamp;

    public UserClass(){

    }

    public UserClass(String userID, String userType, String title, Timestamp timestamp) {
        this.userID = userID;
        this.userType = userType;
        this.title = title;
        this.timestamp = timestamp;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserType() {
        return userType;
    }

    public String getTitle() {
        return title;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
