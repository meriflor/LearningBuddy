package com.project.learningbuddy.firebase;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.Posts;
import com.project.learningbuddy.model.Quizzes;

public class QuizController {
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static void createQuiz(String classID, String quizTitle, String quizDesc, MyCompleteListener myCompleteListener){
        DocumentReference quizRef = firebaseFirestore.collection("quizzes").document();

        Quizzes quizzes = new Quizzes();
        quizzes.setClassID(classID);
        quizzes.setQuizTitle(quizTitle);
        quizzes.setQuizDesc(quizDesc);
        quizzes.setQuizVisibility("Private");
        quizzes.setQuizCreator(firebaseAuth.getCurrentUser().getUid());
        quizzes.setTimestamp(Timestamp.now());

        quizRef.set(quizzes).addOnSuccessListener(new OnSuccessListener<Void>() {
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

        DocumentReference postRef = firebaseFirestore.collection("posts").document();
        Posts posts = new Posts();
        posts.setClassID(classID);
        posts.setPostType("Quiz");
        posts.setPostTitle(quizTitle);
        posts.setPostContent(quizDesc);
        posts.setPostCreatorID(firebaseAuth.getCurrentUser().getUid());
        posts.setTimestamp(Timestamp.now());

        postRef.set(posts).addOnSuccessListener(new OnSuccessListener<Void>() {
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
}
