package com.project.learningbuddy.model;

public class TeacherClass {

    private String classID, className, teacherID, title;

    public TeacherClass(){

    }

    public TeacherClass(String classID, String className, String teacherID, String title) {
        this.classID = classID;
        this.className = className;
        this.teacherID = teacherID;
        this.title = title;
    }

    public String getClassID() {
        return classID;
    }

    public String getClassName() {
        return className;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public String getTitle() {
        return title;
    }
}
