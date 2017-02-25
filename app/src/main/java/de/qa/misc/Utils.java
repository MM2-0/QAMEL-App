package de.qa.misc;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Utils {

    public static final String TAG = Utils.class.getSimpleName();

    public static boolean isOffline(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) return true;
        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            //Device is connected to Wi-Fi
            return false;
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            TelephonyManager telephonyManager
                    = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            // Device is connected to 2G network
            if (is2gNetwork(telephonyManager.getNetworkType())) return true;
            // Device is connected to 3G or 4G network.
            // Check if user allows the use of mobile networks.
            return !Prefs.getBoolean(context, Prefs.KEY_USE_MOBILE_DATA, false);
        }
        return true;
    }

    private static boolean is2gNetwork(int type) {
        return type == TelephonyManager.NETWORK_TYPE_GPRS
                || type == TelephonyManager.NETWORK_TYPE_EDGE
                || type == TelephonyManager.NETWORK_TYPE_CDMA
                || type == TelephonyManager.NETWORK_TYPE_1xRTT
                || type == TelephonyManager.NETWORK_TYPE_IDEN;
    }

    public static String sparQlQueryUrl(String query) {
        String baseUrl = "http://live.dbpedia.org/sparql?query=";
        String parameter = "";
        try {
            parameter = URLEncoder.encode(query, "UTF-8");
            parameter += "&format=" + URLEncoder.encode("application/json", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return baseUrl + parameter;
    }
}
