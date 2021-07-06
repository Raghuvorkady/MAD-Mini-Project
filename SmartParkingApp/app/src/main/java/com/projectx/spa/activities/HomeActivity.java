package com.projectx.spa.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectx.spa.R;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button login, availability;
    private ProgressBar progressBar;
    private FirebaseAuth fAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        login = findViewById(R.id.login);
        availability = findViewById(R.id.check);
        progressBar= findViewById(R.id.progress);

        login.setOnClickListener(this);
        availability.setOnClickListener(this);

        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // can be used to either check whether the user has logged in or not
        // alternative way is to use SharedPref

        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();

            login.setVisibility(View.INVISIBLE);
        } else {
            // No user is signed in
        }*/
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        login.setVisibility(View.INVISIBLE);
        availability.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        switch (view.getId()) {

            case R.id.login:
                intent.setClass(this, LoginActivity.class);
                startActivity(intent);

                break;
            case R.id.check:
                intent.setClass(this, AvailableSlotsActivity.class);
                startActivity(intent);


                break;
        }
        login.setVisibility(View.VISIBLE);
        availability.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }
}