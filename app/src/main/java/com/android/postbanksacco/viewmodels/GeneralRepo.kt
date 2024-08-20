package com.android.postbanksacco.viewmodels

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.android.postbanksacco.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import javax.inject.Inject

class GeneralRepo @Inject constructor(private val api: ApiService) {

    suspend fun commonTransactionRequest(endPoint: String, reqBody: JSONObject): ResponseBody {
        val body = reqBody.toString().toRequestBody("application/json".toMediaTypeOrNull())
        return withContext(Dispatchers.IO) {
            api.commonPostRequest(endPoint, body)
        }
    }
    suspend fun commonPostWithAuthRequest(endPoint: String, reqBody: JSONObject,tkn:String): ResponseBody {
        val body = reqBody.toString().toRequestBody("application/json".toMediaTypeOrNull())
        return withContext(Dispatchers.IO) {
            api.commonPostWithAuthRequest(endPoint, body,tkn)
        }
    }
    suspend fun commonGetRequest(endPoint: String,tkn:String): ResponseBody {
        return withContext(Dispatchers.IO) {
            api.commonGetRequest(endPoint,tkn)
        }
    }

    // Helper function to deserialize the response
    inline fun <reified T> parseResponse(responseBody: ResponseBody): T? {
        return try {
            val json = responseBody.string()
            Gson().fromJson(json, T::class.java)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            null
        }
    }

}