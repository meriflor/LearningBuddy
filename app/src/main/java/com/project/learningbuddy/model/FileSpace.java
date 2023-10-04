package com.project.learningbuddy.model;

import android.net.Uri;

public class FileSpace {
    private String fileName, userID, filePath;
    private String fileUri;

    public FileSpace() {
    }

    public FileSpace(String fileName, String userID, String filePath, String fileUri) {
        this.fileName = fileName;
        this.userID = userID;
        this.filePath = filePath;
        this.fileUri = fileUri;
    }

    public String getFileName() {
        return fileName;
    }

    public String getUserID() {
        return userID;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileUri() {
        return fileUri;
    }
}
