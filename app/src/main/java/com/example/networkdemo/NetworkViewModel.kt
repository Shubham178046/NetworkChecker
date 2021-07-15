package com.example.networkdemo

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import java.lang.Exception

class NetworkViewModel : ViewModel() {
    val validNetworks: MutableSet<Network> = HashSet()

    lateinit var networkCallback: ConnectivityManager.NetworkCallback
    var networkStat: MutableLiveData<Boolean>? = MutableLiveData()
    var Stat: MutableLiveData<Boolean>? = MutableLiveData(false)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun registerNetwork(context: Context) {
        Stat!!.value = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        viewModelScope.launch {
            val result = async { createNetworkCallback(cm) }
            val networkRequest = async {
                NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()
            }
            cm.registerNetworkCallback(networkRequest.await(), result.await())
        }
        // networkCallback = createNetworkCallback(cm)

    }

    /*fun getNetworkResult() = liveData(Dispatchers.IO) {
        emit(networkStat!!.value).takeIf { networkStat != null }
    }*/

    private suspend fun createNetworkCallback(cm: ConnectivityManager) =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            object : ConnectivityManager.NetworkCallback() {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun onAvailable(network: Network) {
                    Log.d(TAG, "onAvailable: ${network}")
                    val networkCapabilities = cm.getNetworkCapabilities(network)
                    val hasInternetCapability =
                        networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
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
                                    if (validNetworks != null) {
                                        networkStat!!.value = validNetworks.size > 0
                                        Stat!!.value = true
                                    }
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    networkStat!!.value = false
                                    Stat!!.value = true
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
                    networkStat!!.value = validNetworks.size > 0
                    Stat!!.value = true
                    validNetworks.remove(network)
                }

            }
        } else {
            TODO("VERSION.SDK_INT < LOLLIPOP")
        }

}