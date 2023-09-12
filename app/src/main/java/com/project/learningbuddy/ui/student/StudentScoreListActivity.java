package com.project.learningbuddy.ui.student;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.StudentAnswersAdapter;
import com.project.learningbuddy.adapter.StudentScoresAdapter2;
import com.project.learningbuddy.firebase.QuizAttemptController;
import com.project.learningbuddy.firebase.UserController;
import com.project.learningbuddy.listener.AttemptListener01;
import com.project.learningbuddy.listener.UserDataListener;
import com.project.learningbuddy.model.Answers;
import com.project.learningbuddy.model.QuizAttempts;
import com.project.learningbuddy.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StudentScoreListActivity extends AppCompatActivity {

    public static final String QUIZID = "Quiz ID";
    public static final String CLASSID = "Class ID";
    public static final String TITLE = "Quiz Title";
    public static final String DESC = "Quiz Desc";
    public String quizID, classID, quizTitle, quizDesc;
    public TextView scoreText, scorePtsText, userNameText, userEmailText, startAttemptText, endAttemptText;
    public RecyclerView questionAnswerView, classScoreView;
    public StudentScoresAdapter2 scoresAdapter;
    public StudentAnswersAdapter answersAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_score_list);

        Intent intent = getIntent();
        quizID = intent.getStringExtra(QUIZID);
        classID = intent.getStringExtra(CLASSID);
        quizTitle = intent.getStringExtra(TITLE);
        quizDesc = intent.getStringExtra(DESC);

        Toolbar toolbar = findViewById(R.id.studentScore_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable navIcon = toolbar.getNavigationIcon();
        if (navIcon != null) {
            navIcon.setColorFilter(getResources().getColor(R.color.violet), PorterDuff.Mode.SRC_ATOP);
        }

        scoreText = findViewById(R.id.studentScore_text);
        scorePtsText = findViewById(R.id.totalPts_text);
        userNameText = findViewById(R.id.studentName_text);
        userEmailText = findViewById(R.id.studentEmail_text);
        startAttemptText = findViewById(R.id.startAttempt_text);
        endAttemptText = findViewById(R.id.endAttempt_text);

        getUserDetails();
        getUserQuizAttemptData();
        getUserAnswers();
        getClassScore();
    }

    private void getClassScore() {
        Query classScoreQuery = FirebaseFirestore.getInstance()
                .collection("classes")
                .document(classID)
                .collection("quizzes")
                .document(quizID)
                .collection("student_attempts")
                .orderBy("score", Query.Direction.DESCENDING)
                .orderBy("end_timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<QuizAttempts> options = new FirestoreRecyclerOptions.Builder<QuizAttempts>()
                .setQuery(classScoreQuery, QuizAttempts.class)
                .build();
        scoresAdapter = new StudentScoresAdapter2(options);
        classScoreView = findViewById(R.id.studentClassScore_recyclerView);
        classScoreView.setLayoutManager(new LinearLayoutManager(this));
        classScoreView.setAdapter(scoresAdapter);
    }

    private void getUserAnswers() {
        FirebaseFirestore.getInstance()
                .collection("classes")
                .document(classID)
                .collection("quizzes")
                .document(quizID)
                .collection("student_attempts")
                .whereEqualTo("userID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                            List<Map<String, Object>> answersDataList = (List<Map<String, Object>>) documentSnapshot.get("answers");
                            if (answersDataList != null) {
                                List<Answers> answersList = new ArrayList<>();
                                for (Map<String, Object> answerData : answersDataList) {
                                    Answers answer = new Answers();
                                    answer.setQuestion((String) answerData.get("question"));
                                    answer.setAnswer((String) answerData.get("answer"));
                                    answer.setCorrect((Boolean) answerData.get("correct"));
                                    answersList.add(answer);
                                }
                                answersAdapter = new StudentAnswersAdapter(answersList);
                                questionAnswerView = findViewById(R.id.studentQuizAnswer_recyclerView);
                                questionAnswerView.setLayoutManager(new LinearLayoutManager(this));
                                questionAnswerView.setAdapter(answersAdapter);
                            }
                        }
                    } else {
                        Log.d("TAG", "Problem occured in fetching the studentscorelist");
                    }
                });
    }

    private void getUserQuizAttemptData() {
        QuizAttemptController.getQuizAttemptData(classID, quizID, new AttemptListener01() {
            @Override
            public void onSuccess(QuizAttempts attempts) {
                int score = attempts.getScore();
                scoreText.setText(String.valueOf(score));
//              getting the total of the questions PROBLEM
                String pts;
                if(score >1)
                    pts = "pts.";
                else
                    pts = "pt.";

                scorePtsText.setText("/"+attempts.getTotal()+" "+pts);

                Timestamp startTimestamp = attempts.getStart_timestamp();
                Timestamp endTimestamp = attempts.getEnd_timestamp();

                Date start = startTimestamp.toDate();
                Date end = endTimestamp.toDate();

                SimpleDateFormat sdf = new SimpleDateFormat("MMM. d, yyyy h:mm a", Locale.US);

                String formattedStartDate = sdf.format(start);
                String formattedEndDate = sdf.format(end);
                startAttemptText.setText(formattedStartDate);
                endAttemptText.setText(formattedEndDate);
            }
            @Override
            public void onFailure(Exception e) {
                Log.d("TAG", e.getMessage());
            }
        });
    }

    private void getUserDetails() {
        UserController.getUserData(FirebaseAuth.getInstance().getCurrentUser().getUid(), new UserDataListener() {
            @Override
            public void onSuccess(User user) {
                userNameText.setText(user.getFullName());
                userEmailText.setText(user.getEmail());
            }
            @Override
            public void onFailure(Exception exception) {
                Log.d("TAG", "A problem occurred in fetching the userData");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        scoresAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        scoresAdapter.stopListening();
    }
}
