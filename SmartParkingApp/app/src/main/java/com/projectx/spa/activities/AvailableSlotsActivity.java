package com.projectx.spa.activities;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.projectx.spa.R;
import com.projectx.spa.adapters.AvailableSlotsAdapter;
import com.projectx.spa.helpers.Constants;
import com.projectx.spa.helpers.FbHelper;
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
        setContentView(R.layout.activity_available_slots);

        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        fbHelper = new FbHelper(this);

        parkingSlots = new ArrayList<>();
        updateRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(this);

        trackMultipleDocuments();
    }

    @Override
    public void onRefresh() {
        updateRecyclerView();
        makeSnackMessage("Updated");
        swipeRefreshLayout.setRefreshing(false);
    }

    public void updateRecyclerView() {
        availableSlotsAdapter = new AvailableSlotsAdapter(this, parkingSlots);
        recyclerView.setAdapter(availableSlotsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void trackMultipleDocuments() {
        Query query = FirebaseFirestore.getInstance()
                .collection(Constants.PARKING_SLOTS);

        ListenerRegistration registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG2", "Listen failed.", e);
                    return;
                }

                if (value != null) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        ParkingSlot parkingSlot = dc.getDocument().toObject(ParkingSlot.class);
                        String str = parkingSlot.toString();

                        switch (dc.getType()) {
                            case ADDED:
                                parkingSlots.add(parkingSlot);
                                availableSlotsAdapter.notifyItemInserted(parkingSlots.size() - 1);
                                Log.d("ADDED", "New : " + str);
//                                makeToast("ADDED\n" + str);
                                break;
                            case MODIFIED:
                                try {
                                    int index = parkingSlots.indexOf(parkingSlot);
                                    parkingSlots.set(index, parkingSlot);
                                    availableSlotsAdapter.notifyDataSetChanged();
                                } catch (IndexOutOfBoundsException indexException) {
                                    indexException.printStackTrace();
                                }
                                Log.d("MODIFIED", "Modified : " + str);
//                                makeToast("MODIFIED\n" + str);
                                break;
                            case REMOVED:
                                try {
                                    int index = parkingSlots.indexOf(parkingSlot);
                                    parkingSlots.remove(parkingSlot);
                                    availableSlotsAdapter.notifyItemRemoved(index);
                                } catch (IndexOutOfBoundsException indexException) {
                                    indexException.printStackTrace();
                                }
                                Log.d("REMOVED", "Removed : " + str);
//                                makeToast("REMOVED\n" + str);
                                break;
                        }
                    }
                }

                // it will read all the documents present in the Collection on detecting any change
                /*for (QueryDocumentSnapshot doc : value) {
                    if (doc.exists()) {
                        Map<String, Object> data = doc.getData();
                        String str = "name: " + data.get("name") + "\nemail: " + data.get("email") + "\nrandomInt: " + data.get("randomInt");
                        Log.d("TAG1", str);
                        makeToast(str);
                    }
                }*/
            }
        });

        // Stop listening to changes
//        registration.remove();
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