package com.project.learningbuddy.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class Questions {
    public String question, answer;
    public List<String> options;
    public Timestamp timestamp;

    public Questions(){

    }

    public Questions(String question, String answer, List<String> options, Timestamp timestamp) {
        this.question = question;
        this.answer = answer;
        this.options = options;
        this.timestamp = timestamp;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public List<String> getOptions() {
        return options;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
