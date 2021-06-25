package com.projectx.spa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.projectx.spa.R;
import com.projectx.spa.models.ParkingSlot;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class CheckAvailability extends AppCompatActivity {
    private final String COLLECTIONS = "parking-spaces";
    TextView textView;
    TextView placeName;
    TextView landmark;
    TextView totalSpace;
    TextView availableSpace;
    private FirebaseFirestore firebaseFirestore;
    private ParkingSlot parkingSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_availability);

        textView = findViewById(R.id.data);
        placeName = findViewById(R.id.placeName);
        landmark = findViewById(R.id.landmark);
        totalSpace = findViewById(R.id.totalSpace);
        availableSpace = findViewById(R.id.availableSpace);

        firebaseFirestore = FirebaseFirestore.getInstance();

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
        textView.setText(""); // to clear the existing text
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
                                    setCard();
                                    textView.append(parkingSlot.toString() + "\n");
                                } else makeToast("doc does not exist");
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    //    to set the Available Spaces Card
    private void setCard() {
        placeName.setText(parkingSlot.getLocation());
        landmark.setText(parkingSlot.getLandmark());
        totalSpace.setText("Total Space: " + parkingSlot.getTotalSpace());
        availableSpace.setText("Available Space: " + parkingSlot.getAvailableSpace());
    }

    //    for next button action
    public void next(View view) {
        Intent intent = new Intent(this, SerializationTest.class);
        intent.putExtra("key", parkingSlot);
        startActivity(intent);
    }

    //    for creating Toast
    private void makeToast(String toastMessage) {
        Toast.makeText(CheckAvailability.this, toastMessage, Toast.LENGTH_SHORT).show();
    }
}