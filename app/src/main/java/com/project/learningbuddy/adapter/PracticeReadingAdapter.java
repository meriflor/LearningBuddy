package com.project.learningbuddy.adapter;

import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.project.learningbuddy.R;

public class PracticeReadingAdapter extends FirestoreRecyclerAdapter<PracticeReadingRetrieve, PracticeReadingAdapter.FileHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    private TextToSpeech textToSpeech;
    private Uri imageUri;
    private Boolean permissionGranted;

    public PracticeReadingAdapter(FirestoreRecyclerOptions<PracticeReadingRetrieve> options, TextToSpeech textToSpeech, Uri imageUri, Boolean permissionGranted) {
        super(options);
        this.textToSpeech = textToSpeech;
        this.imageUri = imageUri;
        this.permissionGranted = permissionGranted;
    }

    @Override
    protected void onBindViewHolder(PracticeReadingAdapter.FileHolder holder, int position, PracticeReadingRetrieve model) {
        Log.d("MATERIALTYPE", "inside the adapter and an item and the text is" + model.getText());
        holder.text.setText(model.getText());
        if(model.getImageUri() == null){
            holder.fileImage.setVisibility(View.GONE);
        }else{
            ViewTreeObserver vto = holder.itemView.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    holder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    int parentWidth = holder.fileImage.getWidth();

                    ViewGroup.LayoutParams layoutParams = holder.fileImage.getLayoutParams();
                    layoutParams.height = parentWidth;
                    holder.fileImage.setLayoutParams(layoutParams);

                    Glide.with(holder.itemView.getContext())
                            .load(Uri.parse(model.getImageUri()))
                            .into(holder.fileImage);
                }
            });
        }
        holder.speakImage2.setOnClickListener(view -> {
            String textToSpeak = model.getText();
            if (textToSpeech != null) {
                textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    @Override
    public PracticeReadingAdapter.FileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_practice_reading, parent, false);
        return new FileHolder(view);
    }

    public class FileHolder extends RecyclerView.ViewHolder {
        TextView text;
        Button speakImage2;
        PhotoView fileImage;
        public FileHolder(View itemView) {
            super(itemView);
            fileImage = itemView.findViewById(R.id.file_image);
            text = itemView.findViewById(R.id.text_to_speech);
            speakImage2 = itemView.findViewById(R.id.speak_icon_2);
        }

    }
}
