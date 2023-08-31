package com.project.learningbuddy.ui.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.PostsAdapter;
import com.project.learningbuddy.model.Posts;
import com.project.learningbuddy.ui.teacher.announcement.TeacherAnnouncementActivity;
import com.project.learningbuddy.ui.teacher.announcement.TeacherCreateAnnouncement;
import com.project.learningbuddy.ui.teacher.announcement.ViewAnnouncementActivity;
import com.project.learningbuddy.ui.teacher.learningmaterials.TeacherLearningMaterialsActivity;
import com.project.learningbuddy.ui.teacher.learningmaterials.ViewLearningMaterialActivity;
import com.project.learningbuddy.ui.teacher.quizzes.TeacherQuizzesActivity;
import com.project.learningbuddy.ui.teacher.quizzes.ViewQuizzesActivity;

import org.checkerframework.checker.nullness.qual.NonNull;

public class TeacherClassroomActivity extends AppCompatActivity {

    public static final String CLASSNAME ="Class Name";
    public static final String CLASSID = "Class Room Id";
    public static final String YEARLEVEL = "Class Year Level";
    public static final String SECTION = "Class Section";
    public static final String CLASSSUBJECT = "Subject Name";
    public String className, classID, classYearLevel, classSection, subjectName;

    public RecyclerView recyclerView;
    public PostsAdapter adapter;
    public TextView tv_className, tv_classYearNSection;
    public CardView createAnnouncement;
    public FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_classroom);

        Intent intent = getIntent();
        className = intent.getStringExtra(CLASSNAME);
        classID = intent.getStringExtra(CLASSID);
        classYearLevel = intent.getStringExtra(YEARLEVEL);
        classSection = intent.getStringExtra(SECTION);
        subjectName = intent.getStringExtra(CLASSSUBJECT);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.student_classroomToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(className);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_className = findViewById(R.id.class_name);
        tv_classYearNSection = findViewById(R.id.class_year_level_section);

        tv_className.setText(className);
        tv_classYearNSection.setText(classYearLevel + " | " + classSection);

        createAnnouncement = findViewById(R.id.create_announcement_post);
        createAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherClassroomActivity.this, TeacherCreateAnnouncement.class);
                intent.putExtra(TeacherCreateAnnouncement.CLASSID, classID);
                startActivity(intent);
            }
        });

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

        learningMaterials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherClassroomActivity.this, TeacherLearningMaterialsActivity.class);
                intent.putExtra(TeacherLearningMaterialsActivity.CLASSID, classID);
                intent.putExtra(TeacherLearningMaterialsActivity.CLASSNAME, className);
                startActivity(intent);
            }
        });

        getPostsList();
    }

    private void getPostsList() {
        Query postQuery = FirebaseFirestore.
                getInstance().collection("classes")
                .document(classID)
                .collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Posts> options = new FirestoreRecyclerOptions.Builder<Posts>()
                .setQuery(postQuery, Posts.class).build();
        adapter = new PostsAdapter(options);
        recyclerView = findViewById(R.id.teacher_post_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new PostsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String postType = documentSnapshot.getString("postType");
                String documentID = documentSnapshot.getString("getID");
                Log.d("TAG", documentID + " is the ID biatch");
                switch (postType){
                    case "Announcement":
                        Intent announcementIntent = new Intent(TeacherClassroomActivity.this, ViewAnnouncementActivity.class);
                        firebaseFirestore.collection("classes")
                                .document(classID)
                                .collection("announcements")
                                        .document(documentID).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                announcementIntent.putExtra(ViewAnnouncementActivity.ANNOUNCEMENTID, documentSnapshot.getId());
                                                announcementIntent.putExtra(ViewAnnouncementActivity.ANNOUNCEMENTTITLE, documentSnapshot.getString("announcementTitle"));
                                                announcementIntent.putExtra(ViewAnnouncementActivity.ANNOUNCEMENTCONTENT, documentSnapshot.getString("announcementContent"));
                                                announcementIntent.putExtra(ViewAnnouncementActivity.CLASSID, classID);
                                                startActivity(announcementIntent);
                                            }
                                        });
                        break;
                    case "Quiz":
                        Intent quizIntent = new Intent(TeacherClassroomActivity.this, ViewQuizzesActivity.class);
                        firebaseFirestore.collection("classes")
                                .document(classID)
                                .collection("quizzes")
                                .document(documentID).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        quizIntent.putExtra(ViewQuizzesActivity.QUIZID, documentSnapshot.getId());
                                        quizIntent.putExtra(ViewQuizzesActivity.TITLE, documentSnapshot.getString("quizTitle"));
                                        quizIntent.putExtra(ViewQuizzesActivity.DESC, documentSnapshot.getString("quizContent"));
                                        quizIntent.putExtra(ViewQuizzesActivity.CLASSID, classID);
                                        startActivity(quizIntent);
                                    }
                                });
                        break;
                    case "Learning Material":
                        Intent matIntent = new Intent(TeacherClassroomActivity.this, ViewLearningMaterialActivity.class);
                        firebaseFirestore.collection("classes")
                                .document(classID)
                                .collection("quizzes")
                                .document(documentID).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        matIntent.putExtra(ViewLearningMaterialActivity.MATID, documentSnapshot.getId());
                                        matIntent.putExtra(ViewLearningMaterialActivity.CLASSID, classID);
                                        startActivity(matIntent);
                                    }
                                });
                        break;
                    default:
                        Toast.makeText(TeacherClassroomActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
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

    public void openSettings(View view) {
        Intent intent = new Intent(TeacherClassroomActivity.this, TeacherSettingsActivity.class);
        intent.putExtra(TeacherSettingsActivity.CLASSID, classID);
        intent.putExtra(TeacherSettingsActivity.CLASSNAME, className);
        intent.putExtra(TeacherSettingsActivity.YEARLEVEL, classYearLevel);
        intent.putExtra(TeacherSettingsActivity.SECTION, classSection);
        intent.putExtra(TeacherSettingsActivity.SUBJECTNAME, subjectName);
        Log.d("TAG", className);
        startActivity(intent);
    }

    public void openPeopleList(View view) {
        Intent intent = new Intent(TeacherClassroomActivity.this, TeacherClassroomPeople.class);
        intent.putExtra(TeacherClassroomPeople.CLASSID, classID);
        startActivity(intent);
    }
}
