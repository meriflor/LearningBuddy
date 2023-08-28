package com.project.learningbuddy.ui.teacher.quizzes;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.project.learningbuddy.R;
import com.project.learningbuddy.firebase.QuestionsController;
import com.project.learningbuddy.listener.MyCompleteListener;

import java.util.ArrayList;
import java.util.List;

public class TeacherCreateQuestion extends AppCompatActivity {
    public static final String QUIZID = "Quiz ID";
    public static final String CLASSID = "Class ID";
    private String quizID, classID;

    private LinearLayout optionsContainer;
    private Button btnAddOption, btnAddQues;
    private EditText et_question;
    private Spinner spinner_correctAns;
    private List<EditText> optionsEditTexts = new ArrayList<>();
    private Typeface customFont;
    public ArrayAdapter<CharSequence> spinnerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_question_create);

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

        optionsContainer = findViewById(R.id.options_container);
        btnAddOption = findViewById(R.id.btn_option_create);
        et_question = findViewById(R.id.et_question);
        btnAddQues = findViewById(R.id.btn_add_question);
        spinner_correctAns = findViewById(R.id.spinner_correct_answer);

        customFont = ResourcesCompat.getFont(this, R.font.quicksand);

        btnAddOption.setOnClickListener(v -> addOptionField());
        btnAddQues.setOnClickListener(v -> addQuestion());
    }

    public void addQuestion() {
        String question = et_question.getText().toString().trim();
        if (question.isEmpty()) {
            showToast("Please enter a question");
        } else if (spinnerAdapter.getCount() == 0) {
            showToast("Please enter at least one option");
        } else {
            int correctAnswerIndex = spinner_correctAns.getSelectedItemPosition();
            String correctAnswer = optionsEditTexts.get(correctAnswerIndex).getText().toString();

            List<String> options = new ArrayList<>();
            for (EditText editText : optionsEditTexts) {
                String optionText = editText.getText().toString().trim();
                if (!optionText.isEmpty()) {
                    options.add(optionText);
                }
            }

            if (options.isEmpty()) {
                showToast("Please enter at least one non-empty option");
            } else {
                QuestionsController.createQuestion(classID, quizID, question, options, correctAnswer, new MyCompleteListener() {
                    @Override
                    public void onSuccess() {
                        showToast("Question Added");
                        finish();
                    }

                    @Override
                    public void onFailure() {
                        showToast("Something went wrong!");
                    }
                });
            }
        }
    }

    public void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void addOptionField() {
        EditText et_option = new EditText(this);
        et_option.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        et_option.setHint("Option . . .");
        et_option.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.violet));
        // Set custom font family
        et_option.setTypeface(customFont);
        et_option.setHintTextColor(ContextCompat.getColorStateList(this, R.color.textHint));
        et_option.setTextColor(ContextCompat.getColorStateList(this, R.color.violet));

        // Add the option EditText to the layout
        optionsContainer.addView(et_option);
        optionsEditTexts.add(et_option);

        // Remove the previous adapter and recreate it
        spinnerAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_layout_01);

        for (EditText editText : optionsEditTexts) {
            String optionText = editText.getText().toString();
            if (!optionText.isEmpty()) {  // Check if the option is not empty
                spinnerAdapter.add(optionText);
            }
            Log.d("OptionText", optionText);
        }

        spinner_correctAns.setAdapter(spinnerAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
