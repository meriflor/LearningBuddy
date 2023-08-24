package com.project.learningbuddy.ui.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.project.learningbuddy.R;
import com.project.learningbuddy.ui.teacher.announcement.TeacherAnnouncementActivity;

import org.checkerframework.checker.nullness.qual.NonNull;

public class TeacherClassroomActivity extends AppCompatActivity {

    public static final String CLASSNAME ="Class Name";
    public static final String CLASSID = "Class Room Id";
    public String className, classID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_classroom);

        Intent intent = getIntent();
        className = intent.getStringExtra(CLASSNAME);
        classID = intent.getStringExtra(CLASSID);


        Log.d("TAG", "ClassName: "+className);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.student_classroomToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(className);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Floating buttons
        FloatingActionButton quizzes = findViewById(R.id.quizzes);
        FloatingActionButton learningMaterials = findViewById(R.id.learningMaterial);
        FloatingActionButton announcement = findViewById(R.id.announcements);

        quizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherClassroomActivity.this, TeacherQuizzesActivity.class);
                intent.putExtra(TeacherQuizzesActivity.CLASSID,classID);
                startActivity(intent);
            }
        });

        announcement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherClassroomActivity.this, TeacherAnnouncementActivity.class);
                intent.putExtra(TeacherAnnouncementActivity.CLASSID,classID);
                startActivity(intent);
            }
        });
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
