package com.project.learningbuddy.ui.teacher.learningmaterials;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.LearningMaterialsAdapter;
import com.project.learningbuddy.model.LearningMaterials;

import org.checkerframework.checker.nullness.qual.NonNull;

public class TeacherLearningMaterialsActivity extends AppCompatActivity {

    public static final String CLASSID = "Class ID";
    public static final String CLASSNAME = "Class Name";
    public String classID, className;

    public ImageView btnCreate;
    public RecyclerView recyclerView;
    public LearningMaterialsAdapter adapter;

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

        getLearningMaterialsList();

    }

    private void getLearningMaterialsList() {
        Query matQuery = FirebaseFirestore.getInstance().collection("learning_materials")
                .whereEqualTo("classID", classID)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<LearningMaterials> options = new FirestoreRecyclerOptions.Builder<LearningMaterials>()
                .setQuery(matQuery, LearningMaterials.class).build();
        adapter = new LearningMaterialsAdapter(options);
        recyclerView = findViewById(R.id.teacher_learning_materials_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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
