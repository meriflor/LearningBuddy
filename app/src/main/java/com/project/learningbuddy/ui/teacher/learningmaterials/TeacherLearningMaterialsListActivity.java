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
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.LearningMaterialsAdapter;
import com.project.learningbuddy.model.LearningMaterials;

import org.checkerframework.checker.nullness.qual.NonNull;

public class TeacherLearningMaterialsListActivity extends AppCompatActivity {

    public static final String CLASSID = "Class ID";
    public static final String CLASSNAME = "Class Name";
    public String classID, className;

    public FloatingActionButton btnCreate, btnCreatePracticeReading;
    public FloatingActionsMenu menu;
    public RecyclerView recyclerView;
    public LearningMaterialsAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_learning_materials_list);

//        Intent (Passing of classID)
        Intent intent = getIntent();
        classID = intent.getStringExtra(CLASSID);
        className = intent.getStringExtra(CLASSNAME);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.teacher_learnMatToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        menu = findViewById(R.id.floatingMenu_learningMat);

        btnCreate = findViewById(R.id.learning_material_create);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.collapse();
                Intent intent = new Intent(TeacherLearningMaterialsListActivity.this, TeacherCreateLearningMaterials.class);
                intent.putExtra(TeacherCreateLearningMaterials.CLASSID, classID);
                intent.putExtra(TeacherCreateLearningMaterials.CLASSNAME, className);
                startActivity(intent);
//                Intent intent = new Intent(TeacherLearningMaterialsListActivity.this, com.project.learningbuddy.ui.teacher.sample.TeacherCreateLearningMaterials.class);
//                intent.putExtra(com.project.learningbuddy.ui.teacher.sample.TeacherCreateLearningMaterials.CLASSID, classID);
//                intent.putExtra(com.project.learningbuddy.ui.teacher.sample.TeacherCreateLearningMaterials.CLASSNAME, className);
//                startActivity(intent);
            }
        });

        btnCreatePracticeReading = findViewById(R.id.learning_material_create_practice_reading);
        btnCreatePracticeReading.setOnClickListener(view -> {
            menu.collapse();
            Intent intent1 = new Intent(TeacherLearningMaterialsListActivity.this, TeacherCreateLearningMaterialsPracticeReading.class);
            intent1.putExtra(TeacherCreateLearningMaterialsPracticeReading.CLASSID, classID);
            intent1.putExtra(TeacherCreateLearningMaterialsPracticeReading.CLASSNAME, className);
            startActivity(intent1);
        });

        getLearningMaterialsList();

    }

    private void getLearningMaterialsList() {
        Query matQuery = FirebaseFirestore.getInstance().collection("classes")
                .document(classID)
                .collection("learning_materials")
                .whereEqualTo("materialCreator", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<LearningMaterials> options = new FirestoreRecyclerOptions.Builder<LearningMaterials>()
                .setQuery(matQuery, LearningMaterials.class).build();
        adapter = new LearningMaterialsAdapter(options);
        recyclerView = findViewById(R.id.teacher_learning_materials_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new LearningMaterialsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                menu.collapse();
                Intent intent = new Intent(TeacherLearningMaterialsListActivity.this, ViewLearningMaterialActivity.class);
                intent.putExtra(ViewLearningMaterialActivity.MATID, documentSnapshot.getId());
                intent.putExtra(ViewLearningMaterialActivity.CLASSID, classID);
                intent.putExtra(ViewLearningMaterialActivity.MATERIALTYPE, documentSnapshot.getString("materialType"));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
