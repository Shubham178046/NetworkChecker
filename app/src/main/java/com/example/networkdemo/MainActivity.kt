package com.example.networkdemo

import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    var networkViewModel: NetworkViewModel? = null
    var customdialog: Dialog? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        networkViewModel = ViewModelProviders.of(this).get(NetworkViewModel::class.java)
        findViewById<Button>(R.id.btnCheckInternet).setOnClickListener {
            showProgress(this)
            if (MyReachability.getConnectivityStatus(this) != 0 && MyReachability.getConnectivityStatusString(
                    this
                ) != MyReachability.NOT_CONNECT
            ) {
                networkViewModel!!.registerNetwork(this)
                networkViewModel!!.Stat!!.observe(this, Observer {
                    if (it == true) {
                        if (networkViewModel!!.networkStat!!.value == true) {
                            closeProgress()
                            Toast.makeText(
                                this@MainActivity,
                                "Network Available",
                                Toast.LENGTH_LONG
                            )
                                .show()
                            networkViewModel!!.Stat!!.removeObservers(this)
                        } else {
                            closeProgress()
                            Toast.makeText(
                                this@MainActivity,
                                "Network UnAvailable",
                                Toast.LENGTH_LONG
                            )
                                .show()
                            networkViewModel!!.Stat!!.removeObservers(this)
                        }
                    }
                })
            }else{
                closeProgress()
                Toast.makeText(
                    this@MainActivity,
                    "Network UnAvailable",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkViewModel!!.Stat!!.removeObservers(this)
    }

    fun showProgress(ctx: Context) {
        if (customdialog != null) {
            try {
                if (customdialog!!.isShowing()) {
                    customdialog!!.dismiss()
                }
            } catch (e: Exception) {
            }
        }
        customdialog = Dialog(ctx, R.style.ActivityDialog)
        customdialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customdialog!!.getWindow()?.setGravity(Gravity.CENTER)
        customdialog!!.setCancelable(false)
        val inflater = layoutInflater
        val dialogLayout: View = inflater.inflate(R.layout.custom_loader, null)
        customdialog!!.setContentView(dialogLayout)
        Glide.with(this).load(R.drawable.loading)
            .into(customdialog!!.findViewById(R.id.custom_loading_imageView))
        customdialog!!.show()
    }

    fun closeProgress() {
        try {
            if (customdialog != null && customdialog!!.isShowing()) {
                customdialog!!.dismiss()
            }
        } catch (e: Exception) {
        }
    }
}