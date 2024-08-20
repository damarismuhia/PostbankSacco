package com.android.postbanksacco.data.response


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("data")
    val `data`: LoginData,
    @SerializedName("message")
    val message: String
)
    data class LoginData(
        @SerializedName("email")
        val email: String,
        @SerializedName("fullName")
        val fullName: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("msisdn")
        val msisdn: String,
        @SerializedName("token")
        val token: String
    )
