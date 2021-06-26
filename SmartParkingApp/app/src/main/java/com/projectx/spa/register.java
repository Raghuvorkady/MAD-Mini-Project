package com.projectx.spa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity implements View.OnClickListener {
    Button register;
    EditText name,email,phone,password,building,area,totalspace;
    TextView loginbtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    String userid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register = findViewById(R.id.register);
        register.setOnClickListener(this);
        name = findViewById(R.id.Name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        loginbtn = findViewById(R.id.login1);
        loginbtn.setOnClickListener(this);
        building= findViewById(R.id.building);
        area=findViewById(R.id.area);
        totalspace= findViewById(R.id.slots);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();


        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));//add .class file of vehicle number entry
            finish();
        }

    }
    public void onClick(View v ) {
        if(v.equals(loginbtn)){
            startActivity(new Intent(getApplicationContext(),login.class));
        }
        if (v.equals(register)){
            String emails=email.getText().toString().trim();
            String pwd=password.getText().toString().trim();
            String fname=name.getText().toString();
            String phno=phone.getText().toString();
            String place=building.getText().toString();
            String local=area.getText().toString();
            String avail=totalspace.getText().toString();

            if (TextUtils.isEmpty(emails)){
                email.setError("Email is required.");
                return;
            }
            if(TextUtils.isEmpty(pwd)){
                password.setError("password is required");
                return;
            }
            if(TextUtils.isEmpty(fname)){
                name.setError("name is required");
                return;
            }
            if(TextUtils.isEmpty(place)){
                building.setError("phone building name is required");
                return;
            }
            if(TextUtils.isEmpty(avail)){
                totalspace.setError("Total slots is required");
                return;
            }
            if (pwd.length()<6){
                password.setError("password must be atleast 6 characters");
                return;
            }


                // register in firebase
            fAuth.createUserWithEmailAndPassword(emails,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(register.this,"User created", Toast.LENGTH_SHORT).show();
                        userid=fAuth.getCurrentUser().getUid();
                        DocumentReference documentReference= fstore.collection("users").document(userid);
                        Map<String,Object> user = new HashMap<>();
                        user.put("fname",fname);
                        user.put("email",emails);
                        user.put("phone_no",phno);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("TAG","onSuccess: user profile is created for "+userid);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("TAG","onFailure: "+e.toString());
                            }
                        });

                        // parking data
                        DocumentReference documentReference1= fstore.collection("parking data").document(userid);
                        Map<String,Object> data = new HashMap<>();
                        data.put("authoriser",fname);
                        data.put("building",place);
                        data.put("address",local);
                        data.put("total_slot",avail);

                        documentReference1.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("TAG","onSuccess: user profile is created for "+userid);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("TAG","onFailure: "+e.toString());
                            }
                        });


                        startActivity(new Intent(getApplicationContext(),vehicle_entry.class));//add .class file of vehicle number entry
                    }
                    else{
                        Toast.makeText(register.this,"Error !!"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
            );
        }
    }



}