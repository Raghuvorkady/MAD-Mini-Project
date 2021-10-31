package com.projectx.spa.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.projectx.spa.R;
import com.projectx.spa.adapters.AvailableSlotsAdapter;
import com.projectx.spa.helpers.Constants;
import com.projectx.spa.helpers.FbHelper;
import com.projectx.spa.interfaces.OnMultiDocumentListener;
import com.projectx.spa.models.ParkingSlot;

import java.util.ArrayList;
import java.util.List;

public class AvailableSlotsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private List<ParkingSlot> parkingSlots;
    private FbHelper fbHelper;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AvailableSlotsAdapter availableSlotsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().setTitle("Available slot");
        setContentView(R.layout.activity_available_slots);

        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        fbHelper = new FbHelper(this);

        parkingSlots = new ArrayList<>();
        updateRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(this);

        String collectionPath = Constants.PARKING_SLOTS;
        trackDocumentChanges(collectionPath);
    }

    @Override
    public void onRefresh() {
        updateRecyclerView();
        makeSnackMessage("Updated");
        swipeRefreshLayout.setRefreshing(false);
    }

    private void trackDocumentChanges(String collectionPath) {
        fbHelper.trackDocuments(ParkingSlot.class, collectionPath, new OnMultiDocumentListener() {
            @Override
            public <T> void onAdded(T object) {
                parkingSlots.add((ParkingSlot) object);
                availableSlotsAdapter.notifyItemInserted(parkingSlots.size() - 1);
            }

            @Override
            public <T> void onModified(T object) {
                try {
                    ParkingSlot parkingSlot = (ParkingSlot) object;
                    int index = parkingSlots.indexOf(parkingSlot);
                    parkingSlots.set(index, parkingSlot);
                    availableSlotsAdapter.notifyDataSetChanged();
                } catch (IndexOutOfBoundsException indexException) {
                    indexException.printStackTrace();
                }
            }

            @Override
            public <T> void onRemoved(T object) {
                try {
                    ParkingSlot parkingSlot = (ParkingSlot) object;
                    int index = parkingSlots.indexOf(parkingSlot);
                    parkingSlots.remove(parkingSlot);
                    availableSlotsAdapter.notifyItemRemoved(index);
                } catch (IndexOutOfBoundsException indexException) {
                    indexException.printStackTrace();
                }
            }
        });
    }

    public void updateRecyclerView() {
        availableSlotsAdapter = new AvailableSlotsAdapter(this, parkingSlots);
        recyclerView.setAdapter(availableSlotsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void makeToast(String toastMessage) {
        Toast.makeText(AvailableSlotsActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
    }

    private void makeSnackMessage(String str) {
        Snackbar snackbar = Snackbar.make(swipeRefreshLayout, str + " ", Snackbar.LENGTH_SHORT);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
}