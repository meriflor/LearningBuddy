package com.project.learningbuddy.ui.student.quizzes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.PerfectScoresAdapter;
import com.project.learningbuddy.adapter.StudentScoresAdapter;
import com.project.learningbuddy.firebase.UserController;
import com.project.learningbuddy.listener.UserDataListener;
import com.project.learningbuddy.model.QuizAttempts;
import com.project.learningbuddy.model.User;
import com.project.learningbuddy.useractivity.Homepage;

public class ViewQuizResultActivity extends AppCompatActivity {

    public static final String CLASSID = "Class ID";
    public static final String QUESTIONSIZE = "Question size";
    public static final String QUIZID = "Quiz ID";

    public String classID, quizID;
    public int score, questionSize;

    public ImageButton home;
    public RecyclerView perfectView, recordView;
    public TextView userName, scoreText, ptsText, recordText;
    public View horizontalView;
    public StudentScoresAdapter recordAdapter;
    public PerfectScoresAdapter perfectAdapter;

    public FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_score);

        Intent intent = getIntent();
        classID = intent.getStringExtra(CLASSID);
        score = intent.getIntExtra("score", -1);
        questionSize = intent.getIntExtra("questionSize", -1);
        quizID = intent.getStringExtra(QUIZID);

        userName = findViewById(R.id.userName_text);
        scoreText = findViewById(R.id.score_text);
        ptsText = findViewById(R.id.pts_text);
        horizontalView = findViewById(R.id.horizontal_view);
        recordText = findViewById(R.id.records_text);
        home = findViewById(R.id.home_btn);
        perfectView = findViewById(R.id.perfect_recyclerView);
        recordView = findViewById(R.id.records_recyclerView);

        scoreText.setText(String.valueOf(score));
        if(score>1)
            ptsText.setText("pts.");
        else
            ptsText.setText("pt.");

        home.setOnClickListener(view -> {
            Intent intent1 = new Intent(ViewQuizResultActivity.this, Homepage.class);
            startActivity(intent1);
        });

        getUserName();
//        displayPerfectScores();
        displayStudentScores();
        Log.d("TAG", "classID: "+classID+", quizID: "+quizID);
    }

    private void displayStudentScores() {
        Query perfectScoreQuery = firebaseFirestore.collection("classes")
                .document(classID)
                .collection("quizzes")
                .document(quizID)
                .collection("student_attempts")
                .whereEqualTo("score", questionSize)
                .orderBy("score", Query.Direction.DESCENDING)
                .orderBy("end_timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<QuizAttempts> options = new FirestoreRecyclerOptions.Builder<QuizAttempts>()
                .setQuery(perfectScoreQuery, QuizAttempts.class).build();
        perfectAdapter = new PerfectScoresAdapter(options);
        perfectView.setLayoutManager(new LinearLayoutManager(this));
        perfectView.setAdapter(perfectAdapter);
//        =====================================
        Query scoreQuery = firebaseFirestore.collection("classes")
                .document(classID)
                .collection("quizzes")
                .document(quizID)
                .collection("student_attempts")
                .whereLessThan("score", questionSize)
                .orderBy("score", Query.Direction.DESCENDING)
                .orderBy("end_timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<QuizAttempts> options1 = new FirestoreRecyclerOptions.Builder<QuizAttempts>()
                .setQuery(scoreQuery, QuizAttempts.class).build();
        recordAdapter = new StudentScoresAdapter(options1);
        recordView.setLayoutManager(new LinearLayoutManager(this));
        recordView.setAdapter(recordAdapter);


        perfectScoreQuery.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                    Log.d("scorePERFECT", documentSnapshot.getId());
                }
                if(task.getResult().isEmpty()){
                    perfectView.setVisibility(View.GONE);
                }else{
                    Boolean perfectScoreExist = true;
                    Log.d("TAG", "THE PERFECT EXIST though");
                    scoreQuery.get().addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                                Log.d("scoreRECORD", documentSnapshot.getId());
                            }

                            if(task1.getResult().isEmpty()){
                                recordView.setVisibility(View.GONE);
                            }else{
                                Boolean otherRecordsExist = true;
                                Log.d("TAG", "THE SCORE RECORDS EXIST too though");

                                if(perfectScoreExist && otherRecordsExist){
                                    horizontalView.setVisibility(View.VISIBLE);
                                }
                            }
                        }else{
                            Log.d("ERROR", "There's a problem on fetching this query");
                        }
                    });
                }
            }else{
                Log.d("ERROR", "There's a problem on fetching this query");
            }
        });
    }

    private void getUserName() {
        UserController.getUserData(firebaseAuth.getCurrentUser().getUid(), new UserDataListener() {
            @Override
            public void onSuccess(User user) {
                String fullName = user.getFullName();
                userName.setText(fullName);
            }

            @Override
            public void onFailure(Exception exception) {
                // Handle error
                Log.d("TAG", exception.getMessage());
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        perfectAdapter.startListening();
        recordAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        perfectAdapter.stopListening();
        recordAdapter.stopListening();
    }
}
