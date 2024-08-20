package com.android.postbanksacco.data.network
import android.content.Context
import android.net.ConnectivityManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
/**
 * LiveNetworkMonitor is a class meant to check the application's network connectivity
 *
 * @param context a [Context] object. This is what is used to get the system's connectivity manager
 * */
class LiveNetworkMonitor @Inject constructor(
    private val context: Context
): NetworkMonitor {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    override fun isConnected(): Boolean {
        val network =connectivityManager.activeNetwork
        return network != null
    }
}
interface NetworkMonitor {
    fun isConnected():Boolean
}
class NetworkMonitorInterceptor @Inject constructor(
    private val liveNetworkMonitor: NetworkMonitor
): Interceptor {
    @Throws(NoNetworkException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if(liveNetworkMonitor.isConnected()){
            return chain.proceed(chain.request())
        }else{
            throw NoNetworkException("no internet detected")
        }
    }
}
class NoNetworkException(message: String) : IOException(message)























/*
class LiveNetworkMonitoor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isInternetAvailable()) {
            throw NoNetworkException("No internet connection")
        }
        return chain.proceed(chain.request())
    }
    private fun isInternetAvailable(): Boolean {
      val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
*/
