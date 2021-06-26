package com.projectx.spa;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private Button register;
    private EditText name, email, phone, password, building, area, totalSpace;
    private TextView loginBtn;
    private FirebaseAuth fAuth;
    private FirebaseFirestore firestore;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register = findViewById(R.id.register);
        name = findViewById(R.id.Name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.login1);
        building = findViewById(R.id.building);
        area = findViewById(R.id.area);
        totalSpace = findViewById(R.id.slots);

        register.setOnClickListener(this);
        loginBtn.setOnClickListener(this);

        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));//add .class file of vehicle number entry
            finish();
        }
    }

    public void onClick(View v) {
        if (v.equals(loginBtn)) {
            startActivity(new Intent(getApplicationContext(), Login.class));
        }
        if (v.equals(register)) {
            String emails = email.getText().toString().trim();
            String pwd = password.getText().toString().trim();
            String name = this.name.getText().toString();
            String phone = this.phone.getText().toString();
            String place = building.getText().toString();
            String local = area.getText().toString();
            String avail = totalSpace.getText().toString();

            if (TextUtils.isEmpty(emails)) {
                email.setError("Email is required.");
                return;
            }
            if (TextUtils.isEmpty(pwd)) {
                password.setError("password is required");
                return;
            }
            if (TextUtils.isEmpty(name)) {
                this.name.setError("name is required");
                return;
            }
            if (TextUtils.isEmpty(place)) {
                building.setError("phone building name is required");
                return;
            }
            if (TextUtils.isEmpty(avail)) {
                totalSpace.setError("Total slots is required");
                return;
            }
            if (pwd.length() < 6) {
                password.setError("password must be atleast 6 characters");
                return;
            }

            // register in firebase
            fAuth.createUserWithEmailAndPassword(emails, pwd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                makeToast("User created");
                                userid = fAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = firestore.collection("users").document(userid);

                                Map<String, Object> user = new HashMap<>();
                                user.put("name", name);
                                user.put("email", emails);
                                user.put("phone_no", phone);

                                documentReference
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("TAG", "onSuccess: user profile is created for " + userid);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("TAG", "onFailure: " + e.toString());
                                            }
                                        });

                                // parking data
                                DocumentReference documentReference1 = firestore.collection("parking-data").document(userid);

                                Map<String, Object> data = new HashMap<>();
                                data.put("authorizer", name);
                                data.put("building", place);
                                data.put("address", local);
                                data.put("totalSlots", avail);

                                documentReference1
                                        .set(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("TAG", "onSuccess: user profile is created for " + userid);
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