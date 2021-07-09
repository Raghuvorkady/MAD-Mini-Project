package com.projectx.spa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.projectx.spa.R;
import com.projectx.spa.helpers.Constants;
import com.projectx.spa.models.Vehicles;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BillsPageActivity extends AppCompatActivity {

    TextView t1, t2, t3, t4;
    String avail, total, str, id;
    boolean flag = false;
    static String TAG = DetailsActivity.class.getSimpleName();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Timestamp exitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bills_page);

        t1 = findViewById(R.id.textView5);
        t2 = findViewById(R.id.textView2);
        t3 = findViewById(R.id.textView3);
        t4 = findViewById(R.id.textView6);

        Intent intent = getIntent();
        str = intent.getStringExtra("number");
        id = intent.getStringExtra("id");

        DocumentReference docRef = db.collection(Constants.PARKING_SLOTS).document(id);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        avail = documentSnapshot.get("availableSpace").toString();
                        total = documentSnapshot.get("totalSpace").toString();
                        Log.d(TAG, "avail=" + avail);
                        Log.d(TAG, "total=" + total);

                        database();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "avail failed");
                    }
                });

//        database();
    }

    private void database() {
        if (Integer.parseInt(avail) <= Integer.parseInt(total)) {
            db.collection(Constants.PARKED_VEHICLES)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    Vehicles vehicles = document.toObject(Vehicles.class);

                                    if (vehicles.getVehicleNumber().equals(str)) {
                                        flag = true;
                                        int val = Integer.parseInt(avail) + 1;
                                        Date entryDate = vehicles.getEntryTime().toDate();
                                        t1.setText(str);
                                        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                        exitTime = Timestamp.now();
                                        Date exittime = exitTime.toDate();
                                        String entime = timeFormat.format(entryDate);
                                        String extime = timeFormat.format(exittime);
                                        t2.append(" " + entime);
                                        t3.append(" " + extime);

                                        DocumentReference doc = db.collection(Constants.PARKED_VEHICLES).document(document.getId());
                                        doc.update("exitTime", exitTime)
                                                // TODO: 09-07-2021 add on failure
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // TODO: 09-07-2021 onsuccess for updating
                                                        db.collection(Constants.PARKING_SLOTS).document(id).update("availableSpace", val);
                                                        Log.d(TAG, "updated successfully");
                                                        long time_difference = exittime.getTime() - entryDate.getTime();
                                                        long minutes_difference = (time_difference / 1000) / 60;
                                                        int amt = (int) Math.ceil(minutes_difference * 10 / 20);
                                                        t4.append("" + amt);
                                                        Log.d(TAG, String.valueOf(minutes_difference));
                                                        moveFirestoreDocument(document.getReference(), db.collection(Constants.PARKED_HISTORY).document());

                                                    }
                                                });
                                        Log.d(TAG, exitTime.toDate().toString());


                                    }

                                }
                                if (!flag) {
                                    show("data not found");
                                }
                            } else {
                                show("Error getting documents." + task.getException());
                            }
                        }
                    });
        } else {
            Log.d(TAG, "error");
        }
    }


    private void show(String toastMessage) {
        Log.d(TAG, toastMessage);
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
    }

    public void moveFirestoreDocument(DocumentReference fromPath, final DocumentReference toPath) {
        fromPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        toPath.set(document.getData())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                        fromPath.delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error deleting document", e);
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(BillsPageActivity.this, AdminHomeActivity.class);
        startActivity(setIntent);
        finish();
    }
}
