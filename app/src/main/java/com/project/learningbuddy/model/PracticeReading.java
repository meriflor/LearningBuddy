package com.project.learningbuddy.model;

import android.net.Uri;

import com.google.firebase.Timestamp;

public class PracticeReading {
    private String text, imageName;
    private Uri fileUri;
    public PracticeReading(){

    }

    public PracticeReading(String text, Uri fileUri, String imageName) {
        this.text = text;
        this.fileUri = fileUri;
        this.imageName = imageName;
    }

    public String getImageName() {
        return imageName;
    }

    public String getText() {
        return text;
    }

    public Uri getFileUri() {
        return fileUri;
    }

}
