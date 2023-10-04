package com.project.learningbuddy.ui.teacher.filespace;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.CoreHelper;
import com.project.learningbuddy.adapter.FileSpaceAdapter;
import com.project.learningbuddy.adapter.FileSpaceUploadAdapter;
import com.project.learningbuddy.firebase.FileSpaceController;
import com.project.learningbuddy.listener.ExistListener;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.FileSpace;
import com.project.learningbuddy.model.PracticeReading;

import java.util.List;

public class FileSpaceActivity extends AppCompatActivity {

    public ImageView close, post;
    public EditText postTitle, postContent, postFileName;
    public Button phoneStorage, cloudStorage, addFile;

    public RecyclerView fileToUploadView;
    public FileSpaceAdapter fileSpaceAdapter;
    public FileSpaceUploadAdapter fileSpaceUploadAdapter;

//    -----------------
    private static final int READ_PERMISSION_CODE = 1;
    private static final int PICK_IMAGE_REQUEST = 1;
    public CoreHelper coreHelper;
    public int counter;

    public Uri chosenFileUri;
    public String chosenFileName;
    public String chosenFileUrl;

    public List<FileSpace> fileSpaces;
    public List<String> fileNames;

    public String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public FirebaseStorage storage = FirebaseStorage.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_file_space_add_upload_files);

        close = findViewById(R.id.icon_close);
        post = findViewById(R.id.icon_post);
        postTitle = findViewById(R.id.post_title_editText);
        postContent = findViewById(R.id.post_content_editText);
        phoneStorage = findViewById(R.id.phone_storage_btn);
        cloudStorage = findViewById(R.id.cloud_storage_btn);
        postFileName = findViewById(R.id.fileName_editText);
        addFile = findViewById(R.id.addFile_btn);
        fileToUploadView = findViewById(R.id.file_recyclerView);

        counter = 0;

        fileSpaceUploadAdapter = new FileSpaceUploadAdapter(this, fileSpaces);
        fileToUploadView.setLayoutManager(new LinearLayoutManager(this));
        fileToUploadView.setAdapter(fileSpaceAdapter);
        fileSpaceUploadAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
            }
        });

        phoneStorage.setOnClickListener(view -> {
            checkStoragePermission();
        });
        cloudStorage.setOnClickListener(view -> {
            openDialog();
        });

        addFile.setOnClickListener(view -> {
            String theFileName = postFileName.getText().toString().trim();
            if(theFileName.isEmpty()){
                if(chosenFileUri == null){
                    showToast("Choose File");
                }else{
                    fileSpaces.add(new FileSpace(theFileName, userID, chosenFileUrl, String.valueOf(chosenFileUri)));
                    fileSpaceUploadAdapter.notifyDataSetChanged();
                }
            }
        });

        post.setOnClickListener(view -> {
            uploadFilesToStorage();
        });

        close.setOnClickListener(view -> {
            finish();
        });
    }

    private void uploadFilesToStorage() {
        String title = postTitle.getText().toString().trim();
        String content = postContent.getText().toString().trim();
        if(fileSpaces.size() != 0 && !title.isEmpty()){
            Dialog dialog = new Dialog(this);
            View dialogView = getLayoutInflater().inflate(R.layout.loading_dialog, null);
            TextView message = dialogView.findViewById(R.id.loading_message);
            message.setText("Uploaded 0/"+fileSpaces.size());
            dialog.setCancelable(false);
            dialog.show();
            StorageReference storageReference = storage.getReference();
            String materialID = coreHelper.generateRandomUID();
            FileSpaceController.createLearningMaterialsPublic(materialID, title, content, new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    for(int i=0; i<fileSpaces.size(); i++){
                        FileSpace fileModel = fileSpaces.get(i);
                        String fileName = fileModel.getFileName();
                        String encodedFileName = Uri.encode(fileName);
                        String filePath = "files/"+userID+"/"+encodedFileName;

                        storageReference.child(filePath)
                                .putFile(Uri.parse(fileModel.getFileUri()))
                                .addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){
                                        storageReference.child(filePath)
                                                .getDownloadUrl()
                                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                    @Override
                                                    public void onComplete(Task<Uri> task) {
                                                        counter++;
                                                        message.setText("Uploaded "+counter+"/"+fileSpaces.size());
                                                        if(task.isSuccessful()){
                                                            Uri fileUri = task.getResult();
                                                            saveFilesToFirestore(fileName, filePath, fileUri);
                                                        }else{
                                                            storageReference.child(filePath)
                                                                    .delete();
                                                            showToast("Couldn't save "+fileName);
                                                        }

                                                        if(counter == fileSpaces.size()){
                                                            dialog.dismiss();
                                                            coreHelper.createAlert("Success", "File(s) uploaded and saved successfully!", "OK", "", null, null, null);
                                                            finish();
                                                        }
                                                    }
                                                });
                                    }else{
                                        message.setText("Uploaded "+counter+"/"+fileSpaces.size());
                                        counter++;
                                        showToast("Couldn't upload "+fileName);
                                    }
                                });
                    }
                }

                @Override
                public void onFailure() {
                    dialog.dismiss();
                    coreHelper.createAlert("Error", "Failed to create the parent document for the post.", "OK", "", null, null, null);
                }
            });
        }
    }

    private void saveFilesToFirestore(String fileName, String filePath, Uri fileUri) {
        FileSpaceController.createFileSpace(fileName, filePath, fileUri, new MyCompleteListener() {
            @Override
            public void onSuccess() {
                Log.d("TAG", fileName + " Uploaded");
            }

            @Override
            public void onFailure() {
                Log.d("TAG", fileName + " Upload Failed");
            }
        });
    }

    private void openDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.pop_up_window_add_cloud_file, null);
        dialogBuilder.setView(dialogView);
        AlertDialog dialog = dialogBuilder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        FileSpaceController.checkFilesExist(new ExistListener() {
            @Override
            public void onExist(Boolean exist) {
                if(!exist){
                    showToast("You have no uploaded files yet");
                }else{
                    dialog.show();

                    RecyclerView fileView = dialogView.findViewById(R.id.cloudFiles_recycerView);
                    Button cancel = dialogView.findViewById(R.id.cancel_btn);

                    Query filesQuery = FirebaseFirestore.getInstance()
                            .collection("files")
                            .orderBy("timestamp", Query.Direction.ASCENDING);
                    FirestoreRecyclerOptions<FileSpace> options = new FirestoreRecyclerOptions.Builder<FileSpace>()
                            .setQuery(filesQuery, FileSpace.class).build();
                    fileSpaceAdapter = new FileSpaceAdapter(options);
                    fileView.setLayoutManager(new LinearLayoutManager(FileSpaceActivity.this));
                    fileView.setAdapter(fileSpaceAdapter);

                    fileSpaceAdapter.setOnClickListener(new FileSpaceAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                            String fileName = documentSnapshot.getString("fileName");
                            String fileUri = documentSnapshot.getString("fileUri");
                            String userID = documentSnapshot.getString("userID");
                            String filePath = documentSnapshot.getString("filePath");
                            Uri uri = Uri.parse(fileUri);

                            fileSpaces.add(new FileSpace(fileName, userID, filePath, String.valueOf(uri)));
                            dialog.dismiss();
                        }
                    });

                    cancel.setOnClickListener(view -> {
                        dialog.dismiss();
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                showToast(e.getMessage());
            }
        });
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void checkStoragePermission() {
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
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            chosenFileUri = data.getData();
            chosenFileName = coreHelper.getFileNameFromUri(chosenFileUri);
//            speakImage.setVisibility(View.VISIBLE);
//            speakImage.setImageURI(imageUri);
//            speakName.setText(imageFileName);
            postFileName.setText(chosenFileName);
        }
    }
}
