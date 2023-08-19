package com.project.learningbuddy;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class LearningBuddy extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}

