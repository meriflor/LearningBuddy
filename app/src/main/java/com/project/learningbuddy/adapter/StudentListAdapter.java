package com.project.learningbuddy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.learningbuddy.R;
import com.project.learningbuddy.firebase.ClassController;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.UserClass;

public class StudentListAdapter extends FirestoreRecyclerAdapter<UserClass, StudentListAdapter.ClassesHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    String classID;
    public StudentListAdapter(@NonNull FirestoreRecyclerOptions<UserClass> options, String classID) {
        super(options);
        this.classID = classID;
    }

    @Override
    protected void onBindViewHolder(@NonNull StudentListAdapter.ClassesHolder holder, int position, @NonNull UserClass model) {
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(model.getUserID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        holder.crown.setVisibility(View.GONE);
                        holder.title.setVisibility(View.GONE);
                        holder.remove.setVisibility(View.VISIBLE);
                        holder.name.setText(documentSnapshot.getString("fullName"));

                    }
                });
    }

    @NonNull
    @Override
    public StudentListAdapter.ClassesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_people, parent, false);
        return new ClassesHolder(view);
    }

    public class ClassesHolder extends RecyclerView.ViewHolder {

        TextView name, title;
        Button remove;
        ImageView crown;

        public ClassesHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.people_fullName);
            remove = itemView.findViewById(R.id.remove);
            crown = itemView.findViewById(R.id.class_crown);
            title = itemView.findViewById(R.id.user_title);

            remove.setOnClickListener(v->{
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    ClassController.removeUser(classID, getSnapshots().getSnapshot(position).getString("userType"), getSnapshots().getSnapshot(position).getString("userID"), getSnapshots().getSnapshot(position).getId(), new MyCompleteListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(itemView.getContext(), "User Removed", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(itemView.getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }
}
