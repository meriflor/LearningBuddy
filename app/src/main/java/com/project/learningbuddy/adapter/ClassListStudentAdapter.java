package com.project.learningbuddy.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.learningbuddy.R;
import com.project.learningbuddy.model.ClassList;

public class ClassListStudentAdapter extends FirestoreRecyclerAdapter<ClassList, ClassListStudentAdapter.ClassesHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ClassListStudentAdapter(@NonNull FirestoreRecyclerOptions<ClassList> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ClassListStudentAdapter.ClassesHolder holder, int position, @NonNull ClassList model) {
        FirebaseFirestore.getInstance()
                .collection("classes")
                .document(model.getClassID())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Long backgroundLayout = documentSnapshot.getLong("backgroundLayout");
                        int intValue = backgroundLayout.intValue();
                        String classYearLevel = documentSnapshot.getString("classYearLevel");
                        String classSection = documentSnapshot.getString("classSection");

                        View customLayout = LayoutInflater.from(holder.itemView.getContext())
                                .inflate(intValue, holder.cardView, false);

                        TextView tvclassName = customLayout.findViewById(R.id.tv_class_name);
                        TextView tvclassYearLevel = customLayout.findViewById(R.id.tv_class_year_level);
                        TextView tvclassSection = customLayout.findViewById(R.id.tv_class_section);
                        TextView extra = customLayout.findViewById(R.id.tv_extra_info);

                        FirebaseFirestore.getInstance()
                                .collection("classes")
                                .document(model.getClassID())
                                .collection("teachers")
                                .get().addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){
                                        for(QueryDocumentSnapshot documentSnapshot1:task.getResult()){
                                            if(documentSnapshot1.getString("title").equals("Adviser")){
                                                String teacherName = documentSnapshot1.getString("fullName");
                                                holder.cardView.addView(customLayout);
                                                tvclassName.setText(model.getClassName());
                                                tvclassYearLevel.setText(classYearLevel);
                                                tvclassSection.setText(classSection);
                                                extra.setText(teacherName);
                                            }
                                        }
                                    }
                                });

                    }
                });
    }
    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){mListener = listener;};


    @NonNull
    @Override
    public ClassListStudentAdapter.ClassesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class, parent, false);
        return new ClassesHolder(view, mListener);
    }

    public class ClassesHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        public ClassesHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

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
