package com.project.learningbuddy.firebase;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.learningbuddy.listener.MyCompleteListener;

import java.util.HashMap;
import java.util.Map;

public class AnnouncementController {
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static String userID = firebaseAuth.getCurrentUser().getUid();

    public static void createAnnouncement(String classID, String announcementTitle, String announcementContent, MyCompleteListener myCompleteListener) {
        DocumentReference classRef = firebaseFirestore.collection("classes").document(classID);

        Map<String, Object> announcementData = new HashMap<>();
        announcementData.put("announcementTitle", announcementTitle);
        announcementData.put("announcementContent", announcementContent);
        announcementData.put("announcementCreator", userID);
        announcementData.put("timestamp", Timestamp.now());

        // Add the announcement as a subcollection under the specific class document
        classRef.collection("announcements").add(announcementData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String announcementId = documentReference.getId();

                        Map<String, Object> posts = new HashMap<>();
                        posts.put("classID", classID);
                        posts.put("getID", announcementId);
                        posts.put("postType", "Announcement");
                        posts.put("timestamp", Timestamp.now());

                        classRef.collection("posts").add(posts)
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

    public static void updateAnnouncement(String classID, String announcementID, String announcementTitle, String announcementContent, MyCompleteListener myCompleteListener) {
        Map<String, Object> announcement = new HashMap<>();
        announcement.put("announcementTitle", announcementTitle);
        announcement.put("announcementContent", announcementContent);

        firebaseFirestore.collection("classes")
                .document(classID)
                .collection("announcements")
                .document(announcementID)
                .update(announcement)
                .addOnSuccessListener(unused -> {
                    Map<String, Object> post = new HashMap<>();
                    post.put("timestamp", Timestamp.now());

                    firebaseFirestore.collection("classes")
                            .document(classID)
                            .collection("posts")
                            .whereEqualTo("getID", announcementID)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        firebaseFirestore.collection("classes")
                                                .document(classID)
                                                .collection("posts")
                                                .document(documentSnapshot.getId())
                                                .update(post);
                                        myCompleteListener.onSuccess();
                                    }
                                }else{
                                    myCompleteListener.onFailure();
                                }
                            });
                });
    }


    public static void deleteAnnouncement(String classID, String announcementID, MyCompleteListener myCompleteListener) {
        firebaseFirestore
                .collection("classes")
                .document(classID)
                .collection("posts")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot documentSnapshot:task.getResult()){
                            if(documentSnapshot.getString("getID").equals(announcementID)){
                                firebaseFirestore.collection("classes")
                                        .document(classID)
                                        .collection("posts")
                                        .document(documentSnapshot.getId()).delete();
                                firebaseFirestore
                                        .collection("classes")
                                        .document(classID)
                                        .collection("announcements")
                                        .document(announcementID)
                                        .delete();
                                myCompleteListener.onSuccess();
                            }
                        }
                    }else{
                        myCompleteListener.onFailure();
                    }
                });
    }
}
