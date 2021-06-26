package com.projectx.spa.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.projectx.spa.models.ParkingSlot;

import java.util.ArrayList;
import java.util.List;

public class FBHelper {
    private final String COLLECTIONS = "parking-spaces";
    private final FirebaseFirestore firebaseFirestore;
    private final Context context;

    public FBHelper(Context context) {
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.context = context;
    }

    public FirebaseFirestore getFirebaseFirestore() {
        return firebaseFirestore;
    }

    public void addDataToFirestore(ParkingSlot parkingSlot) {
        DocumentReference documentReference = firebaseFirestore.collection(COLLECTIONS).document();
        // db.collection(COLLECTIONS).document("area1").set(slot);
        documentReference
                .set(parkingSlot)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i("TAG1", "Data added successfully");
                        makeToast("Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG2", "Data could not be added successfully");
                        makeToast("Failure");
                    }
                });
    }

    public List<ParkingSlot> readDataFromFirestore() {
        List<ParkingSlot> parkingSlots = new ArrayList<>();
        firebaseFirestore.collection(COLLECTIONS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    // creating ParkingSlot object
                                    ParkingSlot parkingSlot = document.toObject(ParkingSlot.class);
                                    parkingSlots.add(parkingSlot);
                                    Log.i("TAG3", "Data read successfully");
                                    makeToast("Data read successfully");
                                } else {
                                    Log.i("TAG4", "Data does not exist");
                                    makeToast("doc does not exist");
                                }
                            }
                        } else {
                            Log.w("TAG5", "Error getting documents.", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG6", "Data could not be added successfully");
                    }
                });
        return parkingSlots;
    }


    private void makeToast(String toastMessage) {
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    }

    /*DocumentReference docRef = db.collection("cities").document("LAB");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                City city = documentSnapshot.toObject(City.class);
                textView.setText(city.toString());
            }
        });*/
}
