package com.project.learningbuddy.firebase;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.learningbuddy.listener.ExistListener;
import com.project.learningbuddy.listener.MyCompleteListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ClassController {
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static String userID = firebaseAuth.getCurrentUser().getUid();

    public static String[] backgroundLayouts = {
            "class_backgroundimage1",
            "class_backgroundimage2",
            "class_backgroundimage3",
            "class_backgroundimage4",
            "class_backgroundimage5",
            "class_backgroundimage6",
            "class_backgroundimage7",
            "class_backgroundimage8",
            // Add more background layouts as needed
    };

    public static void createClass(String className, String subjectName, String classYearLevel, String classSection, String ownerTeacherID, MyCompleteListener myCompleteListener){
        int randomIndex = new Random().nextInt(backgroundLayouts.length);
        String backgroundLayout = backgroundLayouts[randomIndex];

        DocumentReference classRef = firebaseFirestore
                .collection("classes")
                .document();

        Map<String, Object> classes = new HashMap<>();
        classes.put("className", className);
        classes.put("subjectName", subjectName);
        classes.put("classYearLevel", classYearLevel);
        classes.put("classSection", classSection);
        classes.put("accessCode", classRef.getId());
        classes.put("ownerTeacherID", ownerTeacherID);
        classes.put("backgroundLayout", backgroundLayout);
        classes.put("timestamp", Timestamp.now());

        DocumentReference teachClass = firebaseFirestore
                .collection("teacher_class")
                .document();

        Map<String, Object> teach = new HashMap<>();
        teach.put("classID", classRef.getId());
        teach.put("userID", ownerTeacherID);
        teach.put("title", "Adviser");
        teach.put("className", className);

        classRef.set(classes).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                firebaseFirestore
                        .collection("users")
                        .document(ownerTeacherID)
                        .get()
                        .addOnSuccessListener(documentReference->{
                            Map<String, Object> teachers = new HashMap<>();
                            teachers.put("userID", ownerTeacherID);
                            teachers.put("userType", "Teacher");
                            teachers.put("title", "Adviser");
                            teachers.put("fullName", documentReference.getString("fullName"));
                            teachers.put("email", documentReference.getString("email"));
                            teachers.put("timestamp", Timestamp.now());
                            firebaseFirestore
                                    .collection("classes")
                                    .document(classRef.getId())
                                    .collection("teachers")
                                    .add(teachers)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            myCompleteListener.onSuccess(); // Pass the created Classes instance
                                            teachClass.set(teach);
                                        }
                                    });
                        });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                myCompleteListener.onFailure();
            }
        });
    }

    public static void checkClassExist(String accessCode, ExistListener classExistenceListener) {
        firebaseFirestore.collection("classes")
                .whereEqualTo("accessCode", accessCode)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            classExistenceListener.onExist(true);
                        } else {
                            classExistenceListener.onExist(false);
                        }
                    } else {
                        // Error occurred while checking class existence
                        classExistenceListener.onFailure(task.getException());
                    }
                });
    }

//    Checking if you have already joined the class
    public static void checkStudentClassExist(String classID, ExistListener existListener){
        firebaseFirestore.collection("student_class")
                .whereEqualTo("userID", firebaseAuth.getCurrentUser().getUid())
                .whereEqualTo("classID", classID)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot.isEmpty()) {
                            existListener.onExist(false);
                        } else {
                            existListener.onExist(true);
                        }
                    }else{
                        existListener.onFailure(new Exception("There's something wrong fetching documents!"));
                    }
                });
    }

    public static void joinClass(String classID, String studentID, MyCompleteListener myCompleteListener){
        firebaseFirestore.collection("users")
                .document(studentID)
                .get().addOnSuccessListener(documentSnapshot -> {
                    String email = documentSnapshot.getString("email");
                    Map<String, Object> studentData = new HashMap<>();
                    studentData.put("email", email);
                    studentData.put("userID", studentID);
                    studentData.put("userType", "Student");
                    studentData.put("timestamp", Timestamp.now());
                    firebaseFirestore.collection("classes")
                            .document(classID)
                            .collection("students")
                            .add(studentData);
                    firebaseFirestore.collection("classes")
                            .document(classID)
                            .get().addOnCompleteListener(task -> {
                                DocumentSnapshot documentSnapshot1 = task.getResult();
                                String className = documentSnapshot1.getString("className");
                                Map<String, Object> studClass = new HashMap<>();
                                studClass.put("classID", classID);
                                studClass.put("className", className);
                                studClass.put("userID", studentID);
                                studClass.put("timestamp", Timestamp.now());
                                firebaseFirestore.collection("student_class")
                                        .document().set(studClass).addOnCompleteListener(task1 -> {
                                            if(task1.isSuccessful()){
                                                myCompleteListener.onSuccess();
                                            }else{
                                                myCompleteListener.onFailure();
                                            }
                                        });
                            });
                });
    }

    public static void updateClass(String classID, String className, String subjectName, String yearLevel, String section, MyCompleteListener myCompleteListener){
        Map<String, Object> classes = new HashMap<>();
        classes.put("className", className);
        classes.put("subjectName", subjectName);
        classes.put("classYearLevel", yearLevel);
        classes.put("classSection", section);

        firebaseFirestore.collection("classes")
                .document(classID)
                .update(classes)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myCompleteListener.onSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });
        if(className!=null){
            Map<String, Object> teach = new HashMap<>();
            teach.put("className", className);
            firebaseFirestore.collection("teacher_class")
                    .whereEqualTo("classID", classID)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                                firebaseFirestore.collection("teacher_class")
                                        .document(documentSnapshot.getId())
                                        .update(teach);
                            }
                        }
                    });
        }
    }

    public static void deleteClass(String classID, MyCompleteListener myCompleteListener){
        firebaseFirestore.collection("classes")
                .document(classID)
                .delete().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        deleteCollection(classID, "quizzes", "questions");
                        deleteCollection(classID, "quizzes", "scores");
                        deleteCollection(classID, "learning_materials", "files");

                        deleteCollection(classID, "learning_materials", "");
                        deleteCollection(classID, "announcements", "");
                        deleteCollection(classID, "quizzes", "");
                        deleteCollection(classID, "posts", "");
                        deleteCollection(classID, "teachers", "");
                        deleteCollection(classID, "students", "");

                        deleteCollection(classID, "teacher_class", "");
                        deleteCollection(classID, "student_class", "");

                        myCompleteListener.onSuccess();
                    }else{
                        myCompleteListener.onFailure();
                    }
                });
    }

    public static void deleteCollection(String classID, String collection, String subCollection){
        if(subCollection.isEmpty()){
            if(collection.equals("teacher_class") || collection.equals("student_class")){
                CollectionReference collectionReference = firebaseFirestore.collection(collection);
                collectionReference.whereEqualTo("classID", classID)
                    .get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                                collectionReference.document(documentSnapshot.getId()).delete();
                            }
                        }
                    });
            }else{
                CollectionReference collectionReference = firebaseFirestore.collection("classes")
                        .document(classID).collection(collection);
                collectionReference.get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                            collectionReference.document(documentSnapshot.getId()).delete();
                        }
                    }
                });
            }
        }else{
            CollectionReference collectionReference = firebaseFirestore.collection("classes")
                    .document(classID)
                    .collection(collection);
//            if(subCollection.equals("questions")){
                collectionReference.get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                            CollectionReference quesRef = collectionReference.document(documentSnapshot.getId())
                                .collection(subCollection);
                            quesRef.get().addOnCompleteListener(task1 -> {
                                for(QueryDocumentSnapshot documentSnapshot1:task1.getResult()){
                                    quesRef.document(documentSnapshot1.getId()).delete();
                                }
                            });
                        }
                    }
                });
//            }
        }
    }

    public static void addUserToClass(String email, String classID, String className, String userType, MyCompleteListener myCompleteListener){
        firebaseFirestore.collection("users")
                .whereEqualTo("email", email)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String userID = documentSnapshot.getId();
                            Map<String, Object> user = new HashMap<>();
                            user.put("userID", userID);
                            user.put("email", email);
                            user.put("timestamp", Timestamp.now());

                            if (userType.equals("Teacher")) {
                                user.put("userType", "Teacher");
                                user.put("title", "Co-Adviser");
                                user.put("fullName", documentSnapshot.getString("fullName"));
                            } else if (userType.equals("Student")) {
                                user.put("userType", "Student");
                            }

                            String collection = userType.equals("Teacher") ? "teachers" : "students";

                            firebaseFirestore.collection("classes")
                                    .document(classID)
                                    .collection(collection)
                                    .add(user)
                                    .addOnSuccessListener(documentReference ->
                                            myCompleteListener.onSuccess()
                                    )
                                    .addOnFailureListener(e -> myCompleteListener.onFailure());

                            if(userType.equals("Teacher")){
                                Map<String, Object> teach = new HashMap<>();
                                teach.put("className", className);
                                teach.put("userID", userID);
                                teach.put("classID", classID);
                                teach.put("title", "Co-Adviser");
                                firebaseFirestore.collection("teacher_class")
                                        .add(teach);
                            }else{
                                Map<String, Object> stud = new HashMap<>();
                                stud.put("className", className);
                                stud.put("userID", userID);
                                stud.put("classID", classID);
                                stud.put("title", "Student");
                                firebaseFirestore.collection("student_class")
                                        .add(stud);
                            }
                        }
                    }
                });
    }

    public static void removeUser(String classID, String userType, String userID, String userClassID, MyCompleteListener myCompleteListener){
        String collectionGroup, collection;
        if(userType.equals("Teacher")){
            collectionGroup = "teachers";
            collection = "teacher_class";
        }else{
            collectionGroup = "students";
            collection = "student_class";
        }

        firebaseFirestore
                .collection("classes")
                .document(classID)
                .collection(collectionGroup)
                .document(userClassID)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TAG", userClassID + " and userTYpe "+userType);
                        myCompleteListener.onSuccess();
                        firebaseFirestore
                                .collection(collection)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                                        for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                                            String docUserID = documentSnapshot.getString("userID");
                                            String docClassID = documentSnapshot.getString("classID");
                                            if(docUserID.equals(userID) && docClassID.equals(classID)){
                                                String docteachClassID = documentSnapshot.getId();
                                                firebaseFirestore
                                                        .collection(collection)
                                                        .document(docteachClassID)
                                                        .delete();
                                            }
                                        }
                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });
    }

    public static void leaveClass(String classID, MyCompleteListener myCompleteListener){
        CollectionReference studRef = firebaseFirestore
                .collection("classes")
                .document(classID)
                .collection("students");
        CollectionReference studClassRef = firebaseFirestore
                .collection("student_class");

        studRef.get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                            if(documentSnapshot.getString("userID").equals(userID)){
                                studRef.document(documentSnapshot.getId()).delete();
                            }
                        }

                        studClassRef.get()
                                .addOnCompleteListener(task1 -> {
                                    if(task.isSuccessful()){
                                        for(QueryDocumentSnapshot documentSnapshot: task1.getResult()){
                                            if(documentSnapshot.getString("userID").equals(userID)
                                            && documentSnapshot.getString("classID").equals(classID)){
                                                studClassRef.document(documentSnapshot.getId()).delete();
                                            }
                                        }
                                        myCompleteListener.onSuccess();
                                    }else{
                                        myCompleteListener.onFailure();
                                    }
                                });
                    }else{
                        Log.d("TAG", "Something wrong with fetching the userID");
                    }
                });
    }
}
