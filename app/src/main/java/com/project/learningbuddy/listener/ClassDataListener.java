package com.project.learningbuddy.listener;

import com.project.learningbuddy.model.Classes;

public interface ClassDataListener {
    void onSuccess(Classes classes);
    void onFailure(Exception exception);
}
