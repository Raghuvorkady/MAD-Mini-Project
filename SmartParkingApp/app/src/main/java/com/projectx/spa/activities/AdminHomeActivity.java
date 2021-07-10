package com.projectx.spa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.projectx.spa.R;
import com.projectx.spa.helpers.Constants;
import com.projectx.spa.helpers.UserSession;
import com.projectx.spa.models.ParkingSlot;

import androidx.cardview.widget.CardView;
import es.dmoral.toasty.Toasty;

public class AdminHomeActivity extends AppCompatActivity implements View.OnClickListener{

    TextView nameTextView, buildingTextView, landTextView, availableTextView;
    CardView parkedVehicle,history;
    int availableSpace;
    String id;
    private final String TAG = getClass().getSimpleName();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    UserSession userSession;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        // user = new User("VpnN3ycQOyRjg5vpqPoO6ag5lBb2", "testName", "testMail", "1213213", Timestamp.now());

        nameTextView = findViewById(R.id.name_text_view);
        buildingTextView = findViewById(R.id.building_text_view);
        landTextView = findViewById(R.id.land_text_view);
        availableTextView = findViewById(R.id.linear_layout1_available_slots);
        parkedVehicle=findViewById(R.id.contraint_layout3_info_card);
        history=findViewById(R.id.out_card);
        progress=findViewById(R.id.loading);

        parkedVehicle.setOnClickListener(this);
        history.setOnClickListener(this);

        userSession = new UserSession(this);
        id = userSession.getUserDetails().get(Constants.PREF_ID);
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection(Constants.PARKING_SLOTS).document(id);
        trackSingleDocumentTest(documentReference);
    }

    @Override
    public void onClick(View view) {
        parkedVehicle.setVisibility(View.INVISIBLE);
        history.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);

        Intent intent=new Intent();
        if (view.equals(parkedVehicle)){
            intent.setClass(this, ParkedVehiclesActivity.class);
            startActivity(intent);
        }
        if (view.equals(history)){
            intent.setClass(this, ParkedHistoryActivity.class);
            startActivity(intent);
        }
        parkedVehicle.setVisibility(View.VISIBLE);
        history.setVisibility(View.VISIBLE);
        progress.setVisibility(View.INVISIBLE);
    }
    private void trackSingleDocumentTest(DocumentReference documentReference) {
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG1", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("TAG1", "Current data: " + snapshot);
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
                    Log.d("TAG1", "Current data: null");

                }
            }
        });
    }

    public void historyPage(View view) {
        Intent intent = new Intent(this, ParkedHistoryActivity.class);
        startActivity(intent);
    }

    public void viewParkedVehicles(View view) {
        Log.d(TAG, "ADMIN vehicles");
        Intent intent = new Intent(this, ParkedVehiclesActivity.class);
        startActivity(intent);
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
    //    Log.d(TAG, "yet to implement");
   // }
}