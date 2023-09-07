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
import com.project.learningbuddy.model.Quizzes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QuizzesAdapter extends FirestoreRecyclerAdapter<Quizzes, QuizzesAdapter.ClassesHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public QuizzesAdapter(FirestoreRecyclerOptions<Quizzes> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(QuizzesAdapter.ClassesHolder holder, int position, Quizzes model) {
        holder.quizIcon.setBackgroundResource(R.drawable.icon_pencil);
        holder.quizTitle.setText(model.getQuizTitle());
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(model.getQuizCreator());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String fullName = documentSnapshot.getString("fullName");
                    holder.quizCreatorName.setText(fullName);
                }
            }
        });
        // Convert Timestamp to Date
        Date date = model.getTimestamp().toDate();

        // Format the Date to "Month Day, Year" format
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(date);

        holder.quizTimestamp.setText(formattedDate);
        holder.cardView.setVisibility(View.VISIBLE);
    }

    @Override
    public ClassesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ClassesHolder(view, mListener);
    }

    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){mListener = listener;};

    class ClassesHolder extends RecyclerView.ViewHolder {
        ImageView quizIcon;
        TextView quizTitle, quizCreatorName, quizTimestamp;
        CardView cardView;
        public ClassesHolder(View itemView, OnItemClickListener listener) {
            super(itemView);

            quizIcon = itemView.findViewById(R.id.post_icon);
            quizTitle = itemView.findViewById(R.id.post_title);
            quizCreatorName = itemView.findViewById(R.id.post_user);
            quizTimestamp = itemView.findViewById(R.id.post_date);
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
