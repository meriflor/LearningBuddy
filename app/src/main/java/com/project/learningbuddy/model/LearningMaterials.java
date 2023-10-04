package com.project.learningbuddy.model;

import com.google.firebase.Timestamp;

import java.util.List;
import java.util.Map;

public class LearningMaterials {
    private String classID, materialTitle, materialContent, materialCreator, materialType;
    private Timestamp timestamp;
    private List<String> files;

    public LearningMaterials(){

    }

    public LearningMaterials(String classID, String materialTitle, String materialContent, String materialCreator, String materialType, Timestamp timestamp, List<String> files) {
        this.classID = classID;
        this.materialTitle = materialTitle;
        this.materialContent = materialContent;
        this.materialCreator = materialCreator;
        this.materialType = materialType;
        this.timestamp = timestamp;
        this.files = files;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public void setMaterialTitle(String materialTitle) {
        this.materialTitle = materialTitle;
    }

    public void setMaterialContent(String materialContent) {
        this.materialContent = materialContent;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialCreator(String materialCreator) {
        this.materialCreator = materialCreator;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public String getClassID() {
        return classID;
    }

    public String getMaterialTitle() {
        return materialTitle;
    }

    public String getMaterialContent() {
        return materialContent;
    }

    public String getMaterialCreator() {
        return materialCreator;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public List<String> getFiles() {
        return files;
    }
}
