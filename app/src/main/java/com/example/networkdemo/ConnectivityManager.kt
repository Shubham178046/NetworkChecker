package com.example.networkdemo

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

class ConnectivityManager
constructor(
    application: Context,
) {
    private val connectionLiveData = ConnectionLiveData(application)

    // observe this in ui
    var isNetworkAvailable = MutableLiveData<Boolean>(false)

    fun registerConnectionObserver(lifecycleOwner: LifecycleOwner) {
        Log.d(TAG, "registerConnectionObserver: "+"Step 1")


       /* connectionLiveData.observe(lifecycleOwner, { isConnected ->
            Log.d(TAG, "registerConnectionObserver: "+"Step Final")
            isConnected?.let { isNetworkAvailable.value = it }
            connectionLiveData.removeObservers(lifecycleOwner)
        })*/
    }

    fun unregisterConnectionObserver(lifecycleOwner: LifecycleOwner) {
        connectionLiveData.removeObservers(lifecycleOwner)
    }
}