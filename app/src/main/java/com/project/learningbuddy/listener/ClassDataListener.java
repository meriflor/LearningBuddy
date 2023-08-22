package com.project.learningbuddy.listener;

import com.project.learningbuddy.model.Class;

public interface ClassDataListener {
    void onSuccess(Class classes);
    void onFailure(Exception exception);
}
