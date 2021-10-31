package com.projectx.spa.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.orhanobut.logger.Logger;
import com.projectx.spa.R;
import com.projectx.spa.helpers.Constants;
import com.projectx.spa.helpers.FbHelper;
import com.projectx.spa.helpers.UserSession;
import com.projectx.spa.interfaces.OnSnapshotListener;
import com.projectx.spa.models.ParkingSlot;

import es.dmoral.toasty.Toasty;

public class AdminHomeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView nameTextView;
    private TextView buildingTextView;
    private TextView landTextView;
    private TextView availableTextView;
    private CardView parkedVehicleCardView, historyCardView;
    private int availableSpace;
    private UserSession userSession;
    private ProgressBar progress;

    private FbHelper fbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        //getSupportActionBar().setTitle("Admin");
//        getActionBar().setTitle("Admin");
//actionbar

        nameTextView = findViewById(R.id.name_text_view);
        buildingTextView = findViewById(R.id.building_text_view);
        landTextView = findViewById(R.id.land_text_view);
        availableTextView = findViewById(R.id.linear_layout1_available_slots);
        parkedVehicleCardView = findViewById(R.id.constraint_layout3_info_card);
        historyCardView = findViewById(R.id.history_card);
        progress = findViewById(R.id.loading);

        parkedVehicleCardView.setOnClickListener(this);
        historyCardView.setOnClickListener(this);

        fbHelper = new FbHelper(this);

        userSession = new UserSession(this);
        String id = userSession.getUserDetails().get(Constants.PREF_ID);
        if (id != null) {
            String documentPath = Constants.PARKING_SLOTS + "/" + id;
            trackDocumentChanges(documentPath);
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

    private void trackDocumentChanges(String documentPath) {
        fbHelper.trackDocument(ParkingSlot.class, documentPath, new OnSnapshotListener() {
            @Override
            public <T> void onSuccess(T object) {
                // TODO: 12-07-2021 Name is not being updated because of using cached name
                String name = userSession.getUserDetails().get(Constants.PREF_NAME);
                ParkingSlot parkingSlot = (ParkingSlot) object;
                String building = parkingSlot.getBuilding();
                String land = parkingSlot.getAddress();
                availableSpace = parkingSlot.getAvailableSpace();

                nameTextView.setText(name);
                buildingTextView.setText(building);
                landTextView.setText(land);
                availableTextView.setText(String.valueOf(availableSpace));
            }

            @Override
            public void onFailure(String errorMessage) {
                Logger.e(errorMessage);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
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
        fbHelper.logoutUser(new OnSnapshotListener() {
            @Override
            public <T> void onSuccess(T object) {
                Toasty.success(getApplicationContext(), (String) object, Toasty.LENGTH_SHORT).show();
                userSession.clearUserData();
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toasty.error(getApplicationContext(), errorMessage, Toasty.LENGTH_SHORT).show();
            }
        });
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