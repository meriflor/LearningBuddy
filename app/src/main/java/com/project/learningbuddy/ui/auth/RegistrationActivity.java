package com.project.learningbuddy.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.learningbuddy.MainActivity;
import com.project.learningbuddy.R;
import com.project.learningbuddy.firebase.UserController;
import com.project.learningbuddy.listener.MyCompleteListener;
import com.project.learningbuddy.useractivity.Homepage;

public class RegistrationActivity extends AppCompatActivity {

    private EditText et_email, et_fullName, et_password, et_repassword;
    private Button register_button;
    private Spinner sp_userType;
    private TextView login_button;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    String email, fullName, password, retypePassword, userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

//        Initialization
        et_email = findViewById(R.id.email_register);
        et_fullName = findViewById(R.id.fullname_register);
        et_password = findViewById(R.id.password_register);
        et_repassword = findViewById(R.id.retype_password_register);
        sp_userType = findViewById(R.id.user_type_register);
        register_button = findViewById(R.id.register_btn);
        login_button = findViewById(R.id.signin_btn);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        ArrayAdapter <CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.userType, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        sp_userType.setAdapter(adapter);

//      Already have an account
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

//       Register account
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userType = sp_userType.getSelectedItem().toString();
                email = et_email.getText().toString().trim();
                fullName = et_fullName.getText().toString().trim();
                password = et_password.getText().toString().trim();
                retypePassword = et_repassword.getText().toString().trim();

                //conditions not met
                if(TextUtils.isEmpty(email) || !email.contains("@")){
                    et_email.setError("Please enter your Email");
                }else if(fullName.isEmpty()){
                    et_fullName.setError("Please enter your name");
                }else if(TextUtils.isEmpty(password)) {
                    et_password.setError("Please enter your Password");
                }else if(password.length() < 8) {
                    et_password.setError("Password must be 8 character");
                }else if(retypePassword.isEmpty()||!retypePassword.equals(password)) {
                    et_repassword.setError("Password don't match");
                }else if(userType.equals("Select User Type")){
                    showToast("Choose user type");
                }else{
                    Dialog dialog = new Dialog(RegistrationActivity.this);
                    View view1 = getLayoutInflater().inflate(R.layout.loading_dialog, null);
                    TextView message = view1.findViewById(R.id.loading_message);
                    message.setText("Signing Up . . .");
                    dialog.setContentView(view1);
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                showToast("Registered Successfully!");
                                UserController.createUser(email, fullName, userType, new MyCompleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        dialog.dismiss();
                                        startActivity(new Intent(RegistrationActivity.this, Homepage.class));
                                        finish();
                                    }
                                    @Override
                                    public void onFailure() {
                                        showToast("Something went wrong!");
                                    }
                                });
                            }else{
                                showToast("Error");
                            }
                        }
                    });
                }
            }
        });
    }

    private void showToast(String text) {
        Toast.makeText(this, text,
                Toast.LENGTH_SHORT)
                .show();
    }
}