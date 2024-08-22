package com.android.postbanksacco.utils.interfaces

import com.android.postbanksacco.data.model.ContactModel
import com.android.postbanksacco.data.model.HomeModel
import com.android.postbanksacco.data.response.HomeItemsData
import com.android.postbanksacco.data.response.SubscriboData


interface OnMenuItemClick {
    fun navigateTo(pos:Int,obj: HomeModel)
}

interface ContactCallBack {
    fun onItemSelected(cont: ContactModel)
}
interface OnSubsItemClick {
    fun navigateTo(service: SubscriboData)
}
