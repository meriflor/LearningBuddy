package com.project.learningbuddy.firebase;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.listener.UserDataListener;
import com.project.learningbuddy.model.User;

import java.util.HashMap;
import java.util.Map;

public class UserController {
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static void UserController(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public static void createUser(String email, String fullName, String userType, MyCompleteListener myCompleteListener){
        String userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference userDoc = firebaseFirestore.collection("users").document(userID);
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("fullName", fullName);
        userData.put("userType", userType);
        userData.put("timestamp", Timestamp.now());

        userDoc.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                myCompleteListener.onSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                myCompleteListener.onFailure();
            }
        });
    }

//    public static void displayUser(String uid){
//        DocumentReference userDoc = firebaseFirestore.collection("users").document(uid);
//        Map<String, Object> userData = new HashMap<>();
//
//    }

    public static void getUserData(String uid, UserDataListener listener) {
        DocumentReference userDoc = firebaseFirestore.collection("users").document(uid);

        userDoc.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                listener.onSuccess(user);
            } else {
                listener.onFailure(new Exception("User not found"));
            }
        }).addOnFailureListener(listener::onFailure);
    }
}
