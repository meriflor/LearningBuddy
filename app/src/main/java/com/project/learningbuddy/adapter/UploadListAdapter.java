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
import com.project.learningbuddy.model.UploadFileModel;

import java.util.List;

/*This adapter is required only if you are going to show the list of selected images*/
public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.FileHolder> {

    Context context;
    List<UploadFileModel> imagesList;

    public UploadListAdapter(Context context, List<UploadFileModel> imagesList) {
        this.context = context;
        this.imagesList = imagesList;
    }

    @Override
    public UploadListAdapter.FileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_files_upload, parent, false);
        return new FileHolder(view);
    }

    @Override
    public void onBindViewHolder(UploadListAdapter.FileHolder holder, int position) {
        holder.imageName.setText(imagesList.get(position).getImageName());
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, imagesList.get(position).getImageName()+" removed!", Toast.LENGTH_SHORT).show();
                imagesList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    class FileHolder extends RecyclerView.ViewHolder{
        TextView imageName;
        ImageView btnRemove;
        public FileHolder(View itemView) {
            super(itemView);
            imageName = itemView.findViewById(R.id.file_name);
            btnRemove = itemView.findViewById(R.id.file_upload_delete);
        }
    }
}