package com.project.learningbuddy.firebase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.learningbuddy.listener.ClassDataListener;
import com.project.learningbuddy.model.Class;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassController {
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static void createClass(String className, String subjectName, String classYearLevel, String classSection, String ownerTeacherID, ClassDataListener classDataListener){
        DocumentReference classRef = firebaseFirestore
                .collection("classes")
                .document();

        Class classes = new Class();
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
                classDataListener.onSuccess(classes); // Pass the created Class instance
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                classDataListener.onFailure(new Exception("Something went wrong!"));
            }
        });
    }
}
