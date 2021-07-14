package com.projectx.spa.helpers;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.orhanobut.logger.Logger;
import com.projectx.spa.interfaces.OnAuthListener;
import com.projectx.spa.interfaces.OnGetDataListener;
import com.projectx.spa.interfaces.OnMultiDocumentListener;
import com.projectx.spa.interfaces.OnSnapshotListener;
import com.projectx.spa.interfaces.Settable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                        FirebaseUser user = authResult.getUser();
                        if (user != null) {
                            listener.onSuccess(user);
                        } else {
                            listener.onFailure("FirebaseUser is null");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure(e.getMessage());
                    }
                });
    }

    /**
     * FirebaseUser registration method
     */
    public void registerUser(String email, String password, OnAuthListener listener) {
        firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = authResult.getUser();
                        if (user != null) {
                            listener.onSuccess(user);
                        } else {
                            listener.onFailure("FirebaseUser is null");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure(e.getMessage());
                    }
                });
    }

    /**
     * Sends a mail to reset your password
     */
    public void resetPassword(String email, OnSnapshotListener listener) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.onSuccess("Reset link sent to your email");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure("Error! Reset Link is not sent" + e.getMessage());
                    }
                });
    }

    /**
     * To sign out the current Firebase User
     *
     * @param listener to listen for onSuccess() and onFailure()
     */
    public void logoutUser(OnSnapshotListener listener) {
        firebaseAuth.signOut();
        if (firebaseAuth.getCurrentUser() == null) {
            listener.onSuccess("Sign out successful");
        } else {
            listener.onFailure("Sign out failed");
        }
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
     * Adds an object to the Firebase Cloud Firestore based on collectionPath and documentPath.
     *
     * @param object         is the object that needs to be added to Firestore
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

    /**
     * To read a particular document from the Firestore
     *
     * @param className    is the Class name(Model) of the object which
     *                     will be returned back
     * @param documentPath is the document path
     * @param listener     is the event listener
     * @implNote Inorder to access the object which is returned in onSuccess(),
     * you need to cast it to it's respective Class
     */
    public <T> void readDocumentFromFirestore(Class<T> className, String documentPath, OnSnapshotListener listener) {
        DocumentReference documentReference = toDocumentReference(documentPath);

        documentReference
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        T snapshotObject = snapshot.toObject(className);

                        if (snapshotObject != null) {
                            listener.onSuccess(snapshotObject);
                        } else {
                            listener.onFailure("The document does not exist!");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure(e.getMessage());
                    }
                });
    }

    /**
     * To read all the documents in a Collection from the Firestore
     *
     * @param className      is the Class name(Model) of the object which
     *                       will be returned back
     * @param collectionPath is the Collection path
     * @param listener       is the event listener
     * @implNote Inorder to access the object which is returned in onSuccess(),
     * you need to cast it to it's respective Class
     */
    public <T> void readCollectionFromFirestore(Class<T> className, String collectionPath, OnSnapshotListener listener) {
        List<T> objects = new ArrayList<>();
        firebaseFirestore.collection(collectionPath)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && (task.getResult() != null)) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    T object = document.toObject(className);
                                    objects.add(object);
//                                    makeSuccessToast("Data read successfully");
                                } else {
//                                    makeFailureToast("doc does not exist");
                                    listener.onFailure("doc does not exist");
                                }
                                listener.onSuccess(objects);
                            }
                        } else {
                            listener.onFailure("Error getting documents." + task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure("Data could not be added successfully " + e.getMessage());
                    }
                });
    }

    /**
     * To track all the documents in a Collection for any changes
     *
     * @param className      is the Class name(Model) of the object which
     *                       will be returned back
     * @param collectionPath is the Collection path
     * @param listener       is the event listener
     * @implNote Inorder to access the object which is returned in onAdded(), onModified(), onFailure()
     * you need to cast it to it's respective Class
     */
    public <T> void trackDocuments(Class<T> className, String collectionPath, OnMultiDocumentListener listener) {
        Query query = firebaseFirestore.collection(collectionPath);

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
                        T object = dc.getDocument().toObject(className);
                        String str = object.toString();

                        switch (dc.getType()) {
                            case ADDED:
                                listener.onAdded(object);
                                Logger.d("ADDED" + str);
                                break;
                            case MODIFIED:
                                listener.onModified(object);
                                Logger.d("MODIFIED" + str);
                                break;
                            case REMOVED:
                                listener.onRemoved(object);
                                Logger.d("REMOVED" + str);
                                break;
                        }
                    }
                }

                /*// it will read all the documents present in the Collection on detecting any change
                for (QueryDocumentSnapshot doc : value) {
                    if (doc.exists()) {
                        Map<String, Object> data = doc.getData();
                        String str = "name: " + data.get("name") + "\n email: " + data.get("email") + "\n randomInt: " + data.get("randomInt");
                        Logger.d("TAG1", str);
                        makeToast(str);
                    }
                }*/
            }
        });

        // Stop listening to changes
//        registration.remove();
    }

    /**
     * To track a single document in a Collection for any changes
     *
     * @param className    is the Class name(Model) of the object which
     *                     will be returned back
     * @param documentPath is the Collection path
     * @param listener     is the event listener
     * @implNote Inorder to access the object which is returned in onAdded(), onModified(), onFailure()
     * you need to cast it to it's respective Class
     */
    public <T> void trackDocument(Class<T> className, String documentPath, OnSnapshotListener listener) {
        DocumentReference documentReference = toDocumentReference(documentPath);
        documentReference
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
                        if (e != null) {
                            Logger.w("Listen failed: " + e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            Logger.d(snapshot);
                            T object = snapshot.toObject(className);
                            if (object != null) {
                                listener.onSuccess(object);
                            }
                        } else {
                            Logger.d("Current data: null");
                            listener.onFailure("Current data: null");
                        }
                    }
                });
    }

    /**
     * Updates a particular field of a document
     */
    public void updateField(String documentPath, Map<String, Object> map, OnSnapshotListener listener) {
        DocumentReference documentReference = toDocumentReference(documentPath);
        documentReference
                .update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.onSuccess("Update successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onSuccess("Update failed : " + e.getMessage());
                    }
                });
    }

    /**
     * Deletes a particular a document in a Collection
     */
    public void deleteDocument(String documentPath, OnSnapshotListener listener) {
        DocumentReference documentReference = toDocumentReference(documentPath);
        documentReference
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.onSuccess("Delete successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onSuccess("Delete failed : " + e.getMessage());
                    }
                });
    }

    private void makeToast(String toastMessage) {
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    }

    private void makeSuccessToast(String toastMessage) {
        Logger.i(toastMessage);
        Toasty.success(context, toastMessage, Toast.LENGTH_SHORT, true).show();
    }

    private void makeFailureToast(String toastMessage) {
        Logger.e(toastMessage);
        Toasty.error(context, toastMessage, Toast.LENGTH_SHORT, true).show();
    }

}