package com.project.learningbuddy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.learningbuddy.R;
import com.project.learningbuddy.model.Answers;

import java.util.List;

public class StudentAnswersAdapter extends RecyclerView.Adapter<StudentAnswersAdapter.AnswerViewHolder>{
    private List<Answers> answersList;

    public StudentAnswersAdapter(List<Answers> answersList) {
        this.answersList = answersList;
    }

    @NonNull
    @Override
    public StudentAnswersAdapter.AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_answer, parent, false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAnswersAdapter.AnswerViewHolder holder, int position) {
        Answers answer = answersList.get(position);

        // Bind data to the views in the AnswerViewHolder
        holder.question.setText(answer.getQuestion());
        holder.answer.setText("Your response: "+answer.getAnswer());
        if(answer.getCorrect()){
            holder.correct.setImageResource(R.drawable.icon_correct);
        }else{
            holder.correct.setImageResource(R.drawable.icon_wrong);
        }
    }

    @Override
    public int getItemCount() {
        return answersList.size();
    }

    static class AnswerViewHolder extends RecyclerView.ViewHolder {
        TextView question, answer;
        ImageView correct;

        AnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.studentQuestion_text);
            answer = itemView.findViewById(R.id.studentAnswer_text);
            correct = itemView.findViewById(R.id.studentCorrect_image);
        }
    }
}
