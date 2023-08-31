package com.project.learningbuddy.firebase;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.FileInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LearningMaterialsController {
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static String userID = firebaseAuth.getCurrentUser().getUid();

//    public static void createLearningMaterial(String classID, String title, String content, List<String> files, MyCompleteListener myCompleteListener) {
//        LearningMaterials learningMaterials = new LearningMaterials();
//        learningMaterials.setClassID(classID);
//        learningMaterials.setMaterialTitle(title);
//        learningMaterials.setMaterialContent(content);
//        learningMaterials.setMaterialCreator(userID);
//        learningMaterials.setFiles(files);
//        learningMaterials.setTimestamp(Timestamp.now());
//
//        // Add the LearningMaterial to Firestore
//        FirebaseFirestore.getInstance().collection("learning_materials")
//                .add(learningMaterials)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        myCompleteListener.onSuccess();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(Exception e) {
//                        myCompleteListener.onFailure();
//                    }
//                });
//
//        DocumentReference postRef = firebaseFirestore.collection("posts").document();
//        Posts posts = new Posts();
//        posts.setClassID(classID);
//        posts.setPostType("Learning Materials");
//        posts.setPostTitle(title);
//        posts.setPostContent(content);
//        posts.setPostCreatorID(userID);
//        posts.setTimestamp(Timestamp.now());
//
//        postRef.set(posts).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public static void createClassLearningMaterial(String classID, String title, String content, List<String> files, MyCompleteListener myCompleteListener) {
//        retrieving classname, subjectname and yearlevel from classes collection -- for learning_materials (tags)
        firebaseFirestore.collection("classes")
                .document(classID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        adding the data to the collection learning_materials
                        Map<String, Object> learningMaterials = new HashMap<>();
                        learningMaterials.put("materialTitle", title);
                        learningMaterials.put("files", files);
                        learningMaterials.put("materialCreator", firebaseAuth.getCurrentUser().getUid());
                        learningMaterials.put("visibility", false);
                        learningMaterials.put("timestamp", Timestamp.now());
                        List<String> tags = new ArrayList<>();
                        tags.add(documentSnapshot.getString("className"));
                        tags.add(documentSnapshot.getString("subjectName"));
                        tags.add(documentSnapshot.getString("classYearLevel"));
                        learningMaterials.put("tags", tags);

                        firebaseFirestore.collection("learning_materials")
                                .add(learningMaterials);
                }
            });

//        adding details to the learning_materials but under a class
        CollectionReference classRef = firebaseFirestore.collection("classes")
                .document(classID)
                .collection("learning_materials");

        Map<String, Object> classLearningMaterials = new HashMap<>();
        classLearningMaterials.put("materialTitle", title);
        classLearningMaterials.put("materialContent", content);
        classLearningMaterials.put("files", files);
        classLearningMaterials.put("materialCreator", firebaseAuth.getCurrentUser().getUid());
        classLearningMaterials.put("timestamp", Timestamp.now());

        classRef.add(classLearningMaterials)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
//                        after adding to the learning materials, the collection of posts will also be updated under the same classes
                        Map<String, Object> posts = new HashMap<>();
                        posts.put("classID", classID);
                        posts.put("getID", documentReference.getId());
                        posts.put("postType", "Learning Material");

                        firebaseFirestore.collection("classes")
                                .document(classID)
                                .collection("posts")
                                .add(posts)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        myCompleteListener.onSuccess();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        myCompleteListener.onFailure();
                                    }
                                });
                    }
                });
    }



    private static List<FileInfo> uploadFiles(List<FileInfo> files) {
        List<FileInfo> uploadedFiles = new ArrayList<>();

        for (FileInfo fileInfo : files) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                    .child("learning_materials")
                    .child(System.currentTimeMillis() + "_" + fileInfo.getFileName());

            UploadTask uploadTask = storageRef.putFile(fileInfo.getFileUri());

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            FileInfo uploadedFile = new FileInfo(fileInfo.getFileType(), fileInfo.getFileName(), uri);
                            uploadedFiles.add(uploadedFile);
                        }
                    });
                }
            });
        }

        return uploadedFiles;
    }
}
