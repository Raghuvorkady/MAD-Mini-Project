package com.projectx.spa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.projectx.spa.helpers.FbHelper;
import com.projectx.spa.helpers.UserSession;
import com.projectx.spa.models.ParkingSlot;

import es.dmoral.toasty.Toasty;

public class AdminHomeActivity extends AppCompatActivity {

    TextView t1, t2, t3, t4;
    int avail;
    String id;
    private final String TAG = getClass().getSimpleName();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        // user = new User("VpnN3ycQOyRjg5vpqPoO6ag5lBb2", "testName", "testMail", "1213213", Timestamp.now());

        t1 = findViewById(R.id.pt2);
        t2 = findViewById(R.id.pt3);
        t3 = findViewById(R.id.pt5);
        t4 = findViewById(R.id.pt6);

        FbHelper fh = new FbHelper(getApplicationContext());

        userSession = new UserSession(this);
        id = userSession.getUserDetails().get(Constants.PREF_ID);
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection(Constants.PARKING_SLOTS).document(id);
        trackSingleDocumentTest(documentReference);
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
                    avail = ps.getAvailableSpace();

                    t1.setText(name.toUpperCase());
                    t2.setText(building);
                    t3.setText(land);
                    t4.setText("" + avail);

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
            case R.id.menu_profile:
                profilePage();
                return true;
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

    private void profilePage() {
        Log.d(TAG, "yet to implement");
    }
}