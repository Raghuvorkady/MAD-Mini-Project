package com.projectx.spa.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.projectx.spa.R;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button login, availability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        login = findViewById(R.id.login);
        availability = findViewById(R.id.check);

        login.setOnClickListener(this);
        availability.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.login:
                intent.setClass(this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.check:
                intent.setClass(this, AvailableSlotsActivity.class);
                startActivity(intent);
                break;
        }
    }
}