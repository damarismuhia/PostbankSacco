package com.android.postbanksacco.data.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class HomeItemsResponse(
    @SerializedName("data")
    val `data`: List<HomeItemsData>,
    @SerializedName("message")
    val message: String
)
@Parcelize
    data class HomeItemsData(
        @SerializedName("discountPercent")
        val discountPercent: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("pricing")
        val pricing: String,
        @SerializedName("serviceName")
        val serviceName: String
    ):Parcelable
