package com.projectx.spa.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class UserSession {
    private final SharedPreferences sharedPreferences;
    private final Editor editor;

    public UserSession(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void createUserLoginSession(String email, String password) {
        editor.putBoolean(Constants.PREF_USER_LOGIN_STATUS, true);
//        editor.putString(Constants.PREF_NAME, name);
        editor.putString(Constants.PREF_EMAIL, email);
        editor.putString(Constants.PREF_PASSWORD, password);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();

//        user.put(Constants.PREF_NAME, sharedPreferences.getString(Constants.PREF_NAME, null));
        user.put(Constants.PREF_EMAIL, sharedPreferences.getString(Constants.PREF_EMAIL, null));
        user.put(Constants.PREF_PASSWORD, sharedPreferences.getString(Constants.PREF_PASSWORD, null));

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