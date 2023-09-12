package com.project.learningbuddy.ui.student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.learningbuddy.R;
import com.project.learningbuddy.firebase.QuizAttemptController;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.Answers;
import com.project.learningbuddy.model.Questions;

import java.util.ArrayList;
import java.util.List;

public class ViewQuestionsActivity extends AppCompatActivity {


    public static final String QUIZID = "Quiz ID";
    public static final String CLASSID = "Class ID";
    public static final String ATTEMPTID = "Attempt ID";

    public String quizID, classID, attemptID;
    public ProgressBar questionProgressBar;
    public TextView questionText;
    public Button nextButton;
    public RadioGroup optionsRadioGroup;

    private List<Questions> questions;
    private List<Answers> answers;
    int currentQuestionIndex, selectedOption, score;
    Boolean correct;

    Typeface typeface;
    public FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_questions);

        Intent intent = getIntent();
        classID = intent.getStringExtra(CLASSID);
        quizID = intent.getStringExtra(QUIZID);
        attemptID = intent.getStringExtra(ATTEMPTID);

        questionProgressBar = findViewById(R.id.question_progressBar);
        questionText = findViewById(R.id.question_text);
        optionsRadioGroup = findViewById(R.id.option_radioGroup);
        nextButton = findViewById(R.id.next_btn);
        questions = new ArrayList<>();
        answers = new ArrayList<>();
        currentQuestionIndex = 0;
        score = 0;

//        typeface = Typeface.createFromAsset(getResources(), R.font.quicksand);


        questionProgressBar.setProgress(1);

        loadQuizQuestions();

        nextButton.setOnClickListener(view -> {
            Log.d("TAG", "The selected option is "+optionsRadioGroup.getCheckedRadioButtonId());
            selectedOption = optionsRadioGroup.getCheckedRadioButtonId();
            int index = optionsRadioGroup.indexOfChild(findViewById(selectedOption));
            Log.d("TAG", "The selected option index means "+index);
            if(selectedOption == -1){
                Toast.makeText(this, "Answer the question", Toast.LENGTH_SHORT).show();
            }else{
                Log.d("THIS TAG", "currectQuestionIndex: "+currentQuestionIndex +" Question size: "+questions.size());
                getAnswer(index);
                currentQuestionIndex++;
                if(currentQuestionIndex+1 == questions.size()){
//                    submitButton.setVisibility(View.VISIBLE);
//                    nextButton.setVisibility(View.GONE);

                    nextButton.setText("SUBMIT");
                }
                if(currentQuestionIndex < questions.size()){
                    displayQuestion();
                    questionProgressBar.setProgress(currentQuestionIndex + 1);
                }else{
//                    >>>>>>>>>>>>go to the score chu2
                    for(int i = 0; i <answers.size(); i++)
                        Log.d("TAGGGGGGGGGG", "These are the answers: "+answers.get(i).getAnswer());

                    Log.d("TAG", "Which has the score of: "+score);

                    saveQuiz();
                }
            }
        });
    }

    private void saveQuiz() {
        QuizAttemptController.endAttempt(classID, quizID, attemptID, answers, score, questions.size(), new MyCompleteListener() {
            @Override
            public void onSuccess() {
                startActivity(new Intent(ViewQuestionsActivity.this, ViewQuizResultActivity.class));
                Intent intent1 = new Intent(ViewQuestionsActivity.this, ViewQuizResultActivity.class);
                intent1.putExtra(ViewQuizResultActivity.CLASSID, classID);
                intent1.putExtra(ViewQuizResultActivity.QUIZID, quizID);
//                intent1.putExtra(ViewQuizResultActivity.SCORE, score);
                intent1.putExtra("score", score);
//                intent1.putExtra(ViewQuizResultActivity.QUESTIONSIZE, questions.size());
                intent1.putExtra("questionSize", questions.size());
                startActivity(intent1);
            }

            @Override
            public void onFailure() {
                Log.d("TAG", "There's a problem storing the data.");
            }
        });
    }

    private void getAnswer(int index) {
        if(questions.get(currentQuestionIndex).getAnswer().equals(questions.get(currentQuestionIndex).getOptions().get(index))){
            correct = true;
            score++;
        }else{
            correct = false;
        }

        Answers answeredQuestion = new Answers(
                questions.get(currentQuestionIndex).getQuestion(),
                questions.get(currentQuestionIndex).getOptions().get(index),
                correct
        );
        answers.add(answeredQuestion);
        Log.d("HOSHII NO", "The answer: "+answers.get(currentQuestionIndex).getQuestion()+", "
                +answers.get(currentQuestionIndex).getAnswer() + " Is it correct? "
                + answers.get(currentQuestionIndex).getCorrect());
    }

    private void loadQuizQuestions() {
        firebaseFirestore
                .collection("classes")
                .document(classID)
                .collection("quizzes")
                .document(quizID)
                .collection("questions")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Questions question = document.toObject(Questions.class);
                            questions.add(question);
                        }
                        int questionSize = task.getResult().size();
                        questionProgressBar.setMax(questionSize);
                        Log.d("TAGGGG", "Size of questions"+questionSize);
                        if (!questions.isEmpty()) {
                            displayQuestion();
                        } else {
                            // Handle case where there are no questions in the quiz
                            Log.d("TAG", "There are no questions fetched");
                        }
                    } else {
                        // Handle errors
                        Log.d("TAG", "There's a problem on fetching the questions");
                    }
                });
    }

    @SuppressLint("ResourceType")
    private void displayQuestion() {
        Questions currentQuestion = questions.get(currentQuestionIndex);
        questionText.setText(currentQuestion.getQuestion());
        optionsRadioGroup.removeAllViews();

        for (int i = 0; i < currentQuestion.getOptions().size(); i++) {
            RadioButton radioButton = new RadioButton(this);

            // Set design --------------------
            // Apply your custom style here programmatically
            radioButton.setLayoutParams(new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT
            ));
            float scale = getResources().getDisplayMetrics().density;
            int marginInPixels = (int) (8 * scale + 0.5f);
            RadioGroup.LayoutParams layoutParams = (RadioGroup.LayoutParams) radioButton.getLayoutParams();
            layoutParams.setMargins(0, marginInPixels, 0, marginInPixels); // Set top margin
            radioButton.setLayoutParams(layoutParams);
            radioButton.setText(currentQuestion.getOptions().get(i));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                radioButton.setTypeface(getResources().getFont(R.font.quicksand)); // Set font
            }
//            radioButton.setTypeface(typeface);
            radioButton.setTextSize(16); // Set text size
            radioButton.setBackgroundResource(R.drawable.radio_button_style); // Set background drawable
            radioButton.setButtonDrawable(android.R.color.transparent); // Hide default button
            radioButton.setTextColor(getResources().getColorStateList(R.drawable.radio_text_style)); // Set text color
            radioButton.setElevation(2);
            //---------------------------------------------

            optionsRadioGroup.addView(radioButton);
        }

        // Clear selection in RadioGroup
        optionsRadioGroup.clearCheck();
    }
}
