package com.project.learningbuddy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.project.learningbuddy.R;
import com.project.learningbuddy.model.JoinClasses;

public class JoinClassesAdapter extends FirestoreRecyclerAdapter<JoinClasses, JoinClassesAdapter.ClassesHolder> {

    public JoinClassesAdapter(@NonNull FirestoreRecyclerOptions<JoinClasses> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(JoinClassesAdapter.ClassesHolder classesHolder, int i, JoinClasses joinClasses) {
        classesHolder.className.setText(joinClasses.getClassName());
        classesHolder.classYearLevel.setText(joinClasses.getClassYearLevel());
        classesHolder.classSection.setText(joinClasses.getClassSection());
    }

    @NonNull
    @Override
    public ClassesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class,
                parent, false);
        return new ClassesHolder(view, mListener);
    }
    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){mListener = listener;};
    class ClassesHolder extends RecyclerView.ViewHolder {
        TextView className, classSection, classYearLevel, ownerTeacherName;

        public ClassesHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            className = itemView.findViewById(R.id.tv_class_name);
            classYearLevel = itemView.findViewById(R.id.tv_class_year_level);
            classSection = itemView.findViewById(R.id.tv_class_section);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(getSnapshots().getSnapshot(position), position);
                        }
                    }
                }
            });
        }
    }
}