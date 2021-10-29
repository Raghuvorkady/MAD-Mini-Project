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
import com.projectx.spa.adapters.ParkedHistoryAdapter;
import com.projectx.spa.helpers.Constants;
import com.projectx.spa.helpers.FbHelper;
import com.projectx.spa.helpers.UserSession;
import com.projectx.spa.interfaces.OnMultiDocumentListener;
import com.projectx.spa.models.ParkedHistory;

import java.util.ArrayList;
import java.util.List;

public class ParkedHistoryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

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
//        getSupportActionBar().setTitle("History");

        id = new UserSession(this).getUserDetails().get(Constants.PREF_ID);
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.parked_history_swipe_refresh_layout);

        fbHelper = new FbHelper(this);

        parkedHistories = new ArrayList<>();
        updateRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(this);

        String collectionPath = Constants.PARKING_SLOTS + "/" + id + "/" + Constants.PARKED_HISTORY;
        trackDocumentChanges(collectionPath);
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

    private void trackDocumentChanges(String collectionPath) {
        fbHelper.trackDocuments(ParkedHistory.class, collectionPath, new OnMultiDocumentListener() {
            @Override
            public <T> void onAdded(T object) {
                parkedHistories.add((ParkedHistory) object);
                parkedHistoryAdapter.notifyItemInserted(parkedHistories.size() - 1);
            }

            @Override
            public <T> void onModified(T object) {
                try {
                    ParkedHistory parkedHistory = (ParkedHistory) object;
                    int index = parkedHistories.indexOf(parkedHistory);
                    parkedHistories.set(index, parkedHistory);
                    parkedHistoryAdapter.notifyDataSetChanged();
                } catch (IndexOutOfBoundsException indexException) {
                    indexException.printStackTrace();
                }
            }

            @Override
            public <T> void onRemoved(T object) {
                try {
                    ParkedHistory parkingSlot = (ParkedHistory) object;
                    int index = parkedHistories.indexOf(parkingSlot);
                    parkedHistories.remove(parkingSlot);
                    parkedHistoryAdapter.notifyItemRemoved(index);
                } catch (IndexOutOfBoundsException indexException) {
                    indexException.printStackTrace();
                }
            }
        });
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