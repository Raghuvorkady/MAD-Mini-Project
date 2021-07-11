package com.projectx.spa.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.projectx.spa.R;
import com.projectx.spa.adapters.ParkedVehiclesAdapter;
import com.projectx.spa.helpers.Constants;
import com.projectx.spa.helpers.FbHelper;
import com.projectx.spa.helpers.UserSession;
import com.projectx.spa.models.ParkedVehicle;

import java.util.ArrayList;
import java.util.List;

public class ParkedVehiclesActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,View.OnClickListener {
    private final String TAG = getClass().getSimpleName();
    private List<ParkedVehicle> parkedVehicles;
    private FbHelper fbHelper;
    String id;
    FloatingActionButton add;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ParkedVehiclesAdapter parkedVehiclesAdapter;
    private int availableSpace;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parked_vehicles);
        getSupportActionBar().setTitle("Parked vehicle");

        id = new UserSession(this).getUserDetails().get(Constants.PREF_ID);
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.parked_vehicles_swipe_refresh_layout);
        add = findViewById(R.id.fab_vehicle_in);
        add.setOnClickListener(this);

        fbHelper = new FbHelper(this);

        parkedVehicles = new ArrayList<>();
        updateRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(this);

        firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference docRef = firebaseFirestore.collection(Constants.PARKING_SLOTS).document(id);
        docRef
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        availableSpace = Integer.parseInt(documentSnapshot.get("availableSpace").toString());
                        Log.d(TAG, "avail=" + availableSpace);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "failed " + e.getMessage());
                    }
                });


        trackMultipleDocuments();
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

    private void trackMultipleDocuments() {
        String collectionReference = Constants.PARKING_SLOTS + "/" + id + "/" + Constants.PARKED_VEHICLES;
        Query query = FirebaseFirestore.getInstance().collection(collectionReference);

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
                        ParkedVehicle parkedVehicle = dc.getDocument().toObject(ParkedVehicle.class);
                        String str = parkedVehicle.toString();

                        switch (dc.getType()) {
                            case ADDED:
                                parkedVehicles.add(parkedVehicle);
                                parkedVehiclesAdapter.notifyItemInserted(parkedVehicles.size() - 1);
                                Log.d("ADDED", "New : " + str);
//                                makeToast("ADDED\n" + str);
                                break;
                            case MODIFIED:
                                try {
                                    int index = parkedVehicles.indexOf(parkedVehicle);
                                    parkedVehicles.set(index, parkedVehicle);
                                    parkedVehiclesAdapter.notifyDataSetChanged();
                                } catch (IndexOutOfBoundsException indexException) {
                                    indexException.printStackTrace();
                                }
                                Log.d("MODIFIED", "Modified : " + str);
//                                makeToast("MODIFIED\n" + str);
                                break;
                            case REMOVED:
                                try {
                                    int index = parkedVehicles.indexOf(parkedVehicle);
                                    parkedVehicles.remove(parkedVehicle);
                                    parkedVehiclesAdapter.notifyItemRemoved(index);
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
        if (view.equals(add)) {
            if (availableSpace > 0) {
                Intent it = new Intent(this, DetailsActivity.class);
                startActivity(it);
            }
            else{
                Toast.makeText(ParkedVehiclesActivity.this, "no space to park", Toast.LENGTH_SHORT).show();

                return;
            }

        }
    }
}