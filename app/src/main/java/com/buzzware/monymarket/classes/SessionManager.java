package com.buzzware.monymarket.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.buzzware.monymarket.Models.UserModel;
import com.google.gson.Gson;


public class SessionManager {

    private static SharedPreferences.Editor prefsEditor;
    private static SharedPreferences preferences;
    private static SessionManager sessionManager;

    public static SessionManager getInstance() {
        if (sessionManager == null)
            sessionManager = new SessionManager();
        return sessionManager;
    }

    public void setUser(Context c, UserModel userModel) {
        preferences = PreferenceManager
                .getDefaultSharedPreferences(c);
        prefsEditor = preferences.edit();
        prefsEditor.putString("USER_INFO", new Gson().toJson(userModel));
        prefsEditor.commit();
    }

    public UserModel getUser(Context c) {
        preferences = PreferenceManager
                .getDefaultSharedPreferences(c);
        String s = preferences.getString("USER_INFO", null);
        return new Gson().fromJson(s, UserModel.class);
    }

}
