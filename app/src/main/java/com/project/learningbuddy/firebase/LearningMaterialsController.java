package com.project.learningbuddy.firebase;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
    public static FirebaseStorage storage = FirebaseStorage.getInstance();

    public static void createClassLearningMaterial(String classID, String title, String content, List<String> files, MyCompleteListener myCompleteListener) {
//        retrieving classname, subjectname and yearlevel from classes collection -- for learning_materials (tags)
        DocumentReference classRef = firebaseFirestore.collection("classes")
                .document(classID);
       classRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        adding the data to the collection learning_materials
                        Map<String, Object> learningMaterials = new HashMap<>();
                        learningMaterials.put("materialTitle", title);
                        learningMaterials.put("materialContent", content);
                        learningMaterials.put("files", files);
                        learningMaterials.put("materialCreator", userID);
                        learningMaterials.put("visibility", false);
                        learningMaterials.put("timestamp", Timestamp.now());
                        List<String> tags = new ArrayList<>();
                        tags.add(documentSnapshot.getString("className"));
                        tags.add(documentSnapshot.getString("subjectName"));
                        tags.add(documentSnapshot.getString("classYearLevel"));
                        learningMaterials.put("tags", tags);

                        firebaseFirestore.collection("learning_materials")
                                .add(learningMaterials)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        String learningMaterialID = documentReference.getId();

                                        Map<String, Object> classLearningMaterials = new HashMap<>();
                                        classLearningMaterials.put("materialTitle", title);
                                        classLearningMaterials.put("materialContent", content);
                                        classLearningMaterials.put("files", files);
                                        classLearningMaterials.put("materialCreator", userID);
                                        classLearningMaterials.put("learningMaterialID", learningMaterialID);
                                        classLearningMaterials.put("timestamp", Timestamp.now());

                                        classRef.collection("learning_materials")
                                                .add(classLearningMaterials)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Map<String, Object> posts = new HashMap<>();
                                                        posts.put("classID", classID);
                                                        posts.put("getID", documentReference.getId());
                                                        posts.put("postType", "Learning Material");
                                                        posts.put("timestamp", Timestamp.now());

                                                        classRef.collection("posts")
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

//    this is when we delete the detail in the learning materials in class
    public static void deleteMaterials(String classID, String materialID, MyCompleteListener myCompleteListener){
        CollectionReference filesRef = firebaseFirestore.collection("classes")
                .document(classID)
                .collection("learning_materials")
                .document(materialID)
                .collection("files");
        DocumentReference materialsRef = firebaseFirestore.collection("classes")
                .document(classID)
                .collection("learning_materials")
                .document(materialID);
        Query postQuery = firebaseFirestore.collection("classes")
                .document(classID)
                .collection("posts")
                .whereEqualTo("getID", materialID);
        CollectionReference postRef = firebaseFirestore.collection("classes")
                .document(classID)
                .collection("posts");

        materialsRef.delete()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        filesRef.get().addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()){
                                for(QueryDocumentSnapshot documentSnapshot:task1.getResult()){
                                    filesRef.document(documentSnapshot.getId()).delete();
                                    myCompleteListener.onSuccess();
                                }
                            }else {
                                myCompleteListener.onFailure();
                            }
                        });
                        postQuery.get().addOnCompleteListener(task2->{
                            if(task2.isSuccessful()){
                                for(QueryDocumentSnapshot documentSnapshot: task2.getResult()){
                                    postRef.document(documentSnapshot.getId()).delete();
                                }
                            }else{
                                Log.d("TAG", "There's a problem deleting the material post");
                            }
                        });
                    }else{
                        Log.d("TAG", "There's a problem deleting the material");
                    }
                });

    }

//    this is when deleting the learning material as a whole
    public static void deleteLearningMaterial(String classID, String materialID, MyCompleteListener myCompleteListener){
        CollectionReference filesRef = firebaseFirestore.collection("classes")
                .document(classID)
                .collection("learning_materials")
                .document(materialID)
                .collection("files");
        DocumentReference materialsRef = firebaseFirestore.collection("classes")
                .document(classID)
                .collection("learning_materials")
                .document(materialID);
        Query postQuery = firebaseFirestore.collection("classes")
                .document(classID)
                .collection("posts")
                .whereEqualTo("getID", materialID);
        CollectionReference postRef = firebaseFirestore.collection("classes")
                .document(classID)
                .collection("posts");
        CollectionReference matFilesRef = firebaseFirestore.collection("learning_materials")
                .document(materialID)
                .collection("files");

        firebaseFirestore.collection("learning_materials")
                .document(materialID).delete().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        matFilesRef.get().addOnCompleteListener(task1 -> {
                            for(QueryDocumentSnapshot documentSnapshot:task1.getResult()){
                                matFilesRef.document(documentSnapshot.getId()).delete();
                                deleteMaterialsFiles(documentSnapshot.getString("filePath"));
                            }
                        });
                    }
                });

        materialsRef.get().addOnSuccessListener(documentSnapshot1 -> {
            if(documentSnapshot1.exists()){
                materialsRef.delete()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                filesRef.get().addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        for(QueryDocumentSnapshot documentSnapshot:task1.getResult()){
                                            filesRef.document(documentSnapshot.getId()).delete();
                                            myCompleteListener.onSuccess();
                                        }
                                    }else {
                                        myCompleteListener.onFailure();
                                    }
                                });
                                postQuery.get().addOnCompleteListener(task2->{
                                    if(task2.isSuccessful()){
                                        for(QueryDocumentSnapshot documentSnapshot: task2.getResult()){
                                            postRef.document(documentSnapshot.getId()).delete();
                                        }
                                    }else{
                                        Log.d("TAG", "There's a problem deleting the material post");
                                    }
                                });
                            }else{
                                Log.d("TAG", "There's a problem deleting the material");
                            }
                        });
            }else{
                Log.d("TAG", "There's no same post exist, it must be already deleted");
            }
        }).addOnFailureListener(e -> {
            Log.d("TAG", e.getMessage());
        });
    }


    public static void deleteMaterialsFiles(String filePath){
        StorageReference storageRef = storage.getReference();
        storageRef.child(filePath).delete();
    }

    public static void createLearningMaterial(String classID, String materialID, String title, String content, MyCompleteListener myCompleteListener){
        firebaseFirestore.collection("classes")
                .document(classID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Map<String, Object> materials = new HashMap<>();
                    materials.put("materialTitle", title);
                    materials.put("materialContent", content);
                    materials.put("materialCreator", userID);
                    materials.put("timestamp", Timestamp.now());
                    materials.put("visibility", false);
                    List<String> tags = new ArrayList<>();
                    tags.add(documentSnapshot.getString("className"));
                    tags.add(documentSnapshot.getString("subjectName"));
                    tags.add(documentSnapshot.getString("classYearLevel"));
                    materials.put("tags", tags);
                    firebaseFirestore.collection("learning_materials")
                            .document(materialID)
                            .set(materials);
                    Map<String, Object> classMaterials = new HashMap<>();
                    classMaterials.put("learningMaterialID", materialID);
                    classMaterials.put("materialTitle", title);
                    classMaterials.put("materialContent", content);
                    classMaterials.put("materialCreator", userID);
                    classMaterials.put("timestamp", Timestamp.now());
                    firebaseFirestore.collection("classes")
                            .document(classID)
                            .collection("learning_materials")
                            .document(materialID).set(classMaterials);
                    myCompleteListener.onSuccess();
                }).addOnFailureListener(e -> {
                    myCompleteListener.onFailure();
                });
    }

    public static void postLearningMaterial(String classID, String materialID){
        Map<String, Object> posts = new HashMap<>();
        posts.put("classID", classID);
        posts.put("getID", materialID);
        posts.put("postType", "Learning Material");
        posts.put("timestamp", Timestamp.now());
        firebaseFirestore.collection("classes")
                .document(classID)
                .collection("posts")
                .document().set(posts);
    }

    public static void addFileToLearningMaterial(String classID, String materialID, String fileName, String filePath, Uri fileUri, MyCompleteListener myCompleteListener){
        Map<String, Object> file = new HashMap<>();
        file.put("fileName", fileName);
        file.put("filePath", filePath);
        file.put("fileUri", fileUri);
        firebaseFirestore.collection("learning_materials")
                .document(materialID)
                .collection("files")
                .document().set(file)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseFirestore.collection("classes")
                                .document(classID)
                                .collection("learning_materials")
                                .document(materialID)
                                .collection("files")
                                .document().set(file);
                    }
                });
    }
}
