package com.projectx.spa.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.projectx.spa.R;
import com.projectx.spa.adapters.ParkingSpacesCardAdapter;
import com.projectx.spa.models.ParkingSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class CheckAvailability extends AppCompatActivity {
    private final String COLLECTIONS = "parking-spaces";
    private FirebaseFirestore firebaseFirestore;
    private ParkingSlot parkingSlot;

    private List<ParkingSlot> parkingSlots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_availability);

        firebaseFirestore = FirebaseFirestore.getInstance();

        parkingSlots = new ArrayList<>();
        /*DocumentReference docRef = db.collection("cities").document("LAB");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                City city = documentSnapshot.toObject(City.class);
                textView.setText(city.toString());
            }
        });*/
    }

    //    to add data (custom class object) to the firebase firestore
    public void addData(View view) {
        Random random = new Random();
        UUID uuid = UUID.randomUUID();
        ParkingSlot slot = new ParkingSlot(
                uuid,
                "City Centre-" + random.nextInt(10),
                "Bengaluru-" + random.nextInt(10),
                random.nextInt(20),
                random.nextInt(10),
                Timestamp.now());

        DocumentReference documentReference = firebaseFirestore.collection(COLLECTIONS).document();
        // db.collection(COLLECTIONS).document("area1").set(slot);
        documentReference.set(slot).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                makeToast("Success");
            }
        });
    }

    //    to read data from the firebase firestore
    public void read(View view) {
        parkingSlots.clear(); // clear existing data from the list
        firebaseFirestore.collection(COLLECTIONS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    Map<String, Object> data = document.getData();
                                    Log.d("TAG", document.getId() + " => " + data);
                                    // creating ParkingSlot object
                                    parkingSlot = document.toObject(ParkingSlot.class);
                                    parkingSlots.add(parkingSlot);
                                    makeToast("data read successfully");
                                } else makeToast("doc does not exist");
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
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