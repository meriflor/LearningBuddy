package com.project.learningbuddy.ui.teacher.learningmaterials;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.chrisbanes.photoview.PhotoView;
import com.project.learningbuddy.R;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewFileActivity extends AppCompatActivity {
    public String materialID, classID, fileName, fileType, fileUriString;
    public Uri fileUri;
    public TextView file_name;
    public ListView matListView;
    public PDFView pdfView;
    public ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_file);
        Intent intent = getIntent();
        materialID = intent.getStringExtra("materialID");
        classID = intent.getStringExtra("classID");
        fileName = intent.getStringExtra("fileName");
        fileType = intent.getStringExtra("fileType");
        fileUriString = intent.getStringExtra("fileUri"); // Retrieve the URI as a String
        fileUri = Uri.parse(fileUriString); // Convert the String back to Uri

        Toolbar toolbar = findViewById(R.id.learningMaterialToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable navIcon = toolbar.getNavigationIcon();
        if (navIcon != null) {
            navIcon.setColorFilter(getResources().getColor(R.color.violet), PorterDuff.Mode.SRC_ATOP);
        }

//        Log.d("Checking passed: ", materialID + " " + fileName + " " + fileType + " " + fileUriString + " " + fileUri);
        file_name = findViewById(R.id.fileName);
        file_name.setText(fileName);
        progressBar = findViewById(R.id.progressBar);

//        checkingFileType();
        if (fileType.equals("PDF")) {
            pdfView = findViewById(R.id.pdf_view);
            new RetrivePDFfromUrl().execute(fileUriString);
        }
        if (fileType.equals("IMAGE")) {
//            ImageView imageView = findViewById(R.id.image_view);
            PhotoView photoView = findViewById(R.id.photo_view);
            Glide.with(this)
                .load(fileUri) // Pass the URI as a Uri object
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Toast.makeText(ViewFileActivity.this, "Loading Image Failed", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(photoView);
//            progressBar.setVisibility(View.GONE);
            photoView.setVisibility(View.VISIBLE);
            Log.d("CHECKING FILETYPE: ", "ImageView");
        }
        if (fileType.equals("VIDEO")) {
            VideoView videoView = findViewById(R.id.video_view);
            MediaController mediaController = new MediaController(this);
            videoView.setVideoURI(fileUri);
            videoView.setMediaController(mediaController);
            videoView.start();
            progressBar.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            Log.d("CHECKING FILETYPE: ", "VideoView");
        }

        if(fileType.equals("DOC") || fileType.equals("XLS") || fileType.equals("PPT")){

        }
    }

    private class RetrivePDFfromUrl extends AsyncTask<String, Void, InputStream> {

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                // below is the step where we are
                // creating our connection.
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    // response is success.
                    // we are getting input stream from url
                    // and storing it in our variable.
//
//                    progressBar.setVisibility(View.GONE);
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }

            } catch (IOException e) {
                // this is the method
                // to handle errors.
                e.printStackTrace();
                return null;
            }
//            progressBar.setVisibility(View.GONE);
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            progressBar.setVisibility(View.GONE);
            pdfView.setVisibility(View.VISIBLE);
            pdfView.fromStream(inputStream).load();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
