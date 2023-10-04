package com.project.learningbuddy.ui.teacher.learningmaterials;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.protobuf.StringValue;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.CoreHelper;
import com.project.learningbuddy.adapter.UploadPracticeReadingListAdapter;
import com.project.learningbuddy.firebase.PracticeReadingController;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.PracticeReading;
import com.project.learningbuddy.model.UploadFileModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TeacherCreateLearningMaterialsPracticeReading extends AppCompatActivity {

    public static final String CLASSID = "Class ID";
    public static final String CLASSNAME = "Class Name";
    public String classID, className;

//    -----------
    private static final int READ_PERMISSION_CODE = 1;
    private static final int PICK_IMAGE_REQUEST = 1;

    List<PracticeReading> fileList;
    List<String> savedFileUri;
    CoreHelper coreHelper;
    FirebaseStorage storage;
    UploadPracticeReadingListAdapter adapter;
    int counter;

    public Uri imageUri;
    public String imageUrl;
    public String imageFileName;
//    --------------

    RecyclerView recyclerView;
    EditText matTitle, matContent;
    ImageView close, post;

    ImageView speakIcon, speakImage, speakRemove;
    EditText speakText;
    TextView speakName;
    LinearLayout imagePicker, addFile;
    TextToSpeech textToSpeech;

//  ------------------
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String userID = firebaseAuth.getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_learning_materials_practice_reading);

        Intent intent = getIntent();
        classID = intent.getStringExtra(CLASSID);
        className = intent.getStringExtra(CLASSNAME);

        close = findViewById(R.id.icon_close);
        close.setOnClickListener(view -> {
            finish();
        });

//        asdlfkjasdlkfjasdlfkjasldkfjasldkfjasldkfjasldkfjasldkfj
        post = findViewById(R.id.icon_post);
        post.setOnClickListener(view -> {
            String title = matTitle.getText().toString().trim();
            String content = matContent.getText().toString().trim();
            if(title.isEmpty()){
                Toast.makeText(this, "Enter a post title.", Toast.LENGTH_SHORT).show();
            }else{
                uploadFilesToStorage(title, content);
            }
        });

        fileList = new ArrayList<>();
        savedFileUri = new ArrayList<>();
        coreHelper = new CoreHelper(this);
        storage = FirebaseStorage.getInstance();

        matTitle = findViewById(R.id.learning_material_title);
        matContent = findViewById(R.id.learning_material_content);
        recyclerView = findViewById(R.id.teacher_upload_practice_reading_recyclerview);

        speakIcon = findViewById(R.id.speech_testing);
        speakText = findViewById(R.id.image_text_to_speech);
        speakName = findViewById(R.id.file_name);
        imagePicker = findViewById(R.id.image_picker);
        addFile = findViewById(R.id.add);
        speakImage = findViewById(R.id.file_image);
        speakRemove = findViewById(R.id.icon_remove_file_name);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i == TextToSpeech.SUCCESS){
                    int langResult = textToSpeech.setLanguage(Locale.US);
                    if(langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED){
                        Toast.makeText(TeacherCreateLearningMaterialsPracticeReading.this, "Language not supported", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(TeacherCreateLearningMaterialsPracticeReading.this, "Text-to-speech initialization failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {

            }

            @Override
            public void onDone(String s) {

            }

            @Override
            public void onError(String s) {

            }
        });

        speakIcon.setOnClickListener(view -> {
            String text = speakText.getText().toString().trim();
            if(!text.isEmpty()){
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "speakText");
            }else{
                Toast.makeText(this, "Please enter text.", Toast.LENGTH_SHORT).show();
            }
        });

        adapter = new UploadPracticeReadingListAdapter(this, fileList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
            }
        });

        imagePicker.setOnClickListener(view -> {
            permissionAndChooseFiles();
        });

        addFile.setOnClickListener(view -> {
            addFileToList();
        });

        speakRemove.setOnClickListener(view -> {
            imageUri = null;
            imageFileName = null;
            speakImage.setVisibility(View.GONE);
            speakName.setText("");
        });


    }

    private void uploadFilesToStorage(String title, String content) {
        if(fileList.size() != 0){
            Dialog dialog = new Dialog(this);
            View view = getLayoutInflater().inflate(R.layout.loading_dialog, null);
            TextView message = view.findViewById(R.id.loading_message);
            message.setText("Uploading . . .");
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(view);
            dialog.show();

            StorageReference storageReference = storage.getReference();
            String materialID = coreHelper.generateRandomUID();

            PracticeReadingController.createPracticeReading(classID, materialID, title, content, new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    for(int i = 0; i < fileList.size(); i++){
                        PracticeReading fileModel = fileList.get(i);
                        if(fileModel.getFileUri()==null){
                            counter++;
                            dialog.dismiss();
                            message.setText("Uploaded "+counter+"/"+fileList.size());
                            dialog.show();
                            createPracticeReadings(classID, materialID, fileModel.getText(), null, null, null);
                            if(counter == fileList.size()){
                                dialog.dismiss();
                                fileToPost(classID, materialID);
                                coreHelper.createAlert("Success", "File(s) uploaded and saved successfully!", "OK", "", null, null, null);
                                finish();
                            }
                        }else{
                            String imageName = fileModel.getImageName();
                            String encodedImageName = Uri.encode(imageName);

                            String filePath = "practiceReadings/"+userID+"/"+materialID+"/"+encodedImageName;
                            storageReference.child(filePath)
                                    .putFile(fileModel.getFileUri())
                                    .addOnCompleteListener(task -> {
                                        if(task.isSuccessful()){
                                            storageReference.child(filePath)
                                                    .getDownloadUrl()
                                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                        @Override
                                                        public void onComplete(Task<Uri> task) {
                                                            counter++;
                                                            dialog.dismiss();
                                                            message.setText("Uploaded "+counter+"/"+fileList.size());
                                                            dialog.show();
                                                            if(task.isSuccessful()){
                                                                Uri fileUri = task.getResult();
                                                                createPracticeReadings(classID, materialID, fileModel.getText(), imageName, filePath, fileUri);
                                                                savedFileUri.add(task.getResult().toString());
                                                            }else{
                                                                storageReference.child(filePath).delete();
                                                                showToast("Couldn't save "+imageName);
                                                            }

                                                            if(counter == fileList.size()){
                                                                dialog.dismiss();
                                                                fileToPost(classID, materialID);
                                                                coreHelper.createAlert("Success", "File(s) uploaded and saved successfully!", "OK", "", null, null, null);
                                                                finish();
                                                            }
                                                        }
                                                    });
                                        }else{
                                            message.setText("Uploaded "+counter+"/"+fileList.size());
                                            counter++;
                                            showToast("Couldn't upload " + imageName);
                                        }
                                    });
                        }

                    }
                }

                @Override
                public void onFailure() {
                    Log.d("PROBLEM", "There's something wrong in storing the data of practice readings in learning materials.");
                }
            });
        }
    }

    private void fileToPost(String classID, String materialID) {
        PracticeReadingController.postPracticeReading(classID, materialID);
    }

    private void createPracticeReadings(String classID, String materialID, String text, String imageName, String filePath, Uri fileUri) {
        PracticeReadingController.addFileToPracticeReading(classID, materialID, text, imageName, filePath, fileUri, new MyCompleteListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void addFileToList() {
        String text = speakText.getText().toString().trim();
        if(!text.isEmpty()){
            if(imageUri==null){
                fileList.add(new PracticeReading(text, null, null));
                adapter.notifyDataSetChanged();
                Log.d("CHECKKIIIINNNGGGGGG", text+" "+imageFileName);
            }else{
                fileList.add(new PracticeReading(text, imageUri, imageFileName));
                adapter.notifyDataSetChanged();
                Log.d("CHECKKIIIINNNGGGGGG", text+" "+imageFileName);
            }
        }else{
            Toast.makeText(this, "Please enter text.", Toast.LENGTH_SHORT).show();
        }

    }

    private void permissionAndChooseFiles() {
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
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageFileName = coreHelper.getFileNameFromUri(imageUri);
            speakImage.setVisibility(View.VISIBLE);
            speakImage.setImageURI(imageUri);
            speakName.setText(imageFileName);
        }
    }

    public void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
