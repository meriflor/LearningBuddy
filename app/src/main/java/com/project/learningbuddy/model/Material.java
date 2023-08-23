package com.project.learningbuddy.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class Material {
    private String classID, materialTitle, materialDesc, materialCreator;
    private Timestamp timestamp;
    private List<String> fileUrls;

    public Material(){

    }

    public Material(String classID, String materialTitle, String materialDesc, String materialCreator, Timestamp timestamp, List<String> fileUrls){
        this.classID = classID;
        this.materialTitle = materialTitle;
        this.materialDesc = materialDesc;
        this.materialCreator = materialCreator;
        this.timestamp = timestamp;
        this.fileUrls = fileUrls;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getMaterialTitle() {
        return materialTitle;
    }

    public void setMaterialTitle(String materialTitle) {
        this.materialTitle = materialTitle;
    }

    public String getMaterialDesc() {
        return materialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        this.materialDesc = materialDesc;
    }

    public String getMaterialCreator() {
        return materialCreator;
    }

    public void setMaterialCreator(String materialCreator) {
        this.materialCreator = materialCreator;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getFileUrls() {
        return fileUrls;
    }

    public void setFileUrls(List<String> fileUrls) {
        this.fileUrls = fileUrls;
    }
}
