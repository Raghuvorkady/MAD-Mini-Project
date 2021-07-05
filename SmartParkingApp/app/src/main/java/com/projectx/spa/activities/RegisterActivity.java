package com.projectx.spa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.projectx.spa.models.ParkingSlot;
import com.projectx.spa.models.User;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button registerBtn;
    private EditText nameEditText, emailEditText, phoneEditText, passwordEditText, buildingEditText, areaEditText, totalSpaceEditText;
    private TextView loginBtn;
    private FirebaseAuth fAuth;
    private FirebaseFirestore firestore;
    private String userId;

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

        registerBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);

        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, HomeActivity.class));//add .class file of vehicle number entry
            finish();
        }
    }

    public void onClick(View v) {
        if (v.equals(loginBtn)) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        if (v.equals(registerBtn)) {
            String email = emailEditText.getText().toString().trim();
            String pwd = passwordEditText.getText().toString().trim();
            String name = this.nameEditText.getText().toString();
            String phone = this.phoneEditText.getText().toString();
            String place = buildingEditText.getText().toString();
            String local = areaEditText.getText().toString();
            String avail = totalSpaceEditText.getText().toString();

            if (TextUtils.isEmpty(email)) {
                emailEditText.setError("Email is required.");
                return;
            }
            if (TextUtils.isEmpty(pwd)) {
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
            if (pwd.length() < 6) {
                passwordEditText.setError("password must be atleast 6 characters");
                return;
            }

            // register in firebase
            fAuth.createUserWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                makeToast("User created");
                                userId = fAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = firestore.collection(Constants.USERS).document(userId);

                                User user = new User(
                                        documentReference.getId(),
                                        name,
                                        email,
                                        phone,
                                        Timestamp.now());

                                documentReference
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("TAG", "onSuccess: user profile is created for " + userId);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("TAG", "onFailure: " + e.toString());
                                            }
                                        });

                                // parking data
                                DocumentReference parkingSlotDocument = firestore.collection(Constants.PARKING_SLOTS).document(userId);

                                ParkingSlot parkingSlot = new ParkingSlot(
                                        parkingSlotDocument.getId(),
                                        place,
                                        local,
                                        avail,
                                        avail,
                                        Timestamp.now(),
                                        documentReference
                                );
                                parkingSlotDocument
                                        .set(parkingSlot)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("TAG", "onSuccess: user profile is created for " + userId);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("TAG", "onFailure: " + e.toString());
                                            }
                                        });

                                startActivity(new Intent(getApplicationContext(), VehicleEntry.class));//add .class file of vehicle number entry
                            } else {
                                makeToast("Error !!" + task.getException().getMessage());
                            }
                        }
                    });
        }
    }

    private void makeToast(String toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }
}