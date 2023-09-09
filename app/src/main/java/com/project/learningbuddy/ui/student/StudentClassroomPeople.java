package com.project.learningbuddy.ui.student;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.StudentListAdapter1;
import com.project.learningbuddy.adapter.TeacherListAdapter1;
import com.project.learningbuddy.model.UserClass;

import org.checkerframework.checker.nullness.qual.NonNull;

public class StudentClassroomPeople extends AppCompatActivity {

    public static final String CLASSID = "Class ID";
    public static final String CLASSNAME = "Class Name";
    public String classID, className;
    public RecyclerView listTeacher, listStudent;

    public AlertDialog.Builder dialogBuilder;
    public AlertDialog dialog;
    public TeacherListAdapter1 teacherAdapter;
    public StudentListAdapter1 studAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_classroom_people);

        Intent intent = getIntent();
        classID = intent.getStringExtra(CLASSID);
        className = intent.getStringExtra(CLASSNAME);

        Toolbar toolbar = findViewById(R.id.class_people_toolbar_2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable navIcon = toolbar.getNavigationIcon();
        if (navIcon != null) {
            navIcon.setColorFilter(getResources().getColor(R.color.violet), PorterDuff.Mode.SRC_ATOP);
        }


//        Initialization
//        addTeacher = findViewById(R.id.btn_add_teacher);
//        addStudent = findViewById(R.id.btn_add_student);


        displayTeacherList();
        displayStudentList();
    }

    private void displayTeacherList() {
        Log.d("tag", classID);
        Query teacherQuery = FirebaseFirestore.getInstance()
                .collection("classes")
                .document(classID)
                .collection("teachers")
                .orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<UserClass> options = new FirestoreRecyclerOptions.Builder<UserClass>()
                .setQuery(teacherQuery, UserClass.class)
                .build();
        teacherAdapter = new TeacherListAdapter1(options, classID);
        listTeacher = findViewById(R.id.teacherlist_recyclerView_2);
        listTeacher.setLayoutManager(new LinearLayoutManager(this));
        listTeacher.setAdapter(teacherAdapter);
    }

    private void displayStudentList() {
        Query teacherQuery = FirebaseFirestore.getInstance()
                .collection("classes")
                .document(classID)
                .collection("students")
                .orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<UserClass> options = new FirestoreRecyclerOptions.Builder<UserClass>()
                .setQuery(teacherQuery, UserClass.class)
                .build();
        studAdapter = new StudentListAdapter1(options, classID);
        listStudent = findViewById(R.id.studentlist_recyclerView_2);
        listStudent.setLayoutManager(new LinearLayoutManager(this));
        listStudent.setAdapter(studAdapter);
    }


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
        teacherAdapter.startListening();
        studAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        teacherAdapter.stopListening();
        studAdapter.stopListening();
    }
}
