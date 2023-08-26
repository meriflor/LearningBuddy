package com.project.learningbuddy.ui.teacher.learningmaterials;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.CoreHelper;
import com.project.learningbuddy.adapter.UploadListAdapter;
import com.project.learningbuddy.firebase.LearningMaterialsController;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.UploadFileModel;

import java.util.ArrayList;
import java.util.List;

public class TeacherCreateLearningMaterials extends AppCompatActivity {

    public static final String CLASSID = "Class ID";
    public static final String CLASSNAME = "Class Name";
    public String classID, className;

//    ---------------------
//    private static final int PICK_FILE = 2;
    private static final int READ_PERMISSION_CODE = 1;
    private static final int PICK_IMAGE_REQUEST_CODE = 2;
//    ----------------------------
    ImageView addMat, matPost;
    RecyclerView recyclerView;
    EditText matTitle, matContent;
    List<UploadFileModel> fileList;
    List<String> savedFileUri;
    UploadListAdapter adapter;
    CoreHelper coreHelper;
    FirebaseStorage storage;
    FirebaseFirestore firebaseFirestore;
    int counter;

    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//    ------------

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_learning_materials_create);

//        Intent (Passing of classID)
        Intent intent = getIntent();
        classID = intent.getStringExtra(CLASSID);
        className = intent.getStringExtra(CLASSNAME);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.teacher_learnMatToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fileList = new ArrayList<>();
        savedFileUri = new ArrayList<>();
        coreHelper = new CoreHelper(this);
        storage = FirebaseStorage.getInstance();

        addMat = findViewById(R.id.learning_material_attach_file);
        matPost = findViewById(R.id.learning_material_post);
        matTitle = findViewById(R.id.learning_material_title);
        matContent = findViewById(R.id.learning_material_content);
        recyclerView = findViewById(R.id.teacher_upload_materials_recyclerview);

        adapter = new UploadListAdapter(this, fileList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
            }
        });

        addMat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissonAndChooseFiles();
            }
        });

        matPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFiles(view);
            }
        });

    }

    private void uploadFiles(View view) {
        if (fileList.size() != 0) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploaded 0/"+fileList.size());
            progressDialog.setCanceledOnTouchOutside(false); //Remove this line if you want your user to be able to cancel upload
            progressDialog.setCancelable(false);    //Remove this line if you want your user to be able to cancel upload
            progressDialog.show();
            final StorageReference storageReference = storage.getReference();
            for (int i = 0; i < fileList.size(); i++) {
                final int finalI = i;
                storageReference.child("learningMaterials/" + classID + "-" + className + "/" + userID + "/" ).child(fileList.get(i).getImageName()).putFile(fileList.get(i).getImageURI()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            storageReference.child("learningMaterials/" + classID + "-" + className + "/" + userID + "/" ).child(fileList.get(finalI).getImageName()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(Task<Uri> task) {
                                    counter++;
                                    progressDialog.setMessage("Uploaded "+counter+"/"+fileList.size());
                                    if (task.isSuccessful()){
                                        savedFileUri.add(task.getResult().toString());
                                    }else{
                                        storageReference.child("learningMaterials/" + classID + "-" + className + "/" + userID + "/" ).child(fileList.get(finalI).getImageName()).delete();
                                        Toast.makeText(TeacherCreateLearningMaterials.this, "Couldn't save "+fileList.get(finalI).getImageName(), Toast.LENGTH_SHORT).show();
                                    }
                                    if (counter == fileList.size()){
                                        saveImageDataToFirestore(progressDialog);
                                    }
                                }
                            });
                        }else{
                            progressDialog.setMessage("Uploaded "+counter+"/"+fileList.size());
                            counter++;
                            Toast.makeText(TeacherCreateLearningMaterials.this, "Couldn't upload "+fileList.get(finalI).getImageName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else {
            coreHelper.createSnackBar(view, "Please add some images first.", "", null, Snackbar.LENGTH_SHORT);
        }
    }

    private void saveImageDataToFirestore(final ProgressDialog progressDialog) {
        progressDialog.setMessage("Saving uploaded images...");

        String title = matTitle.getText().toString().trim();
        String content = matContent.getText().toString().trim();
        List<String> fileUrlList = new ArrayList<>();
        for (UploadFileModel fileModel : fileList) {
            fileUrlList.add(fileModel.getImageURI().toString()); // Assuming getImageURI() returns a Uri
        }
        LearningMaterialsController.createLearningMaterial(classID, title, content, fileUrlList, new MyCompleteListener() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                coreHelper.createAlert("Success", "Images uploaded and saved successfully!", "OK", "", null, null, null);

            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
                coreHelper.createAlert("Error", "Images uploaded but we couldn't save them to database.", "OK", "", null, null, null);
            }
        });
    }

    private void permissonAndChooseFiles() {
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
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    ClipData clipData = data.getClipData();
                    if (clipData != null) {
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            Uri uri = clipData.getItemAt(i).getUri();
                            fileList.add(new UploadFileModel(coreHelper.getFileNameFromUri(uri), uri));
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Uri uri = data.getData();
                        fileList.add(new UploadFileModel(coreHelper.getFileNameFromUri(uri), uri));
                        adapter.notifyDataSetChanged();
                    }
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
