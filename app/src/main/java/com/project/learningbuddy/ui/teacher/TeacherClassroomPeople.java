package com.project.learningbuddy.ui.teacher;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.PeopleListAdapter;
import com.project.learningbuddy.firebase.ClassController;
import com.project.learningbuddy.firebase.UserController;
import com.project.learningbuddy.listener.ExistListener;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.UserClass;

public class TeacherClassroomPeople extends AppCompatActivity {

    public static final String CLASSID = "Class ID";
    public String classID;
    public Button addTeacher, addStudent;
    public RecyclerView listTeacher, listStudent;

    public AlertDialog.Builder dialogBuilder;
    public AlertDialog dialog;
    public PeopleListAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_classroom_people);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.class_people_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable navIcon = toolbar.getNavigationIcon();
        if (navIcon != null) {
            navIcon.setColorFilter(getResources().getColor(R.color.violet), PorterDuff.Mode.SRC_ATOP);
        }

//        intent
        Intent intent = getIntent();
        classID = intent.getStringExtra(CLASSID);

//        Initialization
        addTeacher = findViewById(R.id.btn_add_teacher);
        addStudent = findViewById(R.id.btn_add_student);

        addTeacher.setOnClickListener(v-> {
            addUserEmail("Teacher");
        });

        addStudent.setOnClickListener(v-> {
            addUserEmail("Student");
        });

        displayTeacherList();
        displayStudentList();
    }

    private void displayTeacherList() {
        Query teacherQuery = FirebaseFirestore.getInstance()
                .collection("classes")
                .document(classID)
                .collection("teachers")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<UserClass> options = new FirestoreRecyclerOptions.Builder<UserClass>()
                .setQuery(teacherQuery, UserClass.class)
                .build();
        adapter = new PeopleListAdapter(options, classID);
        listTeacher = findViewById(R.id.teacherlist_recyclerView);
        listTeacher.setLayoutManager(new LinearLayoutManager(this));
        listTeacher.setAdapter(adapter);
    }

    private void displayStudentList() {
//        Query teacherQuery = FirebaseFirestore.getInstance()
//                .collection("classes")
//                .document(classID)
//                .collection("students")
//                .orderBy("timestamp", Query.Direction.DESCENDING);
//
//        FirestoreRecyclerOptions<UserClass> options = new FirestoreRecyclerOptions.Builder<UserClass>()
//                .setQuery(teacherQuery, UserClass.class)
//                .build();
//        adapter = new PeopleListAdapter(options, classID);
//        listStudent = findViewById(R.id.studentlist_recyclerView);
//        listStudent.setLayoutManager(new LinearLayoutManager(this));
//        listStudent.setAdapter(adapter);
    }


    private void addUserEmail(String userType) {
        View addPeoplePopUpWindow = getLayoutInflater().inflate(R.layout.pop_up_window_add_people, null);
        EditText et_addEmail = addPeoplePopUpWindow.findViewById(R.id.add_email);
        Button btnAdd = addPeoplePopUpWindow.findViewById(R.id.btn_add_people);
        Button btnCancel = addPeoplePopUpWindow.findViewById(R.id.btn_add_cancel);

        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(addPeoplePopUpWindow);
        dialog = dialogBuilder.create();
        dialog.show();

        btnAdd.setOnClickListener(v->{
            String email = et_addEmail.getText().toString().trim();
            if(email.isEmpty()){
                showToast("Please enter the user email");
            }else{
                checkEmailUserTypeExist(email, userType);
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(v->{
            dialog.dismiss();
        });

    }

    private void checkEmailUserTypeExist(String email, String userType) {
        UserController.checkEmailUserTypeExist(email, userType, new ExistListener() {
            @Override
            public void onExist(Boolean exist) {
                if(exist){
                    ClassController.addUserToClass(email, classID, userType, new MyCompleteListener() {
                        @Override
                        public void onSuccess() {
                            showToast("User added successfully");
                        }

                        @Override
                        public void onFailure() {
                            showToast("Please check the internet.");
                        }
                    });
                }else{
                    Log.d("TAG", "There's an error in checking email,, it exist but its not there.");
                }
            }

            @Override
            public void onFailure(Exception e) {
                showToast(e.getMessage());
            }
        });
    }

    private void showToast(String text) {
        Toast.makeText(TeacherClassroomPeople.this, text, Toast.LENGTH_SHORT).show();
    }
}
