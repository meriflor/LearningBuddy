package com.project.learningbuddy.firebase;

import android.net.Uri;

import com.google.common.annotations.VisibleForTesting;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.learningbuddy.listener.ExistListener;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.FileSpace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileSpaceController{
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static String userID = firebaseAuth.getCurrentUser().getUid();

    public static void checkFilesExist(ExistListener existListener){
        firebaseFirestore.collection("files")
                .whereEqualTo("userID", userID)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        if(task.getResult().isEmpty()){
                            existListener.onExist(false);
                        }else{
                            existListener.onExist(true);
                        }
                    }else{
                        existListener.onFailure(new Exception("There's a problem on checking the files on the FileSpace"));
                    }
        });
    }

    public static void createLearningMaterialsPublic(String materialID, String postTitle, String postContent, MyCompleteListener myCompleteListener){
        Map<String, Object> publicPost = new HashMap<>();
        publicPost.put("postTitle", postTitle);
        publicPost.put("postContent", postContent);
        publicPost.put("postCreator", userID);
        publicPost.put("postType", "Uploaded Files");
        publicPost.put("timestamp", Timestamp.now());
        publicPost.put("visibility", true);
        firebaseFirestore.collection("learning_materials")
                .document(materialID).set(publicPost)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        myCompleteListener.onSuccess();
                    }else{
                        myCompleteListener.onFailure();
                    }
                });
    }

    public static void createFileSpace(String fileName, String filePath, Uri fileUri, MyCompleteListener myCompleteListener){
        Map<String, Object> fileSpace = new HashMap<>();
        fileSpace.put("fileName", fileName);
        fileSpace.put("filePath", filePath);
        fileSpace.put("fileUri", fileUri);
        fileSpace.put("userID", userID);

        firebaseFirestore.collection("files")
                .document().set(fileSpace)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        myCompleteListener.onSuccess();
                    }else{
                        myCompleteListener.onFailure();
                    }
                });
    }
}
