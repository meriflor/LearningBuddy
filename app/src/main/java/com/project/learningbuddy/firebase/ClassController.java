package com.project.learningbuddy.firebase;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.learningbuddy.listener.ClassDataListener;
import com.project.learningbuddy.listener.ExistListener;
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
                Map<String, Object> teachers = new HashMap<>();
                teachers.put("teacherID", firebaseAuth.getCurrentUser().getUid());
                teachers.put("teacherType", "Adviser");
                teachers.put("timestamp", Timestamp.now());
                firebaseFirestore
                        .collection("classes")
                        .document(classRef.getId())
                        .collection("teachers")
                        .add(teachers)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        classDataListener.onSuccess(classes); // Pass the created Classes instance
                                        firebaseFirestore.collection("classes")
                                                .document(documentReference.getId())
                                                .collection("teachers")
                                                .add(teachers);
                                    }
                                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                classDataListener.onFailure(new Exception("Something went wrong!"));
            }
        });
    }

    public static void checkClassExist(String accessCode, ExistListener classExistenceListener) {
        firebaseFirestore.collection("classes")
                .whereEqualTo("accessCode", accessCode)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            classExistenceListener.onExist(true);
                        } else {
                            classExistenceListener.onExist(false);
                        }
                    } else {
                        // Error occurred while checking class existence
                        classExistenceListener.onFailure(task.getException());
                    }
                });
    }

//    Checking if you have already joined the class
    public static void checkClassJoinedExist(String email, ExistListener existListener){

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

    public static void updateClass(String classID, String className, String subjectName, String yearLevel, String section, MyCompleteListener myCompleteListener){
        Map<String, Object> classes = new HashMap<>();
        classes.put("className", className);
        classes.put("subjectName", subjectName);
        classes.put("classYearLevel", yearLevel);
        classes.put("classSection", section);

        firebaseFirestore.collection("classes")
                .document(classID)
                .update(classes)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myCompleteListener.onSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });
    }

    public static void deleteClass(String classID, MyCompleteListener myCompleteListener){
        firebaseFirestore.collection("classes")
                .document(classID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseFirestore.collection("student_class")
                                .whereEqualTo("classID", classID)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            documentSnapshot.getReference().delete();
                                        }
                                        myCompleteListener.onSuccess();
                                    }
                                });
                        firebaseFirestore.collection("announcements")
                                .whereEqualTo("classID", classID)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            documentSnapshot.getReference().delete();
                                        }
                                        myCompleteListener.onSuccess();
                                    }
                                });
                        firebaseFirestore.collection("quizzes")
                                .whereEqualTo("classID", classID)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                        firebaseFirestore.collection("student_class")
//                                                .whereEqualTo("classID", classID)
//                                                .get()
//                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                                    @Override
//                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                                                            documentSnapshot.getReference().delete();
//                                                        }
//                                                        myCompleteListener.onSuccess();
//                                                    }
//                                                });
                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            documentSnapshot.getReference().delete();
                                        }
                                        myCompleteListener.onSuccess();
                                    }
                                });
                        firebaseFirestore.collection("posts")
                                .whereEqualTo("classID", classID)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            documentSnapshot.getReference().delete();
                                        }
                                        myCompleteListener.onSuccess();
                                    }
                                });
                        firebaseFirestore.collection("learning_materials")
                                .whereEqualTo("classID", classID)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            documentSnapshot.getReference().delete();
                                        }
                                        myCompleteListener.onSuccess();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        myCompleteListener.onFailure();
                    }
                });
    }

    public static void addUserToClass(String email, String classID, String userType, MyCompleteListener myCompleteListener){
        firebaseFirestore.collection("users")
                .whereEqualTo("email", email)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String userID = documentSnapshot.getId();
                            Map<String, Object> user = new HashMap<>();
                            user.put("userID", userID);
                            user.put("timestamp", Timestamp.now());

                            if (userType.equals("Teacher")) {
                                user.put("userType", "Teacher");
                            } else if (userType.equals("Student")) {
                                user.put("userType", "Student");
                            }

                            String collection = userType.equals("Teacher") ? "teachers" : "students";

                            firebaseFirestore.collection("classes")
                                    .document(classID)
                                    .collection(collection)
                                    .add(user)
                                    .addOnSuccessListener(documentReference -> myCompleteListener.onSuccess())
                                    .addOnFailureListener(e -> myCompleteListener.onFailure());
                        }
                    }
                });
    }

    public static void removeUser(String classID, String userType, String userClassID, MyCompleteListener myCompleteListener){
        firebaseFirestore
                .collection("classes")
                .document(classID)
                .collection(userType)
                .document(userClassID)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myCompleteListener.onSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });
    }
}
