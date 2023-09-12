package com.project.learningbuddy.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class QuizAttempts {
    private Boolean attempt;
    private String userID;
    private Timestamp start_timestamp, end_timestamp;
    private int score, total;
    private List<Answers> answers;

    public QuizAttempts(){

    }

    public QuizAttempts(Boolean attempt, String userID, Timestamp start_timestamp, Timestamp end_timestamp, int score, int total, List<Answers> answers) {
        this.attempt = attempt;
        this.userID = userID;
        this.start_timestamp = start_timestamp;
        this.end_timestamp = end_timestamp;
        this.score = score;
        this.total = total;
        this.answers = answers;
    }

    public Boolean getAttempt() {
        return attempt;
    }

    public String getUserID() {
        return userID;
    }

    public Timestamp getStart_timestamp() {
        return start_timestamp;
    }

    public Timestamp getEnd_timestamp() {
        return end_timestamp;
    }

    public int getScore() {
        return score;
    }

    public int getTotal() {
        return total;
    }

    public List<Answers> getAnswers() {
        return answers;
    }
}
