package com.project.learningbuddy.firebase;

import android.net.Uri;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.PracticeReading;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PracticeReadingController {
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static String userID = firebaseAuth.getCurrentUser().getUid();

    public static void createPracticeReading(String classID, String materialID, String title, String content, MyCompleteListener myCompleteListener){
        Map<String, Object> pracRead = new HashMap<>();
        pracRead.put("learningMaterialID", materialID);
        pracRead.put("materialTitle", title);
        pracRead.put("materialContent", content);
        pracRead.put("materialCreator", userID);
        pracRead.put("materialType", "Practice Reading");
        pracRead.put("timestamp", Timestamp.now());
        firebaseFirestore.collection("classes")
                .document(classID)
                .collection("learning_materials")
                .document(materialID)
                .set(pracRead)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public static void postPracticeReading(String classID, String materialID){
        Map<String, Object> posts = new HashMap<>();
        posts.put("classID", classID);
        posts.put("getID", materialID);
        posts.put("postType", "Practice Reading");
        posts.put("timestamp", Timestamp.now());
        firebaseFirestore.collection("classes")
                .document(classID)
                .collection("posts")
                .document().set(posts);
    }

    public static void addFileToPracticeReading(String classID, String materialID, String text, String imageName, String imagePath, Uri imageUri, MyCompleteListener myCompleteListener){
        Map<String, Object> pracRead = new HashMap<>();
        pracRead.put("text", text);
        if(imageUri==null){
            pracRead.put("imageName", null);
            pracRead.put("imagePath", null);
            pracRead.put("imageUri", null);
        }else{
            pracRead.put("imageName", imageName);
            pracRead.put("imagePath", imagePath);
            pracRead.put("imageUri", imageUri);
        }

        firebaseFirestore.collection("classes")
                .document(classID)
                .collection("learning_materials")
                .document(materialID)
                .collection("files")
                .document().set(pracRead)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        myCompleteListener.onSuccess();
                    }else{
                        myCompleteListener.onFailure();
                    }
                });
    }
}
