package com.project.learningbuddy.ui.teacher.quizzes;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.QuestionsAdapter;
import com.project.learningbuddy.firebase.QuizController;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.Questions;

import org.checkerframework.checker.nullness.qual.NonNull;

public class ViewQuizzesActivity extends AppCompatActivity {

    public static final String QUIZID = "Quiz ID";
    public static final String CLASSID = "Class ID";
    public static final String TITLE = "Quiz Title";
    public static final String DESC = "Quiz Desc";
    public String quizID, classID, quizTitle, quizDesc;

    public RecyclerView recyclerView;
    public FloatingActionButton fab;
    public ImageView settings;
    public SwitchCompat visibility;
    public TextView tvTitle, tvDesc;
    public QuestionsAdapter adapter;

    public AlertDialog.Builder dialogBuilder;
    public AlertDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_quiz);

        Intent intent = getIntent();
        quizID = intent.getStringExtra(QUIZID);
        classID = intent.getStringExtra(CLASSID);
        quizTitle = intent.getStringExtra(TITLE);
        quizDesc = intent.getStringExtra(DESC);

        tvTitle = findViewById(R.id.quiz_title);
        tvDesc = findViewById(R.id.quiz_content);
        tvTitle.setText(quizTitle);
        tvDesc.setText(quizDesc);


        //Toolbar
        Toolbar toolbar = findViewById(R.id.quizToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable navIcon = toolbar.getNavigationIcon();
        if (navIcon != null) {
            navIcon.setColorFilter(getResources().getColor(R.color.violet), PorterDuff.Mode.SRC_ATOP);
        }

        fab = findViewById(R.id.fab_quiz_create);
        settings = findViewById(R.id.quiz_settings);
        visibility = findViewById(R.id.quiz_visibility);
        visibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(visibility.isChecked()){
                    visibility.setText("Public");
                    showToast("Quiz is ready");
                    setVisibility(true);
                }else{
                    visibility.setText("Private");
                    showToast("Quiz is set to private");
                    setVisibility(false);
                }
            }
        });
        getVisibility();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQuestion();
            }
        });

        settings.setOnClickListener(v-> {
            viewSettings();
        });

        viewQuestions();
    }

    private void viewSettings() {
        View editQuizInfo = getLayoutInflater().inflate(R.layout.pop_up_window_edit_quiz, null);
        ImageView cancel = editQuizInfo.findViewById(R.id.quiz_cancel);
        EditText editTitle = editQuizInfo.findViewById(R.id.quiz_edit_title);
        EditText editDesc = editQuizInfo.findViewById(R.id.quiz_edit_desc);
        Button edit = editQuizInfo.findViewById(R.id.quiz_edit);
        Button delete = editQuizInfo.findViewById(R.id.quiz_delete);

        editTitle.setText(quizTitle);
        editDesc.setText(quizDesc);

        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(editQuizInfo);
        dialog = dialogBuilder.create();
        dialog.show();
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTitle.getText().toString().trim();
                String desc = editDesc.getText().toString().trim();
                QuizController.editQuiz(classID, quizID, title, desc, new MyCompleteListener() {
                    @Override
                    public void onSuccess() {
                        showToast("Quiz updated successfully");
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure() {
                        showToast("Something went wrong!");
                    }
                });
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuizController.deleteQuiz(classID, quizID, new MyCompleteListener() {
                    @Override
                    public void onSuccess() {
                        showToast("Quiz Deleted");
                        dialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onFailure() {
                        showToast("Something went wrong!");
                    }
                });
            }
        });

        cancel.setOnClickListener(view -> {
            dialog.dismiss();
        });
    }

    private void setVisibility(boolean b) {
        QuizController.quizVisibility(classID, quizID, b, new MyCompleteListener() {
            @Override
            public void onSuccess() {
                Log.d("TAG", "Quiz visibility updated");
            }

            @Override
            public void onFailure() {
                Log.d("TAG", "Something went wrong!");
            }
        });
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void getVisibility() {
        FirebaseFirestore.getInstance()
                        .collection("classes").document(classID)
                        .collection("quizzes").document(quizID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Boolean checkVisibility = documentSnapshot.getBoolean("visibility");
                        visibility.setChecked(checkVisibility);
                    }
                });
    }

    private void viewQuestions() {
        Query quesQuery = FirebaseFirestore.getInstance().collection("classes")
                .document(classID).collection("quizzes")
                .document(quizID).collection("questions");

        FirestoreRecyclerOptions<Questions> options = new FirestoreRecyclerOptions.Builder<Questions>()
                .setQuery(quesQuery, Questions.class)
                .build();
        adapter = new QuestionsAdapter(options, classID, quizID);
        recyclerView = findViewById(R.id.quiz_questions_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void addQuestion() {
        Intent intent = new Intent(ViewQuizzesActivity.this, TeacherCreateQuestion.class);
        intent.putExtra(TeacherCreateQuestion.CLASSID, classID);
        intent.putExtra(TeacherCreateQuestion.QUIZID, quizID);
        startActivity(intent);
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
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
