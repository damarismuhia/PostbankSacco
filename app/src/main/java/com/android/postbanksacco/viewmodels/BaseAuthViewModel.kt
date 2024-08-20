package com.android.postbanksacco.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

/**
 *  Transactions and Requests Base Model class - Holds common methods for other viewModels
 * @property apiServiceHelperImpl ApiServiceHelperImpl
 * @property gson Gson
 * @property context Application
 * @property commonRequestResponseString MutableLiveData<ApiResponse<String>>
 * @constructor
 */
abstract class BaseAuthViewModel constructor(
   // private val aesApiService: ApiService,
    @ApplicationContext context: Context
) : ViewModel() {
    var defaultError = "Your Request failed, please try again later"
    var internetError = "No internet connection"
   // private var context = application
}
