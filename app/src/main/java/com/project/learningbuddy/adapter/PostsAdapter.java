package com.project.learningbuddy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.learningbuddy.R;
import com.project.learningbuddy.model.Posts;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PostsAdapter extends FirestoreRecyclerAdapter<Posts, PostsAdapter.ClassesHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PostsAdapter(@NonNull FirestoreRecyclerOptions<Posts> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PostsAdapter.ClassesHolder holder, int position, @NonNull Posts model) {
        switch (model.getPostType()) {
            case "Quiz":
                holder.postIcon.setBackgroundResource(R.drawable.icon_pencil);
                break;
            case "Announcement":
                holder.postIcon.setBackgroundResource(R.drawable.icon_announcement);
                break;
            case "Learning Materials":
                holder.postIcon.setBackgroundResource(R.drawable.icon_attachment_3);
                break;
            default:
                holder.postIcon.setBackgroundResource(R.drawable.icon_bubble);
                break;
        }
        holder.postTitle.setText(model.getPostTitle());
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(model.getPostCreatorID());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String fullName = documentSnapshot.getString("fullName");
                    holder.postCreator.setText(fullName);
                }
            }
        });
        Date date = model.getTimestamp().toDate();

        // Format the Date to "Month Day, Year" format
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(date);

        holder.postTimestamp.setText(formattedDate);
    }

    @NonNull
    @Override
    public PostsAdapter.ClassesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ClassesHolder(view, mListener);
    }

    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){mListener = listener;};



    public class ClassesHolder extends RecyclerView.ViewHolder {
        ImageView postIcon;
        TextView postTitle, postCreator, postTimestamp;

        public ClassesHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            postIcon = itemView.findViewById(R.id.post_icon);
            postTitle = itemView.findViewById(R.id.post_title);
            postCreator = itemView.findViewById(R.id.post_user);
            postTimestamp = itemView.findViewById(R.id.post_date);

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
