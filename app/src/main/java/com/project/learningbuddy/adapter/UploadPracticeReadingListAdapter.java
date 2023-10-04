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
import com.project.learningbuddy.model.PracticeReading;

import java.util.List;

public class UploadPracticeReadingListAdapter extends RecyclerView.Adapter<UploadPracticeReadingListAdapter.FileHolder> {

    Context context;
    List<PracticeReading> practiceReadingList;

    public UploadPracticeReadingListAdapter(Context context, List<PracticeReading> practiceReadingList){
        this.context = context;
        this.practiceReadingList = practiceReadingList;
    }

    @Override
    public UploadPracticeReadingListAdapter.FileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_files_upload_practice_reading, parent, false);
        return new FileHolder(view);
    }

    @Override
    public void onBindViewHolder(UploadPracticeReadingListAdapter.FileHolder holder, int position) {
        holder.text.setText(practiceReadingList.get(position).getText());
        if(practiceReadingList.get(position).getFileUri() == null){
            holder.fileName.setVisibility(View.GONE);
            holder.fileImage.setVisibility(View.GONE);
        }else{
            holder.fileName.setText(practiceReadingList.get(position).getImageName());
            holder.fileImage.setImageURI(practiceReadingList.get(position).getFileUri());
        }
        holder.btnRemove.setOnClickListener(view -> {
            Toast.makeText(context, practiceReadingList.get(position).getText()+" removed!", Toast.LENGTH_SHORT).show();
            practiceReadingList.remove(position);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return practiceReadingList.size();
    }

    public class FileHolder extends RecyclerView.ViewHolder {
        TextView text, fileName;
        ImageView btnRemove, fileImage;
        public FileHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.file_text);
            fileName = itemView.findViewById(R.id.file_name);
            btnRemove = itemView.findViewById(R.id.file_delete);
            fileImage = itemView.findViewById(R.id.file_image);
        }
    }
}
