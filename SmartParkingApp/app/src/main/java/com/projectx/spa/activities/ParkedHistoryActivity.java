package com.projectx.spa.activities;

import android.os.Build;
import android.os.Bundle;
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
import com.orhanobut.logger.Logger;
import com.projectx.spa.R;
import com.projectx.spa.adapters.ParkedHistoryAdapter;
import com.projectx.spa.helpers.Constants;
import com.projectx.spa.helpers.FbHelper;
import com.projectx.spa.helpers.UserSession;
import com.projectx.spa.models.ParkedHistory;

import java.util.ArrayList;
import java.util.List;

public class ParkedHistoryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = getClass().getSimpleName();
    private List<ParkedHistory> parkedHistories;
    private FbHelper fbHelper;
    String id;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ParkedHistoryAdapter parkedHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parked_history);
        getSupportActionBar().setTitle("History");

        id = new UserSession(this).getUserDetails().get(Constants.PREF_ID);
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.parked_history_swipe_refresh_layout);

        fbHelper = new FbHelper(this);

        parkedHistories = new ArrayList<>();
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
        parkedHistoryAdapter = new ParkedHistoryAdapter(this, parkedHistories);
        recyclerView.setAdapter(parkedHistoryAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void trackMultipleDocuments() {
        String collectionReference = Constants.PARKING_SLOTS + "/" + id + "/" + Constants.PARKED_HISTORY;
        Query query = FirebaseFirestore.getInstance().collection(collectionReference);

        ListenerRegistration registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Logger.w("Listen failed." + e);
                    return;
                }

                if (value != null) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        ParkedHistory parkedHistory = dc.getDocument().toObject(ParkedHistory.class);
                        String str = parkedHistory.toString();

                        switch (dc.getType()) {
                            case ADDED:
                                parkedHistories.add(parkedHistory);
                                parkedHistoryAdapter.notifyItemInserted(parkedHistories.size() - 1);
                                Logger.d("ADDED" + str);
//                                makeToast("ADDED\n" + str);
                                break;
                            case MODIFIED:
                                try {
                                    int index = parkedHistories.indexOf(parkedHistory);
                                    parkedHistories.set(index, parkedHistory);
                                    parkedHistoryAdapter.notifyDataSetChanged();
                                } catch (IndexOutOfBoundsException indexException) {
                                    indexException.printStackTrace();
                                }
                                Logger.d("MODIFIED" + str);
//                                makeToast("MODIFIED\n" + str);
                                break;
                            case REMOVED:
                                try {
                                    int index = parkedHistories.indexOf(parkedHistory);
                                    parkedHistories.remove(parkedHistory);
                                    parkedHistoryAdapter.notifyItemRemoved(index);
                                } catch (IndexOutOfBoundsException indexException) {
                                    indexException.printStackTrace();
                                }
                                Logger.d("REMOVED" + str);
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
                        Logger.d("TAG1", str);
                        makeToast(str);
                    }
                }*/
            }
        });

        // Stop listening to changes
//        registration.remove();
    }

    private void makeToast(String toastMessage) {
        Toast.makeText(ParkedHistoryActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
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