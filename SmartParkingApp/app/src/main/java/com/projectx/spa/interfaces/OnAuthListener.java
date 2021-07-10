package com.projectx.spa.interfaces;

import com.google.firebase.auth.AuthResult;

public interface OnAuthListener {
    void onSuccess(AuthResult authResult);

    void onFailure(String errorString);
}