package com.project.learningbuddy.model;

import com.google.firebase.Timestamp;

public class Announcements {
    private String classID, announcementTitle, announcementContent, announcementCreator;
    private Timestamp timestamp;

    public Announcements(){

    }

    public Announcements(String classID, String announcementTitle, String announcementContent, String announcementCreator, Timestamp timestamp){
        this.classID = classID;
        this.announcementTitle = announcementTitle;
        this.announcementContent = announcementContent;
        this.announcementCreator = announcementCreator;
        this.timestamp = timestamp;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getAnnouncementTitle() {
        return announcementTitle;
    }

    public void setAnnouncementTitle(String announcementTitle) {
        this.announcementTitle = announcementTitle;
    }

    public String getAnnouncementContent() {
        return announcementContent;
    }

    public void setAnnouncementContent(String announcementContent) {
        this.announcementContent = announcementContent;
    }

    public String getAnnouncementCreator() {
        return announcementCreator;
    }

    public void setAnnouncementCreator(String announcementCreator) {
        this.announcementCreator = announcementCreator;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
