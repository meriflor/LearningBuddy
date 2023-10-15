package com.project.learningbuddy.ui.teacher.learningmaterials;

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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.MaterialFilesAdapter;
import com.project.learningbuddy.adapter.PracticeReadingAdapter;
import com.project.learningbuddy.adapter.PracticeReadingRetrieve;
import com.project.learningbuddy.firebase.LearningMaterialsController;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.FileInfo;
import com.project.learningbuddy.model.PracticeReading;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewLearningMaterialActivity extends AppCompatActivity {

    public static final String MATID = "Materials ID";
    public static final String CLASSID = "Class ID";
    public static final String MATERIALTYPE = "Material Type";
    public static final String CLASSNAME = "Class Name";
    public String materialID, classID, materialType, className;
    public TextView matTitle, matContent, matTimestamp;
    public LinearLayout filesContainer;

    public ImageView delete;
    public RecyclerView view;
    public MaterialFilesAdapter adapter;
    public LinearLayout uploadedFilesLayout, practiceReadingLayout;

    public TextToSpeech textToSpeech;
    public PracticeReadingAdapter practiceReadingAdapter;
    public RecyclerView recyclerView;
//    ------------
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int READ_PERMISSION_CODE = 123;
    public Uri imageUri;
    public Boolean permissionGranted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_learning_materials);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.learningMaterialToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable navIcon = toolbar.getNavigationIcon();
        if (navIcon != null) {
            navIcon.setColorFilter(getResources().getColor(R.color.violet), PorterDuff.Mode.SRC_ATOP);
        }

        Intent intent = getIntent();
        materialID = intent.getStringExtra(MATID);
        classID = intent.getStringExtra(CLASSID);
        materialType = intent.getStringExtra(MATERIALTYPE);
        className = intent.getStringExtra(CLASSNAME);

        matTitle = findViewById(R.id.mat_title);
        matContent = findViewById(R.id.mat_content);
        matTimestamp = findViewById(R.id.mat_timestamp);
//        filesContainer = findViewById(R.id.files_container);
        delete = findViewById(R.id.materials_delete);
        uploadedFilesLayout = findViewById(R.id.uploaded_files_layout);
        practiceReadingLayout = findViewById(R.id.practice_reading_layout);

        delete.setVisibility(View.VISIBLE);
        delete.setOnClickListener(view -> {
            deleteMaterials();
        });

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i == TextToSpeech.SUCCESS){
                    int langResult = textToSpeech.setLanguage(Locale.US);
                    if(langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED){
                        Toast.makeText(ViewLearningMaterialActivity.this, "Language not supported", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ViewLearningMaterialActivity.this, "Text-to-speech initialization failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });


//        ------------Permission intent
        if (checkPermissions()) {
            permissionGranted = true;

        } else {
            // Request permissions from the user
            permissionGranted = false;
        }
        Log.d("TAG","PERMISSION GRANTED: "+permissionGranted);

        Log.d("MATERIALTYPE", "this is a confirmed "+materialType);
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

    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check for permissions on Android Marshmallow (API level 23) and higher
            int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return readPermission == PackageManager.PERMISSION_GRANTED;
        } else {
            // No need to check permissions on Android versions lower than Marshmallow
            return true;
        }
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
        Query fileQuery = FirebaseFirestore.getInstance()
                .collection("classes")
                .document(classID)
                .collection("learning_materials")
                .document(materialID)
                .collection("files")
                .orderBy("fileName", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<FileInfo> options = new FirestoreRecyclerOptions.Builder<FileInfo>()
                .setQuery(fileQuery, FileInfo.class).build();

        adapter = new MaterialFilesAdapter(options, this, className);
        view = findViewById(R.id.learning_materials_recyclerview);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.setAdapter(adapter);

        adapter.setOnItemClickListener(new MaterialFilesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Intent intent = new Intent(ViewLearningMaterialActivity.this, ViewFileActivity.class);
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
        DocumentReference matRef = FirebaseFirestore.getInstance()
                .collection("classes")
                .document(classID)
                .collection("learning_materials")
                .document(materialID);

        matRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    Timestamp timestamp = documentSnapshot.getTimestamp("timestamp");
                    Date date = timestamp.toDate();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
                    String formattedDate = dateFormat.format(date);

                    matTitle.setText(documentSnapshot.getString("materialTitle"));
                    matContent.setText(documentSnapshot.getString("materialContent"));
                    Linkify.addLinks(matContent, Linkify.WEB_URLS);
                    matTimestamp.setText(formattedDate);
                });
    }

    private void deleteMaterials() {
        LearningMaterialsController.deleteMaterials(classID, materialID, new MyCompleteListener() {
            @Override
            public void onSuccess() {
                showToast("Deleted successfully");
                finish();
            }

            @Override
            public void onFailure() {
                Log.d("TAG", "Something went wrong!");
            }
        });
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

    public void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
        }
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
