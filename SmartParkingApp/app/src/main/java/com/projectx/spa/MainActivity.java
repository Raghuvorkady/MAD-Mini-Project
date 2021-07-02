package com.projectx.spa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    TextView t1, t2, t3, t4;
    String avail;
    String id;
    static String TAG = Details.class.getSimpleName();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t1 = findViewById(R.id.pt2);
        t2 = findViewById(R.id.pt3);
        t3 = findViewById(R.id.pt5);
        t4 = findViewById(R.id.pt6);

        db.collection("parking-data")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                id = document.getId();
                                String name = document.get("authorizer").toString();
                                String building = document.get("building").toString();
                                String land = document.get("address").toString();
                                avail = document.get("availspace").toString();

                                t1.setText(name.toUpperCase());
                                t2.setText(building);
                                t3.setText(land);
                                t4.setText(avail);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void detailsPage(View view) {
        Intent it = new Intent(this, Details.class);
        it.putExtra("id", id);
        startActivity(it);
    }

    public void historyPage(View view) {
    }
}