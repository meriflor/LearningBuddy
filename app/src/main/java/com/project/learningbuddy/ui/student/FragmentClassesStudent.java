package com.project.learningbuddy.ui.student;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.JoinClassesAdapter;
import com.project.learningbuddy.firebase.ClassController;
import com.project.learningbuddy.listener.ExistListener;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.JoinClasses;

public class FragmentClassesStudent extends Fragment {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private RecyclerView recyclerView;
    private View view;
    private JoinClassesAdapter adapter;

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
        CollectionReference classref = firebaseFirestore.collection("student_class");
        Query classQuery = classref
                .whereEqualTo("studentID", userID)
                .orderBy("className", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<JoinClasses> options = new FirestoreRecyclerOptions.Builder<JoinClasses>()
                .setQuery(classQuery, JoinClasses.class)
                .build();
        adapter = new JoinClassesAdapter(options);

        RecyclerView recyclerView = view.findViewById(R.id.student_classListView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new JoinClassesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String classID = documentSnapshot.getId();
                String className = documentSnapshot.getString("className");
                String classYearLevel = documentSnapshot.getString("classYearLevel");
                String classSection = documentSnapshot.getString("classSection");
                Intent intent = new Intent(getContext(), StudentClassroomActivity.class);
                intent.putExtra(StudentClassroomActivity.CLASSID, classID);
                intent.putExtra(StudentClassroomActivity.CLASSNAME, className);
                intent.putExtra(StudentClassroomActivity.YEARLEVEL, classYearLevel);
                intent.putExtra(StudentClassroomActivity.SECTION, classSection);
                startActivity(intent);
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
//                            check if the student has already joined the class
                            ClassController.joinClass(accessCode, userID, new MyCompleteListener() {
                                @Override
                                public void onSuccess() {
                                    showToast("Classes joined!");
                                }

                                @Override
                                public void onFailure() {
                                    showToast("Something went wrong!");
                                }
                            });
                        }
                        @Override
                        public void onFailure(Exception e) {
                            showToast("Classes doesn't exist!");
                        }
                    });
                    dialog.dismiss();
                }
            }
        });

        btn_cancel.setOnClickListener(v -> dialog.dismiss());
    }

    public void showToast(String text){
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

//    private void checkStudentClassExist(String classID) {
//        FirebaseAuth firebaseAuth;
//        firebaseAuth = FirebaseAuth.getInstance();
//        String studentID = firebaseAuth.getCurrentUser().getUid();
//        DbQuery.checkStudentClassExist(classID, new MyCompleteListener() {
//            @Override
//            public void onSuccess() {
//                Log.d(TAG, "This class already exist in your list");
//            }
//            @Override
//            public void onFailure() {
//                joinClassSuccessfully(classID, studentID);
//            }
//        });
//    }
}
