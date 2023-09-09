package com.project.learningbuddy.ui.student;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.project.learningbuddy.R;

import org.checkerframework.checker.nullness.qual.NonNull;

public class ViewAnnouncementActivity extends AppCompatActivity {
    public static final String CLASSID = "Class ID";
    public static String ANNOUNCEMENTID = "Announcement ID";
    public static String ANNOUNCEMENTTITLE = "Announcement Title";
    public static String ANNOUNCEMENTCONTENT = "Announcement Content";
    public TextView announcementTitle, announcementContent;
    public String classID, announcementID, annTitle, annContent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_announcement);

        Intent intent = getIntent();
        classID = intent.getStringExtra(CLASSID);
        announcementID = intent.getStringExtra(ANNOUNCEMENTID);
        annTitle = intent.getStringExtra(ANNOUNCEMENTTITLE);
        annContent = intent.getStringExtra(ANNOUNCEMENTCONTENT);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.student_classroomToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        announcementTitle = findViewById(R.id.announcement_title_tv);
        announcementContent = findViewById(R.id.announcement_content_tv);
        announcementContent.setMovementMethod(new ScrollingMovementMethod());

        viewAnnouncementData();
    }

    private void viewAnnouncementData() {
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
}
