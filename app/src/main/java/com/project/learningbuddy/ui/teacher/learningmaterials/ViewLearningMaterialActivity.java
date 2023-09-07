package com.project.learningbuddy.ui.teacher.learningmaterials;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.learningbuddy.R;
import com.project.learningbuddy.firebase.LearningMaterialsController;
import com.project.learningbuddy.listener.MyCompleteListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewLearningMaterialActivity extends AppCompatActivity {

    public static final String MATID = "Materials ID";
    public static final String CLASSID = "Class ID";
    public String materialID, classID;
    public TextView matTitle, matContent, matTimestamp;
    public LinearLayout filesContainer;

    public ImageView delete;

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
        filesContainer = findViewById(R.id.files_container);
        delete = findViewById(R.id.materials_delete);

        delete.setOnClickListener(view -> {
            deleteMaterials();
        });

//        viewLearningMaterials();
        viewMaterials();

    }

    private void viewMaterials() {
        FirebaseFirestore.getInstance()
                .collection("classes")
                .document(classID)
                .collection("learning_materials")
                .document(materialID)
                .get()
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
        FirebaseFirestore.getInstance()
                .collection("classes")
                .document(classID)
                .collection("learning_materials")
                .document(materialID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Retrieve data from the document snapshot
                            String title = documentSnapshot.getString("materialTitle");
                            String description = documentSnapshot.getString("materialContent");
                            Timestamp timestamp = documentSnapshot.getTimestamp("timestamp");
                            List<String> attachedFiles = (List<String>) documentSnapshot.get("files");

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

                            // Iterate through the attached files and create TextViews for them
                            for (String fileUrl : attachedFiles) {
//                                TextView fileTextView = new TextView(ViewLearningMaterialActivity.this);
//                                fileTextView.setLayoutParams(new ViewGroup.LayoutParams(
//                                        ViewGroup.LayoutParams.MATCH_PARENT,
//                                        ViewGroup.LayoutParams.WRAP_CONTENT
//                                ));

                                // Determine the file type based on the URL or name
                                String fileType = getFileType(fileUrl);

                                // Display the content based on the file type
                                switch (fileType) {
                                    case "image":
//                                        ImageView imageView = new ImageView(ViewLearningMaterialActivity.this);
//                                        // Use Glide or Picasso to load and display images
//                                        Glide.with(ViewLearningMaterialActivity.this).load(fileUrl).into(imageView);
//                                        filesContainer.addView(imageView);
                                        break;
                                    case "video":
                                        // Use a VideoView to play videos
                                        VideoView videoView = new VideoView(ViewLearningMaterialActivity.this);
                                        videoView.setVideoPath(fileUrl);
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
                                        pdfTextView.setText("PDF: " + fileUrl); // Display PDF URL

                                        pdfTextView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                // Open the PDF URL using an intent
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setData(Uri.parse(fileUrl));
                                                startActivity(intent);
                                            }
                                        });
                                        filesContainer.addView(pdfTextView);
                                        break;
                                }
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Something went wrong!");
                    }
                });
    }
    private String getFileType(String fileUrl) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(fileUrl);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());

        if (mimeType != null) {
            if (mimeType.startsWith("image/")) {
                return "image";
            } else if (mimeType.startsWith("video/")) {
                return "video";
            } else if (mimeType.equals("application/pdf")) {
                return "pdf";
            }
        }

        return "unknown";
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
}
