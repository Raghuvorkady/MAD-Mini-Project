package com.projectx.spa.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.projectx.spa.interfaces.OnAuthListener;
import com.projectx.spa.interfaces.OnGetDataListener;
import com.projectx.spa.interfaces.Settable;
import com.projectx.spa.models.ParkingSlot;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class FbHelper {
    private final FirebaseFirestore firebaseFirestore;
    private final FirebaseAuth firebaseAuth;
    private final Context context;

    public FbHelper(Context context) {
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.context = context;
    }

    /**
     * FirebaseUser authentication using email and password
     */
    public void authenticateUser(String email, String password, OnAuthListener listener) {
        firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        listener.onSuccess(authResult);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFailure(e.getMessage());
            }
        });
    }

    /**
     * Returns the FirebaseFirestore instance
     */
    public FirebaseFirestore getFirebaseFirestore() {
        return firebaseFirestore;
    }

    /**
     * Returns a DocumentReference representation of the string object (DocumentReference).
     *
     * @param documentReference value can be a Document path or Document id
     */
    public DocumentReference toDocumentReference(String documentReference) {
        return firebaseFirestore.document(documentReference);
    }

    /**
     * Adds Parking slot object to Firestore
     */
    /*public void addDataToFirestore(ParkingSlot parkingSlot) {
        DocumentReference documentReference = firebaseFirestore.collection(Constants.PARKING_SLOTS).document();
        // db.collection(COLLECTIONS).document("area1").set(slot);
        parkingSlot.setId(documentReference.getId());
        documentReference
                .set(parkingSlot)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        makeSuccessToast("Data added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeFailureToast("Data could not be added successfully");
                    }
                });
    }*/

    /**
     * Adds an object to the Firebase Cloud Firestore based on collectionPath and documentPath.
     *
     * @param object         is the object that needs to be added to firestore
     * @param collectionPath is the path of a Collection
     * @param documentPath   is the path of a Document
     * @param listener       is the listener which is used to handle callbacks i.e, onSuccess and onFailure
     * @implNote documentPath is optional, if value is null then, this method will generate a new unique
     * id for the document that needs to be created
     */
    public <T extends Settable> void addDataToFirestore(@NonNull T object, @NonNull String collectionPath,
                                                        @Nullable String documentPath, @NonNull final OnGetDataListener listener) {
        DocumentReference documentReference;
        CollectionReference collectionReference = firebaseFirestore.collection(collectionPath);

        if (documentPath == null) {
            documentReference = collectionReference.document();
        } else {
            documentReference = collectionReference.document(documentPath);
        }

        object.setId(documentReference.getId());

        documentReference
                .set(object)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.onSuccess(documentReference);

                        makeSuccessToast("Data added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure("Adding data failed");

                        makeFailureToast("Data could not be added successfully");
                    }
                });
    }

    public List<ParkingSlot> readDataFromFirestore() {
        List<ParkingSlot> parkingSlots = new ArrayList<>();
        firebaseFirestore.collection(Constants.PARKING_SLOTS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    // creating ParkingSlot object
                                    ParkingSlot parkingSlot = document.toObject(ParkingSlot.class);
                                    parkingSlots.add(parkingSlot);
                                    makeSuccessToast("Data read successfully");
                                } else {
                                    makeFailureToast("doc does not exist");
                                }
                            }
                        } else {
                            makeFailureToast("Error getting documents." + task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeFailureToast("Data could not be added successfully");
                    }
                });
        return parkingSlots;
    }


    private void makeToast(String toastMessage) {
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    }

    private void makeSuccessToast(String toastMessage) {
        Log.i("TAG", toastMessage);
        Toasty.success(context, toastMessage, Toast.LENGTH_SHORT, true).show();
    }

    private void makeFailureToast(String toastMessage) {
        Log.e("TAG", toastMessage);
        Toasty.error(context, toastMessage, Toast.LENGTH_SHORT, true).show();
    }

    // TODO: 10-07-2021 trackMultipleDocuments
    /*private <T> void trackMultipleDocuments(String collectionReference, List<T> objectList, ) {
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
                        ParkedVehicle parkedVehicle = dc.getDocument().toObject();
                        String str = parkedVehicle.toString();

                        switch (dc.getType()) {
                            case ADDED:
                                objectList.add(parkedVehicle);
                                parkedVehiclesAdapter.notifyItemInserted(objectList.size() - 1);
                                Log.d("ADDED", "New : " + str);
//                                makeToast("ADDED\n" + str);
                                break;
                            case MODIFIED:
                                try {
                                    int index = objectList.indexOf(parkedVehicle);
                                    objectList.set(index, parkedVehicle);
                                    parkedVehiclesAdapter.notifyDataSetChanged();
                                } catch (IndexOutOfBoundsException indexException) {
                                    indexException.printStackTrace();
                                }
                                Log.d("MODIFIED", "Modified : " + str);
//                                makeToast("MODIFIED\n" + str);
                                break;
                            case REMOVED:
                                try {
                                    int index = objectList.indexOf(parkedVehicle);
                                    objectList.remove(parkedVehicle);
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
                *//*for (QueryDocumentSnapshot doc : value) {
                    if (doc.exists()) {
                        Map<String, Object> data = doc.getData();
                        String str = "name: " + data.get("name") + "\nemail: " + data.get("email") + "\nrandomInt: " + data.get("randomInt");
                        Log.d("TAG1", str);
                        makeToast(str);
                    }
                }*//*
            }
        });

        // Stop listening to changes
//        registration.remove();
    }*/

}