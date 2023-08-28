package com.project.learningbuddy.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.Announcements;
import com.project.learningbuddy.model.Posts;

public class AnnouncementController {
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


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
                        CollectionReference postsRef = firebaseFirestore.collection("classes")
                                .document(classID).collection("posts");

                        Query query = postsRef.whereEqualTo("getID", announcementID);

                        query.get().addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Delete the document
                                    postsRef.document(document.getId()).delete();
                                }
                            }else{
                                Log.d("TAG", "Error getting documents: ", task.getException());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });
    }
}
