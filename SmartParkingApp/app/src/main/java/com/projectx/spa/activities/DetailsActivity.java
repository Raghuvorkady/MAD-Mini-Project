package com.projectx.spa.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectx.spa.R;
import com.projectx.spa.helpers.Constants;
import com.projectx.spa.helpers.FbHelper;
import com.projectx.spa.helpers.UserSession;
import com.projectx.spa.interfaces.OnGetDataListener;
import com.projectx.spa.models.ParkedVehicle;
import com.santalu.maskara.widget.MaskEditText;

public class DetailsActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    MaskEditText maskEditText;
    String vehicleNumber;
    String availableSpace, id;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        id = new UserSession(this).getUserDetails().get(Constants.PREF_ID);
        Log.d(TAG, id);
        // Log is recommended

        DocumentReference docRef = firebaseFirestore.collection(Constants.PARKING_SLOTS).document(id);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        availableSpace = documentSnapshot.get("availableSpace").toString();
                        Log.d(TAG, "avail=" + availableSpace);
                    }
                });

        maskEditText = findViewById(R.id.vehicle_edit_text);
        /*e1.setFilters(new InputFilter[] {new InputFilter.AllCaps()});*/
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void inPage(View view) {
        if (Integer.parseInt(availableSpace) > 0) {
            vehicleNumber = maskEditText.getText().toString();
            if (!vehicleNumber.equals("AA-00-BB-1111")) {
                System.out.println(vehicleNumber);
                // Intent it = new Intent(this, AdminHomeActivity.class);
                if ((vehicleNumber.matches("^[A-Z]{2}[-][0-9]{2}[-][A-Z]{2}[-][0-9]{4}$"))) {
                    maskEditText.setText("");
                    Log.d(TAG, vehicleNumber);
                    new AlertDialog.Builder(this)
                            .setTitle("Insert entry")
                            .setMessage("Are you sure you want to insert " + vehicleNumber + "?")
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                ParkedVehicle parkedVehicle = new ParkedVehicle(null, vehicleNumber, Timestamp.now());
                                FbHelper fbHelper = new FbHelper(getApplicationContext());
                                String collectionReference = Constants.PARKING_SLOTS + "/" + id + "/" + Constants.PARKED_VEHICLES;
                                fbHelper.addDataToFirestore(parkedVehicle, collectionReference, null, new OnGetDataListener() {
                                    @Override
                                    public void onSuccess(DocumentReference dataSnapshotValue) {
                                        Toast.makeText(DetailsActivity.this, "added successfully", Toast.LENGTH_LONG).show();
                                        int updatedAvailableSpace = Integer.parseInt(availableSpace) - 1;
                                        firebaseFirestore.collection(Constants.PARKING_SLOTS).document(id).update("availableSpace", updatedAvailableSpace);
                                        // startActivity(it);
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(String str) {
                                        Log.w(TAG, "Error adding document " + str);
                                    }
                                });
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    Log.d(TAG, "wrong");
                    show("wrong format");
                }
            }
        } else {
            Log.d(TAG, "no space available");
        }
    }

    private void show(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
}