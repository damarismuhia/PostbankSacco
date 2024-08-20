package com.android.postbanksacco.utils.interfaces

import com.android.postbanksacco.data.model.HomeModel
import com.android.postbanksacco.data.response.HomeItemsData
import com.android.postbanksacco.data.response.SubscriboData


interface OnMenuItemClick {
    fun navigateTo(obj: HomeModel)
}
interface OnSubsItemClick {
    fun navigateTo(service: SubscriboData)
}