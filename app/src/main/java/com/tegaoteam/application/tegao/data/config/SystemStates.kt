package com.tegaoteam.application.tegao.data.config

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.tegaoteam.application.tegao.TegaoApplication

object SystemStates {
    private val app = TegaoApplication.instance

    // internet availability
    private val connectivityManager = app.getSystemService(ConnectivityManager::class.java)
    fun isInternetAvailable() = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}