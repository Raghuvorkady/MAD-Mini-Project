package com.projectx.spa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button login,availability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login=findViewById(R.id.login);
        login.setOnClickListener(this);
        availability=findViewById(R.id.check);
        availability.setOnClickListener(this);
    }
    public void onClick(View v){
        if (v.equals(login))
        {
            Intent intent = new Intent(this,login.class);
            startActivity(intent);
        }
        else if (v.equals(availability))
        {
            Intent intent = new Intent(this,availability.class);
            startActivity(intent);
        }

    }
}