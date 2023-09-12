package com.project.learningbuddy.model;

public class Answers {
    private String question, answer;
    private Boolean correct;

    public Answers(){

    }

    public Answers(String question, String answer, Boolean correct) {
        this.question = question;
        this.answer = answer;
        this.correct = correct;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }
}
