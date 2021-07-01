package com.projectx.spa;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class details extends AppCompatActivity {
    EditText editText;
    String s;
    static String TAG=details.class.getSimpleName();
    String avail,id;
    FirebaseFirestore db1 = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        id=intent.getStringExtra("id");
        DocumentReference docRef = db1.collection("parking-data").document(id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               avail=documentSnapshot.get("availspace").toString();
               Log.d(TAG,"avail="+avail);
            }
        });
        editText=findViewById(R.id.editTextTextPersonName);
        InputFilter[] editFilters = editText.getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = new InputFilter.AllCaps();
        editText.setFilters(newFilters);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if ((editText.getText().length() + 1 == 3 || editText.getText().length() + 1 == 6 || editText.getText().length() + 1 == 9)) {
                    if(before-count<0){
                        editText.setText(editText.getText() + "-");
                        editText.setSelection(editText.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

        });
    }


    public void outpage(View view) {
        s=editText.getText().toString();
        editText.setText("");
        if(s.matches("^[A-Z]{2}[-][0-9]{2}[-][A-Z]{2}[-][0-9]{4}$")) {
            Intent it1 = new Intent(this, billpage.class);
            it1.putExtra("number", s);
            it1.putExtra("id",id);
            startActivity(it1);
        }
        else
        {
            Toast.makeText(this,"wrong format",Toast.LENGTH_LONG).show();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void inpage(View view) {
        s=editText.getText().toString();
        editText.setText("");
        Intent it=new Intent(this,MainActivity.class);
        if(s.matches("^[A-Z]{2}[-][0-9]{2}[-][A-Z]{2}[-][0-9]{4}$"))
        {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String strDate = dateFormat.format(date);

            new AlertDialog.Builder(this)
                    .setTitle("Insert entry")
                    .setMessage("Are you sure you want to insert "+s+"?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Map<String, Object> user = new HashMap<>();
                        user.put("number", s);
                        user.put("entrytime",strDate);

                        db.collection("vehicles")
                                .add(user)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                        Toast.makeText(details.this, "added successfully", Toast.LENGTH_LONG).show();
                                        int val=Integer.parseInt(avail)-1;
                                        db.collection("parking-data").document(id).update("availspace",val);
                                        startActivity(it);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                    }
                                });

                    })

                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else
        {
            show("wrong format");
        }

    }
    private void show(String s) {
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }


}