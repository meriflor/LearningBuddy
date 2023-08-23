package com.project.learningbuddy.firebase;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.learningbuddy.listener.ClassDataListener;
import com.project.learningbuddy.listener.ClassExistListener;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.Classes;
import com.project.learningbuddy.model.JoinClasses;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ClassController {
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static void createClass(String className, String subjectName, String classYearLevel, String classSection, String ownerTeacherID, ClassDataListener classDataListener){
        DocumentReference classRef = firebaseFirestore
                .collection("classes")
                .document();

        Classes classes = new Classes();
        classes.setClassName(className);
        classes.setSubjectName(subjectName);
        classes.setClassYearLevel(classYearLevel);
        classes.setClassSection(classSection);
        classes.setAccessCode(classRef.getId());
        classes.setOwnerTeacherID(ownerTeacherID);
        classes.setTimestamp(Timestamp.now());

        classRef.set(classes).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                classDataListener.onSuccess(classes); // Pass the created Classes instance
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                classDataListener.onFailure(new Exception("Something went wrong!"));
            }
        });
    }

    public static void checkClassExist(String accessCode, ClassExistListener classExistenceListener) {
        firebaseFirestore.collection("classes")
                .whereEqualTo("accessCode", accessCode)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            classExistenceListener.onClassExist(true);
                        } else {
                            classExistenceListener.onClassExist(false);
                        }
                    } else {
                        // Error occurred while checking class existence
                        classExistenceListener.onFailure(task.getException());
                    }
                });
    }

    public static void joinClass(String classID, String studentID, MyCompleteListener myCompleteListener){
        Map<String, Object> studentData = new HashMap<>();
        studentData.put("studentID", studentID);
        studentData.put("timestamp", FieldValue.serverTimestamp()); // Timestamp when the student joined

        FirebaseFirestore.getInstance()
                .collection("classes")
                .document(classID)
                .collection("students")
                .document(studentID)
                .set(studentData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        myCompleteListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });

        firebaseFirestore.collection("classes").document(classID)
                .get().addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        String className = documentSnapshot.getString("className");
                        String classSubject = documentSnapshot.getString("classSubject");
                        String classYearLevel = documentSnapshot.getString("classYearLevel");
                        String classSection = documentSnapshot.getString("classSection");

                        // Create a UserClasses object
                        JoinClasses joinClasses = new JoinClasses(
                                classID,
                                studentID,
                                className,
                                classSubject,
                                classYearLevel,
                                classSection,
                                Timestamp.now()
                        );

                        firebaseFirestore.collection("student_class")
                                .add(joinClasses)
                                .addOnSuccessListener(documentReference -> {
                                    if(myCompleteListener != null){
                                        myCompleteListener.onSuccess();
                                    }
                                }).addOnFailureListener(e -> {
                                    if(myCompleteListener != null){
                                        myCompleteListener.onFailure();
                                    }
                                });
                    }else{
                        if(myCompleteListener != null){
                            myCompleteListener.onFailure();
                        }
                    }


                }).addOnFailureListener(e -> {
                    if (myCompleteListener != null) {
                        myCompleteListener.onFailure();
                    }
                });

    }

}
