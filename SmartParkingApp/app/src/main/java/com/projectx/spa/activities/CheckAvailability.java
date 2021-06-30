package com.projectx.spa.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectx.spa.R;
import com.projectx.spa.adapters.ParkingSpacesCardAdapter;
import com.projectx.spa.helpers.FBHelper;
import com.projectx.spa.models.ParkingSlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class CheckAvailability extends AppCompatActivity {
    private List<ParkingSlot> parkingSlots;
    private FBHelper fbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_availability);

        fbHelper = new FBHelper(this);

        parkingSlots = new ArrayList<>();
    }

    //    to add data (custom class object) to the firebase firestore
    public void addData(View view) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "rkm");
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference reference = firebaseFirestore.collection("test").document("doc1");
        map.put("ref", reference);
        reference
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        makeToast("Success");
                    }
                });

        Random random = new Random();
        UUID uuid = UUID.randomUUID();
        ParkingSlot parkingSlot = new ParkingSlot(
                uuid,
                "City Centre-" + random.nextInt(10),
                "Bengaluru-" + random.nextInt(10),
                random.nextInt(20),
                random.nextInt(10),
                Timestamp.now());

        fbHelper.addDataToFirestore(parkingSlot);
    }

    //    to read data from the firebase firestore
    public void read(View view) {
        parkingSlots.clear(); // clear existing data from the list
        parkingSlots = fbHelper.readDataFromFirestore();
    }

    //    for next button action
    public void updateList(View view) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ParkingSpacesCardAdapter parkingSpacesCardAdapter = new ParkingSpacesCardAdapter(this, parkingSlots);
        recyclerView.setAdapter(parkingSpacesCardAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    //    for creating Toast
    private void makeToast(String toastMessage) {
        Toast.makeText(CheckAvailability.this, toastMessage, Toast.LENGTH_SHORT).show();
    }
}