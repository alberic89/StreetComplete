package de.westnordost.streetcomplete.data.platform

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService
//import de.westnordost.streetcomplete.data.preferences.Preferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
//import org.koin.android.ext.android.inject

class InternetConnectionState(context: Context) {
    val isConnectedFlow: Flow<Boolean> = callbackFlow {
        send(isConnected)

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        val networkCallback = object : NetworkCallback() {
            override fun onAvailable(network: Network) { trySend(isConnected) }
            override fun onLost(network: Network) { trySend(isConnected) }
            override fun onUnavailable() { trySend(isConnected) }
        }

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        awaitClose { connectivityManager.unregisterNetworkCallback(networkCallback) }
    }

    //private val prefs by inject<Preferences>()
    //private val ignoreConnectionState: Boolean get() = prefs.ignoreConnectionState

    val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    if (capabilities != null) {
        private val overideConnection: Boolean get() = true
    }

    val isConnected: Boolean get() =
        connectivityManager.activeNetworkInfo?.isConnected == true || overideConnection
    //|| ignoreConnectionState

    

    private val connectivityManager = context.getSystemService<ConnectivityManager>()!!
}
