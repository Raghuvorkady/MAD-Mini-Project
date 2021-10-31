package com.projectx.spa.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.orhanobut.logger.Logger;
import com.projectx.spa.R;
import com.projectx.spa.helpers.Constants;
import com.projectx.spa.helpers.FbHelper;
import com.projectx.spa.helpers.UserSession;
import com.projectx.spa.interfaces.OnGetDataListener;
import com.projectx.spa.interfaces.OnSnapshotListener;
import com.projectx.spa.models.ParkedVehicle;
import com.santalu.maskara.widget.MaskEditText;

import java.util.HashMap;
import java.util.Map;

public class VehicleEntryActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView in;
    private ProgressBar wait;
    private MaskEditText maskEditText;

    private String vehicleNumber;
    private int availableSpace;
    private String userId;

    private FbHelper fbHelper;
    private String parkingSlotDocumentStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_entry);
//        getSupportActionBar().setTitle("Vehicle Entry");

        wait = findViewById(R.id.waiting);
        in = findViewById(R.id.in_card);
        maskEditText = findViewById(R.id.vehicle_edit_text);

        in.setOnClickListener(this);

        fbHelper = new FbHelper(this);

        userId = new UserSession(this).getUserDetails().get(Constants.PREF_ID);
        Logger.d(userId);

        parkingSlotDocumentStr = Constants.PARKING_SLOTS + "/" + userId;
        DocumentReference parkingSlotDocument = fbHelper.toDocumentReference(parkingSlotDocumentStr);
        parkingSlotDocument
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        availableSpace = Integer.parseInt(documentSnapshot.get("availableSpace").toString());
                        Logger.d("avail=" + availableSpace);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Logger.d("failed " + e.getMessage());
                    }
                });

        /*e1.setFilters(new InputFilter[] {new InputFilter.AllCaps()});*/
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onClick(View view) {
        if (view.equals(in)) {
            if (availableSpace > 0) {
                vehicleNumber = maskEditText.getText().toString();
                if (!vehicleNumber.equals("AA-00-BB-1111")) {
                    Logger.d(vehicleNumber);
                    // Intent it = new Intent(this, AdminHomeActivity.class);
                    if ((vehicleNumber.matches("^[A-Z]{2}[-][0-9]{2}[-][A-Z]{2}[-][0-9]{4}$"))) {
                        maskEditText.setText("");
                        Logger.d(vehicleNumber);
                        new AlertDialog.Builder(this)
                                .setTitle("Insert entry")
                                .setMessage("Are you sure you want to insert " + vehicleNumber + "?")
                                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                    in.setVisibility(View.INVISIBLE);
                                    wait.setVisibility(View.VISIBLE);

                                    ParkedVehicle parkedVehicle = new ParkedVehicle(null, vehicleNumber, Timestamp.now());

                                    String collectionReference = Constants.PARKING_SLOTS + "/" + userId + "/" + Constants.PARKED_VEHICLES;

                                    fbHelper.addDataToFirestore(parkedVehicle, collectionReference, null,
                                            new OnGetDataListener() {
                                                @Override
                                                public void onSuccess(DocumentReference dataSnapshotValue) {
                                                    Toast.makeText(VehicleEntryActivity.this, "added successfully", Toast.LENGTH_LONG).show();
                                                    int updatedAvailableSpace = availableSpace - 1;

                                                    Map<String, Object> map = new HashMap<>();
                                                    map.put("availableSpace", updatedAvailableSpace);
                                                    fbHelper.updateField(parkingSlotDocumentStr, map, new OnSnapshotListener() {
                                                        @Override
                                                        public <T> void onSuccess(T object) {
                                                            makeToast(object.toString());
                                                            finish();
                                                        }

                                                        @Override
                                                        public void onFailure(String errorMessage) {
                                                            makeToast(errorMessage);
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onFailure(String str) {
                                                    Logger.e("Error adding document " + str);
                                                }
                                            });
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .show();
                    } else {
                        Logger.e("wrong");
                        makeToast("wrong format");
                    }
                }
            } else {
                Logger.d("no space available");
            }
        }
    }

    private void makeToast(String toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
    }
}