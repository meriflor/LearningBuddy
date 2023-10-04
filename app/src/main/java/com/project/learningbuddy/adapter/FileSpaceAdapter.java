package com.project.learningbuddy.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.project.learningbuddy.R;
import com.project.learningbuddy.model.FileSpace;
import com.project.learningbuddy.ui.teacher.filespace.FileSpaceActivity;

public class FileSpaceAdapter extends FirestoreRecyclerAdapter<FileSpace, FileSpaceAdapter.FileHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FileSpaceAdapter(FirestoreRecyclerOptions<FileSpace> options) {
        super(options);
    }

    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnClickListener(OnItemClickListener listener){
        listener = mListener;
    }




    @Override
    protected void onBindViewHolder(FileSpaceAdapter.FileHolder holder, int position, FileSpace model) {
        holder.fileText.setText(model.getFileName());
    }

    @Override
    public FileSpaceAdapter.FileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new FileHolder(view, mListener);
    }

    public class FileHolder extends RecyclerView.ViewHolder {
        ImageView fileFormat, fileArrow;
        TextView fileText;
        public FileHolder(View itemView, OnItemClickListener listener) {
            super(itemView);

            fileArrow = itemView.findViewById(R.id.file_arrow);
            fileArrow.setVisibility(View.GONE);
            fileText = itemView.findViewById(R.id.file_name);

            itemView.setOnClickListener(view -> {
                Log.d("TAG", "clicked from the adapter");
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
