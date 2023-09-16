package com.project.learningbuddy.model;

public class FileInfo {
//    private String fileType;   // e.g., "pdf", "doc", "jpg"
//    private String fileName;   // e.g., "file.pdf", "document.docx"
//    private Uri fileUri;       // Uri of the file
//
//    public FileInfo(String fileType, String fileName, Uri fileUri) {
//        this.fileType = fileType;
//        this.fileName = fileName;
//        this.fileUri = fileUri;
//    }
//
//    public String getFileType() {
//        return fileType;
//    }
//
//    public String getFileName() {
//        return fileName;
//    }
//
//    public Uri getFileUri() {
//        return fileUri;
//    }

    private String fileName;
    private String filePath;
    private String fileUri;

    public FileInfo() {
    }

    public FileInfo(String fileName, String filePath, String fileUri) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileUri = fileUri;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileUri() {
        return fileUri;
    }
}

