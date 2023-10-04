package com.project.learningbuddy.useractivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.learningbuddy.R;
import com.project.learningbuddy.firebase.UserController;
import com.project.learningbuddy.listener.UserDataListener;
import com.project.learningbuddy.model.User;
import com.project.learningbuddy.ui.auth.LoginActivity;
import com.project.learningbuddy.ui.student.FragmentClassesStudent;
import com.project.learningbuddy.ui.teacher.FragmentClassesTeacher;
import com.project.learningbuddy.ui.teacher.filespace.FileSpaceActivity;
import com.project.learningbuddy.ui.teacher.filespace.FragmentLearningMaterials;

import org.checkerframework.checker.nullness.qual.NonNull;

public class Homepage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

//    public static final String FULLNAME = "fullName";
//    public static final String EMAIL = "email";
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

//        Initialization
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Dialog dialog = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.loading_dialog, null);
        TextView message = view.findViewById(R.id.loading_message);
        message.setText("Loading . . .");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.show();

//        Getting user data
        UserController.getUserData(firebaseAuth.getCurrentUser().getUid(), new UserDataListener() {
            @Override
            public void onSuccess(User user) {
                // Handle successful user data retrieval
                String fullName = user.getFullName();
                String email = user.getEmail();

                Log.d("TAG", user.getFullName() + user.getEmail() + user.getUserType());
                Log.d("TAG", firebaseAuth.getCurrentUser().getUid());

                displayUserNavHeader(fullName, email);
            }

            @Override
            public void onFailure(Exception exception) {
                // Handle error
            }
        });

        drawer = findViewById(R.id.teacher_drawer_Layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.nav_open_drawer, R.string.nav_close_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null){
//            openFragment(new FragmentHome());
//            navigationView.setCheckedItem(R.id.nav_home);
            checkUserType();
            navigationView.setCheckedItem(R.id.nav_classes);
        }
        dialog.dismiss();
    }

    private void displayUserNavHeader(String fullName, String email) {
        View headerView = navigationView.getHeaderView(0);
        TextView tv_fullName, tv_email;

        tv_fullName = headerView.findViewById(R.id.user_name);
        tv_email = headerView.findViewById(R.id.user_email);

        tv_fullName.setText(fullName);
        tv_email.setText(email);
    }


    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
//            case R.id.nav_home:
////                openFragment(new FragmentHome());
//                checkUserType();
//                break;
            case R.id.nav_profile:
                openFragment(new FragmentProfile());
                break;
            case R.id.nav_classes:
                checkUserType();
                break;
            case R.id.nav_feedBack:
                openFragment(new FragmentFeedback());
                break;
            case R.id.nav_logOut:
                logOut();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    private void checkUserType() {
        UserController.getUserData(FirebaseAuth.getInstance().getCurrentUser().getUid(), new UserDataListener() {
            @Override
            public void onSuccess(User user) {
                if(user.getUserType().equals("Student")){
                    openFragment(new FragmentClassesStudent());
                }else if(user.getUserType().equals("Teacher")){
                    openFragment(new FragmentClassesTeacher());
                }
            }

            @Override
            public void onFailure(Exception exception) {
                Log.d("TAG", "There's a problem on fetching userType of the user in the Homepage file.");
            }
        });
    }

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragment).commit();
    }
}
