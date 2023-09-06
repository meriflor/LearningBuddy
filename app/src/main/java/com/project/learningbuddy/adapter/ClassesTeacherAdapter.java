package com.project.learningbuddy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.project.learningbuddy.R;
import com.project.learningbuddy.model.Classes;

import java.util.List;

public class ClassesTeacherAdapter extends RecyclerView.Adapter<ClassesTeacherAdapter.ClassesHolder> {
    private List<Classes> classList;
    public ClassesTeacherAdapter(List<Classes> classList){
        this.classList = classList;
    }
    @NonNull
    @Override
    public ClassesTeacherAdapter.ClassesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class, parent, false);
        return new ClassesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassesTeacherAdapter.ClassesHolder holder, int position) {
        Classes classes = classList.get(position);
        View customLayout = LayoutInflater.from(holder.itemView.getContext())
                .inflate(classes.getBackgroundLayout(), holder.cardView, false);
        holder.cardView.addView(customLayout);
        TextView className = customLayout.findViewById(R.id.tv_class_name);
        TextView classYearLevel = customLayout.findViewById(R.id.tv_class_year_level);
        TextView classSection = customLayout.findViewById(R.id.tv_class_section);

        className.setText(classes.getClassName());
        classYearLevel.setText(classes.getClassYearLevel());
        classSection.setText(classes.getClassSection());
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public class ClassesHolder extends RecyclerView.ViewHolder {
        TextView className, classSection, classYearLevel, studentCount;
        CardView cardView;
        public ClassesHolder(@NonNull View itemView) {
            super(itemView);
            className = itemView.findViewById(R.id.tv_class_name);
            classYearLevel = itemView.findViewById(R.id.tv_class_year_level);
            classSection = itemView.findViewById(R.id.tv_class_section);
            cardView = itemView.findViewById(R.id.class_cardView);
        }
    }
}
