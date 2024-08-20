package com.android.postbanksacco.data.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url


interface ApiService {
    @POST
    suspend fun commonPostRequest(@Url endPoint: String,
                                         @Body body: RequestBody
    ): ResponseBody

    @GET
    suspend fun commonGetRequest(@Url endPoint: String,@Header("Authorization") authHeader: String): ResponseBody

    @POST
    suspend fun commonPostWithAuthRequest(@Url endPoint: String,
                                  @Body body: RequestBody,@Header("Authorization")authHeader: String): ResponseBody

}
