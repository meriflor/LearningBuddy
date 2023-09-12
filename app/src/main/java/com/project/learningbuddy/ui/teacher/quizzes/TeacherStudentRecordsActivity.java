package com.project.learningbuddy.ui.teacher.quizzes;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.StudentScoresAdapter2;
import com.project.learningbuddy.model.QuizAttempts;

import org.checkerframework.checker.nullness.qual.NonNull;

public class TeacherStudentRecordsActivity extends AppCompatActivity {


    public static final String QUIZID = "Quiz ID";
    public static final String CLASSID = "Class ID";
    public String quizID, classID;
    public RecyclerView scoreView;
    public StudentScoresAdapter2 scoresAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_student_records);

        Intent intent = getIntent();
        quizID = intent.getStringExtra(QUIZID);
        classID = intent.getStringExtra(CLASSID);

        Toolbar toolbar = findViewById(R.id.studentRecords_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable navIcon = toolbar.getNavigationIcon();
        if (navIcon != null) {
            navIcon.setColorFilter(getResources().getColor(R.color.violet), PorterDuff.Mode.SRC_ATOP);
        }

        getStudentRecords();
    }

    private void getStudentRecords() {
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
        scoreView = findViewById(R.id.studentRecord_recyclerView);
        scoreView.setLayoutManager(new LinearLayoutManager(this));
        scoreView.setAdapter(scoresAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
