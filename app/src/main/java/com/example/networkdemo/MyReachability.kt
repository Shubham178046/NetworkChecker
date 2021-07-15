package com.example.networkdemo

import android.content.Context
import android.net.ConnectivityManager
import android.provider.SyncStateContract
import android.telephony.TelephonyManager
import android.util.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object MyReachability {
    var TYPE_WIFI = 1
    var TYPE_MOBILE = 2
    var TYPE_NOT_CONNECTED = 0
    val CONNECT_TO_WIFI = "WIFI"
    val NOT_CONNECT = "NOT_CONNECT"
    fun getConnectivityStatus(context: Context): Int {
        val activeNetwork = (context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager).activeNetworkInfo
        if (null != activeNetwork) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) return TYPE_WIFI
            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) return TYPE_MOBILE
        }
        return TYPE_NOT_CONNECTED
    }

    fun getConnectivityStatusString(context: Context): String {
        val conn = getConnectivityStatus(context)
        var status: String? = null
        if (conn == TYPE_WIFI) {
            status = CONNECT_TO_WIFI
        } else if (conn == TYPE_MOBILE) {
            status = getNetworkClass(context)
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = NOT_CONNECT
        }
        return status!!
    }

    private fun getNetworkClass(context: Context): String {
        val info = (context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager).activeNetworkInfo
        if (info == null || !info.isConnected)
            return "-" //not connected
        if (info.type == ConnectivityManager.TYPE_WIFI)
            return "WIFI"
        if (info.type == ConnectivityManager.TYPE_MOBILE) {
            val networkType = info.subtype
            when (networkType) {
                TelephonyManager.NETWORK_TYPE_HSPAP  //api<13 : replace by 15
                -> return "3G"
                TelephonyManager.NETWORK_TYPE_LTE    //api<11 : replace by 13
                -> return "4G"
                else -> return "UNKNOWN"
            }
        }
        return "UNKNOWN"
    }

    private fun hasNetworkAvailable(context: Context): Boolean {
        val service = Context.CONNECTIVITY_SERVICE
        val manager = context.getSystemService(service) as ConnectivityManager?
        val network = manager?.activeNetworkInfo
        Log.d("classTag", "hasNetworkAvailable: ${(network != null)}")
        return (network?.isConnected) ?: false
    }

    fun hasInternetConnected(context: Context): Boolean {
        if (getConnectivityStatus(context) != 0 && getConnectivityStatusString(
                context
            ) != NOT_CONNECT
        ) {
            try {
                val connection =
                    URL("https://www.google.com/").openConnection() as HttpURLConnection
                connection.setRequestProperty("User-Agent", "Test")
                connection.setRequestProperty("Connection", "close")
                connection.connectTimeout = 1500 // configurable
                connection.connect()
                Log.d("classTag", "hasInternetConnected: ${(connection.responseCode == 200)}")
                return connection.responseCode == 200
            } catch (e: IOException) {
                Log.e("classTag", "Error checking internet connection", e)
            }
        } else {
            Log.w("classTag", "No network available!")
        }
        Log.d("classTag", "hasInternetConnected: false")
        return false
    }

    fun hasServerConnected(context: Context): Boolean {
        if (hasNetworkAvailable(context)) {
            try {
                val connection = URL("https://www.google.com").openConnection() as HttpURLConnection
                connection.setRequestProperty("User-Agent", "Test")
                connection.setRequestProperty("Connection", "close")
                connection.connectTimeout = 1500 // configurable
                connection.connect()
                Log.d("classTag", "hasServerConnected: ${(connection.responseCode == 200)}")
                return (connection.responseCode == 200)
            } catch (e: IOException) {
                Log.e("classTag", "Error checking internet connection", e)
            }
        } else {
            Log.w("classTag", "Server is unavailable!")
        }
        Log.d("classTag", "hasServerConnected: false")
        return false
    }
}