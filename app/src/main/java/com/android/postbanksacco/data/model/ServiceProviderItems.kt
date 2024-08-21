package com.android.postbanksacco.data.model

data class ServiceProviderItems(
    val image: Int,
    val title: String,
    val mno: String
){
    override fun toString(): String {
        return title
    }
}

