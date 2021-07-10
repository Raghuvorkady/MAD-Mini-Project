package com.projectx.spa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.projectx.spa.R;
import com.projectx.spa.helpers.Constants;
import com.projectx.spa.helpers.UserSession;
import com.projectx.spa.models.ParkingSlot;

public class AdminHomeActivity extends AppCompatActivity {

    TextView nameTextView, buildingTextView, landTextView, availableTextView;
    int availableSpace;
    String id;
    static String TAG = DetailsActivity.class.getSimpleName();
    UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        // user = new User("VpnN3ycQOyRjg5vpqPoO6ag5lBb2", "testName", "testMail", "1213213", Timestamp.now());

        nameTextView = findViewById(R.id.name_text_view);
        buildingTextView = findViewById(R.id.building_text_view);
        landTextView = findViewById(R.id.land_text_view);
        availableTextView = findViewById(R.id.linear_layout1_available_slots);


        userSession = new UserSession(this);
        id = userSession.getUserDetails().get(Constants.PREF_ID);
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection(Constants.PARKING_SLOTS).document(id);
        trackSingleDocumentTest(documentReference);
    }

    public void detailsPage(View view) throws Throwable {
        Intent it = new Intent(this, DetailsActivity.class);
        it.putExtra(
                "id", id);
        startActivity(it);
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
                    ParkingSlot ps = snapshot.toObject(ParkingSlot.class);
                    String name = userSession.getUserDetails().get(Constants.PREF_NAME);
                    String building = ps.getBuilding();
                    String land = ps.getAddress();
                    availableSpace = ps.getAvailableSpace();

                    nameTextView.setText(name.toUpperCase());
                    buildingTextView.setText(building);
                    landTextView.setText(land);
                    availableTextView.setText("" + availableSpace);

                } else {
                    Log.d("TAG1", "Current data: null");

                }
            }
        });
    }

    public void historyPage(View view) {
    }
}