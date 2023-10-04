package com.project.learningbuddy.listener;

import com.project.learningbuddy.model.LearningMaterials;

public interface LearningMaterialListener {
    void onSuccess(LearningMaterials learningMaterials);
    void onFailure(Exception e);
}
