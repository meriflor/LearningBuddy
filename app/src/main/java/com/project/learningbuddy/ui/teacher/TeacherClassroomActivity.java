package com.project.learningbuddy.ui.teacher;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
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
import com.project.learningbuddy.ui.teacher.learningmaterials.TeacherLearningMaterialsListActivity;
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
    public int backgroundLayout, studentCount;

    public RecyclerView recyclerView;
    public PostsAdapter adapter;
    public TextView tv_className, tv_classYearNSection, tv_subjectName, tv_extra;
    public CardView createAnnouncement;
    public FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FloatingActionsMenu fabMenu;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_classroom);

        Dialog dialog = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.loading_dialog, null);
        TextView message = view.findViewById(R.id.loading_message);
        message.setText("Loading . . .");
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


        Intent intent = getIntent();
        className = intent.getStringExtra(CLASSNAME);
        classID = intent.getStringExtra(CLASSID);
        classYearLevel = intent.getStringExtra(YEARLEVEL);
        classSection = intent.getStringExtra(SECTION);
        subjectName = intent.getStringExtra(CLASSSUBJECT);
        backgroundLayout = intent.getIntExtra("bgLayout", -1);
        studentCount = intent.getIntExtra("studentCount", -1);

        CardView classInfo = findViewById(R.id.class_info);
        View customLayout = LayoutInflater.from(this)
                .inflate(backgroundLayout, classInfo, false);
        ImageView imageView = customLayout.findViewById(R.id.classtransparency);
        int cardviewHeight = classInfo.getHeight();
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.height = cardviewHeight;
        imageView.setLayoutParams(layoutParams);
        classInfo.addView(customLayout);

        tv_className = customLayout.findViewById(R.id.tv_class_name);
        tv_subjectName = customLayout.findViewById(R.id.tv_class_year_level);
        tv_classYearNSection = customLayout.findViewById(R.id.tv_class_section);
        tv_extra = customLayout.findViewById(R.id.tv_extra_info);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.student_classroomToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(className);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_className.setText(className);
        tv_subjectName.setText(subjectName);
        tv_classYearNSection.setText(classYearLevel + " | " + classSection);
        tv_extra.setText(studentCount + " Student(s)");

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
        fabMenu = findViewById(R.id.fab_menu);

        quizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.collapse();
                Intent intent = new Intent(TeacherClassroomActivity.this, TeacherQuizzesActivity.class);
                intent.putExtra(TeacherQuizzesActivity.CLASSID,classID);
                startActivity(intent);
            }
        });

        announcement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.collapse();
                Intent intent = new Intent(TeacherClassroomActivity.this, TeacherAnnouncementActivity.class);
                intent.putExtra(TeacherAnnouncementActivity.CLASSID,classID);
                startActivity(intent);
            }
        });

        learningMaterials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.collapse();
                Intent intent = new Intent(TeacherClassroomActivity.this, TeacherLearningMaterialsListActivity.class);
                intent.putExtra(TeacherLearningMaterialsListActivity.CLASSID, classID);
                intent.putExtra(TeacherLearningMaterialsListActivity.CLASSNAME, className);
                startActivity(intent);
            }
        });

        getPostsList();
        dialog.dismiss();
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
        postQuery.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(!task.getResult().isEmpty()){

                    recyclerView.setAdapter(adapter);

                    adapter.setOnItemClickListener(new PostsAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                            fabMenu.collapse();
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
                                            .collection("learning_materials")
                                            .document(documentID).get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    matIntent.putExtra(ViewLearningMaterialActivity.MATID, documentSnapshot.getId());
                                                    matIntent.putExtra(ViewLearningMaterialActivity.CLASSID, classID);
                                                    matIntent.putExtra(ViewLearningMaterialActivity.MATERIALTYPE, documentSnapshot.getString("materialType"));
                                                    startActivity(matIntent);
                                                }
                                            });
                                    break;
                                case "Practice Reading":
                                    Intent pracIntent = new Intent(TeacherClassroomActivity.this, ViewLearningMaterialActivity.class);
                                    firebaseFirestore.collection("classes")
                                            .document(classID)
                                            .collection("learning_materials")
                                            .document(documentID).get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    pracIntent.putExtra(ViewLearningMaterialActivity.MATID, documentSnapshot.getId());
                                                    pracIntent.putExtra(ViewLearningMaterialActivity.CLASSID, classID);
                                                    pracIntent.putExtra(ViewLearningMaterialActivity.MATERIALTYPE, documentSnapshot.getString("materialType"));
                                                    startActivity(pracIntent);
                                                }
                                            });
                                    break;
                                default:
                                    Toast.makeText(TeacherClassroomActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    TextView noPostYet = findViewById(R.id.noPostYet_text);
                    noPostYet.setVisibility(View.VISIBLE);
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
        intent.putExtra("studentCount", studentCount);
        intent.putExtra("bgLayout", backgroundLayout);
        Log.d("TAG", className);
        startActivity(intent);
    }

    public void openPeopleList(View view) {
        Intent intent = new Intent(TeacherClassroomActivity.this, TeacherClassroomPeople.class);
        intent.putExtra(TeacherClassroomPeople.CLASSID, classID);
        intent.putExtra(TeacherClassroomPeople.CLASSNAME, className);
        startActivity(intent);
    }
}
