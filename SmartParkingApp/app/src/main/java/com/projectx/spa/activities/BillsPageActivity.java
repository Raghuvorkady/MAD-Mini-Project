package com.projectx.spa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orhanobut.logger.Logger;
import com.projectx.spa.R;
import com.projectx.spa.helpers.Constants;
import com.projectx.spa.helpers.FbHelper;
import com.projectx.spa.helpers.UserSession;
import com.projectx.spa.interfaces.OnSnapshotListener;
import com.projectx.spa.models.ParkedHistory;
import com.projectx.spa.models.ParkedVehicle;
import com.projectx.spa.models.ParkingSlot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BillsPageActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    private TextView vehicleNumberTextView;
    private TextView entryTimeTextView;
    private TextView exitTimeTextView;
    private TextView amountTextView;

    private int availableSpace;
    private int totalSpace;
    private String vehicleNumber;
    private String userId;

    private FbHelper fbHelper;
    private ParkingSlot parkingSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bills_page);
        getSupportActionBar().setTitle("Bill");

        vehicleNumberTextView = findViewById(R.id.bill_activity_vehicle_number);
        entryTimeTextView = findViewById(R.id.bill_activity_entry_time);
        exitTimeTextView = findViewById(R.id.bill_activity_exit_time);
        amountTextView = findViewById(R.id.amount);

        Intent intent = getIntent();
        vehicleNumber = intent.getStringExtra(Constants.VEHICLE_NUMBER);

        vehicleNumberTextView.setText(vehicleNumber);

        String vehicleId = intent.getStringExtra(Constants.VEHICLE_ID);
        userId = new UserSession(this).getUserDetails().get(Constants.PREF_ID);


        fbHelper = new FbHelper(this);

        String parkingSlotDocumentPath = Constants.PARKING_SLOTS + "/" + userId;
        String vehicleDocumentPath = Constants.PARKING_SLOTS + "/" + userId + "/" + Constants.PARKED_VEHICLES + "/" + vehicleId;

        fbHelper.readDocumentFromFirestore(ParkingSlot.class, parkingSlotDocumentPath, new OnSnapshotListener() {
            @Override
            public <T> void onSuccess(T object) {
                parkingSlot = (ParkingSlot) object;
                availableSpace = parkingSlot.getAvailableSpace();
                totalSpace = parkingSlot.getTotalSpace();
                Logger.d("avail=" + availableSpace);
                Logger.d("total=" + totalSpace);

                updateFireStore(vehicleDocumentPath);
            }

            @Override
            public void onFailure(String errorMessage) {
                Logger.d(errorMessage);
            }
        });
    }

    private void updateFireStore(String vehicleDocumentPath) {
        if (availableSpace <= totalSpace) {

            int val = availableSpace + 1;

            DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");

            Timestamp exitTime = Timestamp.now();

            fbHelper.readDocumentFromFirestore(ParkedVehicle.class, vehicleDocumentPath, new OnSnapshotListener() {
                @Override
                public <T> void onSuccess(T object) {
                    ParkedVehicle parkedVehicle = (ParkedVehicle) object;

                    Date entryDate = parkedVehicle.getEntryTime().toDate();
                    entryTimeTextView.append(" " + dateFormat.format(entryDate));
                    exitTimeTextView.append(" " + dateFormat.format(exitTime.toDate()));

                    FirebaseFirestore.getInstance().collection(Constants.PARKING_SLOTS).document(userId).update("availableSpace", val);
                    Logger.d("updated successfully");

                    long time_difference = exitTime.toDate().getTime() - entryDate.getTime();
                    long minutes_difference = (time_difference / 1000) / 60;
                    int amountPaid = (int) Math.ceil(minutes_difference * 10 / 20);
                    amountTextView.append(String.valueOf(amountPaid));
                    Logger.d(minutes_difference);

                    String collectionReference = Constants.PARKING_SLOTS + "/" + userId + "/" + Constants.PARKED_HISTORY;
                    DocumentReference historyDocument = FirebaseFirestore.getInstance().collection(collectionReference).document();

                    ParkedHistory parkedHistory = new ParkedHistory(historyDocument.getId(),
                            parkedVehicle.getVehicleNumber(), parkedVehicle.getEntryTime(), exitTime, amountPaid);
                    moveFirestoreDocument(fbHelper.toDocumentReference(vehicleDocumentPath), historyDocument, parkedHistory);

                }

                @Override
                public void onFailure(String errorMessage) {
                    Logger.d(errorMessage);
                }
            });
        }
    }


    private void show(String toastMessage) {
        Logger.d(toastMessage);
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
    }

    public void moveFirestoreDocument(DocumentReference fromPath, DocumentReference toPath, ParkedHistory parkedHistory) {
        toPath.set(parkedHistory)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Logger.d("DocumentSnapshot successfully written!");

                        fromPath.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Logger.d("DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Logger.w("Error deleting document" + e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Logger.w("Error writing document" + e);
                    }
                });
    }
}
