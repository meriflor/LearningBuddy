package com.project.learningbuddy.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.Posts;

import java.util.HashMap;
import java.util.Map;

public class QuizController {
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static String userID = firebaseAuth.getCurrentUser().getUid();

    public static void createQuiz(String classID, String quizTitle, String quizDesc, MyCompleteListener myCompleteListener){
        DocumentReference classRef = firebaseFirestore.collection("classes").document(classID);

        Map<String, Object> quizData = new HashMap<>();
        quizData.put("quizTitle", quizTitle);
        quizData.put("quizDesc", quizDesc);
        quizData.put("quizCreator", userID);
        quizData.put("visibility", false);
        quizData.put("timestamp", Timestamp.now());

        // Add the announcement as a subcollection under the specific class document
        classRef.collection("quizzes").add(quizData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String quizID = documentReference.getId();
//
                        Posts postData = new Posts();
                        postData.setClassID(classID);
                        postData.setGetID(quizID);
                        postData.setPostType("Quiz");

                        classRef.collection("posts").add(postData)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        myCompleteListener.onSuccess();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        myCompleteListener.onFailure();
                                    }
                                });
                    }
                });
    }
    
    public static void deleteQuiz(String classID, String quizID, MyCompleteListener myCompleteListener){
        firebaseFirestore.collection("classes")
                .document(classID)
                .collection("quizzes")
                .document(quizID)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        CollectionReference postsRef = firebaseFirestore.collection("classes")
                                .document(classID).collection("posts");

                        Query query = postsRef.whereEqualTo("getID", quizID);

                        query.get().addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Delete the document
                                    postsRef.document(document.getId()).delete();
                                }
                            }else{
                                Log.d("TAG", "Error getting documents: ", task.getException());
                            }
                        });
                        myCompleteListener.onSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });

    }

    public static void editQuiz(String classID, String quizID, String title, String desc, MyCompleteListener myCompleteListener){
        Map<String, Object> editQuiz = new HashMap<>();
        editQuiz.put("quizTitle", title);
        editQuiz.put("quizDesc", desc);

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

    public static void quizVisibility(String classID, String quizID, Boolean visibility, MyCompleteListener myCompleteListener){
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
