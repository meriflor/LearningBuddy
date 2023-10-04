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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.learningbuddy.R;
import com.project.learningbuddy.model.ClassList;

public class ClassListTeacherAdapter extends FirestoreRecyclerAdapter<ClassList, ClassListTeacherAdapter.ClassesHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ClassListTeacherAdapter(FirestoreRecyclerOptions<ClassList> options) {
        super(options);
    }

    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){mListener = listener;};

    @Override
    protected void onBindViewHolder(ClassListTeacherAdapter.ClassesHolder holder, int position, ClassList model) {
        FirebaseFirestore.getInstance()
                .collection("classes")
                .document(model.getClassID())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        Long backgroundLayout = documentSnapshot.getLong("backgroundLayout");
//                        int intValue = backgroundLayout.intValue();
                        String backgroundLayout = documentSnapshot.getString("backgroundLayout");
                        if(backgroundLayout!=null){
                            int layoutResourceId = holder.itemView.getContext().getResources()
                                    .getIdentifier(backgroundLayout, "layout", holder.itemView.getContext().getPackageName());
                            String classYearLevel = documentSnapshot.getString("classYearLevel");
                            String classSection = documentSnapshot.getString("classSection");

                            View customLayout = LayoutInflater.from(holder.itemView.getContext())
                                    .inflate(layoutResourceId, holder.cardView, false);

                            TextView tvclassName = customLayout.findViewById(R.id.tv_class_name);
                            TextView tvclassYearLevel = customLayout.findViewById(R.id.tv_class_year_level);
                            TextView tvclassSection = customLayout.findViewById(R.id.tv_class_section);
                            TextView extra = customLayout.findViewById(R.id.tv_extra_info);
                            ImageView imageView = customLayout.findViewById(R.id.classtransparency);

                            int cardviewHeight = holder.cardView.getHeight();
                            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                            layoutParams.height = cardviewHeight;
                            imageView.setLayoutParams(layoutParams);

                            FirebaseFirestore.getInstance()
                                    .collection("classes")
                                    .document(model.getClassID())
                                    .collection("students")
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            int students = queryDocumentSnapshots.size();
                                            holder.cardView.removeAllViews();
                                            holder.cardView.addView(customLayout);
                                            tvclassName.setText(model.getClassName());
                                            tvclassYearLevel.setText(classYearLevel);
                                            tvclassSection.setText(classSection);
                                            extra.setText(students + " students(s)");
                                        }
                                    });
                        }


                    }
                });
    }

    @Override
    public ClassListTeacherAdapter.ClassesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class, parent, false);
        return new ClassesHolder(view, mListener);
    }

    public class ClassesHolder extends RecyclerView.ViewHolder {
//        TextView className, classSection, classYearLevel, studentCount;
        CardView cardView;
        public ClassesHolder(View itemView, OnItemClickListener listener) {
            super(itemView);

//            className = itemView.findViewById(R.id.tv_class_name);
//            classYearLevel = itemView.findViewById(R.id.tv_class_year_level);
//            classSection = itemView.findViewById(R.id.tv_class_section);
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
