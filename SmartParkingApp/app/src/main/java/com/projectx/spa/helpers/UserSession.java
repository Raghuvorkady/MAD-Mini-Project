package com.projectx.spa.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class UserSession {
    private final SharedPreferences sharedPreferences;
    private final Editor editor;

    public UserSession(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void createUserLoginSession(@NonNull String id, @NonNull String name, @NonNull String email) {
        editor.putBoolean(Constants.PREF_USER_LOGIN_STATUS, true);
        editor.putString(Constants.PREF_ID, id);
        editor.putString(Constants.PREF_NAME, name);
        editor.putString(Constants.PREF_EMAIL, email);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();

        user.put(Constants.PREF_NAME, sharedPreferences.getString(Constants.PREF_NAME, null));
        user.put(Constants.PREF_EMAIL, sharedPreferences.getString(Constants.PREF_EMAIL, null));
        user.put(Constants.PREF_ID, sharedPreferences.getString(Constants.PREF_ID, null));

        return user;
    }

    public void clearUserData() {
        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();
    }

    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(Constants.PREF_USER_LOGIN_STATUS, false);
    }
}