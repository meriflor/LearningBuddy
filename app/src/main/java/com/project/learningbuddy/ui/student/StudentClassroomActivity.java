package com.project.learningbuddy.ui.student;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.PostsAdapter;
import com.project.learningbuddy.model.Posts;

public class StudentClassroomActivity extends AppCompatActivity {

    public static final String CLASSID = "Class ID";
    public static final String CLASSNAME = "Class Name";
    public static final String YEARLEVEL = "Class Year Level";
    public static final String SECTION = "Class Section";

    private String classID, className, classYearLevel, classSection;
    public RecyclerView recyclerView;
    public PostsAdapter adapter;
    public TextView tv_className, tv_classYearNSection;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_student_classroom);

        Intent intent = getIntent();
        classID = intent.getStringExtra(CLASSID);;
        className = intent.getStringExtra(CLASSNAME);
        classYearLevel = intent.getStringExtra(YEARLEVEL);;
        classSection = intent.getStringExtra(SECTION);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.student_classroomToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(className);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_className = findViewById(R.id.class_name);
        tv_classYearNSection = findViewById(R.id.class_year_level_section);

        tv_className.setText(className);
        tv_classYearNSection.setText(classYearLevel + " | " + classSection);

        getPostsList();
    }

    private void getPostsList() {
        Query postQuery = FirebaseFirestore.getInstance().collection("posts")
                .whereEqualTo("classID", classID)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Posts> options = new FirestoreRecyclerOptions.Builder<Posts>()
                .setQuery(postQuery, Posts.class).build();
        adapter = new PostsAdapter(options);
        recyclerView = findViewById(R.id.student_post_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
