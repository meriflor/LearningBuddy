package com.project.learningbuddy.firebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.learningbuddy.listener.CheckVisibility;
import com.project.learningbuddy.listener.MyCompleteListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizController {
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static String userID = firebaseAuth.getCurrentUser().getUid();

    public static void createQuiz(String classID, String quizTitle, String quizDesc, MyCompleteListener myCompleteListener){
        DocumentReference classRef = firebaseFirestore.collection("classes").document(classID);

        Map<String, Object> quizData = new HashMap<>();
        quizData.put("quizTitle", quizTitle);
        quizData.put("quizContent", quizDesc);
        quizData.put("quizCreator", userID);
        quizData.put("visibility", false);
        quizData.put("timestamp", Timestamp.now());

        classRef.collection("quizzes").add(quizData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        myCompleteListener.onSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });
    }

    public static void publicQuiz(String classID, String quizID, boolean visibility, MyCompleteListener myCompleteListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference quizRef = db.collection("classes").document(classID).collection("quizzes").document(quizID);

        // Update quiz visibility
        quizRef
                .update("visibility", visibility)
                .addOnSuccessListener(unused -> {
                    if (visibility) {
                        // If making the quiz public, create a corresponding post
                        Map<String, Object> post = new HashMap<>();
                        post.put("classID", classID);
                        post.put("getID", quizID);
                        post.put("postType", "Quiz");
                        post.put("timestamp", Timestamp.now());

                        db.collection("classes")
                                .document(classID)
                                .collection("posts")
                                .add(post)
                                .addOnSuccessListener(documentReference -> myCompleteListener.onSuccess())
                                .addOnFailureListener(e -> myCompleteListener.onFailure());
                    } else {
                        // If making the quiz private, delete the corresponding post
                        db.collection("classes")
                                .document(classID)
                                .collection("posts")
                                .whereEqualTo("getID", quizID)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            document.getReference().delete();
                                        }
                                        myCompleteListener.onSuccess();
                                    } else {
                                        myCompleteListener.onFailure();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> myCompleteListener.onFailure());
    }


    public static void deleteQuiz(String classID, String quizID, MyCompleteListener myCompleteListener){
        firebaseFirestore
                .collection("classes")
                .document(classID)
                .collection("quizzes")
                .document(quizID)
                .collection("questions")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                            documentSnapshot.getReference().delete();
                        }
                        firebaseFirestore
                                .collection("classes")
                                .document(classID)
                                .collection("quizzes")
                                .document(quizID)
                                .delete();
                        firebaseFirestore
                                .collection("classes")
                                .document(classID)
                                .collection("posts")
                                .get()
                                .addOnCompleteListener(tasks -> {
                                    if(tasks.isSuccessful()){
                                        for(QueryDocumentSnapshot documentSnapshot:tasks.getResult()){
                                            if(documentSnapshot.getString("getID").equals(quizID)){
                                                documentSnapshot.getReference().delete();
                                            }
                                        }
                                    }
                                });
                        myCompleteListener.onSuccess();
                    }else{
                        myCompleteListener.onFailure();
                    }
                });

    }

    public static void editQuiz(String classID, String quizID, String title, String desc, MyCompleteListener myCompleteListener){
        Map<String, Object> editQuiz = new HashMap<>();
        editQuiz.put("quizTitle", title);
        editQuiz.put("quizContent", desc);

        firebaseFirestore.collection("classes")
                .document(classID)
                .collection("quizzes")
                .document(quizID)
                .update(editQuiz).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myCompleteListener.onSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });

    }

    public static void checkVisibility(String classID, String quizID, CheckVisibility checkVisibility){
        firebaseFirestore.collection("classes")
                .document(classID)
                .collection("quizzes")
                .document(quizID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        checkVisibility.visibilityCheck(documentSnapshot.getBoolean("visibility"));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        checkVisibility.onFailure(new Exception("Something went wrong!"));
                    }
                });
    }

    public static void editVisibility(String classID, String quizID, Boolean visibility, MyCompleteListener myCompleteListener){
        DocumentReference quizRef = FirebaseFirestore.getInstance()
                .collection("classes")
                .document(classID)
                .collection("quizzes")
                .document(quizID);
        Map<String, Object> vData = new HashMap<>();
        vData.put("visibility", visibility);
        quizRef.update(vData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                myCompleteListener.onSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@org.checkerframework.checker.nullness.qual.NonNull Exception e) {
                myCompleteListener.onFailure();
            }
        });
    }
}
