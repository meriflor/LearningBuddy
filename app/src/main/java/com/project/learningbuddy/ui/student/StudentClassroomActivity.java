package com.project.learningbuddy.ui.student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.PostsAdapter;
import com.project.learningbuddy.firebase.ClassController;
import com.project.learningbuddy.firebase.QuizAttemptController;
import com.project.learningbuddy.listener.ExistListener;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.Posts;

import org.checkerframework.checker.nullness.qual.NonNull;

public class StudentClassroomActivity extends AppCompatActivity {

    public static final String CLASSID = "Class ID";
    public static final String CLASSNAME = "Class Name";
    public static final String YEARLEVEL = "Class Year Level";
    public static final String SECTION = "Class Section";
    public static final String CLASSSUBJECT = "Subject Name";

    public String classID, className, classYearLevel, classSection, subjectName, teacherName;
    public int backgroundLayout;
    public RecyclerView recyclerView;
    public PostsAdapter adapter;
    public TextView tv_className, tv_classYearNSection, tv_subjectName, tv_extra;
    public ImageView peopleList, leaveClass;

    public FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_classroom);

        Intent intent = getIntent();
        classID = intent.getStringExtra(CLASSID);
        className = intent.getStringExtra(CLASSNAME);
        classYearLevel = intent.getStringExtra(YEARLEVEL);
        classSection = intent.getStringExtra(SECTION);
        subjectName = intent.getStringExtra(CLASSSUBJECT);
        backgroundLayout = intent.getIntExtra("bgLayout", -1);
        teacherName = intent.getStringExtra("teacherName");

        //Toolbar
        Toolbar toolbar = findViewById(R.id.student_classroomToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(className);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        tv_className.setText(className);
        tv_subjectName.setText(subjectName);
        tv_classYearNSection.setText(classYearLevel + " | " + classSection);
        tv_extra.setText(teacherName);
        Log.d("TAGG", className+subjectName+classYearLevel+classSection+teacherName);

        peopleList = findViewById(R.id.class_student_list);
        peopleList.setOnClickListener(view -> {
            Intent intent1 = new Intent(StudentClassroomActivity.this, StudentClassroomPeople.class);
            intent1.putExtra(StudentClassroomPeople.CLASSID, classID);
            intent1.putExtra(StudentClassroomPeople.CLASSNAME, className);
            startActivity(intent1);
        });

        leaveClass = findViewById(R.id.class_leave);
        leaveClass.setOnClickListener(view -> {
            openConfirmationDialog();
        });

        getPostsList();
    }

    private void openConfirmationDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//        View view = LayoutInflater.from(StudentClassroomActivity.this).inflate(R.layout.pop_up_window_confirmation_deletion_question, null);
        View view = getLayoutInflater().inflate(R.layout.pop_up_window_confirmation_deletion_question, null);
        TextView message = view.findViewById(R.id.confirmation_message);
        Button confirmBtn = view.findViewById(R.id.btn_confirm);
        Button cancelBtn = view.findViewById(R.id.btn_cancel);

        dialogBuilder.setView(view);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        message.setText("Are you sure to leave the " + className+" class?");

        confirmBtn.setOnClickListener(v -> {
            ClassController.leaveClass(classID, new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    showToast("Leaving the class . . .");
                    dialog.dismiss();
                    finish();
                }

                @Override
                public void onFailure() {
                    showToast("Something went wrong!");
                }
            });
        });

        cancelBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void getPostsList() {
        Query postQuery = FirebaseFirestore.
                getInstance().collection("classes")
                .document(classID)
                .collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        postQuery.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                QuerySnapshot querySnapshot = task.getResult();
                TextView textPostEmpty = findViewById(R.id.text_post_empty);
                if(querySnapshot.isEmpty()){
                    textPostEmpty.setVisibility(View.VISIBLE);
                }else{
                    textPostEmpty.setVisibility(View.GONE);
                }
            }
        });

        FirestoreRecyclerOptions<Posts> options = new FirestoreRecyclerOptions.Builder<Posts>()
                .setQuery(postQuery, Posts.class).build();
        adapter = new PostsAdapter(options);
        recyclerView = findViewById(R.id.student_post_recyclerview);
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
                        Intent announcementIntent = new Intent(StudentClassroomActivity.this, ViewAnnouncementActivity.class);
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
                        Intent quizIntent = new Intent(StudentClassroomActivity.this, ViewQuizzesActivity.class);
                        firebaseFirestore.collection("classes")
                                .document(classID)
                                .collection("quizzes")
                                .document(documentID).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        QuizAttemptController.checkAttempt(classID, documentSnapshot.getId(), new ExistListener() {
                                            @Override
                                            public void onExist(Boolean exist) {
                                                if(exist){
                                                    showToast("Open list of scores");
                                                    Intent intent1 = new Intent(StudentClassroomActivity.this, StudentScoreListActivity.class);
                                                    intent1.putExtra(StudentScoreListActivity.QUIZID, documentSnapshot.getId());
                                                    intent1.putExtra(StudentScoreListActivity.TITLE, documentSnapshot.getString("quizTitle"));
                                                    intent1.putExtra(StudentScoreListActivity.DESC, documentSnapshot.getString("quizContent"));
                                                    intent1.putExtra(StudentScoreListActivity.CLASSID, classID);
                                                    startActivity(intent1);
                                                }else{
                                                    quizIntent.putExtra(ViewQuizzesActivity.QUIZID, documentSnapshot.getId());
                                                    quizIntent.putExtra(ViewQuizzesActivity.TITLE, documentSnapshot.getString("quizTitle"));
                                                    quizIntent.putExtra(ViewQuizzesActivity.DESC, documentSnapshot.getString("quizContent"));
                                                    quizIntent.putExtra(ViewQuizzesActivity.CLASSID, classID);
                                                    startActivity(quizIntent);
                                                }
                                            }

                                            @Override
                                            public void onFailure(Exception e) {
                                                Log.d("TAG", "There's a problem checking student attempt of this specific quiz");
                                            }
                                        });
                                    }
                                });
                        break;
//                    case "Learning Material":
//                        Intent matIntent = new Intent(StudentClassroomActivity.this, ViewLearningMaterialActivity.class);
//                        firebaseFirestore.collection("classes")
//                                .document(classID)
//                                .collection("quizzes")
//                                .document(documentID).get()
//                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                    @Override
//                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                        matIntent.putExtra(ViewLearningMaterialActivity.MATID, documentSnapshot.getId());
//                                        matIntent.putExtra(ViewLearningMaterialActivity.CLASSID, classID);
//                                        startActivity(matIntent);
//                                    }
//                                });
//                        break;
                    default:
                        Toast.makeText(StudentClassroomActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
