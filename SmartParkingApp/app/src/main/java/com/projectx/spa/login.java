package com.projectx.spa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity implements View.OnClickListener {
    Button log_in;
    EditText email,password;
    TextView register;
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

        fAuth=FirebaseAuth.getInstance();
    }
    public void onClick(View v){
        if(v.equals(register)) {
            startActivity(new Intent(getApplicationContext(), register.class));
        }
    }
}