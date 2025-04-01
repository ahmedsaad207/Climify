package com.delighted2wins.climify.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NetworkManager(private val context: Context) {

    fun isNetworkAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) { // 21-27

            // networks
            val networks = cm.allNetworks// API 21-30

            // network info
            var networkInfo: NetworkInfo?//API 21-28

            // get state and return result
            for (network in networks) {
                networkInfo = cm.getNetworkInfo(network)//API 21-28

                networkInfo?.let {
                    if (it.state.equals(NetworkInfo.State.CONNECTED)) return true
                }
            }
        } else {
            //28->32

            // network
            val network = cm.activeNetwork ?: return false

            // network capabilities
            val networkCapabilities = cm.getNetworkCapabilities(network) ?: return false

            // return result
            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
        return false
    }

    fun observeNetworkChanges(): Flow<Boolean> = callbackFlow {
        val networkChangeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                trySend(isNetworkAvailable())
            }
        }
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(networkChangeReceiver, filter)

        awaitClose {
            context.unregisterReceiver(networkChangeReceiver)
        }
    }
}