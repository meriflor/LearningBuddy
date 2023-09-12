package com.project.learningbuddy.firebase;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.learningbuddy.listener.AttemptListener;
import com.project.learningbuddy.listener.AttemptListener01;
import com.project.learningbuddy.listener.ExistListener;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.Answers;
import com.project.learningbuddy.model.QuizAttempts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizAttemptController {
    private static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static String userID = firebaseAuth.getCurrentUser().getUid();

    public static void startAttempt(String classID, String quizID, AttemptListener attemptListener){
        Map<String, Object> attempts = new HashMap<>();
        attempts.put("userID", userID);
        attempts.put("attempt", true);
        attempts.put("score", 0);
        attempts.put("start_timestamp", Timestamp.now());

        firebaseFirestore
                .collection("classes")
                .document(classID)
                .collection("quizzes")
                .document(quizID)
                .collection("student_attempts")
                .add(attempts)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentReference documentReference = task.getResult();
                        String docID = documentReference.getId();
                        attemptListener.getDocID(docID);
                    }else{
                        attemptListener.onFailure();
                    }
                });
    }

    public static void checkAttempt(String classID, String quizID, ExistListener existListener){
        firebaseFirestore.collection("classes")
                .document(classID)
                .collection("quizzes")
                .document(quizID)
                .collection("student_attempts")
                .whereEqualTo("userID", userID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Check if there are any documents that match the criteria
                        boolean hasAttempts = !task.getResult().isEmpty();
                        existListener.onExist(hasAttempts);
                    } else {
                        existListener.onExist(false);
                    }
                });
    }


    public static void endAttempt(String classID, String quizID, String attemptID, List<Answers> answers, int score, int questionSize, MyCompleteListener myCompleteListener){
        Map<String, Object> attempt = new HashMap<>();
        attempt.put("score", score);
        attempt.put("answers", answers);
        attempt.put("total", questionSize);
        attempt.put("end_timestamp", Timestamp.now());

        firebaseFirestore.collection("classes")
                .document(classID)
                .collection("quizzes")
                .document(quizID)
                .collection("student_attempts")
                .document(attemptID)
                .update(attempt)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        myCompleteListener.onSuccess();
                    }else{
                        myCompleteListener.onFailure();
                    }
                });

    }

    public static void checkAttemptScores(String classID, String quizID, int questionSize, ExistListener existListener){
        firebaseFirestore.collection("classes")
                .document(classID)
                .collection("quizzes")
                .document(quizID)
                .collection("student_attempts")
                .whereEqualTo("score", questionSize)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Check if there are any documents that match the criteria
                        boolean hasAttempts = !task.getResult().isEmpty();
                        existListener.onExist(hasAttempts);
                    } else {
                        existListener.onExist(false);
                    }
                });
    }

    public static void getQuizAttemptData(String classID, String quizID, AttemptListener01 listener) {
        Query attemptQuery = firebaseFirestore.collection("classes")
                        .document(classID).collection("quizzes")
                        .document(quizID).collection("student_attempts")
                        .whereEqualTo("userID", userID);

        attemptQuery.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                    QuizAttempts quizAttempts = documentSnapshot.toObject(QuizAttempts.class);
                    listener.onSuccess(quizAttempts);
                }
            }else{
                Log.d("TAG", "Error occurred while fetching the quiz attempts data");
            }
        });
    }
}
