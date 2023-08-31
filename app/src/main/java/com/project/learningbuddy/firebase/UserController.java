package com.project.learningbuddy.firebase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.learningbuddy.listener.ExistListener;
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

    public static void checkEmailUserTypeExist(String email, String userType, ExistListener existListener){
        firebaseFirestore.collection("users")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                        boolean emailExists = false; // Assume email doesn't exist initially
                        boolean sameType = false;
                        for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                            if(documentSnapshot.getString("email").equals(email)){
                                emailExists = true; // Set to true if a matching email is found
                                if(documentSnapshot.getString("userType").equals(userType)){
                                    sameType = true;
                                    break; // No need to continue checking once a match is found
                                }
                            }
                        }
                        if(!emailExists){
                            existListener.onFailure(new Exception("User does not exist!"));
                        }else if(!sameType){
                            if(userType.equals("Student"))
                                existListener.onFailure(new Exception("This user is a teacher."));
                            else
                                existListener.onFailure(new Exception("This user is a student."));
                        }else{
                            existListener.onExist(true);
                        }
                    }
                });
    }
}
