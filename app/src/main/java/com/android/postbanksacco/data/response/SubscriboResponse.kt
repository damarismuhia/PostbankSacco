package com.android.postbanksacco.data.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubscriboResponse(
    @SerializedName("data")
    val `data`: List<SubscriboData>,
    @SerializedName("message")
    val message: String
):Parcelable
@Parcelize
    data class SubscriboData(
        @SerializedName("amountPaid")
        val amountPaid: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("serviceName")
        val serviceName: String,
        @SerializedName("subscriberEmail")
        val subscriberEmail: String
    ):Parcelable
