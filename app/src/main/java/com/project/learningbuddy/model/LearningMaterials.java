package com.project.learningbuddy.model;

import com.google.firebase.Timestamp;

import java.util.List;
import java.util.Map;

public class LearningMaterials {
    private String classID, materialTitle, materialContent, materialCreator;
    private Timestamp timestamp;
    private List<Map<String, String>> files;

    public LearningMaterials(){

    }

    public LearningMaterials(String classID, String materialTitle, String materialContent, String materialCreator, Timestamp timestamp, List<Map<String, String>> files) {
        this.classID = classID;
        this.materialTitle = materialTitle;
        this.materialContent = materialContent;
        this.materialCreator = materialCreator;
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

    public void setMaterialCreator(String materialCreator) {
        this.materialCreator = materialCreator;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setFiles(List<Map<String, String>> files) {
        this.files = files;
    }
}
