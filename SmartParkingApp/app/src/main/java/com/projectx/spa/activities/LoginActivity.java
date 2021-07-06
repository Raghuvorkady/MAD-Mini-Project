package com.projectx.spa.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.projectx.spa.R;
import com.projectx.spa.helpers.UserSession;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button logIn;
    private EditText emailEditText, passwordEditText;
    private TextView register, forgot;
    private FirebaseAuth fAuth;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userSession = new UserSession(this);
        Intent intent = new Intent();
        if (userSession.isUserLoggedIn()) {
            intent.setClass(this, VehicleEntry.class);
            startActivity(intent);
        }

        logIn = findViewById(R.id.login);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        register = findViewById(R.id.reg);
        forgot = findViewById(R.id.forgot);

        logIn.setOnClickListener(this);
        register.setOnClickListener(this);
        forgot.setOnClickListener(this);

        fAuth = FirebaseAuth.getInstance();
    }

    public void onClick(View v) {
        if (v.equals(register)) {
            startActivity(new Intent(this, RegisterActivity.class));
        } else if (v.equals(forgot)) {
            EditText resetMail = new EditText(v.getContext());
            resetMail.setHint("email");
            AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
            passwordResetDialog.setTitle("Reset password?");
            passwordResetDialog.setMessage("Enter your email to receive reset link");
            passwordResetDialog.setView(resetMail);

            passwordResetDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //extract email
                    String mail = resetMail.getText().toString();
                    fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            makeToast("Reset link sent to your email");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            makeToast("Error ! Reset Link is not sent" + e.getMessage());
                        }
                    });

                }
            });
            passwordResetDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //goto login page
                }
            });
            passwordResetDialog.create().show();
        } else if (v.equals(logIn)) {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                emailEditText.setError("Email is required.");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                passwordEditText.setError("password is required");
                return;
            }
            if (password.length() < 6) {
                passwordEditText.setError("password must be atleast 6 characters");
                return;
            }

            //authenticating data in firebase
            fAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                userSession.createUserLoginSession(email, password);

                                makeToast("Sign in successful");
                                startActivity(new Intent(getApplicationContext(), VehicleEntry.class));//add .class file of vehicle number entry
                                finish();
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