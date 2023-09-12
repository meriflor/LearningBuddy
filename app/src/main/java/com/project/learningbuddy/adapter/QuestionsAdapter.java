package com.project.learningbuddy.adapter;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.project.learningbuddy.R;
import com.project.learningbuddy.firebase.QuestionsController;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.Questions;

public class QuestionsAdapter extends FirestoreRecyclerAdapter<Questions, QuestionsAdapter.ClassesHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private String classID;
    private String quizID;
    public QuestionsAdapter(@NonNull FirestoreRecyclerOptions<Questions> options, String classID, String quizID) {
        super(options);
        this.classID = classID;
        this.quizID = quizID;
    }

    @Override
    protected void onBindViewHolder(@NonNull QuestionsAdapter.ClassesHolder holder, int position, @NonNull Questions model) {
        holder.question.setText(model.getQuestion());
        holder.answer.setText(model.getAnswer());

        LinearLayout optionsLayout = holder.layoutOptions;
        optionsLayout.removeAllViews();


        for (String option : model.getOptions()) {
            TextView optionTextView = new TextView(holder.itemView.getContext());
            optionTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            optionTextView.setText(option);
            optionTextView.setTypeface(ResourcesCompat.getFont(optionTextView.getContext(), R.font.quicksand));
            optionTextView.setTextColor(ContextCompat.getColorStateList(optionTextView.getContext(), R.color.violet));
            optionTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            optionTextView.setPadding(0, 12, 0, 0);
            optionsLayout.addView(optionTextView);
        }
    }

    @NonNull
    @Override
    public QuestionsAdapter.ClassesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent,false);
        return new ClassesHolder(view);
    }

    public class ClassesHolder extends RecyclerView.ViewHolder {
        TextView question, answer;
        ImageView delete;
        LinearLayout layoutOptions;
        public ClassesHolder(@NonNull View itemView) {
            super(itemView);

            question = itemView.findViewById(R.id.tv_questionName);
            answer = itemView.findViewById(R.id.question_correct_answer);
            layoutOptions = itemView.findViewById(R.id.options_container);
            delete = itemView.findViewById(R.id.question_delete);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        showDeleteConfirmationDialog(position);
                    }
                }
            });


        }
        private void showDeleteConfirmationDialog(int position) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(itemView.getContext());
            View view = LayoutInflater.from(itemView.getContext())
                    .inflate(R.layout.pop_up_window_confirmation_deletion_question, null);
            TextView message = view.findViewById(R.id.confirmation_message);
            Button confirm = view.findViewById(R.id.btn_confirm);
            Button cancel = view.findViewById(R.id.btn_cancel);

            message.setText("Are you sure you want to delete this question?");
            dialogBuilder.setView(view);
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();

            cancel.setOnClickListener(view1 -> {
                dialog.cancel();
            });
            confirm.setOnClickListener(view2 ->{
                QuestionsController.deleteQuestion(classID, quizID, getSnapshots().getSnapshot(position).getId(), new MyCompleteListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(itemView.getContext(), "Question Deleted", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(itemView.getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }
}
