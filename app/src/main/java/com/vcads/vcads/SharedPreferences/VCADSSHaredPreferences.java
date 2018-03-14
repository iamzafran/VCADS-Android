package com.vcads.vcads.SharedPreferences;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Zaly on 3/9/2018.
 */

public class VCADSSHaredPreferences {
    private static String PREF_LICENSE_PLATE = "PREF_LICENSE_PLATE";

    public static String getLicensePlate(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_LICENSE_PLATE, null);
    }

    public static void setPrefLicensePlate(Context context,String license_plate)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_LICENSE_PLATE,license_plate).apply();
    }

}
