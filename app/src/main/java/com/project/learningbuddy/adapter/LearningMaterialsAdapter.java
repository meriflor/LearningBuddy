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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.learningbuddy.R;
import com.project.learningbuddy.model.LearningMaterials;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LearningMaterialsAdapter extends FirestoreRecyclerAdapter<LearningMaterials, LearningMaterialsAdapter.ClassesHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public LearningMaterialsAdapter(FirestoreRecyclerOptions<LearningMaterials> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(LearningMaterialsAdapter.ClassesHolder holder, int position, LearningMaterials model) {
        if(model.getMaterialType() == null){
            holder.matIcon.setBackgroundResource(R.drawable.icon_attachment_3);
        }else{
            if(model.getMaterialType().equals("Practice Reading")){
                holder.matIcon.setBackgroundResource(R.drawable.icon_speak_02_rounded);
            }else if(model.getMaterialType().equals("Uploaded Files")){
                holder.matIcon.setBackgroundResource(R.drawable.icon_attachment_3);
            }else{
                holder.matIcon.setBackgroundResource(R.drawable.icon_bubble);
            }
        }
        holder.matTitle.setText(model.getMaterialTitle());
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(model.getMaterialCreator());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String fullName = documentSnapshot.getString("fullName");
                    holder.matCreator.setText(fullName);
                }
            }
        });
        Date date = model.getTimestamp().toDate();

        // Format the Date to "Month Day, Year" format
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(date);

        holder.matTimestamp.setText(formattedDate);
        holder.cardView.setVisibility(View.VISIBLE);
    }


    @Override
    public LearningMaterialsAdapter.ClassesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ClassesHolder(view, mListener);
    }

    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){mListener = listener;};


    public class ClassesHolder extends RecyclerView.ViewHolder {
        ImageView matIcon;
        TextView matTitle, matCreator, matTimestamp;
        CardView cardView;

        public ClassesHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            matIcon = itemView.findViewById(R.id.post_icon);
            matTitle = itemView.findViewById(R.id.post_title);
            matCreator = itemView.findViewById(R.id.post_user);
            matTimestamp = itemView.findViewById(R.id.post_date);
            cardView = itemView.findViewById(R.id.post_cardView);

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
