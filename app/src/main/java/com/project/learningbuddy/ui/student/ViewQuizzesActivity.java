package com.project.learningbuddy.ui.student;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.project.learningbuddy.R;

public class ViewQuizzesActivity extends AppCompatActivity {
    public static final String QUIZID = "Quiz ID";
    public static final String CLASSID = "Class ID";
    public static final String TITLE = "Quiz Title";
    public static final String DESC = "Quiz Desc";
    public String quizID, classID, quizTitle, quizDesc;
    public TextView tvTitle, tvDesc;
    public Button start;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_quizzes);

        Intent intent = getIntent();
        quizID = intent.getStringExtra(QUIZID);
        classID = intent.getStringExtra(CLASSID);
        quizTitle = intent.getStringExtra(TITLE);
        quizDesc = intent.getStringExtra(DESC);

        tvTitle = findViewById(R.id.student_quizTitle);
        tvDesc = findViewById(R.id.student_quizContent);
        start = findViewById(R.id.btn_start);

        tvTitle.setText(quizTitle);
        tvDesc.setText(quizDesc);


    }
}
