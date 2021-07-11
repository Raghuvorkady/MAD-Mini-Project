package com.projectx.spa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.orhanobut.logger.Logger;
import com.projectx.spa.R;
import com.projectx.spa.helpers.Constants;
import com.projectx.spa.helpers.UserSession;
import com.projectx.spa.models.ParkingSlot;

import es.dmoral.toasty.Toasty;

public class AdminHomeActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();

    private TextView nameTextView;
    private TextView buildingTextView;
    private TextView landTextView;
    private TextView availableTextView;
    private CardView parkedVehicleCardView, historyCardView;
    private int availableSpace;
    private UserSession userSession;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        getSupportActionBar().setTitle("Admin");
//        getActionBar().setTitle("Admin");
//actionbar

        nameTextView = findViewById(R.id.name_text_view);
        buildingTextView = findViewById(R.id.building_text_view);
        landTextView = findViewById(R.id.land_text_view);
        availableTextView = findViewById(R.id.linear_layout1_available_slots);
        parkedVehicleCardView = findViewById(R.id.contraint_layout3_info_card);
        historyCardView = findViewById(R.id.history_card);
        progress = findViewById(R.id.loading);

        parkedVehicleCardView.setOnClickListener(this);
        historyCardView.setOnClickListener(this);

        userSession = new UserSession(this);
        String id = userSession.getUserDetails().get(Constants.PREF_ID);
        DocumentReference documentReference;
        if (id != null) {
            documentReference = FirebaseFirestore.getInstance().collection(Constants.PARKING_SLOTS).document(id);
            trackSingleDocumentTest(documentReference);
        } else {
            Logger.d("id is null");
        }
    }

    @Override
    public void onClick(View view) {
        parkedVehicleCardView.setVisibility(View.INVISIBLE);
        historyCardView.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);

        Intent intent = new Intent();
        if (view.equals(parkedVehicleCardView)) {
            intent.setClass(this, ParkedVehiclesActivity.class);
            startActivity(intent);
        }
        if (view.equals(historyCardView)) {
            intent.setClass(this, ParkedHistoryActivity.class);
            startActivity(intent);
        }

        parkedVehicleCardView.setVisibility(View.VISIBLE);
        historyCardView.setVisibility(View.VISIBLE);
        progress.setVisibility(View.INVISIBLE);
    }

    private void trackSingleDocumentTest(DocumentReference documentReference) {
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Logger.w("Listen failed: " + e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Logger.d(snapshot);
                    ParkingSlot parkingSlot = snapshot.toObject(ParkingSlot.class);
                    if (parkingSlot != null) {
                        String name = userSession.getUserDetails().get(Constants.PREF_NAME);
                        String building = parkingSlot.getBuilding();
                        String land = parkingSlot.getAddress();
                        availableSpace = parkingSlot.getAvailableSpace();

                        nameTextView.setText(name);
                        buildingTextView.setText(building);
                        landTextView.setText(land);
                        availableTextView.setText(String.valueOf(availableSpace));
                    }
                } else {
                    Logger.d("Current data: null");

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            //case R.id.menu_profile:
            //     profilePage();
            //    return true;
            case R.id.menu_logout:
                logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logOut() {
        Toasty.success(this, "Logout successful", Toasty.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();
        userSession.clearUserData();
        finish();
    }

    //private void profilePage() {
    //    Logger.d(TAG, "yet to implement");
    // }

    @Override
    public void onBackPressed() {
        Intent i = getIntent();

        if (LoginActivity.class.getSimpleName().equals(i.getStringExtra("callingActivity"))) {
            Logger.d("onBackPressed");
            super.onBackPressed();
        } else {
            Logger.d("onBackPressed");
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}