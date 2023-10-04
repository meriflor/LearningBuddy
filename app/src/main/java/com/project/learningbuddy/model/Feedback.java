package com.project.learningbuddy.model;

import com.google.firebase.Timestamp;

public class Feedback {
    private String message, userID, userType, userName;
    private Timestamp timestamp;

    public Feedback() {
    }

    public Feedback(String message, String userID, String userType, String userName, Timestamp timestamp) {
        this.message = message;
        this.userID = userID;
        this.userType = userType;
        this.userName = userName;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserType() {
        return userType;
    }

    public String getUserName(){
        return userName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
