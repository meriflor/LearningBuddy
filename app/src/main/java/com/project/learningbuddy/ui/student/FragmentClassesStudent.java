package com.project.learningbuddy.ui.student;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.ClassListStudentAdapter;
import com.project.learningbuddy.firebase.ClassController;
import com.project.learningbuddy.listener.ExistListener;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.ClassList;

public class FragmentClassesStudent extends Fragment {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private RecyclerView recyclerView;
    private View view;
    private ClassListStudentAdapter adapter;

    private FloatingActionButton fab;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    String userID = firebaseAuth.getCurrentUser().getUid();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_classes_student, container, false);
        fab = view.findViewById(R.id.student_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showJoinDialog();
            }
        });

        getClassList();
        return view;
    }

    private void getClassList() {
        Query classQuery = firebaseFirestore.collection("student_class")
                .whereEqualTo("userID", userID)
                .orderBy("className", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ClassList> options = new FirestoreRecyclerOptions.Builder<ClassList>()
                .setQuery(classQuery, ClassList.class)
                .build();
        adapter = new ClassListStudentAdapter(options);

        recyclerView = view.findViewById(R.id.student_classListView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ClassListStudentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String classID = documentSnapshot.getString("classID");
                Log.d("TAG", classID);

                firebaseFirestore
                        .collection("classes")
                        .document(classID)
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String className = documentSnapshot.getString("className");
                                String classYearLevel = documentSnapshot.getString("classYearLevel");
                                String classSection = documentSnapshot.getString("classSection");
                                String subjectName = documentSnapshot.getString("subjectName");
//                                Long backgroundLayout = documentSnapshot.getLong("backgroundLayout");
//                                int intValue = backgroundLayout.intValue();


                                String backgroundLayout = documentSnapshot.getString("backgroundLayout");
                                int layoutResourceId = getContext().getResources()
                                        .getIdentifier(backgroundLayout, "layout", getContext().getPackageName());

                                Intent intent = new Intent(getContext(), StudentClassroomActivity.class);
                                intent.putExtra(StudentClassroomActivity.CLASSNAME,className);
                                intent.putExtra(StudentClassroomActivity.CLASSID,classID);
                                intent.putExtra(StudentClassroomActivity.CLASSSUBJECT,subjectName);
                                intent.putExtra(StudentClassroomActivity.YEARLEVEL, classYearLevel);
                                intent.putExtra(StudentClassroomActivity.SECTION, classSection);
                                intent.putExtra("bgLayout", layoutResourceId);
                                firebaseFirestore
                                        .collection("classes")
                                        .document(classID)
                                        .collection("teachers")
                                        .get().addOnCompleteListener(task -> {
                                            if(task.isSuccessful()){
                                                for(QueryDocumentSnapshot documentSnapshot1:task.getResult()){
                                                    if(documentSnapshot1.getString("title").equals("Adviser")){
                                                        String teacherName = documentSnapshot1.getString("fullName");

                                                        Log.d("TAG", className+ classSection+ subjectName+ teacherName);
                                                        intent.putExtra("teacherName", teacherName);

                                                        startActivity(intent);
                                                    }
                                                }
                                            }
                                        });
                            }
                        }).addOnFailureListener(e -> {
                            Log.d("TAG", e.getMessage());
                        });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void showJoinDialog() {
        dialogBuilder = new AlertDialog.Builder(getContext());
        View joinClassView = getLayoutInflater().inflate(R.layout.pop_up_window_join_class, null);

        EditText et_accessCode = joinClassView.findViewById(R.id.class_access_code);
        Button btn_join = joinClassView.findViewById(R.id.class_btn_join);
        Button btn_cancel = joinClassView.findViewById(R.id.class_btn_cancel);

        dialogBuilder.setView(joinClassView);
        dialog = dialogBuilder.create();
        dialog.show();

        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accessCode = et_accessCode.getText().toString().trim();
                if(accessCode.isEmpty()){
                    showToast("Please enter the access code.");
                }else{
                    ClassController.checkClassExist(accessCode, new ExistListener() {
                        @Override
                        public void onExist(Boolean exist) {
                            if(exist){
                                ClassController.checkStudentClassExist(accessCode, new ExistListener() {
                                    @Override
                                    public void onExist(Boolean exist) {
                                        Log.d("RAAAAAAAAAAAAAG", "is it already exist?: "+exist);
                                        if(exist){
                                            showToast("Already joined the class!");
                                        }else{
                                            ClassController.joinClass(accessCode, userID, new MyCompleteListener() {
                                                @Override
                                                public void onSuccess() {
                                                    showToast("Class joined!");
                                                    dialog.dismiss();
                                                }

                                                @Override
                                                public void onFailure() {
                                                    showToast("Something went wrong!");
                                                }
                                            });
                                        }
                                    }
                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.d("TAG", e.getMessage());
                                    }
                                });
                            }else{
                                showToast("Class not exist!");
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.d("TAG", e.getMessage());
                        }
                    });
                }
            }
        });

        btn_cancel.setOnClickListener(v -> dialog.dismiss());
    }

    public void showToast(String text){
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }
}
