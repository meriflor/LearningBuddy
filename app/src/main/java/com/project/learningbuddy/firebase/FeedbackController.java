package com.project.learningbuddy.firebase;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.listener.UserDataListener;
import com.project.learningbuddy.model.User;

import java.util.HashMap;
import java.util.Map;

public class FeedbackController {
    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private static String userID = firebaseAuth.getCurrentUser().getUid();

    public static void createFeedbackMessage(String message, MyCompleteListener myCompleteListener){
        UserController.getUserData(userID, new UserDataListener() {
            @Override
            public void onSuccess(User user) {
                Map<String, Object> feedback = new HashMap<>();
                feedback.put("message", message);
                feedback.put("userID", userID);
                feedback.put("userName", user.getFullName());
                feedback.put("timestamp", Timestamp.now());
                feedback.put("userType", user.getUserType());
                firebaseFirestore.collection("feedbacks")
                        .document()
                        .set(feedback)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                myCompleteListener.onSuccess();
                            }else{
                                myCompleteListener.onFailure();
                            }
                        });
            }

            @Override
            public void onFailure(Exception exception) {
                Log.d("TAG", exception.getMessage());
            }
        });
    };
}
