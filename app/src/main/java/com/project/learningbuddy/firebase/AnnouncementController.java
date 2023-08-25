package com.project.learningbuddy.firebase;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.Announcements;
import com.project.learningbuddy.model.Posts;

public class AnnouncementController {
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static void createAnnouncement(String classID, String announcementTitle, String announcementContent, MyCompleteListener myCompleteListener){
        String userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference annRef = firebaseFirestore.collection("announcements").document();
        Announcements announcements = new Announcements();
        announcements.setClassID(classID);
        announcements.setAnnouncementTitle(announcementTitle);
        announcements.setAnnouncementContent(announcementContent);
        announcements.setAnnouncementCreator(userID);
        announcements.setTimestamp(Timestamp.now());

        annRef.set(announcements).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                myCompleteListener.onSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                myCompleteListener.onFailure();
            }
        });

        DocumentReference annPostRef = firebaseFirestore.collection("posts").document();
        Posts posts = new Posts();
        posts.setClassID(classID);
        posts.setPostType("Announcement");
        posts.setPostTitle(announcementTitle);
        posts.setPostContent(announcementContent);
        posts.setPostCreatorID(userID);
        posts.setTimestamp(Timestamp.now());

        annPostRef.set(posts).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                myCompleteListener.onSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                myCompleteListener.onFailure();
            }
        });
    }

    public static void deleteAnnouncement(String announcementID, MyCompleteListener myCompleteListener){
        firebaseFirestore.collection("announcements")
                .document(announcementID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        myCompleteListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        myCompleteListener.onFailure();
                    }
                });
    }
}
