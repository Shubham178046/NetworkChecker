package com.example.networkdemo

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val TAG = "C-Manager"

/**
 * Save all available networks with an internet connection to a set (@validNetworks).
 * As long as the size of the set > 0, this LiveData emits true.
 * MinSdk = 21.
 *
 * Inspired by:
 * https://github.com/AlexSheva-mason/Rick-Morty-Database/blob/master/app/src/main/java/com/shevaalex/android/rickmortydatabase/utils/networking/ConnectionLiveData.kt
 */
class ConnectionLiveData(context: Context) : LiveData<Boolean>() {


    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private val validNetworks: MutableSet<Network> = HashSet()

    private fun checkValidNetworks() {
        Log.d(TAG, "checkValidNetworks: " + "Come")
        postValue(validNetworks.size > 0)
        Log.d(TAG, "checkValidNetworks: " + "Value" + value + validNetworks.size)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActive() {
        networkCallback = createNetworkCallback()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .build()
        cm.registerNetworkCallback(networkRequest, networkCallback)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onInactive() {
        cm.unregisterNetworkCallback(networkCallback)
    }

    private fun createNetworkCallback() =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            object : ConnectivityManager.NetworkCallback() {

                /*
                  Called when a network is detected. If that network has internet, save it in the Set.
                  Source: https://developer.android.com/reference/android/net/ConnectivityManager.NetworkCallback#onAvailable(android.net.Network)
                 */
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun onAvailable(network: Network) {
                    Log.d(TAG, "onAvailable: ${network}")
                    val networkCapabilities = cm.getNetworkCapabilities(network)
                    val hasInternetCapability =
                        networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)
                    Log.d(TAG, "onAvailable: ${network}, $hasInternetCapability")
                    if (hasInternetCapability == true) {
                        Log.d(TAG, "onAvailable: " + "hasInternetCapability")
                        // check if this network actually has internet
                        CoroutineScope(Dispatchers.IO).launch {
                            val hasInternet = DoesNetworkHaveInternet.execute(network.socketFactory)
                            if (hasInternet) {
                                Log.d(TAG, "onAvailable: " + "hasInternet")
                                withContext(Dispatchers.Main) {
                                    Log.d(TAG, "onAvailable: adding network. ${network}")
                                    validNetworks.add(network)
                                    checkValidNetworks()
                                }
                            }
                        }
                    } else {
                        Log.d(TAG, "onAvailable: " + "Else")
                    }
                }

                /*
                  If the callback was registered with registerNetworkCallback() it will be called for each network which no longer satisfies the criteria of the callback.
                  Source: https://developer.android.com/reference/android/net/ConnectivityManager.NetworkCallback#onLost(android.net.Network)
                 */
                override fun onLost(network: Network) {
                    Log.d(TAG, "onLost: ${network}")
                    validNetworks.remove(network)
                    checkValidNetworks()
                }

            }
        } else {
            TODO("VERSION.SDK_INT < LOLLIPOP")
        }

}