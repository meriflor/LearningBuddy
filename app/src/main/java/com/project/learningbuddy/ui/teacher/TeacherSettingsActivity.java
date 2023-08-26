package com.project.learningbuddy.ui.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.CoreHelper;
import com.project.learningbuddy.firebase.ClassController;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.useractivity.Homepage;

import org.checkerframework.checker.nullness.qual.NonNull;

public class TeacherSettingsActivity extends AppCompatActivity {

    public static final String CLASSID = "Class ID";
    public static final String CLASSNAME ="Class Name";
    public static final String YEARLEVEL = "Class Year Level";
    public static final String SECTION = "Class Section";
    public static final String SUBJECTNAME = "Subject Name";
    public String className, classID, classYearLevel, classSection, subjectName;

    public EditText et_aboutClassName, et_aboutClassSubject, et_aboutClassYearLevel, et_aboutClassSection;
    public Button btn_aboutSave;
    public ImageButton btn_aboutDelete;

    public CoreHelper coreHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_settings);

        Intent intent = getIntent();
        classID = intent.getStringExtra(CLASSID);
        className = intent.getStringExtra(CLASSNAME);
        subjectName = intent.getStringExtra(SUBJECTNAME);
        classYearLevel = intent.getStringExtra(YEARLEVEL);
        classSection = intent.getStringExtra(SECTION);

        Toolbar toolbar = findViewById(R.id.teacher_settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(className);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_aboutClassName = findViewById(R.id.et_about_className);
        et_aboutClassSubject = findViewById(R.id.et_about_classSubject);
        et_aboutClassYearLevel = findViewById(R.id.et_about_classYearLevel);
        et_aboutClassSection = findViewById(R.id.et_about_classSection);
        btn_aboutSave = findViewById(R.id.btn_about_save);
        btn_aboutDelete = findViewById(R.id.btn_about_delete);

        et_aboutClassName.setText(className);
        et_aboutClassSubject.setText(subjectName);
        et_aboutClassYearLevel.setText(classYearLevel);
        et_aboutClassSection.setText(classSection);

        btn_aboutSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings();
            }
        });

        btn_aboutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteClass();
            }
        });
    }

    private void deleteClass() {
        ClassController.deleteClass(classID, new MyCompleteListener() {
            @Override
            public void onSuccess() {
                showToast("Deleted successfully.");
                startActivity(new Intent(TeacherSettingsActivity.this, Homepage.class));
            }

            @Override
            public void onFailure() {
                Log.d("TAG", "Something went wrong!");
            }
        });
    }

    public void saveSettings() {
        String thisClassName = et_aboutClassName.getText().toString().trim();
        String thisSubjectName = et_aboutClassSubject.getText().toString().trim();
        String thisYearLevel = et_aboutClassYearLevel.getText().toString().trim();
        String thisSection = et_aboutClassSection.getText().toString().trim();

        ClassController.updateClass(classID, thisClassName, thisSubjectName, thisYearLevel, thisSection, new MyCompleteListener() {
            @Override
            public void onSuccess() {
                showToast("Updated successfully.");
                Intent intent = new Intent(TeacherSettingsActivity.this, Homepage.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure() {
                Log.e("TAG", "Something went wrong!");
            }
        });
    }

    public void showToast(String text){
        Toast.makeText(TeacherSettingsActivity.this, text, Toast.LENGTH_SHORT);
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
}
