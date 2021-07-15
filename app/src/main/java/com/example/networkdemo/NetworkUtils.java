package com.example.networkdemo;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {
    private static final String GENERATE_204 = "https://www.google.com";

    /** Checks if the internet connection is available. */
    public static boolean isNetworkAvailable(@Nullable ConnectivityManager connectivityManager) {
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            return false;
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(GENERATE_204).openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setDefaultUseCaches(false);
            connection.setUseCaches(false);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                return true;
            }
        } catch (IOException e) {
            // Does nothing.
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return false;
    }

    private NetworkUtils() {}
}
