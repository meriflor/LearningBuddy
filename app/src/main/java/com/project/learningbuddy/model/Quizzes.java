package com.project.learningbuddy.model;

import com.google.firebase.Timestamp;

public class Quizzes {

    private String classID, quizTitle, quizDesc, quizVisibility, quizCreator;
    private Timestamp timestamp;

    public Quizzes(){

    }

    public Quizzes(String classID, String quizTitle, String quizDesc, String quizVisibility, String quizCreator, Timestamp timestamp){
        this.classID = classID;
        this.quizTitle = quizTitle;
        this.quizDesc = quizDesc;
        this.quizVisibility = quizVisibility;
        this.quizCreator = quizCreator;
        this.timestamp = timestamp;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public String getQuizDesc() {
        return quizDesc;
    }

    public void setQuizDesc(String quizDesc) {
        this.quizDesc = quizDesc;
    }

    public String getQuizVisibility() {
        return quizVisibility;
    }

    public void setQuizVisibility(String quizVisibility) {
        this.quizVisibility = quizVisibility;
    }

    public String getQuizCreator() {
        return quizCreator;
    }

    public void setQuizCreator(String quizCreator) {
        this.quizCreator = quizCreator;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
