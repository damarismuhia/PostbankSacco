package com.android.postbanksacco.utils

import android.view.LayoutInflater
import android.view.ViewGroup

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T
object  Constants{
    var baseUrl:String = ""
    var getRootFunction:Int = -1

}