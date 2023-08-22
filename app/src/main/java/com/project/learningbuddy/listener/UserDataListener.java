package com.project.learningbuddy.listener;


import com.project.learningbuddy.model.User;

public interface UserDataListener {
    void onSuccess(User user);
    void onFailure(Exception exception);
}
