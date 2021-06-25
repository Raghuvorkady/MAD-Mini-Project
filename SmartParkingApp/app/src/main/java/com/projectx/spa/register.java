package com.projectx.spa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class register extends AppCompatActivity implements View.OnClickListener {
    Button register;
    EditText name,email,phone,password;
    TextView loginbtn;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register = findViewById(R.id.register);
        register.setOnClickListener(this);
        name = findViewById(R.id.Name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        loginbtn = findViewById(R.id.login1);
        loginbtn.setOnClickListener(this);

        fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));//add .class file of vehicle number entry
            finish();
        }

    }
    public void onClick(View v ) {
        if(v.equals(loginbtn)){
            startActivity(new Intent(getApplicationContext(),login.class));
        }
        if (v.equals(register)){
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


                // register in firebase
            fAuth.createUserWithEmailAndPassword(emails,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(register.this,"User created", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),vehicle_entry.class));//add .class file of vehicle number entry
                    }
                    else{
                        Toast.makeText(register.this,"Error !!"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
            );
        }
    }



}