package edu.sv.ues.mv12013.splashparcial2.core;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String PREF = "splash_parcial2_prefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_REMEMBER = "remember";

    private final SharedPreferences sp;
    public PreferenceManager(Context ctx) {
        sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void saveSession(String email, boolean remember) {
        sp.edit().putString(KEY_EMAIL, email).putBoolean(KEY_REMEMBER, remember).apply();
    }

    public String getEmail() { return sp.getString(KEY_EMAIL, null); }
    public boolean isRemembered() { return sp.getBoolean(KEY_REMEMBER, false); }
    public void clear() { sp.edit().clear().apply(); }
}