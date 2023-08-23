package com.project.learningbuddy.ui.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

//import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.ClassesAdapter;
import com.project.learningbuddy.firebase.ClassController;
import com.project.learningbuddy.listener.ClassDataListener;
import com.project.learningbuddy.model.Classes;
//import com.trialProjects.test100.FirebaseServices.DbQuery;
//import com.trialProjects.test100.Listener.MyCompleteListener;
//import com.trialProjects.test100.R;

public class FragmentClassesTeacher extends Fragment {

    //widgets
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText et_className, et_subjectName, et_classYearLevel, et_classSection;
    private Button btn_create, btn_cancel;
    private FloatingActionButton fab;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private ClassesAdapter adapter;
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
        CollectionReference classRef = firebaseFirestore.collection("classes");
        Query classQuery = classRef
                .whereEqualTo("ownerTeacherID", userID)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Classes> options = new FirestoreRecyclerOptions.Builder<Classes>().setQuery(classQuery, Classes.class).build();
        adapter = new ClassesAdapter(options);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ClassesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String classID = documentSnapshot.getId().toString();
                String className = documentSnapshot.getString("className");
                Intent intent = new Intent(getContext(), TeacherClassroomActivity.class);
                intent.putExtra(TeacherClassroomActivity.CLASSNAME,className);
                intent.putExtra(TeacherClassroomActivity.CLASSID,classID);

                startActivity(intent);
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
                    ClassController.createClass(className, subjectName, classYearLevel, classSection, FirebaseAuth.getInstance().getCurrentUser().getUid(), new ClassDataListener() {
                        @Override
                        public void onSuccess(Classes classes) {
                            showToast("Classes created successfully!");
                            adapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onFailure(Exception exception) {
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