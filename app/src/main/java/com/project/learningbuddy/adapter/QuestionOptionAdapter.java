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
import com.project.learningbuddy.model.Options;

import java.util.List;

public class QuestionOptionAdapter extends RecyclerView.Adapter<QuestionOptionAdapter.QuestionHolder> {

    Context context;
    List<String> options;
    private OnItemClickListener listener;


    public QuestionOptionAdapter(Context context, List<String> options, OnItemClickListener listener) {
        this.context = context;
        this.options = options;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }


    @Override
    public QuestionOptionAdapter.QuestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_options, parent, false);
        return new QuestionHolder(view);
    }

    @Override
    public void onBindViewHolder(QuestionOptionAdapter.QuestionHolder holder, int position) {
        holder.optionsText.setText(options.get(position));
        holder.optionRemove.setOnClickListener(view -> {
            listener.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public class QuestionHolder extends RecyclerView.ViewHolder {
        TextView optionsText;
        ImageView optionRemove;
        public QuestionHolder(View itemView) {
            super(itemView);

            optionsText = itemView.findViewById(R.id.option_text);
            optionRemove = itemView.findViewById(R.id.option_remove);
        }
    }
}
