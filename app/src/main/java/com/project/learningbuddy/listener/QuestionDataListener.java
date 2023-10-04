package com.project.learningbuddy.listener;

import com.project.learningbuddy.model.Questions;

public interface QuestionDataListener {
    void onSuccess(Questions question);
    void onExist(Boolean exist);
    void onFailure(Exception exception);
}
