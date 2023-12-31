package com.project.learningbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.window.SplashScreen;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.project.learningbuddy.ui.auth.LoginActivity;
import com.project.learningbuddy.ui.auth.RegistrationActivity;
import com.project.learningbuddy.useractivity.Homepage;

public class Splashcreen extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2000; // milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashcreen);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(Splashcreen.this, Homepage.class));
            finish();
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Start the main activity
                    Intent intent = new Intent(Splashcreen.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Close the splash activity
                }
            }, SPLASH_DELAY);
        }
    }
}