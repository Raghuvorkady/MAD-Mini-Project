package com.projectx.spa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity implements View.OnClickListener {
    Button log_in;
    EditText email,password;
    TextView register;
    TextView forgot;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        log_in=findViewById(R.id.login);
        log_in.setOnClickListener(this);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        register=findViewById(R.id.reg);
        register.setOnClickListener(this);
        forgot=findViewById(R.id.forgot);
        forgot.setOnClickListener(this);

        fAuth=FirebaseAuth.getInstance();
    }
    public void onClick(View v){
        if(v.equals(register)) {
            startActivity(new Intent(getApplicationContext(), register.class));
        }
        else if(v.equals(forgot)){
            EditText resetmail = new EditText(v.getContext());
            AlertDialog.Builder passwordresetDialog = new AlertDialog.Builder(v.getContext());
            passwordresetDialog.setTitle("Reset password?");
            passwordresetDialog.setMessage("Enter your email to recieve reset link");
            passwordresetDialog.setView(resetmail);

            passwordresetDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //extract email
                    String mail=resetmail.getText().toString();
                    fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(login.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(login.this, "Error ! Reset Link is not sent"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
            passwordresetDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //goto login page

                }
            });
            passwordresetDialog.create().show();
        }
        else if(v.equals(log_in)){
            String emails=email.getText().toString().trim();
            String pwd=password.getText().toString().trim();

            if (TextUtils.isEmpty(emails)){
                email.setError("Email is required.");
                return;
            }
            if(TextUtils.isEmpty(pwd)){
                password.setError("password is required");
                return;
            }
            if (pwd.length()<6){
                password.setError("password must be atleast 6 characters");
                return;
            }


            //authenticating data in firebase

            fAuth.signInWithEmailAndPassword(emails,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(login.this,"Sign in successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),vehicle_entry.class));//add .class file of vehicle number entry
                    }
                    else{
                        Toast.makeText(login.this,"Error !!"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }

                }
            });


        }
    }
}