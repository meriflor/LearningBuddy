package com.project.learningbuddy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.project.learningbuddy.R;
import com.project.learningbuddy.model.Classes;

import org.checkerframework.checker.nullness.qual.NonNull;

public class ClassesAdapter extends FirestoreRecyclerAdapter<Classes, ClassesAdapter.ClassesHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ClassesAdapter(FirestoreRecyclerOptions<Classes> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(ClassesAdapter.ClassesHolder holder, int position, Classes classes) {
        holder.className.setText(classes.getClassName());
        holder.classYearLevel.setText(classes.getClassYearLevel());
        holder.classSection.setText(classes.getClassSection());
    }

    @NonNull
    @Override
    public ClassesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new ClassesHolder(view, mListener);
    }

    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    class ClassesHolder extends RecyclerView.ViewHolder {
        TextView className, classSection, classYearLevel, studentCount;

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