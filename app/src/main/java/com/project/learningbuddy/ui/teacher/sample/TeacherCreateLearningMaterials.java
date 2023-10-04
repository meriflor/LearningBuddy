package com.project.learningbuddy.ui.teacher.sample;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.CoreHelper;
import com.project.learningbuddy.adapter.FileSpaceAdapter;
import com.project.learningbuddy.adapter.UploadListAdapter;
import com.project.learningbuddy.firebase.FileSpaceController;
import com.project.learningbuddy.firebase.LearningMaterialsController;
import com.project.learningbuddy.listener.ExistListener;
import com.project.learningbuddy.model.FileSpace;
import com.project.learningbuddy.model.LearningMaterials;
import com.project.learningbuddy.model.UploadFileModel;

import java.util.ArrayList;
import java.util.List;

public class TeacherCreateLearningMaterials extends AppCompatActivity {
    public static final String CLASSID = "Class ID";
    public static final String CLASSNAME = "Class Name";
    String classID, className;
    public ImageView close, post, phoneStorage, cloudStorage, remove;
    public EditText postTitle, postContent, postFileName;
    public Button addBtn;
    public LinearLayout fileNameLayout;
    public RecyclerView toUploadFileRecyclerView;

    public CoreHelper coreHelper;

    public FileSpaceAdapter fileSpaceAdapter;

    public static final int READ_PERMISSION_CODE = 1;
    public static final int PICK_IMAGE_REQUEST_CODE = 2;
    List<FileSpace> fileSpaces;
    List<UploadFileModel> uploadFileModel;
    UploadListAdapter uploadListAdapter;
    Uri fileUri;
    String fileName;
    int counter;
    Boolean called = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_learning_materials_create);

        Intent intent = getIntent();
        classID = intent.getStringExtra(CLASSID);
        className = intent.getStringExtra(CLASSNAME);

        close = findViewById(R.id.closeImage_btn);
        post = findViewById(R.id.postImage_btn);
        remove = findViewById(R.id.fileRemove_btn);
        phoneStorage = findViewById(R.id.phoneStorage_btn);
        cloudStorage = findViewById(R.id.cloudStorage_btn);
        postTitle = findViewById(R.id.post_title_editText);
        postContent = findViewById(R.id.post_content_editText);
        postFileName = findViewById(R.id.fileName_editText);
        addBtn = findViewById(R.id.fileName_addBtn);
        fileNameLayout = findViewById(R.id.fileName_layout);
        toUploadFileRecyclerView = findViewById(R.id.fileToUploadList_recyclerView);

        uploadFileModel = new ArrayList<>();
        uploadListAdapter = new UploadListAdapter(TeacherCreateLearningMaterials.this, uploadFileModel);
        coreHelper = new CoreHelper(TeacherCreateLearningMaterials.this);
        counter = 0;
        toUploadFileRecyclerView.setLayoutManager(new LinearLayoutManager(TeacherCreateLearningMaterials.this));
        toUploadFileRecyclerView.setAdapter(uploadListAdapter);

        Query fileQuery = FirebaseFirestore.getInstance()
                .collection("files")
                .whereEqualTo("userID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderBy("fileName", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<FileSpace> options = new FirestoreRecyclerOptions.Builder<FileSpace>()
                .setQuery(fileQuery, FileSpace.class).build();
        fileSpaceAdapter = new FileSpaceAdapter(options);

        close.setOnClickListener(view -> {
            finish();
        });

        phoneStorage.setOnClickListener(view -> {
            permissionToChooseFiles();
        });

        remove.setOnClickListener(view -> {
            removingDetails();
        });

        addBtn.setOnClickListener(view -> {
            String newFileName = postFileName.getText().toString().trim();
            Log.d("TAG", newFileName+" "+fileUri);
            if(newFileName!=null && fileUri!=null){
                uploadFileModel.add(new UploadFileModel(newFileName, fileUri));
                uploadListAdapter.notifyDataSetChanged();
                removingDetails();
            }else{
                coreHelper.showToast(TeacherCreateLearningMaterials.this, "Choose a file to upload");
            }
        });

        cloudStorage.setOnClickListener(view -> {
            FileSpaceController.checkFilesExist(new ExistListener() {
                @Override
                public void onExist(Boolean exist) {
                    if(!exist){
                        coreHelper.showToast(TeacherCreateLearningMaterials.this, "You have no files yet");
                    }else{
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TeacherCreateLearningMaterials.this);
                        View dialogView = getLayoutInflater().inflate(R.layout.pop_up_window_add_cloud_file, null);
                        dialogBuilder.setView(dialogView);
                        AlertDialog dialog = dialogBuilder.create();
                        dialog.setCancelable(false);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                        Button cancelBtn = dialogView.findViewById(R.id.cancel_btn);
                        cancelBtn.setOnClickListener(view1 -> {
                            dialog.dismiss();
                        });

                        RecyclerView dialogRecyclerView = dialogView.findViewById(R.id.cloudFiles_recycerView);
                        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(TeacherCreateLearningMaterials.this));
                        dialogRecyclerView.setAdapter(fileSpaceAdapter);

                        fileSpaceAdapter.setOnClickListener(new FileSpaceAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                                coreHelper.showToast(TeacherCreateLearningMaterials.this, "Item Clicked");
                                fileName = documentSnapshot.getString("fileName");
                                fileUri = Uri.parse(documentSnapshot.getString("fileUri"));
                                postFileName.setText(fileName);
                                fileNameLayout.setVisibility(View.VISIBLE);
                                addBtn.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
                @Override
                public void onFailure(Exception e) {
                    coreHelper.showToast(TeacherCreateLearningMaterials.this, "Problem fetching data from the FileSpaceController");
                }
            });
        });

        post.setOnClickListener(view -> {
            if(fileSpaces.size() != 0){
                saveDataToStorage();
            }else{
                coreHelper.showToast(TeacherCreateLearningMaterials.this, "Choose at least one file");
            }

        });
    }

    private void saveDataToStorage() {
//        String materialID = coreHelper.generateRandomUID();
//        LearningMaterialsController.createLearningMaterial(classID, materialID,);
        coreHelper.showToast(TeacherCreateLearningMaterials.this, "About to save now... to be continued lol");
    }

    private void removingDetails() {
        fileUri = null;
        fileName = null;
        fileNameLayout.setVisibility(View.GONE);
        postFileName.setText("");
        addBtn.setVisibility(View.GONE);
    }

    private void permissionToChooseFiles() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //If permission is granted
                pickImage();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_CODE);
            }
        } else {
            //no need to check permissions in android versions lower then marshmallow
            pickImage();
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            Log.d("TAG", fileUri+ " is the Uri");
            Log.d("TAG", coreHelper.getFileNameFromUri(fileUri));
            fileName = coreHelper.getFileNameFromUri(fileUri);
            postFileName.setText(fileName);
            fileNameLayout.setVisibility(View.VISIBLE);
            addBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
            fileSpaceAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
            fileSpaceAdapter.stopListening();
    }
}
