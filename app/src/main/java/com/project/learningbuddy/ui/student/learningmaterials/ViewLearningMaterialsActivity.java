package com.project.learningbuddy.ui.student.learningmaterials;

import static com.google.common.io.Files.getFileExtension;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.MaterialFilesAdapter;
import com.project.learningbuddy.adapter.PracticeReadingAdapter;
import com.project.learningbuddy.adapter.PracticeReadingRetrieve;
import com.project.learningbuddy.model.FileInfo;
import com.project.learningbuddy.ui.teacher.learningmaterials.ViewFileActivity;
import com.project.learningbuddy.ui.teacher.learningmaterials.ViewLearningMaterialActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewLearningMaterialsActivity extends AppCompatActivity {
    public static final String MATID = "Materials ID";
    public static final String CLASSID = "Class ID";
    public static final String MATTYPE = "Material Type";
    public static final String MATTITLE = "Material Title";
    public static final String MATCONTENT = "Material Content";
    public static final String MATTIMESTAMP = "Material Timestamp";
    public String materialID, classID, materialType, materialTitle, materialContent, materialTimestamp;

    public TextView matTitle, matContent, matTimestamp;

    public ImageView delete;
    public RecyclerView view;
    public MaterialFilesAdapter adapter;

    public LinearLayout uploadedFilesLayout, practiceReadingLayout;

    public TextToSpeech textToSpeech;
    public PracticeReadingAdapter practiceReadingAdapter;
    public RecyclerView recyclerView;

    public Uri imageUri;
    public Boolean permissionGranted;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_learning_materials);

        Intent intent = getIntent();
        materialID = intent.getStringExtra(MATID);
        classID = intent.getStringExtra(CLASSID);
        materialType = intent.getStringExtra(MATTYPE);
        materialTitle = intent.getStringExtra(MATTITLE);
        materialContent = intent.getStringExtra(MATCONTENT);
        materialTimestamp = intent.getStringExtra(MATTIMESTAMP);

        Toolbar toolbar = findViewById(R.id.learningMaterialToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable navIcon = toolbar.getNavigationIcon();
        if (navIcon != null) {
            navIcon.setColorFilter(getResources().getColor(R.color.violet), PorterDuff.Mode.SRC_ATOP);
        }


        matTitle = findViewById(R.id.mat_title);
        matContent = findViewById(R.id.mat_content);
        matTimestamp = findViewById(R.id.mat_timestamp);
        delete = findViewById(R.id.materials_delete);
        uploadedFilesLayout = findViewById(R.id.uploaded_files_layout);
        practiceReadingLayout = findViewById(R.id.practice_reading_layout);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i == TextToSpeech.SUCCESS){
                    int langResult = textToSpeech.setLanguage(Locale.US);
                    if(langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED){
                        Toast.makeText(ViewLearningMaterialsActivity.this, "Language not supported", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ViewLearningMaterialsActivity.this, "Text-to-speech initialization failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(materialType == null){
            uploadedFilesLayout.setVisibility(View.VISIBLE);
            practiceReadingLayout.setVisibility(View.GONE);

            Log.d("MATERIALTYPE", "did you got here? This is null");
            viewFiles();
        }else{
            if(materialType.equals("Practice Reading")){
                practiceReadingLayout.setVisibility(View.VISIBLE);
                uploadedFilesLayout.setVisibility(View.GONE);
                Log.d("MATERIALTYPE", "did you got here? This is Practice Reading");
                viewPracticeReadingList();
            }else{
                uploadedFilesLayout.setVisibility(View.VISIBLE);
                practiceReadingLayout.setVisibility(View.GONE);
                Log.d("MATERIALTYPE", "did you got here? This is Uploaded Files");
                viewFiles();
            }
        }

        viewPostInfo();
    }

    private void viewPracticeReadingList() {
        Log.d("MATERIALTYPE", "You are inside the method now");
        Query fileQuery = FirebaseFirestore.getInstance()
                .collection("classes")
                .document(classID)
                .collection("learning_materials")
                .document(materialID)
                .collection("files");

        FirestoreRecyclerOptions<PracticeReadingRetrieve> options = new FirestoreRecyclerOptions.Builder<PracticeReadingRetrieve>()
                .setQuery(fileQuery, PracticeReadingRetrieve.class).build();
        Log.d("TAG","PERMISSION GRANTED: "+permissionGranted);
        practiceReadingAdapter = new PracticeReadingAdapter(options, textToSpeech, imageUri, permissionGranted);
        recyclerView = findViewById(R.id.learning_materials_practice_reading_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(practiceReadingAdapter);
    }

    private void viewFiles() {
        Log.d("TAG", "Have you gone here? viewFiles()");
        Query fileQuery = FirebaseFirestore.getInstance()
                .collection("classes")
                .document(classID)
                .collection("learning_materials")
                .document(materialID)
                .collection("files")
                .orderBy("fileName", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<FileInfo> options = new FirestoreRecyclerOptions.Builder<FileInfo>()
                .setQuery(fileQuery, FileInfo.class).build();

        adapter = new MaterialFilesAdapter(options);
        view = findViewById(R.id.learning_materials_recyclerview);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.setAdapter(adapter);

        adapter.setOnItemClickListener(new MaterialFilesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Intent intent = new Intent(ViewLearningMaterialsActivity.this, ViewFileActivity.class);
                intent.putExtra("fileName", documentSnapshot.getString("fileName"));
                intent.putExtra("fileType", getFileType(documentSnapshot.getString("fileName")));
                Uri fileUri = Uri.parse(documentSnapshot.getString("fileUri")); // Replace with the actual URI
                intent.putExtra("fileUri", fileUri.toString()); // Convert Uri to String and pass it

                intent.putExtra("classID", classID);
                intent.putExtra("materialID", materialID);
                startActivity(intent);

                Log.d("TAGGG", "You have clicked the item");
            }
        });
    }

    private void viewPostInfo() {
//        DocumentReference matRef = FirebaseFirestore.getInstance()
//                .collection("classes")
//                .document(classID)
//                .collection("learning_materials")
//                .document(materialID);
//
//        matRef.get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    Timestamp timestamp = documentSnapshot.getTimestamp("timestamp");
//                    Date date = timestamp.toDate();
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
//                    String formattedDate = dateFormat.format(date);
//
//                    matTitle.setText(documentSnapshot.getString("materialTitle"));
//                    matContent.setText(documentSnapshot.getString("materialContent"));
//                    Linkify.addLinks(matContent, Linkify.WEB_URLS);
//                    matTimestamp.setText(formattedDate);
//                });

        matTitle.setText(materialTitle);
        matContent.setText(materialContent);
        Linkify.addLinks(matContent, Linkify.WEB_URLS);
        matTimestamp.setText(materialTimestamp);
    }

    private String getFileType(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();

        // List of known extensions for different file types
        if (extension.equals("pdf")) {
            return "PDF";
        } else if (extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png")) {
            return "IMAGE";
        } else if (extension.equals("doc") || extension.equals("docx")) {
            return "DOC";
        } else if (extension.equals("xls") || extension.equals("xlsx")) {
            return "XLS";
        } else if (extension.equals("txt")) {
            return "TXT";
        } else if (extension.equals("ppt") || extension.equals("pptx")) {
            return "PPT";
        } else if (extension.equals("mp3") || extension.equals("ogg")) {
            return "MP3";
        } else if (extension.equals("avi") || extension.equals("mp4")) {
            return "VIDEO";
        } else {
            return "Unknown"; // If the extension doesn't match known types
        }
    }

    @Override
    public boolean onOptionsItemSelected(@org.checkerframework.checker.nullness.qual.NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(materialType == null){
            if(adapter!=null){
                adapter.startListening();
            }
        }else{
            if(materialType.equals("Practice Reading")){
                if(practiceReadingAdapter!=null){
                    practiceReadingAdapter.startListening();
                }
            }else{
                if(adapter!=null){
                    adapter.startListening();
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null){
            adapter.stopListening();
        }
        if(practiceReadingAdapter!=null){
            practiceReadingAdapter.stopListening();
        }

    }
    @Override
    protected void onDestroy() {
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
