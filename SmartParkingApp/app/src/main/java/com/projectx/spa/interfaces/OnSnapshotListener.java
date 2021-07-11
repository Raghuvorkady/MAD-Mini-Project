package com.projectx.spa.interfaces;

public interface OnSnapshotListener {
    // if data gets added successfully
    <T> void onSuccess(T object);

    // if data does not get added
    void onFailure(String errorMessage);

}