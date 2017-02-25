package de.qa.misc;

import android.content.Context;
import android.preference.PreferenceManager;

public class Prefs {

    public static final String KEY_USE_MOBILE_DATA = "use_mobile_data";

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defaultValue);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(key, value)
                .apply();
    }
}
