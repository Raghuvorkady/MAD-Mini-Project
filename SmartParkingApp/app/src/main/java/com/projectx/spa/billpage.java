package com.projectx.spa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.text.DateFormat.getDateTimeInstance;

public class billpage extends AppCompatActivity {
    TextView t1,t2,t3,t4;
    String avail,str,id;
    int flag=0;
    static String TAG=details.class.getSimpleName();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseFirestore db1 = FirebaseFirestore.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String exittime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billpage);
        t1 = findViewById(R.id.textView5);
        t2 = findViewById(R.id.textView2);
        t3 = findViewById(R.id.textView3);
        t4 = findViewById(R.id.textView6);
        Intent intent = getIntent();
        str = intent.getStringExtra("number");
        id=intent.getStringExtra("id");
        DocumentReference docRef = db1.collection("parking-data").document(id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                avail=documentSnapshot.get("availspace").toString();
                Log.d(TAG,"avail="+avail);
            }
        });
        database();

    }
    private void database()
    {
        db.collection("vehicles")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if (document.get("number").equals(str)) {
                                    flag=1;
                                    int val = Integer.parseInt(avail) + 1;
                                    db1.collection("parking-data").document(id).update("availspace",val);
                                    String entrydate = document.get("entrytime").toString();
                                    t1.setText(str);
                                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                    Date date = new Date();
                                    String strDate = dateFormat.format(date);
                                    exittime = strDate;
                                    String entime[] = entrydate.split("\\s+");
                                    String extime[] = exittime.split("\\s+");
                                    t2.append(" " + entime[1]);
                                    t3.append(" " + extime[1]);
                                    db.collection("vehicles").document(document.getId()).update("exittime", exittime);
                                    Log.d(TAG, exittime);

                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                    try {
                                        Date entry = formatter.parse(entrydate);
                                        Date exit = formatter.parse(exittime);
                                        long time_difference = exit.getTime() - entry.getTime();
                                        long minutes_difference = (time_difference / 1000) / 60;
                                        int amt = (int) Math.ceil(minutes_difference * 10 / 20);
                                        t4.append("" + amt);
                                        Log.d(TAG, String.valueOf(minutes_difference));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    Map<String, Object> user = new HashMap<>();
                                    moveFirestoreDocument(document.getReference(),db.collection("history").document());
                                    break;
                                }

                            }
                            if(flag==0)
                            {
                                show("data not found");
                            }
                        }
                        else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });



    }


    private void show(String s) {
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }

    public void moveFirestoreDocument(DocumentReference fromPath, final DocumentReference toPath) {
        fromPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        toPath.set(document.getData())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                        fromPath.delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error deleting document", e);
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(billpage.this,MainActivity.class);
        startActivity(setIntent);
        finish();
    }
}
