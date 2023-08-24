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
import com.project.learningbuddy.model.Announcements;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AnnouncementsAdapter extends FirestoreRecyclerAdapter<Announcements, AnnouncementsAdapter.ClassesHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AnnouncementsAdapter(@NonNull FirestoreRecyclerOptions<Announcements> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AnnouncementsAdapter.ClassesHolder holder, int position, @NonNull Announcements model) {
        holder.announcementIcon.setBackgroundResource(R.drawable.icon_announcement);
        holder.announcementTitle.setText(model.getAnnouncementTitle());
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(model.getAnnouncementCreator());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String fullName = documentSnapshot.getString("fullName");
                    holder.announcementCreator.setText(fullName);
                }
            }
        });
        Date date = model.getTimestamp().toDate();

        // Format the Date to "Month Day, Year" format
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(date);

        holder.announcementTimestamp.setText(formattedDate);
    }

    @NonNull
    @Override
    public AnnouncementsAdapter.ClassesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post,
                parent, false);
        return new ClassesHolder(view, mListener);
    }

    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){mListener = listener;};


    public class ClassesHolder extends RecyclerView.ViewHolder {
        ImageView announcementIcon;
        TextView announcementTitle, announcementCreator, announcementTimestamp;

        public ClassesHolder(View itemView, OnItemClickListener listener) {
            super(itemView);

            announcementIcon = itemView.findViewById(R.id.post_icon);
            announcementTitle = itemView.findViewById(R.id.post_title);
            announcementCreator = itemView.findViewById(R.id.post_user);
            announcementTimestamp = itemView.findViewById(R.id.post_date);

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
