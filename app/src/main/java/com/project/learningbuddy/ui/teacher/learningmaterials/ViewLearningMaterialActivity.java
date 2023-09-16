package com.project.learningbuddy.ui.teacher.learningmaterials;

import static com.google.common.io.Files.getFileExtension;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.project.learningbuddy.firebase.LearningMaterialsController;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.FileInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewLearningMaterialActivity extends AppCompatActivity {

    public static final String MATID = "Materials ID";
    public static final String CLASSID = "Class ID";
    public String materialID, classID;
    public TextView matTitle, matContent, matTimestamp;
    public LinearLayout filesContainer;

    public ImageView delete;
    public RecyclerView view;
    public MaterialFilesAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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

        matTitle = findViewById(R.id.mat_title);
        matContent = findViewById(R.id.mat_content);
        matTimestamp = findViewById(R.id.mat_timestamp);
//        filesContainer = findViewById(R.id.files_container);
        delete = findViewById(R.id.materials_delete);

        delete.setOnClickListener(view -> {
            deleteMaterials();
        });

//        viewLearningMaterials();
        viewPostInfo();
        viewFiles();

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

        adapter = new MaterialFilesAdapter(options);
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

    private void viewLearningMaterials() {
        DocumentReference matRef = FirebaseFirestore.getInstance()
                .collection("classes")
                .document(classID)
                .collection("learning_materials")
                .document(materialID);

        matRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Retrieve data from the document snapshot
                            String title = documentSnapshot.getString("materialTitle");
                            String description = documentSnapshot.getString("materialContent");
                            Timestamp timestamp = documentSnapshot.getTimestamp("timestamp");

                            Date date = timestamp.toDate();

                            // Format the Date to "Month Day, Year" format
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
                            String formattedDate = dateFormat.format(date);

                            // Set data to the TextViews
                            matTitle.setText(title);
                            matContent.setText(description);
                            matTimestamp.setText(formattedDate);

                            // Clear any existing views in the filesContainer
                            filesContainer.removeAllViews();

                            matRef.collection("files")
                                    .get().addOnCompleteListener(task -> {
                                        if(task.isSuccessful()){
                                            for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                                                String fileUri = queryDocumentSnapshot.getString("fileUri");
                                                TextView fileTextView = new TextView(ViewLearningMaterialActivity.this);
                                                fileTextView.setLayoutParams(new ViewGroup.LayoutParams(
                                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                                ));

                                                // Determine the file type based on the URL or name
                                                String fileType = getFileType(fileUri);

                                                // Display the content based on the file type
                                                switch (fileType) {
                                                    case "image":
                                                        ImageView imageView = new ImageView(ViewLearningMaterialActivity.this);
                                                        // Use Glide or Picasso to load and display images
                                                        Glide.with(ViewLearningMaterialActivity.this).load(fileUri).into(imageView);
                                                        filesContainer.addView(imageView);
                                                        break;
                                                    case "video":
                                                        // Use a VideoView to play videos
                                                        VideoView videoView = new VideoView(ViewLearningMaterialActivity.this);
                                                        videoView.setVideoPath(fileUri);
                                                        videoView.setLayoutParams(new ViewGroup.LayoutParams(
                                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                                ViewGroup.LayoutParams.WRAP_CONTENT
                                                        ));
                                                        filesContainer.addView(videoView);
                                                        break;
                                                    case "pdf":
                                                        TextView pdfTextView = new TextView(ViewLearningMaterialActivity.this);
                                                        pdfTextView.setLayoutParams(new ViewGroup.LayoutParams(
                                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                                ViewGroup.LayoutParams.WRAP_CONTENT
                                                        ));
                                                        pdfTextView.setText("PDF: " + fileUri); // Display PDF URL

                                                        pdfTextView.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                // Open the PDF URL using an intent
                                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                                intent.setData(Uri.parse(fileUri));
                                                                startActivity(intent);
                                                            }
                                                        });
                                                        filesContainer.addView(pdfTextView);
                                                        break;
                                                }
                                            }
                                        }

                                    });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
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
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
