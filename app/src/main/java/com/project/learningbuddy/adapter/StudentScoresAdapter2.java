package com.project.learningbuddy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.learningbuddy.R;
import com.project.learningbuddy.model.QuizAttempts;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StudentScoresAdapter2 extends FirestoreRecyclerAdapter<QuizAttempts, StudentScoresAdapter2.ClassesHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public StudentScoresAdapter2(@NonNull FirestoreRecyclerOptions<QuizAttempts> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull StudentScoresAdapter2.ClassesHolder holder, int position, @NonNull QuizAttempts model) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(model.getUserID())
                .get().addOnSuccessListener(documentSnapshot -> {
                    String fullName = documentSnapshot.getString("fullName");
                    Timestamp timestamp = model.getEnd_timestamp();
                    if(timestamp != null) {
                        Date date = timestamp.toDate();
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM. d, yyyy h a", Locale.getDefault());
                        String formattedDate = sdf.format(date);
                        String pts;
                        if(model.getScore()>1)
                            pts = "pts.";
                        else
                            pts = "pt.";
                        if(model.getUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.violet));
                            holder.userName.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.violet_light));
                            holder.timestmap.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.violet_light));
                            holder.scoreText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.violet_light));
                            holder.ptsText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.violet_light));
                        }
                        holder.userName.setText(fullName);
                        holder.timestmap.setText("Attempt: "+formattedDate);
                        holder.scoreText.setText(String.valueOf(model.getScore()));
                        holder.ptsText.setText(pts);
                    }
                });
        holder.userName.setText("");
    }

    @NonNull
    @Override
    public StudentScoresAdapter2.ClassesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scores_2, parent, false);
        return new ClassesHolder(view);
    }

    public class ClassesHolder extends RecyclerView.ViewHolder {
        TextView userName, timestmap, scoreText, ptsText;
        CardView cardView;
        public ClassesHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName_title);
            timestmap = itemView.findViewById(R.id.endTimestamp_text);
            scoreText = itemView.findViewById(R.id.score_text);
            ptsText = itemView.findViewById(R.id.pts_text);
            cardView = itemView.findViewById(R.id.score_cardView);
        }
    }
}
