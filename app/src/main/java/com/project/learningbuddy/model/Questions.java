package com.project.learningbuddy.model;

import java.util.List;

public class Questions {
    public String question, answer;
    public List<String> options;

    public Questions(){

    }

    public Questions(String question, String answer, List<String> options) {
        this.question = question;
        this.answer = answer;
        this.options = options;
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
}
