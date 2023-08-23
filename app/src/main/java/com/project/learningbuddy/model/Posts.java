package com.project.learningbuddy.model;

import com.google.firebase.Timestamp;

public class Posts {

    private String classID, postType, postTitle, postContent, postCreatorID;
    private Timestamp timestamp;

    public Posts(){

    }

    public Posts(String classID, String postType, String postTitle, String postContent, String postCreatorID, Timestamp timestamp){
        this.classID = classID;
        this.postType = postType;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postCreatorID = postCreatorID;
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

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getPostCreatorID() {
        return postCreatorID;
    }

    public void setPostCreatorID(String postCreatorID) {
        this.postCreatorID = postCreatorID;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
