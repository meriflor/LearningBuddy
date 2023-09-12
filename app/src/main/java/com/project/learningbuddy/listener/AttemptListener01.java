package com.project.learningbuddy.listener;

import com.project.learningbuddy.model.QuizAttempts;

public interface AttemptListener01 {
    void onSuccess(QuizAttempts attempts);
    void onFailure(Exception e);
}
