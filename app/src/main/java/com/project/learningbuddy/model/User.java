package com.project.learningbuddy.model;

import java.sql.Timestamp;

public class User {
    private String fullName;
    private String email;
    private String userType;
    private com.google.firebase.Timestamp timestamp;

    public User() {
        // Default constructor required for Firestore
    }

    public User(String fullName, String email, String userType, com.google.firebase.Timestamp timestamp) {
        this.fullName = fullName;
        this.email = email;
        this.userType = userType;
        this.timestamp = timestamp;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getUserType() {
        return userType;
    }

    public com.google.firebase.Timestamp getTimestamp() {
        return timestamp;
    }

    // Other getters and setters if needed
}
