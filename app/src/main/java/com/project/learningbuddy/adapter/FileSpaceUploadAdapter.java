package com.project.learningbuddy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.project.learningbuddy.R;
import com.project.learningbuddy.model.FileSpace;

import java.util.List;

public class FileSpaceUploadAdapter extends RecyclerView.Adapter<FileSpaceUploadAdapter.FileHolder> {

    Context context;
    List<FileSpace> fileSpaces;

    public FileSpaceUploadAdapter(Context context, List<FileSpace> fileSpaces) {
        this.context = context;
        this.fileSpaces = fileSpaces;
    }

    @Override
    public FileSpaceUploadAdapter.FileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_files_upload, parent, false);
        return new FileHolder(view);
    }

    @Override
    public void onBindViewHolder(FileSpaceUploadAdapter.FileHolder holder, int position) {
        holder.fileName.setText(fileSpaces.get(position).getFileName());
        holder.remove.setOnClickListener(view -> {
            Toast.makeText(context, "Removed!", Toast.LENGTH_SHORT).show();
            fileSpaces.remove(position);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return fileSpaces.size();
    }

    public class FileHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        ImageView remove;
        public FileHolder(View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
            remove = itemView.findViewById(R.id.file_upload_delete);
        }
    }
}
