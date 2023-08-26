package com.project.learningbuddy.model;

import android.net.Uri;

/*This model class is required only if you are going to show the list of selected images*/
public class UploadFileModel {
    String imageName;
    Uri imageURI;

    public UploadFileModel() {
    }

    public UploadFileModel(String imageName, Uri imageURI) {
        this.imageName = imageName;
        this.imageURI = imageURI;
    }

    public String getImageName() {
        return imageName;
    }

    public Uri getImageURI() {
        return imageURI;
    }
}