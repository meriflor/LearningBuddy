package com.project.learningbuddy.model;

import android.net.Uri;

public class FileInfo {
    private String fileType;   // e.g., "pdf", "doc", "jpg"
    private String fileName;   // e.g., "file.pdf", "document.docx"
    private Uri fileUri;       // Uri of the file

    public FileInfo(String fileType, String fileName, Uri fileUri) {
        this.fileType = fileType;
        this.fileName = fileName;
        this.fileUri = fileUri;
    }

    public String getFileType() {
        return fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public Uri getFileUri() {
        return fileUri;
    }
}

