package com.project.learningbuddy.firebase;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class OfflineFileHandler {
    public static void downloadFile(Context context, String fileName, String fileUri, String filePath, String className){
        File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // Create the "LearningBuddy" directory within the downloads directory
        File learningBuddyDirectory = new File(downloadsDirectory, "LearningBuddy");
        if (!learningBuddyDirectory.exists()) {
            learningBuddyDirectory.mkdirs();
        }

        // Create the "className" directory within the LearningBuddy directory
        File classNameDirectory = new File(learningBuddyDirectory, className);
        if (!classNameDirectory.exists()) {
            classNameDirectory.mkdirs();
        }

        // Create a destination file path
        File destinationFile = new File(classNameDirectory, fileName);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUri));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(fileName)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "LearningBuddy/" + className + "/" + fileName);

        // Enqueue the download request
//        downloadManager.enqueue(request);
        long downloadId = downloadManager.enqueue(request);

        // Monitor the download
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadId == id) {
                    // Download completed successfully
                    Toast.makeText(context, "File downloaded successfully", Toast.LENGTH_SHORT).show();
                    context.unregisterReceiver(this); // Unregister the receiver
                }
            }
        };

        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public static String sanitizeDirectoryName(String className) {
        // Remove invalid characters and spaces
        return className.replaceAll("[^a-zA-Z0-9]", "_");
    }
}
