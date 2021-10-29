package com.projectx.spa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orhanobut.logger.Logger;
import com.projectx.spa.R;
import com.projectx.spa.adapters.ParkedVehiclesAdapter;
import com.projectx.spa.helpers.Constants;
import com.projectx.spa.helpers.FbHelper;
import com.projectx.spa.helpers.UserSession;
import com.projectx.spa.interfaces.OnMultiDocumentListener;
import com.projectx.spa.models.ParkedVehicle;

import java.util.ArrayList;
import java.util.List;

public class ParkedVehiclesActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private FloatingActionButton addFAB;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ParkedVehiclesAdapter parkedVehiclesAdapter;

    private FbHelper fbHelper;

    private int availableSpace;
    private List<ParkedVehicle> parkedVehicles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parked_vehicles);
//        getSupportActionBar().setTitle("Parked vehicles");

        String id = new UserSession(this).getUserDetails().get(Constants.PREF_ID);
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.parked_vehicles_swipe_refresh_layout);
        addFAB = findViewById(R.id.fab_vehicle_in);
        addFAB.setOnClickListener(this);

        fbHelper = new FbHelper(this);

        parkedVehicles = new ArrayList<>();
        updateRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(this);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference docRef = firebaseFirestore.collection(Constants.PARKING_SLOTS).document(id);
        docRef
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        availableSpace = Integer.parseInt(String.valueOf(documentSnapshot.get("availableSpace")));
                        Logger.d("avail=" + availableSpace);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Logger.d("failed " + e.getMessage());
                    }
                });

        String collectionPath = Constants.PARKING_SLOTS + "/" + id + "/" + Constants.PARKED_VEHICLES;
        trackDocumentChanges(collectionPath);
    }

    @Override
    public void onRefresh() {
        updateRecyclerView();
        makeSnackMessage("Updated");
        swipeRefreshLayout.setRefreshing(false);
    }

    public void updateRecyclerView() {
        parkedVehiclesAdapter = new ParkedVehiclesAdapter(this, parkedVehicles);
        recyclerView.setAdapter(parkedVehiclesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void trackDocumentChanges(String collectionPath) {
        fbHelper.trackDocuments(ParkedVehicle.class, collectionPath, new OnMultiDocumentListener() {
            @Override
            public <T> void onAdded(T object) {
                parkedVehicles.add((ParkedVehicle) object);
                parkedVehiclesAdapter.notifyItemInserted(parkedVehicles.size() - 1);
            }

            @Override
            public <T> void onModified(T object) {
                try {
                    ParkedVehicle parkedVehicle = (ParkedVehicle) object;
                    int index = parkedVehicles.indexOf(parkedVehicle);
                    parkedVehicles.set(index, parkedVehicle);
                    parkedVehiclesAdapter.notifyDataSetChanged();
                } catch (IndexOutOfBoundsException indexException) {
                    indexException.printStackTrace();
                }
            }

            @Override
            public <T> void onRemoved(T object) {
                try {
                    ParkedVehicle parkedVehicle = (ParkedVehicle) object;
                    int index = parkedVehicles.indexOf(parkedVehicle);
                    parkedVehicles.remove(parkedVehicle);
                    parkedVehiclesAdapter.notifyItemRemoved(index);
                } catch (IndexOutOfBoundsException indexException) {
                    indexException.printStackTrace();
                }
            }
        });
    }

    private void makeToast(String toastMessage) {
        Toast.makeText(ParkedVehiclesActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
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


    @Override
    public void onClick(View view) {
        if (view.equals(addFAB)) {
            if (availableSpace > 0) {
                Intent it = new Intent(this, VehicleEntryActivity.class);
                startActivity(it);
            } else {
                Toast.makeText(ParkedVehiclesActivity.this, "no space to park", Toast.LENGTH_SHORT).show();
            }
        }
    }
}