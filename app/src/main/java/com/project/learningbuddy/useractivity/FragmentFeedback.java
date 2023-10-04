package com.project.learningbuddy.useractivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.learningbuddy.R;
import com.project.learningbuddy.adapter.FeedbackAdapter;
import com.project.learningbuddy.firebase.FeedbackController;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.model.Feedback;

public class FragmentFeedback extends Fragment {

    public FeedbackAdapter adapter;
    public RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        FloatingActionButton fab = view.findViewById(R.id.add_feedback_fab);
        recyclerView  = view.findViewById(R.id.feedback_recyclerView);

        fab.setOnClickListener(view1-> {
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.pop_up_window_add_feedback, null);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            dialogBuilder.setView(dialogView);
            AlertDialog dialog = dialogBuilder.create();
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            EditText feedbackMessage = dialogView.findViewById(R.id.feedback_message);
            Button postFeedback = dialogView.findViewById(R.id.feedback_message_button);
            Button cancelFeedback = dialogView.findViewById(R.id.feedback_dialog_cancel_button);
            
            postFeedback.setOnClickListener(v1->{
                String message = feedbackMessage.getText().toString().trim();
                if(message.isEmpty()){
                    Toast.makeText(getContext(), "Enter your feedback.", Toast.LENGTH_SHORT).show();
                }else{
                    FeedbackController.createFeedbackMessage(message, new MyCompleteListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getContext(), "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                        @Override
                        public void onFailure() {
                            Log.d("FEEDBACK", "There's a problem in posting the feedback");
                            Toast.makeText(getContext(), "Check you internet network.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            
            cancelFeedback.setOnClickListener(v->{
                dialog.dismiss();
            });
        });

        getFeedbackList();

        return view;
    }

    private void getFeedbackList() {
        Query feedbackQuery = FirebaseFirestore.getInstance()
                .collection("feedbacks")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Feedback> options = new FirestoreRecyclerOptions.Builder<Feedback>()
                .setQuery(feedbackQuery, Feedback.class).build();
        adapter = new FeedbackAdapter(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
