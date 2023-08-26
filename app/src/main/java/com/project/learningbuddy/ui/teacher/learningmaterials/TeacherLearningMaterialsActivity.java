package com.project.learningbuddy.ui.teacher.learningmaterials;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.project.learningbuddy.R;

import org.checkerframework.checker.nullness.qual.NonNull;

public class TeacherLearningMaterialsActivity extends AppCompatActivity {

    public static final String CLASSID = "Class ID";
    public static final String CLASSNAME = "Class Name";
    public String classID, className;

    public ImageView btnCreate;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_learning_materials);

//        Intent (Passing of classID)
        Intent intent = getIntent();
        classID = intent.getStringExtra(CLASSID);
        className = intent.getStringExtra(CLASSNAME);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.teacher_learnMatToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnCreate = findViewById(R.id.learning_material_create);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherLearningMaterialsActivity.this, TeacherCreateLearningMaterials.class);
                intent.putExtra(TeacherCreateLearningMaterials.CLASSID, classID);
                intent.putExtra(TeacherCreateLearningMaterials.CLASSNAME, className);
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
