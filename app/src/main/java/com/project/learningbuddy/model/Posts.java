package com.project.learningbuddy.model;

import com.google.firebase.Timestamp;

public class Posts {

    private String classID, getID, postType;
    private Timestamp timestamp;

    public Posts(){

    }

    public Posts(String classID, String getID, String postType, Timestamp timestamp){
        this.classID = classID;
        this.getID = getID;
        this.postType = postType;
        this.timestamp = timestamp;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getGetID() {
        return getID;
    }

    public void setGetID(String getID) {
        this.getID = getID;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
