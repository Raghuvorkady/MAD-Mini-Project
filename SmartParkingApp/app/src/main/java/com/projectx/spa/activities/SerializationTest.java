package com.projectx.spa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.projectx.spa.R;
import com.projectx.spa.models.ParkingSlot;

public class SerializationTest extends AppCompatActivity {
    ParkingSlot parkingSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        TextView textView = findViewById(R.id.text);

        Intent intent = getIntent();
        parkingSlot = (ParkingSlot) intent.getSerializableExtra("key");

        textView.setText(parkingSlot.toString());

    }

//    trying to deserialize TimeStamp object
    public void readTime(View view) {
        //Timestamp timestamp = new Timestamp(new Date());
        String time = parkingSlot.getCreatedTime();
        long seconds = Long.parseLong(time.substring(18, 28));
        int nanoseconds = Integer.parseInt(time.substring(42, 51));
        makeToast(seconds + "\n" + nanoseconds);
        Timestamp timestamp = new Timestamp(seconds, nanoseconds);
        makeToast(timestamp.toDate() + "");
    }

    private void makeToast(String toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }
}