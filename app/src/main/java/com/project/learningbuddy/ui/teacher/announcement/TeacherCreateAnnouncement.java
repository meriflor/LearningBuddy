package com.project.learningbuddy.ui.teacher.announcement;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.project.learningbuddy.R;
import com.project.learningbuddy.firebase.AnnouncementController;
import com.project.learningbuddy.listener.MyCompleteListener;

import org.checkerframework.checker.nullness.qual.NonNull;

public class TeacherCreateAnnouncement extends AppCompatActivity {

    public static final String CLASSID = "Class Room Id";
    public String classID, title, content, annID, classId;

    public ImageView btn_announcementPost;
    public EditText et_announcementTitle, et_announcementContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_announcement_create);

//        Intent (Passing of classID)
        Intent intent = getIntent();
        classID = intent.getStringExtra(CLASSID);
        annID = intent.getStringExtra("id");
        title = intent.getStringExtra("title");
        content = intent.getStringExtra("content");
        classId = intent.getStringExtra("classID");

        //Toolbar
        Toolbar toolbar = findViewById(R.id.student_classroomToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_announcementPost = findViewById(R.id.announcement_post);
        et_announcementTitle = findViewById(R.id.announcement_title);
        et_announcementContent = findViewById(R.id.announcement_content);

        if(annID != null){
            et_announcementTitle.setText(title);
            et_announcementContent.setText(content);
        }

        btn_announcementPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String announcementTitle = et_announcementTitle.getText().toString().trim();
                String announcementContent = et_announcementContent.getText().toString().trim();
                if(announcementTitle.isEmpty() || announcementContent.isEmpty()){
                    showToast("Please provide the necessary information.");
                }else{
//                    create announcement and post at the same time
                    if(annID != null){
                        AnnouncementController.updateAnnouncement(classId, annID, announcementTitle, announcementContent, new MyCompleteListener() {
                            @Override
                            public void onSuccess() {
                                showToast("Updated Successfully.");
                                annID = "";
                                Intent intent2 = new Intent(TeacherCreateAnnouncement.this, TeacherAnnouncementActivity.class);
                                if(classID == null)
                                    intent2.putExtra(TeacherAnnouncementActivity.CLASSID, classId);
                                else
                                    intent2.putExtra(TeacherAnnouncementActivity.CLASSID, classID);
                                startActivity(intent2);
                            }

                            @Override
                            public void onFailure() {
                                showToast("Something went wrong!");
                            }
                        });
                    }else{
                        AnnouncementController.createAnnouncement(classID, announcementTitle, announcementContent, new MyCompleteListener() {
                            @Override
                            public void onSuccess() {
                                showToast("Announcement Posted.");
                                finish();
                            }
                            @Override
                            public void onFailure() {
                                showToast("Something went wrong!");
                            }
                        });
                    }
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

    public void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
