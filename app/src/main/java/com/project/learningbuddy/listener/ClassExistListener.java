package com.project.learningbuddy.listener;

public interface ClassExistListener {
    void onClassExist(Boolean exist);
    void onFailure(Exception e);
}
