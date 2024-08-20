package com.android.postbanksacco.ui.dialog

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// model the details in the dialog recyclerview
@Parcelize
data class TransactionsModel (
    val label : String,
    val content : String,
):Parcelable



