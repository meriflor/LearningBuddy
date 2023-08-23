package com.project.learningbuddy.model;

import com.google.firebase.Timestamp;

public class JoinClasses {
    private String classID, studentID, className, classSubject, classYearLevel, classSection;
    private Timestamp timestamp;

    public JoinClasses(){

    }

    public JoinClasses(String classID, String studentID, String className, String classSubject, String classYearLevel, String classSection, Timestamp timestamp) {
        this.classID = classID;
        this.studentID = studentID;
        this.className = className;
        this.classSubject = classSubject;
        this.classYearLevel = classYearLevel;
        this.classSection = classSection;
        this.timestamp = timestamp;
    }

    public String getClassID() {
        return classID;
    }

    public String getStudentID() {
        return studentID;
    }

    public String getClassName() {
        return className;
    }

    public String getClassSection() {
        return classSection;
    }

    public String getClassYearLevel() {
        return classYearLevel;
    }

    public String getClassSubject() {
        return classSubject;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
