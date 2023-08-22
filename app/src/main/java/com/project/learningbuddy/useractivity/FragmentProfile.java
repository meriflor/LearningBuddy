package com.project.learningbuddy.useractivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.project.learningbuddy.R;
import com.project.learningbuddy.firebase.UserController;
import com.project.learningbuddy.listener.UserDataListener;
import com.project.learningbuddy.model.User;

public class FragmentProfile extends Fragment {

    private TextView tv_email, tv_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //ImageView profile = (ImageView) view.findViewById(R.id.profile_image);
        tv_email = (TextView) view.findViewById(R.id.profile_email);
        tv_name = (TextView) view.findViewById(R.id.profile_fullName);

//        Dialog dialog = new Dialog(getContext());
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setContentView(R.layout.loading);
//        dialog.show();

        UserController.getUserData(FirebaseAuth.getInstance().getCurrentUser().getUid(), new UserDataListener() {
            @Override
            public void onSuccess(User user) {
                String fullName = user.getFullName();
                String email = user.getEmail();

                tv_name.setText(fullName);
                tv_email.setText(email);
            }

            @Override
            public void onFailure(Exception exception) {
                Log.d("TAG", "get failed with ");
            }
        });

//        DocumentReference documentReference =  app_fireStore.collection("USERS").document(userID);
//        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    DocumentSnapshot documentSnapshot = task.getResult();
//                    if (documentSnapshot.exists()) {
//                        name.setText(documentSnapshot.getString("FULL_NAME"));
//                        email.setText(documentSnapshot.getString("EMAIL"));
//                        dialog.dismiss();
//                    }else{
//                        Log.d("LOGGER", "No such document");
//                    }
//                }else {
//                    Log.d("LOGGER", "get failed with ", task.getException());
//                }
//            }
//        });
        return view;
    }


}