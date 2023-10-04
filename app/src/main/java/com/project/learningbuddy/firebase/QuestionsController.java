package com.project.learningbuddy.firebase;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.listener.QuestionDataListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionsController {
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static String userID = firebaseAuth.getCurrentUser().getUid();

    public static void createQuestion(String classID, String quizID, String question, List<String> options, String answer, MyCompleteListener myCompleteListener){
        CollectionReference quesRef = firebaseFirestore.collection("classes").document(classID)
                .collection("quizzes").document(quizID).collection("questions");
        Map<String, Object> questions = new HashMap<>();
        questions.put("question", question);
        questions.put("options", options);
        questions.put("answer", answer);
        questions.put("timestamp", Timestamp.now());
        quesRef.add(questions)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        myCompleteListener.onSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        myCompleteListener.onFailure();
                    }
                });
    }
    public static void deleteQuestion(String classID, String quizID, String questionID, MyCompleteListener myCompleteListener){
        firebaseFirestore.collection("classes").document(classID)
                .collection("quizzes").document(quizID)
                .collection("questions").document(questionID)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public static void checkQuestionExist(String classID, String quizID, QuestionDataListener questionDataListener){
        firebaseFirestore.collection("classes")
                .document(classID)
                .collection("quizzes")
                .document(quizID)
                .collection("questions")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            Log.d("TAG", "QuestionsExists");
                        } else {
                            questionDataListener.onExist(false);
                        }
                    }else{
                        questionDataListener.onFailure(new Exception("There's a problem on fetching the questions in QuestionsController"));
                    }
                });
    }
}
