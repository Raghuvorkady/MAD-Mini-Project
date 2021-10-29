package com.projectx.spa.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.projectx.spa.R;
import com.projectx.spa.helpers.UserSession;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button login, availability;
    private ProgressBar progressBar;
    /*private FirebaseAuth fAuth;
    private FirebaseFirestore firestore;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        getSupportActionBar().hide();


        // Initialize Logger
        Logger.addLogAdapter(new AndroidLogAdapter());

        login = findViewById(R.id.login);
        availability = findViewById(R.id.check);
        progressBar = findViewById(R.id.progress);

        login.setOnClickListener(this);
        availability.setOnClickListener(this);

        /*fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        FbHelper fbHelper = new FbHelper(this);*/

        /*User user = new User("id", "testName", "testMail", "1213213", Timestamp.now());

        ParkingSlot parkingSlot = new ParkingSlot("id", "building", "address", "50", "20", Timestamp.now(), fbHelper.toDocumentReference("test/doc1"));

        DocumentReference doc = FirebaseFirestore.getInstance().collection("test").document("12345");

        // testing: https://firebase.google.com/docs/firestore/manage-data/add-data?authuser=0#increment_a_numeric_value
        for increment: FieldValue.increment(1)
        for decrement: FieldValue.increment(-1)
        doc.update("phoneNo", FieldValue.increment(-1)).addOnSuccessListener(s -> Logger.d("tag", "success"));

        String docId = "testId";*/

        /*fbHelper.addDataToFirestore(user, "test", "12345", new OnGetDataListener() {
            @Override
            public void onSuccess(DocumentReference dataSnapshotValue) {
                Logger.d("BOOL1", dataSnapshotValue.toString());
            }

            @Override
            public void onFailure(String str) {
                Logger.d("BOOL2", str);
            }
        });*/


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
            String docId = user.getUid();

            login.setVisibility(View.INVISIBLE);
        } else {
            // No user is signed in
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserSession userSession = new UserSession(this);
        if (userSession.isUserLoggedIn()) {
            login.setText("Vehicle Entry");
        } else {
            login.setText("Login");
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        login.setVisibility(View.INVISIBLE);
        availability.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        Intent intent = new Intent();
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