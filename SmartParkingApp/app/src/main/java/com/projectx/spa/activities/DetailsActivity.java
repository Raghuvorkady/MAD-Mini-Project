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
import com.projectx.spa.models.Vehicles;

import br.com.sapereaude.maskedEditText.MaskedEditText;

public class DetailsActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    MaskedEditText e1;
    String s;
    String avail, id;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        id = new UserSession(this).getUserDetails().get(Constants.PREF_ID);
        Log.d(TAG, id);
        // Log is recommended

        DocumentReference docRef = db.collection(Constants.PARKING_SLOTS).document(id);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        avail = documentSnapshot.get("availableSpace").toString();
                        Log.d(TAG, "avail=" + avail);
                    }
                });

        e1 = findViewById(R.id.phone_input);
        /*e1.setFilters(new InputFilter[] {new InputFilter.AllCaps()});*/
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void inPage(View view) {
        if (Integer.parseInt(avail) > 0) {
            s = e1.getText().toString();
            if (!s.equals("AA-00-BB-1111")) {
                System.out.println(s);
//                Intent it = new Intent(this, AdminHomeActivity.class);
//                if (s.matches("^[A-Z]{2}[-][0-9]{2}[-][A-Z]{2}[-][0-9]{4}$")) {
                if (true) {
                    e1.setText("");
                    Log.d(TAG, s);
                    new AlertDialog.Builder(this)
                            .setTitle("Insert entry")
                            .setMessage("Are you sure you want to insert " + s + "?")
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                Vehicles vh = new Vehicles(null, s, Timestamp.now());
                                FbHelper fbHelper = new FbHelper(getApplicationContext());
                                fbHelper.addDataToFirestore(vh, Constants.PARKED_VEHICLES, null, new OnGetDataListener() {
                                    @Override
                                    public void onSuccess(DocumentReference dataSnapshotValue) {
                                        Toast.makeText(DetailsActivity.this, "added successfully", Toast.LENGTH_LONG).show();
                                        int val = Integer.parseInt(avail) - 1;
                                        db.collection(Constants.PARKING_SLOTS).document(id).update("availableSpace", val);
//                                        startActivity(it);
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