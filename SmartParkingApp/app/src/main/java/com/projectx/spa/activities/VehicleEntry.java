package com.projectx.spa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.projectx.spa.R;
import com.projectx.spa.helpers.Constants;
import com.projectx.spa.helpers.UserSession;

import androidx.appcompat.app.AppCompatActivity;

public class VehicleEntry extends AppCompatActivity implements View.OnClickListener {

    boolean isLogoutPressed = false;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_entry);

        TextView textView = findViewById(R.id.test_textView);

        Button logout = findViewById(R.id.logout);

        logout.setOnClickListener(this);

        userSession = new UserSession(this);

        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("USER", user.getDisplayName());
        String name = "";
        if (user != null) {
            name = user.getDisplayName();
        }*/

        textView.setText("Hey, " + userSession.getUserDetails().get(Constants.PREF_EMAIL));
//        textView.setText("Hey, " + name);
    }

    @Override
    public void onClick(View v) {
        isLogoutPressed = true;

        FirebaseAuth.getInstance().signOut();
        userSession.clearUserData();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

        // Closing all the Activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add new Flag to start new Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (isLogoutPressed) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Press the logout button to go back", Toast.LENGTH_SHORT).show();
        }
    }
}