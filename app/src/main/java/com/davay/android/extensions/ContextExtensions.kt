package com.davay.android.extensions

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities


fun Context.isInternetReachable(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork?.let {
        connectivityManager.getNetworkCapabilities(it)
    }

    return activeNetwork?.let {
        val hasWifiTransport = it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        val hasCellularTransport = it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        val hasEthernetTransport = it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)

        hasWifiTransport || hasCellularTransport || hasEthernetTransport
    } ?: false
}
