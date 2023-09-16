package com.project.learningbuddy.adapter;

import static com.google.common.io.Files.getFileExtension;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.project.learningbuddy.R;
import com.project.learningbuddy.model.FileInfo;

public class MaterialFilesAdapter extends FirestoreRecyclerAdapter<FileInfo, MaterialFilesAdapter.FileHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MaterialFilesAdapter(@NonNull FirestoreRecyclerOptions<FileInfo> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MaterialFilesAdapter.FileHolder holder, int position, @NonNull FileInfo model) {
        Log.d("TAGGGGG", "Got here?");
        String fileType = getFileType(model.getFileName());
        Log.d("TAGGGGG", fileType);
        if(fileType.equals("PDF")){
            holder.icon.setImageResource(R.drawable.icon_file_pdf);
            Log.d("TAGGGGG", "Got here PDF?");
        }else if(fileType.equals("JPG")){
            holder.icon.setImageResource(R.drawable.icon_file_jpg);
        }else if(fileType.equals("PNG")){
            holder.icon.setImageResource(R.drawable.icon_file_png);
        }else if(fileType.equals("DOC")){
            holder.icon.setImageResource(R.drawable.icon_file_doc);
        }else if(fileType.equals("XLS")){
            holder.icon.setImageResource(R.drawable.icon_file_xls);
        }else if(fileType.equals("TXT")){
            holder.icon.setImageResource(R.drawable.icon_file_txt);
        }else if(fileType.equals("PPT")){
            holder.icon.setImageResource(R.drawable.icon_file_ppt);
        }else if(fileType.equals("MP3")){
            holder.icon.setImageResource(R.drawable.icon_file_mp3);
        }else if(fileType.equals("MP4")){
            holder.icon.setImageResource(R.drawable.icon_file_mp4);
            Log.d("TAGGGGG", "Got here MP4?");
        }else{
            holder.icon.setImageResource(R.drawable.icon_file);
        }
        holder.name.setText(model.getFileName());
        Log.d("TAGGGGG", model.getFileName());
    }

    public static String getFileType(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();

        // List of known extensions for different file types
        if (extension.equals("pdf")) {
            return "PDF";
        } else if (extension.equals("jpg") || extension.equals("jpeg")) {
            return "JPG";
        } else if (extension.equals("png")) {
            return "PNG";
        } else if (extension.equals("doc") || extension.equals("docx")) {
            return "DOC";
        } else if (extension.equals("xls") || extension.equals("xlsx")) {
            return "XLS";
        } else if (extension.equals("txt")) {
            return "TXT";
        } else if (extension.equals("ppt") || extension.equals("pptx")) {
            return "PPT";
        } else if (extension.equals("mp3") || extension.equals("ogg")) {
            return "MP3";
        } else if (extension.equals("avi") || extension.equals("mp4")) {
            return "MP4";
        } else {
            return "Unknown"; // If the extension doesn't match known types
        }
    }
    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){mListener = listener;};

    @NonNull
    @Override
    public MaterialFilesAdapter.FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new FileHolder(view, mListener);
    }

    public class FileHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView name;
        public FileHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            icon = itemView.findViewById(R.id.file_icon);
            name = itemView.findViewById(R.id.file_name);

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
