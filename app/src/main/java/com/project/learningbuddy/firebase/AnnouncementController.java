package com.project.learningbuddy.firebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.Announcements;
import com.project.learningbuddy.model.Posts;

public class AnnouncementController {
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

//    public static void createAnnouncement(String classID, String announcementTitle, String announcementContent, MyCompleteListener myCompleteListener){
//        String userID = firebaseAuth.getCurrentUser().getUid();
//        DocumentReference annRef = firebaseFirestore.collection("announcements").document();
//        Announcements announcements = new Announcements();
//        announcements.setClassID(classID);
//        announcements.setAnnouncementTitle(announcementTitle);
//        announcements.setAnnouncementContent(announcementContent);
//        announcements.setAnnouncementCreator(userID);
//        announcements.setTimestamp(Timestamp.now());
//
//        annRef.set(announcements).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                myCompleteListener.onSuccess();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(Exception e) {
//                myCompleteListener.onFailure();
//            }
//        });
//
//        DocumentReference annPostRef = firebaseFirestore.collection("posts").document();
//        Posts posts = new Posts();
//        posts.setClassID(classID);
//        posts.setGetID(annRef.getId());
//        posts.setPostType("Announcement");
//        posts.setPostTitle(announcementTitle);
//        posts.setPostContent(announcementContent);
//        posts.setPostCreatorID(userID);
//        posts.setTimestamp(Timestamp.now());
//
//        annPostRef.set(posts).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                myCompleteListener.onSuccess();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(Exception e) {
//                myCompleteListener.onFailure();
//            }
//        });
//    }

//    public static void deleteAnnouncement(String announcementID, MyCompleteListener myCompleteListener){
//        firebaseFirestore.collection("announcements")
//                .document(announcementID)
//                .delete()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        myCompleteListener.onSuccess();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(Exception e) {
//                        myCompleteListener.onFailure();
//                    }
//                });
//    }

    public static void createAnnouncement(String classID, String announcementTitle, String announcementContent, MyCompleteListener myCompleteListener) {
        String userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference classRef = firebaseFirestore.collection("classes").document(classID);

        Announcements announcementData = new Announcements();
        announcementData.setAnnouncementTitle(announcementTitle);
        announcementData.setAnnouncementContent(announcementContent);
        announcementData.setAnnouncementCreator(userID);
        announcementData.setTimestamp(Timestamp.now());

        // Add the announcement as a subcollection under the specific class document
        classRef.collection("announcements").add(announcementData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String announcementId = documentReference.getId();
//
                        Posts postData = new Posts();
                        postData.setClassID(classID);
                        postData.setGetID(announcementId);
                        postData.setPostType("Announcement");

                        classRef.collection("posts").add(postData)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
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
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        myCompleteListener.onFailure();
                    }
                });
    }

    public static void deleteAnnouncement(String classID, String announcementID, MyCompleteListener myCompleteListener) {
            firebaseFirestore.collection("classes")
                    .document(classID)
                    .collection("announcements")
                    .document(announcementID)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myCompleteListener.onSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });
    }
}
