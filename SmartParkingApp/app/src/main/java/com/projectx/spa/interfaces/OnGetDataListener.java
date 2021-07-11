package com.projectx.spa.interfaces;

import com.google.firebase.firestore.DocumentReference;

public interface OnGetDataListener {
    // if data gets added successfully
    void onSuccess(DocumentReference dataSnapshotValue);

    // if data does not get added
    void onFailure(String errorMessage);

}