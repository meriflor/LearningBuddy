package com.project.learningbuddy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.project.learningbuddy.R;
import com.project.learningbuddy.model.UserClass;

public class TeacherListAdapter1 extends FirestoreRecyclerAdapter<UserClass, TeacherListAdapter1.ClassesHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    String classID;
    public TeacherListAdapter1(@NonNull FirestoreRecyclerOptions<UserClass> options, String classID) {
        super(options);
        this.classID = classID;
    }

    @Override
    protected void onBindViewHolder(@NonNull TeacherListAdapter1.ClassesHolder holder, int position, @NonNull UserClass model) {
        if (model.getTitle() != null) {
            if (model.getTitle().equals("Adviser")) {
                holder.crown.setImageResource(R.drawable.icon_crown);
                holder.title.setText(model.getTitle());
                holder.remove.setVisibility(View.GONE);
            } else if (model.getTitle() .equals("Co-Adviser")) {
                holder.crown.setVisibility(View.GONE);
                holder.title.setText(model.getTitle());
                holder.remove.setVisibility(View.GONE);
            } else {
                holder.crown.setVisibility(View.GONE);
                holder.title.setVisibility(View.GONE);
                holder.remove.setVisibility(View.GONE);
            }
        } else {
            holder.crown.setVisibility(View.GONE);
            holder.title.setVisibility(View.GONE);
        }
        holder.name.setText(model.getFullName());
    }

    @NonNull
    @Override
    public TeacherListAdapter1.ClassesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_people, parent, false);
        return new TeacherListAdapter1.ClassesHolder(view);
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
        }
    }
}
