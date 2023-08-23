package com.project.learningbuddy.ui.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.QuizzesAdapter;
import com.project.learningbuddy.firebase.QuizController;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.Quizzes;

public class TeacherQuizzesActivity extends AppCompatActivity {

    public static final String CLASSID = "Class Room Id";
    public String classID;
    public ImageView createQuiz;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private RecyclerView recyclerView;
    private QuizzesAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_quizzes);

//        Intent (Passing of classID)
        Intent intent = getIntent();
        classID = intent.getStringExtra(CLASSID);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.student_classroomToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        creating quiz
        createQuiz = findViewById(R.id.quiz_create);
        createQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createQuizDialog();
            }
        });

//        displaying quizzes
        getQuizList();
    }

    public void createQuizDialog(){
        dialogBuilder = new AlertDialog.Builder(TeacherQuizzesActivity.this);
        View createQuizView = getLayoutInflater().inflate(R.layout.pop_up_window_create_quiz, null);
        EditText et_quizTitle = createQuizView.findViewById(R.id.quiz_title);
        EditText et_quizDesc = createQuizView.findViewById(R.id.quiz_desc);
        Button btnCreate = createQuizView.findViewById(R.id.quiz_btn_create);
        Button btnCancel = createQuizView.findViewById(R.id.quiz_btn_cancel);

        dialogBuilder.setView(createQuizView);
        dialog = dialogBuilder.create();
        dialog.show();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quizTitle = et_quizTitle.getText().toString().trim();
                String quizDesc = et_quizDesc.getText().toString().trim();

                if(quizTitle.isEmpty() || quizDesc.isEmpty()){
                    return;
                }else{
                    QuizController.createQuiz(classID, quizTitle, quizDesc, new MyCompleteListener() {
                        @Override
                        public void onSuccess() {
                            showToast("Quiz created successfully!");
                            dialog.dismiss();
                        }

                        @Override
                        public void onFailure() {
                            showToast("Something went wrong!");
                        }
                    });
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void getQuizList(){
        Query quizQuery = FirebaseFirestore.getInstance().collection("quizzes")
                .whereEqualTo("classID", classID)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Quizzes> options = new FirestoreRecyclerOptions.Builder<Quizzes>()
                .setQuery(quizQuery, Quizzes.class).build();
        adapter = new QuizzesAdapter(options);
        recyclerView = findViewById(R.id.teacher_quiz_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
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
