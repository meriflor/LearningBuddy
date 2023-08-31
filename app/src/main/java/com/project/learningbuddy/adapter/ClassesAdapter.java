package com.project.learningbuddy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.project.learningbuddy.R;
import com.project.learningbuddy.model.Classes;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Random;

public class ClassesAdapter extends FirestoreRecyclerAdapter<Classes, ClassesAdapter.ClassesHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    int[] backgroundLayouts = {
            R.layout.class_backgroundimage1,
            R.layout.class_backgroundimage2
            // Add more background layouts as needed
    };
    public ClassesAdapter(FirestoreRecyclerOptions<Classes> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(ClassesAdapter.ClassesHolder holder, int position, Classes classes) {
        int randomIndex = new Random().nextInt(backgroundLayouts.length);
        int backgroundLayoutResId = backgroundLayouts[randomIndex];
        View customLayout = LayoutInflater.from(holder.itemView.getContext())
                .inflate(backgroundLayoutResId, holder.cardView, false);
        ImageView iconImageView = customLayout.findViewById(R.id.imageView2);
        holder.cardView.addView(customLayout);
        TextView className = customLayout.findViewById(R.id.tv_class_name);
        TextView classYearLevel = customLayout.findViewById(R.id.tv_class_year_level);
        TextView classSection = customLayout.findViewById(R.id.tv_class_section);

        className.setText(classes.getClassName());
        classYearLevel.setText(classes.getClassYearLevel());
        classSection.setText(classes.getClassSection());

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
        CardView cardView;

        public ClassesHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            className = itemView.findViewById(R.id.tv_class_name);
            classYearLevel = itemView.findViewById(R.id.tv_class_year_level);
            classSection = itemView.findViewById(R.id.tv_class_section);
            cardView = itemView.findViewById(R.id.class_cardView);

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