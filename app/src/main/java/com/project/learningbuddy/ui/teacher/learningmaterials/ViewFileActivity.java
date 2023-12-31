package com.project.learningbuddy.ui.teacher.learningmaterials;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Switch;
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
import java.util.List;

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


        PackageManager pm = getPackageManager();
        boolean isWordInstalled = isPackageInstalled(pm, "com.microsoft.office.word");
        boolean isPowerPointInstalled = isPackageInstalled(pm, "com.microsoft.office.powerpoint");
        boolean isExcelInstalled = isPackageInstalled(pm, "com.microsoft.office.excel");

        Log.d("TAG", "isWordInstalled: "+ isWordInstalled +" isPowerPointInstalled"+ isPowerPointInstalled+" isExcelInstalled"+ isExcelInstalled);

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
        else if (fileType.equals("IMAGE")) {
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
        else if (fileType.equals("VIDEO")) {
            VideoView videoView = findViewById(R.id.video_view);
            MediaController mediaController = new MediaController(this);
            videoView.setVideoURI(fileUri);
            videoView.setMediaController(mediaController);
            videoView.start();
            progressBar.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            Log.d("CHECKING FILETYPE: ", "VideoView");
        }

//        if(fileType.equals("DOC") || fileType.equals("XLS") || fileType.equals("PPT")){
//            View dialogView = LayoutInflater.from(this).inflate(R.layout.pop_up_window_open_with, null);
//
//            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//            dialogBuilder.setView(dialogView);
//            LinearLayout applicationBtn, browserBtn;
//            ImageView applicationImage = dialogView.findViewById(R.id.icon_application_image);
//            applicationBtn = dialogView.findViewById(R.id.application_btn);
//            browserBtn = dialogView.findViewById(R.id.browser_btn);
//            AlertDialog dialog = dialogBuilder.create();
//            dialog.setCancelable(false);
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            dialog.show();

//            switch(fileType){
//                case "DOC":
//                    applicationImage.setImageResource(R.drawable.icon_word);
//                    break;
//                case "XLS":
//                    applicationImage.setImageResource(R.drawable.icon_excel);
//                    break;
//                case "PPT":
//                    applicationImage.setImageResource(R.drawable.icon_powerpoint);
//                    break;
//                default:
//                    applicationImage.setImageResource(R.drawable.icon_file);
//                    break;
//            }
//        }

        if (fileType.equals("DOC")){
            if(isWordInstalled)
                openDocumentInApp(fileUriString, "DOC", "com.microsoft.office.word");
            else
                promptToInstallApp("com.microsoft.office.word");
        }

        if (fileType.equals("XLS")){
            if(isExcelInstalled)
                openDocumentInApp(fileUriString, "XLS", "com.microsoft.office.excel");
            else
                promptToInstallApp("com.microsoft.office.excel");
        }

        if (fileType.equals("PPT")){
            if(isPowerPointInstalled)
                openDocumentInApp(fileUriString, "PPT","com.microsoft.office.powerpoint");
            else
                promptToInstallApp("com.microsoft.office.powerpoint");
        }
    }

    private void promptToInstallApp(String packageName) {
        Intent playStoreIntent = new Intent(Intent.ACTION_VIEW);
        playStoreIntent.setData(Uri.parse("market://details?id=" + packageName));
        startActivity(playStoreIntent);
        finish();
    }

    private void openDocumentInApp(String documentUrl, String fileType, String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if(fileType.equals("DOC")){
            intent.setDataAndType(Uri.parse(documentUrl), "application/msword");
        }if (fileType.equals("XLS")) {
            intent.setDataAndType(Uri.parse(documentUrl), "application/vnd.ms-excel");
        }if (fileType.equals("PPT")) {
            intent.setDataAndType(Uri.parse(documentUrl), "application/vnd.ms-powerpoint");
        }
        // Check if there's an activity that can handle the intent
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);

//        if (!activities.isEmpty()) {
            // There's an activity that can handle the intent, so start it
            intent.setPackage(packageName);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
//        } else {
//            // No activity found to handle the intent
//            Toast.makeText(this, "No app found to open this file type.", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//        intent.setPackage(packageName);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
    }

    private boolean isPackageInstalled(PackageManager pm, String packageName) {
        try {
            pm.getPackageInfo(packageName, 0);
            Log.d("Package Check", packageName + " is installed.");
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("Package Check", packageName + " is not installed.");
            return false;
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
