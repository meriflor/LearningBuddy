package com.project.learningbuddy.model;

public class ClassList {

    private String classID, className, userID, title;

    public ClassList(){

    }

    public ClassList(String classID, String className, String userID, String title) {
        this.classID = classID;
        this.className = className;
        this.userID = userID;
        this.title = title;
    }

    public String getClassID() {
        return classID;
    }

    public String getClassName() {
        return className;
    }

    public String getUserID() {
        return userID;
    }

    public String getTitle() {
        return title;
    }
}
