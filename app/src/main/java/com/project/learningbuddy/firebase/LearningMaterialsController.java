//package com.project.learningbuddy.firebase;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.Timestamp;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//import com.project.learningbuddy.listener.MyCompleteListener;
//import com.project.learningbuddy.model.FileInfo;
//import com.project.learningbuddy.model.LearningMaterials;
//import com.project.learningbuddy.model.Posts;
//
//import java.util.ArrayList;
//import java.util.List;
//import android.net.Uri;
//
//public class LearningMaterialsController {
//    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//    public static String userID = firebaseAuth.getCurrentUser().getUid();
//
//    public static void createLearningMaterial(String classID, String title, String content, List<FileInfo> files, MyCompleteListener myCompleteListener) {
//        // Upload files to Firebase Storage and get their download URLs
//        List<FileInfo> uploadedFiles = uploadFiles(files);
//
//        LearningMaterials learningMaterials = new LearningMaterials();
//        learningMaterials.setClassID(classID);
//        learningMaterials.setMaterialTitle(title);
//        learningMaterials.setMaterialContent(content);
//        learningMaterials.setMaterialCreator(userID);
//        learningMaterials.setFiles(uploadedFiles);
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
//
//    private static List<FileInfo> uploadFiles(List<FileInfo> files) {
//        List<FileInfo> uploadedFiles = new ArrayList<>();
//
//        for (FileInfo fileInfo : files) {
//            StorageReference storageRef = FirebaseStorage.getInstance().getReference()
//                    .child("learning_materials")
//                    .child(System.currentTimeMillis() + "_" + fileInfo.getFileName());
//
//            UploadTask uploadTask = storageRef.putFile(fileInfo.getFileUri());
//
//            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            FileInfo uploadedFile = new FileInfo(fileInfo.getFileType(), fileInfo.getFileName(), uri);
//                            uploadedFiles.add(uploadedFile);
//                        }
//                    });
//                }
//            });
//        }
//
//        return uploadedFiles;
//    }
//}
