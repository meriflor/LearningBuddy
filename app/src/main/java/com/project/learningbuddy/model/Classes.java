package com.project.learningbuddy.model;

public class Classes {

    private String className;
    private String subjectName;
    private String classYearLevel;
    private String classSection;
    private String accessCode;
    private String ownerTeacherID;
    private int backgroundLayout;
    private com.google.firebase.Timestamp timestamp;

    public Classes(){

    }

    public Classes(String className, String subjectName, String classYearLevel, String classSection, String accessCode, String ownerTeacherID, int backgroundLayout, com.google.firebase.Timestamp timestamp){
        this.className = className;
        this.subjectName = subjectName;
        this.classYearLevel = classYearLevel;
        this.classSection = classSection;
        this.accessCode = accessCode;
        this.ownerTeacherID = ownerTeacherID;
        this.backgroundLayout = backgroundLayout;
        this.timestamp = timestamp;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setClassYearLevel(String classYearLevel) {
        this.classYearLevel = classYearLevel;
    }

    public void setClassSection(String classSection) {
        this.classSection = classSection;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public void setOwnerTeacherID(String ownerTeacherID) {
        this.ownerTeacherID = ownerTeacherID;
    }

    public String getClassName() {
        return className;
    }

    public String getSubjectName(){
        return subjectName;
    }

    public String getClassYearLevel(){
        return  classYearLevel;
    }

    public String getClassSection() {
        return classSection;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public String getOwnerTeacherID() {
        return ownerTeacherID;
    }

    public com.google.firebase.Timestamp getTimestamp() {
        return timestamp;
    }

    public int getBackgroundLayout() {
        return backgroundLayout;
    }

    public void setTimestamp(com.google.firebase.Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
