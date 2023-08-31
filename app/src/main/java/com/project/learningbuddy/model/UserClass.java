package com.project.learningbuddy.model;

import com.google.firebase.Timestamp;

public class UserClass {
    private String userID, userType;
    private Timestamp timestamp;

    public UserClass(){

    }

    public UserClass(String userID, String userType, Timestamp timestamp) {
        this.userID = userID;
        this.userType = userType;
        this.timestamp = timestamp;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserType() {
        return userType;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
