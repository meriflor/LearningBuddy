package com.project.learningbuddy.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.learningbuddy.MainActivity;
import com.project.learningbuddy.R;

public class LoginActivity extends AppCompatActivity {

    private EditText et_email, et_password;
    private Button sign_btn, register_btn;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        Initialization
        et_email = findViewById(R.id.email_login);
        et_password = findViewById(R.id.password_login);
        sign_btn = findViewById(R.id.login_btn);
        register_btn = findViewById(R.id.signup_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

//        Don't have an account yet
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

//        Signing In
        sign_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = et_email.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                if(email.isEmpty()){
                    et_email.setError("Please enter your email.");
                    return;
                }if(password.isEmpty()){
                    et_password.setError("Please enter your password");
                    return;
                }else{
                    loginUser(email, password);
                }
            }
        });
    }

    public void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

//    Authentication
    public void loginUser(String email, String password){
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                showToast("Logged In");
//                getUserData(email, firebaseAuth.getCurrentUser().getUid());
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                showToast("Email not yet registered.");
            }
        });
    }

//    private void getUserData(String email, String uid) {
//        DocumentReference userDoc = firebaseFirestore.collection("users").document(uid);
//        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    DocumentSnapshot
//                }
//            }
//        })
//    }
}