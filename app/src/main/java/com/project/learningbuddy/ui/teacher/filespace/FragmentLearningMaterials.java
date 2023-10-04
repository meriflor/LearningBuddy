package com.project.learningbuddy.ui.teacher.filespace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.TeacherDraftListAdapter;
import com.project.learningbuddy.adapter.TeacherPostListAdapter;
import com.project.learningbuddy.firebase.LearningMaterialsController;
import com.project.learningbuddy.listener.ExistListener;
import com.project.learningbuddy.listener.LearningMaterialListener;
import com.project.learningbuddy.model.LearningMaterials;
import com.project.learningbuddy.ui.teacher.learningmaterials.TeacherCreateLearningMaterials;
import com.project.learningbuddy.ui.teacher.learningmaterials.ViewLearningMaterialActivity;

public class FragmentLearningMaterials extends Fragment {

    private FloatingActionsMenu floatingActionsMenu;
    private FloatingActionButton addFiles, addPracticeReading;
    private TextView draft, posts;
    private RecyclerView draftView, postView;
    private TeacherDraftListAdapter draftListAdapter;
    private TeacherPostListAdapter postListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_file_space, container, false);

        floatingActionsMenu = view.findViewById(R.id.add_learningMaterials_fileSpace_fab);
        draft = view.findViewById(R.id.draft_viewAll_text);
        posts = view.findViewById(R.id.post_viewAll_text);
        draftView = view.findViewById(R.id.draft_learningMaterials_recyclerView);
        postView = view.findViewById(R.id.posts_learningMaterials_recyclerView);
        addFiles = view.findViewById(R.id.uploadFiles_fab);

//        floatingActionButton.setOnClickListener(view1 -> {
//            Intent intent = new Intent(getContext(), TeacherCreateLearningMaterials.class);
//            startActivity(intent);
//        });
        addFiles.setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), FileSpaceActivity.class));
        });

        getDraftList();
        getPostList();

        return view;
    }

    private void getPostList() {
        Query postQuery = FirebaseFirestore.getInstance()
                .collection("learning_materials")
                .whereEqualTo("materialCreator", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("visibility", true)
                .orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<LearningMaterials> options = new FirestoreRecyclerOptions.Builder<LearningMaterials>()
                .setQuery(postQuery, LearningMaterials.class).build();
        postListAdapter = new TeacherPostListAdapter(options);
        postView.setLayoutManager(new LinearLayoutManager(getContext()));
        postView.setAdapter(postListAdapter);

        postQuery.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(task.getResult().isEmpty())
                    posts.setVisibility(View.GONE);
                else
                    posts.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getDraftList() {
        Query draftQuery = FirebaseFirestore.getInstance()
                .collection("learning_materials")
                .whereEqualTo("materialCreator", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("visibility", false)
                .orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<LearningMaterials> options = new FirestoreRecyclerOptions.Builder<LearningMaterials>()
                .setQuery(draftQuery, LearningMaterials.class).build();
        draftListAdapter = new TeacherDraftListAdapter(options);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        draftView.setLayoutManager(layoutManager);
        draftView.setAdapter(draftListAdapter);

        draftQuery.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(task.getResult().isEmpty()){
                    draft.setVisibility(View.GONE);
                }else{
                    draft.setVisibility(View.VISIBLE);
                }
            }
        });

//        In order to view this is through the ViewLearningMaterialActivity
//        However, if we do it, we have to pass the data "classID". But in this page it doesn't pass classID from somewhere
//        but we have to query it-- but this will cause another read
//        another problem is that if we pass a null to solve it on the other activity(ViewLearningMatieralActivity)
//        we need to add a condition that if there are no classID then we have to do this and do that.
//        for short we have to double the conditions again.
//        BUT if we add another layout for this specific viewing of learning materials then it means were getting more layout again
//        if we do this, then its better to separate the two learning materials from classes and this will be dependent
//        ====but the problem here is, that we'll be doubling the uploaded files. Like for example, they uploaded this specific file
//        in their classes and they also want this specific file to be publicize---causes to doubling of files inthe storage
//        and eventually we'll need larger storage. AND its not advisable to do this way.

//        Another idea pops up and this is, if the user(teacher) has already uploaded this kind of file in their class,, it will also
//        be saved in a collection (files)-- it will need userID, fileName, filePath, fileUri.
//        With this, everytime they uploaded files, they can choose to attach files from their storage or could be their online storage
//        This will solve the doubling of files, and more efficient and reasonable idea.
//        for example, if they have a subject Science in Grade 4 both Lilac and Peonies section, uploading video about a topic can
//        cause problem on our storage, so what if we can reuse that uploaded file already?

//        draftListAdapter.setOnItemClickListener(new TeacherDraftListAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
//                Intent intent = new Intent(getContext(), ViewLearningMaterialActivity.class);
//                intent.putExtra(ViewLearningMaterialActivity.CLASSID, documentSnapshot.getString("classID"));
//            }
//        });
    }

    @Override
    public void onStart() {
        super.onStart();
        draftListAdapter.startListening();
        postListAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        draftListAdapter.stopListening();
        postListAdapter.stopListening();
    }
}
