package com.project.learningbuddy.ui.teacher.announcement;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.AnnouncementsAdapter;
import com.project.learningbuddy.model.Announcements;

import org.checkerframework.checker.nullness.qual.NonNull;

public class TeacherAnnouncementActivity extends AppCompatActivity {

    public static final String CLASSID = "Class Room Id";
    public String classID;

    public ImageView createAnnouncement;
    public AnnouncementsAdapter adapter;
    public RecyclerView recyclerView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_announcement);

//        Intent (Passing of classID)
        Intent intent = getIntent();
        classID = intent.getStringExtra(CLASSID);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.student_classroomToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        Button
        createAnnouncement = findViewById(R.id.announcement_create);
        createAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherAnnouncementActivity.this, TeacherCreateAnnouncement.class);
                intent.putExtra(TeacherCreateAnnouncement.CLASSID, classID);
                startActivity(intent);
            }
        });

        getAnnouncementList();
    }

    public void getAnnouncementList(){
        Query annQuery = FirebaseFirestore.
                getInstance().collection("classes")
                .document(classID)
                .collection("announcements")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Announcements> options = new FirestoreRecyclerOptions.Builder<Announcements>()
                .setQuery(annQuery, Announcements.class).build();
        adapter = new AnnouncementsAdapter(options);
        recyclerView = findViewById(R.id.teacher_announcement_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AnnouncementsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String announcementID = documentSnapshot.getId().toString();
                Intent intent = new Intent(TeacherAnnouncementActivity.this, ViewAnnouncementActivity.class);
                intent.putExtra(ViewAnnouncementActivity.CLASSID, classID);
                intent.putExtra(ViewAnnouncementActivity.ANNOUNCEMENTID, announcementID);
                intent.putExtra(ViewAnnouncementActivity.ANNOUNCEMENTTITLE, documentSnapshot.getString("announcementTitle"));
                intent.putExtra(ViewAnnouncementActivity.ANNOUNCEMENTCONTENT, documentSnapshot.getString("announcementContent"));
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
}
