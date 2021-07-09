package com.projectx.spa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.projectx.spa.R;
import com.projectx.spa.helpers.Constants;
import com.projectx.spa.helpers.FBHelper;
import com.projectx.spa.models.ParkingSlot;
import com.projectx.spa.models.User;

public class AdminHomeActivity extends AppCompatActivity {

    TextView t1, t2, t3, t4;
    int avail;
    String id;
    static String TAG = DetailsActivity.class.getSimpleName();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Intent it=getIntent();
        user = new User("VpnN3ycQOyRjg5vpqPoO6ag5lBb2", "testName", "testMail", "1213213", Timestamp.now());
        //user=it.getParcelableExtra("user");
        t1 = findViewById(R.id.pt2);
        t2 = findViewById(R.id.pt3);
        t3 = findViewById(R.id.pt5);
        t4 = findViewById(R.id.pt6);
        id=user.getId();
        FBHelper fh=new FBHelper(getApplicationContext());
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection(Constants.PARKING_SLOTS).document(user.getId());
        trackSingleDocumentTest(documentReference);
    }

    public void detailsPage(View view) throws Throwable {
        Intent it = new Intent(this, DetailsActivity.class);
        it.putExtra("id", id);
        startActivity(it);
        finalize();
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
                    ParkingSlot ps=snapshot.toObject(ParkingSlot.class);
                    String name=user.getName();
                    String building = ps.getBuilding();
                    String land = ps.getAddress();
                    avail = ps.getAvailableSpace();

                    t1.setText(name.toUpperCase());
                    t2.setText(building);
                    t3.setText(land);
                    t4.setText(""+avail);

                } else {
                    Log.d("TAG1", "Current data: null");

                }
            }
        });
    }

    public void historyPage(View view) {
    }
}