package com.project.learningbuddy.ui.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.ClassListTeacherAdapter;
import com.project.learningbuddy.firebase.ClassController;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.TeacherClass;

public class FragmentClassesTeacher extends Fragment {

    //widgets
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText et_className, et_subjectName, et_classYearLevel, et_classSection;
    private Button btn_create, btn_cancel;
    private FloatingActionButton fab;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private ClassListTeacherAdapter adapter;
    private View view;
    String userID = firebaseAuth.getCurrentUser().getUid();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_classes_teacher, container, false);

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createClass();
            }
        });

        getClassList();

        return view;
    }
    private void getClassList(){
        Query classQuery = firebaseFirestore
                .collection("teacher_class")
                .whereEqualTo("userID", userID)
                .orderBy("className", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<TeacherClass> options = new FirestoreRecyclerOptions.Builder<TeacherClass>()
                .setQuery(classQuery, TeacherClass.class)
                .build();
        adapter = new ClassListTeacherAdapter(options);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new ClassListTeacherAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String classID = documentSnapshot.getString("classID");

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
                                Long backgroundLayout = documentSnapshot.getLong("backgroundLayout");
                                int intValue = backgroundLayout.intValue();
                                Intent intent = new Intent(getContext(), TeacherClassroomActivity.class);
                                intent.putExtra(TeacherClassroomActivity.CLASSNAME,className);
                                intent.putExtra(TeacherClassroomActivity.CLASSID,classID);
                                intent.putExtra(TeacherClassroomActivity.CLASSSUBJECT,subjectName);
                                intent.putExtra(TeacherClassroomActivity.YEARLEVEL, classYearLevel);
                                intent.putExtra(TeacherClassroomActivity.SECTION, classSection);
                                intent.putExtra("bgLayout", intValue);
                                firebaseFirestore
                                        .collection("classes")
                                        .document(classID)
                                        .collection("students")
                                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                int students = queryDocumentSnapshots.size();
                                                intent.putExtra("studentCount", students);

                                                startActivity(intent);
                                            }
                                        });
                            }
                        });
            }
        });
    }

    private void createClass() {
        View createClassPopUpWindow = getLayoutInflater().inflate(R.layout.pop_up_window_create_class, null);
        et_className = createClassPopUpWindow.findViewById(R.id.class_name);
        et_subjectName = createClassPopUpWindow.findViewById(R.id.class_subject_name);
        et_classYearLevel = createClassPopUpWindow.findViewById(R.id.class_year_level);
        et_classSection = createClassPopUpWindow.findViewById(R.id.class_section);

        btn_create = createClassPopUpWindow.findViewById(R.id.class_btn_create);
        btn_cancel = createClassPopUpWindow.findViewById(R.id.class_btn_cancel);

        dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setView(createClassPopUpWindow);
        dialog = dialogBuilder.create();
        dialog.show();

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String className = et_className.getText().toString().trim();
                String subjectName = et_subjectName.getText().toString().trim();
                String classYearLevel = et_classYearLevel.getText().toString().trim();
                String classSection = et_classSection.getText().toString().trim();

                if(className.isEmpty() || subjectName.isEmpty() || classYearLevel.isEmpty() || classSection.isEmpty()){
                    showToast("Please fill in the blanks.");
                    return;
                }else{
                    ClassController.createClass(className, subjectName, classYearLevel, classSection, FirebaseAuth.getInstance().getCurrentUser().getUid(), new MyCompleteListener() {
                        @Override
                        public void onSuccess() {
                            showToast("Classes created successfully!");
                            adapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onFailure() {
                            showToast("Something went wrong!");
                        }
                    });
                }
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void showToast(String text){
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
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
}