package com.project.learningbuddy.adapter;

public class PracticeReadingRetrieve {
    private String text, imageUri, imageName;

    public PracticeReadingRetrieve() {
    }

    public PracticeReadingRetrieve(String text, String imageUri, String imageName) {
        this.text = text;
        this.imageUri = imageUri;
        this.imageName = imageName;
    }

    public String getText() {
        return text;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getImageName() {
        return imageName;
    }
}
