package com.project.learningbuddy.ui.teacher.quizzes;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.CustomSpinnerAdapter;
import com.project.learningbuddy.adapter.QuestionOptionAdapter;
import com.project.learningbuddy.firebase.QuestionsController;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.Options;

import java.util.ArrayList;
import java.util.List;

public class TeacherQuizQuestionCreateActivity extends AppCompatActivity implements QuestionOptionAdapter.OnItemClickListener{
    public static final String QUIZID = "Quiz ID";
    public static final String CLASSID = "Class ID";
    private String quizID, classID;

    private EditText et_question, et_option;
    private Button btnAddOption, btnAddQuestion;
    private TextView noOptionYetText;
    private List<String> optionList;
    private RecyclerView recyclerView;
    private ArrayAdapter<String> spinnerAdapter;

    private QuestionOptionAdapter adapter;
    private Spinner spinner;

//    --------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_quiz_question_create);

        Intent intent = getIntent();
        quizID = intent.getStringExtra(QUIZID);
        classID = intent.getStringExtra(CLASSID);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.teacher_quizToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable navIcon = toolbar.getNavigationIcon();
        if (navIcon != null) {
            navIcon.setColorFilter(getResources().getColor(R.color.violet), PorterDuff.Mode.SRC_ATOP);
        }

        et_question = findViewById(R.id.et_question);
        et_option = findViewById(R.id.add_option_editText);
        btnAddOption = findViewById(R.id.add_option_btn);
        btnAddQuestion = findViewById(R.id.btn_add_question);
        recyclerView = findViewById(R.id.option_recyclerView);
        noOptionYetText = findViewById(R.id.no_options_yet_text);

        optionList = new ArrayList<>();
        adapter = new QuestionOptionAdapter(this, optionList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        spinner = findViewById(R.id.spinner_correct_answer);
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, optionList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        updateText();

        btnAddOption.setOnClickListener(view -> {
            addOptionList();
        });

        btnAddQuestion.setOnClickListener(view -> {
            addQuestion();
        });
    }

    private void updateText() {
        if(optionList.isEmpty()){
            noOptionYetText.setVisibility(View.VISIBLE);
        }else{
            noOptionYetText.setVisibility(View.GONE);
        }
    }

    private void addQuestion() {
        String question = et_question.getText().toString().trim();
        if(question.isEmpty()){
            showToast("Enter the question.");
        }else if(spinnerAdapter.getCount() == 0){
            showToast("Choose at least one option.");
        }else{
            int correctSelectedOption = spinner.getSelectedItemPosition();
            String correctAns = optionList.get(correctSelectedOption);
            Log.d("TAG", "The selected correct answer is: "+correctAns);
            QuestionsController.createQuestion(classID, quizID, question, optionList, correctAns, new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    showToast("Question added.");
                    finish();
                }

                @Override
                public void onFailure() {
                    Log.d("TAG", "There's a problem on creating a question in the TeacherQuizQuestionCreateActivity class.");
                }
            });

        }
    }

    private void addOptionList() {
        String option = et_option.getText().toString().trim();
        if(option.isEmpty()){
            Toast.makeText(this, "Enter an option.", Toast.LENGTH_SHORT).show();
        }else{
            optionList.add(option);
            spinnerAdapter.notifyDataSetChanged();
            adapter.notifyDataSetChanged();
            updateText();
            et_option.setText("");
        }
    }

    public void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(int position) {
        if(position >= 0 && position < optionList.size()){
            showToast("Removed!");
            optionList.remove(position);
            adapter.notifyDataSetChanged();
            spinnerAdapter.notifyDataSetChanged();
            updateText();
            et_option.setText("");
        }else{
            Log.d("TAG", "Something wrong in the itemClick method! TeahcerQuizCreateActivity.");
        }
    }
}
