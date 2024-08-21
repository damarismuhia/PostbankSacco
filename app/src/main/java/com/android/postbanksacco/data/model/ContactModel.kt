package com.android.postbanksacco.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactModel(
  var name:String,
  var phonenumber:String,
 ):Parcelable

