package com.project.learningbuddy.ui.teacher.announcement;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.project.learningbuddy.R;
import com.project.learningbuddy.firebase.AnnouncementController;
import com.project.learningbuddy.listener.MyCompleteListener;

import org.checkerframework.checker.nullness.qual.NonNull;

public class ViewAnnouncementActivity extends AppCompatActivity {
    public static String ANNOUNCEMENTID = "Announcement ID";
    public static String ANNOUNCEMENTTITLE = "Announcement Title";
    public static String ANNOUNCEMENTCONTENT = "Announcement Content";
    public ImageView deleteAnnouncement;
    public TextView announcementTitle, announcementContent;
    public String announcementID, annTitle, annContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_announcement);

//        Intent
        Intent intent = getIntent();
        announcementID = intent.getStringExtra(ANNOUNCEMENTID);
        annTitle = intent.getStringExtra(ANNOUNCEMENTTITLE);
        annContent = intent.getStringExtra(ANNOUNCEMENTCONTENT);

        Log.d("TAG", intent.getStringExtra(ANNOUNCEMENTTITLE));

        Log.d("TAG", annTitle);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.student_classroomToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        announcementTitle = findViewById(R.id.announcement_title_tv);
        announcementContent = findViewById(R.id.announcement_content_tv);
        deleteAnnouncement = findViewById(R.id.announcement_delete);
        announcementContent.setMovementMethod(new ScrollingMovementMethod());

        deleteAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnnouncementController.deleteAnnouncement(announcementID, new MyCompleteListener() {
                    @Override
                    public void onSuccess() {
                        showToast("Announcement deleted");
                        finish();
                    }
                    @Override
                    public void onFailure() {
                        showToast("Something went wrong!");
                    }
                });
            }
        });

        announcementTitle.setText(annTitle);
        announcementContent.setText(annContent);
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

    public void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
