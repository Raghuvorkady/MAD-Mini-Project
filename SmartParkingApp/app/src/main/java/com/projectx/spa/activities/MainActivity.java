package com.projectx.spa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.projectx.spa.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button login, availability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.login);
        availability = findViewById(R.id.check);

        login.setOnClickListener(this);
        availability.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.equals(login)) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        } else if (v.equals(availability)) {
            Intent intent = new Intent(this, Availability.class);
            startActivity(intent);
        }
    }
}