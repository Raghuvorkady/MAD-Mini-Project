package com.projectx.spa.interfaces;

import com.google.firebase.auth.FirebaseUser;

public interface OnAuthListener {

    void onSuccess(FirebaseUser firebaseUser);

    void onFailure(String errorMessage);

}