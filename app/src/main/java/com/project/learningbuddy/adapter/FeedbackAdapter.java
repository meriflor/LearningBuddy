package com.project.learningbuddy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.project.learningbuddy.R;
import com.project.learningbuddy.model.Feedback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FeedbackAdapter extends FirestoreRecyclerAdapter<Feedback, FeedbackAdapter.FeedbackHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FeedbackAdapter(FirestoreRecyclerOptions<Feedback> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(FeedbackAdapter.FeedbackHolder holder, int position, Feedback model) {
        Timestamp timestamp = model.getTimestamp();
        Date date = timestamp.toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(date);

        holder.userName.setText(model.getUserName());
        holder.userType_timestamp.setText(model.getUserType()+" | "+formattedDate);
        holder.userMessage.setText(model.getMessage());
    }

    @Override
    public FeedbackAdapter.FeedbackHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
        return new FeedbackHolder(view);
    }

    public class FeedbackHolder extends RecyclerView.ViewHolder {
        TextView userName, userType_timestamp, userMessage;
        public FeedbackHolder(View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.feedback_userName);
            userType_timestamp = itemView.findViewById(R.id.feedback_userType_timestamp);
            userMessage = itemView.findViewById(R.id.feedback_user_message);
        }
    }
}
