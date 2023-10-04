package com.project.learningbuddy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.project.learningbuddy.R;
import com.project.learningbuddy.model.LearningMaterials;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TeacherDraftListAdapter extends FirestoreRecyclerAdapter<LearningMaterials, TeacherDraftListAdapter.DraftHolder> {
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


    public TeacherDraftListAdapter(FirestoreRecyclerOptions<LearningMaterials> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(TeacherDraftListAdapter.DraftHolder holder, int position, LearningMaterials model) {
        Timestamp timestamp = model.getTimestamp();
        Date date = timestamp.toDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM. dd, yyyy hh:mm aaa", Locale.getDefault());
        String formattedDate = simpleDateFormat.format(date);
        holder.title.setText(model.getMaterialTitle());
        holder.timestamp.setText(formattedDate);
        holder.content.setText(model.getMaterialContent());
    }

    @Override
    public TeacherDraftListAdapter.DraftHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_draft_learning_material, parent, false);
        return new DraftHolder(view, mListener);
    }

    public class DraftHolder extends RecyclerView.ViewHolder {
        TextView title, timestamp, content;
        public DraftHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.draft_title_text);
            timestamp = itemView.findViewById(R.id.draft_timestamp_text);
            content = itemView.findViewById(R.id.draft_content_text);

            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }
}
