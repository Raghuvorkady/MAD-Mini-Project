package com.projectx.spa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectx.spa.R;
import com.projectx.spa.helpers.Constants;
import com.projectx.spa.helpers.FBHelper;
import com.projectx.spa.helpers.UserSession;
import com.projectx.spa.interfaces.OnGetDataListener;
import com.projectx.spa.models.ParkingSlot;
import com.projectx.spa.models.User;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button registerBtn;
    private EditText nameEditText, emailEditText, phoneEditText, passwordEditText, buildingEditText, areaEditText, totalSpaceEditText;
    private TextView loginBtn;
    private FirebaseAuth fAuth;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private String userId;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerBtn = findViewById(R.id.register);
        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        phoneEditText = findViewById(R.id.phone);
        passwordEditText = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginRoute);
        buildingEditText = findViewById(R.id.building);
        areaEditText = findViewById(R.id.area);
        totalSpaceEditText = findViewById(R.id.slots);
        progressBar = findViewById(R.id.progressIndicator);

        registerBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);

        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, HomeActivity.class));//add .class file of vehicle number entry
            finish();
        }

        userSession = new UserSession(this);
    }

    public void onClick(View v) {
        if (v.equals(loginBtn)) {
            // finish() will destroy this Activity and navigate it back to LoginActivity
            finish();
        }
        if (v.equals(registerBtn)) {
            userRegistration();
        }
    }

    private void userRegistration() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String name = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String place = buildingEditText.getText().toString();
        String local = areaEditText.getText().toString();
        String avail = totalSpaceEditText.getText().toString();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required.");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("password is required");
            return;
        }
        if (TextUtils.isEmpty(name)) {
            this.nameEditText.setError("name is required");
            return;
        }
        if (TextUtils.isEmpty(place)) {
            buildingEditText.setError("phone building name is required");
            return;
        }
        if (TextUtils.isEmpty(avail)) {
            totalSpaceEditText.setError("Total slots is required");
            return;
        }
        if (password.length() < 6) {
            passwordEditText.setError("password must be atleast 6 characters");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        registerBtn.setVisibility(View.INVISIBLE);
        loginBtn.setVisibility(View.INVISIBLE);
        // register in firebase
        fAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            makeToast("User created");
                            userId = fAuth.getCurrentUser().getUid();

                            FBHelper fbHelper = new FBHelper(getApplicationContext());

                            User user = new User(null, name, email, phone, Timestamp.now());

                            fbHelper.addDataToFirestore(user, Constants.USERS, userId, new OnGetDataListener() {
                                @Override
                                public void onSuccess(DocumentReference userDocument) {
                                    Log.d("REG", userDocument.toString());

                                    ParkingSlot parkingSlot = new ParkingSlot(null,
                                            place, local, avail, avail, Timestamp.now(), userDocument);

                                    fbHelper.addDataToFirestore(parkingSlot, Constants.PARKING_SLOTS, userId, new OnGetDataListener() {
                                        @Override
                                        public void onSuccess(DocumentReference dataSnapshotValue) {
                                            Log.d("BOOL2", "Data added successfully");

                                            userSession.createUserLoginSession(name, email);

                                            Intent intent = new Intent(getApplicationContext(), VehicleEntry.class);
                                            intent.putExtra("user", user);
                                            startActivity(intent);//add .class file of vehicle number entry

                                            finish();
                                        }

                                        @Override
                                        public void onFailure(String str) {
                                            Log.d("BOOL2", str);
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(String str) {
                                    Log.d("REG", str);
                                }
                            });
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            registerBtn.setVisibility(View.VISIBLE);
                            loginBtn.setVisibility(View.VISIBLE);
                            makeToast("Error !! " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void makeToast(String toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }
}