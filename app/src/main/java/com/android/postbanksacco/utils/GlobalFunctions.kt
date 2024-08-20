package com.android.postbanksacco.utils

import android.view.View
import com.android.postbanksacco.data.network.NoNetworkException
import okhttp3.internal.http2.ConnectionShutdownException
import org.json.JSONObject
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException
const val tagTracker = "TAG TRACKER"

fun resolveException(e: Exception): String {
    val message = "Error occurred.Please try again later."
    when (e) {
        is NoNetworkException ->{
            return "no internet detected"
        }
        is SocketTimeoutException -> {
            return e.message ?: "An error occurred while trying to connect to the service. Please try again later"
        }
        is ConnectException ->{
            return "Please check your internet and try again"
        }
        is SocketException -> {
            return "Socket error occurred.Please check your internet and try again later."
        }
        is ConnectionShutdownException -> {
            return "Connection shutdown. Please check your internet and try again later"
        }
        is UnknownHostException, is SSLHandshakeException -> {
            return "Unable to connect to server.Please try again later."
        }

    }
    if (e is HttpException) {
            val errorResponseJson = e.response()?.errorBody()?.string()
            val errorJson = JSONObject(errorResponseJson ?: "")
            return errorJson.optString("message", message)

    }
    return message
}

fun View.makeVisible() {
    visibility = View.VISIBLE
}
fun View.makeGone() {
    visibility = View.INVISIBLE
}




