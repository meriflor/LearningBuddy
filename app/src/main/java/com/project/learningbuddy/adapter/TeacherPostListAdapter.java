package com.project.learningbuddy.adapter;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.project.learningbuddy.R;
import com.project.learningbuddy.firebase.LearningMaterialsController;
import com.project.learningbuddy.listener.ExistListener;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.LearningMaterials;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TeacherPostListAdapter extends FirestoreRecyclerAdapter<LearningMaterials, TeacherPostListAdapter.MaterialHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){mListener = listener;};

    public TeacherPostListAdapter(FirestoreRecyclerOptions<LearningMaterials> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(TeacherPostListAdapter.MaterialHolder holder, int position, LearningMaterials model) {
        Timestamp timestamp = model.getTimestamp();
        Date date = timestamp.toDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM. dd, yyyy hh:mm aaa", Locale.getDefault());
        String formattedDate = simpleDateFormat.format(date);
        holder.title.setText(model.getMaterialTitle());
        holder.timestamp.setText(formattedDate);
        if(!model.getMaterialContent().isEmpty()){
            holder.content.setText(model.getMaterialContent());
        }else{
            holder.content.setVisibility(View.GONE);
        }
    }

    @Override
    public TeacherPostListAdapter.MaterialHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_learning_material, parent,  false);
        return new MaterialHolder(view, mListener);
    }

    public class MaterialHolder extends RecyclerView.ViewHolder {
        TextView title, timestamp, content, likes;
        ImageView likeImage;
        RecyclerView uploadedFilesView;
        LinearLayout likeBtn;
        public MaterialHolder(View itemView, OnItemClickListener listener) {
            super(itemView);

            title = itemView.findViewById(R.id.post_materialTitle_text);
            timestamp = itemView.findViewById(R.id.post_materialTimestamp_text);
            content = itemView.findViewById(R.id.post_materialContent_text);
            likes    = itemView.findViewById(R.id.like_btn_text);
            likeImage = itemView.findViewById(R.id.like_btn_image);
            likeBtn = itemView.findViewById(R.id.like_btn);

            likeBtn.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                        LearningMaterialsController.checkUserLike(getSnapshots().getSnapshot(position).getId(), new ExistListener() {
                            @Override
                            public void onExist(Boolean exist) {
                                if(exist){
                                    Toast.makeText(itemView.getContext(), "You already liked the post.", Toast.LENGTH_SHORT).show();
                                }else{
                                    LearningMaterialsController.userLikeLearningmaterials(getSnapshots().getSnapshot(position).getId(), new MyCompleteListener() {
                                        @Override
                                        public void onSuccess() {
                                            Toast.makeText(itemView.getContext(), "Liked!", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure() {
                                            Log.d("TAG", "There's a problem adding user to the likes of the learningMaterial post.");
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.d("TAG", "There's a problem in checking the user like.");
                            }
                        });
                    }
                }
            });
        }
    }
}
