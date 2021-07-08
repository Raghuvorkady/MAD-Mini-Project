package com.projectx.spa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectx.spa.R;
import com.projectx.spa.helpers.Constants;
import com.projectx.spa.helpers.UserSession;
import com.projectx.spa.models.ParkingSlot;
import com.projectx.spa.models.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
            progressBar.setVisibility(View.VISIBLE);
            registerBtn.setVisibility(View.INVISIBLE);
            loginBtn.setVisibility(View.INVISIBLE);
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
            progressBar.setVisibility(View.INVISIBLE);
            registerBtn.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("password is required");
            progressBar.setVisibility(View.INVISIBLE);
            registerBtn.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
            return;
        }
        if (email.isEmpty() || !(Patterns.EMAIL_ADDRESS.matcher(email).matches())){
            this.emailEditText.setError("email is not proper");
            progressBar.setVisibility(View.INVISIBLE);
            registerBtn.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
            return;
        }
        if (TextUtils.isEmpty(name)) {
            this.nameEditText.setError("name is required");
            progressBar.setVisibility(View.INVISIBLE);
            registerBtn.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
            return;
        }
        if (TextUtils.isEmpty(place)) {
            buildingEditText.setError("phone building name is required");
            progressBar.setVisibility(View.INVISIBLE);
            registerBtn.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
            return;
        }
        if (TextUtils.isEmpty(avail)) {
            totalSpaceEditText.setError("Total slots is required");
            progressBar.setVisibility(View.INVISIBLE);
            registerBtn.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
            return;
        }
        if (password.length() < 6) {
            passwordEditText.setError("password must be atleast 6 characters");
            progressBar.setVisibility(View.INVISIBLE);
            registerBtn.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
            return;
        }

        // register in firebase
        fAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            makeToast("User created");
                            userId = fAuth.getCurrentUser().getUid();
                            DocumentReference userDocument = firestore.collection(Constants.USERS).document(userId);

                            User user = new User(userDocument.getId(), name, email,
                                    phone, Timestamp.now());

                            userDocument
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            Log.d("TAG", "onSuccess: user profile is created for " + userId);

                                            // parking data
                                            DocumentReference parkingSlotDocument = firestore.collection(Constants.PARKING_SLOTS).document();

                                            ParkingSlot parkingSlot = new ParkingSlot(parkingSlotDocument.getId(),
                                                    place, local, avail, avail, Timestamp.now(), userDocument);

                                            parkingSlotDocument
                                                    .set(parkingSlot)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            registerBtn.setVisibility(View.VISIBLE);
                                                            loginBtn.setVisibility(View.VISIBLE);
                                                            Log.d("TAG", "onSuccess: user profile is created for " + userId);
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            registerBtn.setVisibility(View.VISIBLE);
                                                            loginBtn.setVisibility(View.VISIBLE);
                                                            Log.d("TAG", "onFailure: " + e.toString());
                                                        }
                                                    });

                                            userSession.createUserLoginSession(email, password);

                                            // add .class file of vehicle number entry
                                            startActivity(new Intent(getApplicationContext(), VehicleEntry.class));
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("TAG", "onFailure: " + e.toString());
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
        progressBar.setVisibility(View.INVISIBLE);
        registerBtn.setVisibility(View.VISIBLE);
        loginBtn.setVisibility(View.VISIBLE);
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }
}