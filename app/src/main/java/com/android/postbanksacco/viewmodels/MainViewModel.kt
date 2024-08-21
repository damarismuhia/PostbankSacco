package com.android.postbanksacco.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.postbanksacco.data.model.ContactModel
import com.google.gson.Gson
import com.android.postbanksacco.data.network.ApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import com.android.postbanksacco.data.response.GeneralResponse
import com.android.postbanksacco.data.response.HomeItemsResponse
import com.android.postbanksacco.data.response.LoginResponse
import com.android.postbanksacco.data.response.SubscriboResponse
import com.android.postbanksacco.data.response.simulateLoginResponse
import com.android.postbanksacco.ui.dialog.TransactionsModel
import com.android.postbanksacco.utils.CacheKeys
import com.android.postbanksacco.utils.EncryptedPref
import com.android.postbanksacco.utils.extensions.showErrorDialog
import com.android.postbanksacco.utils.resolveException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repo: GeneralRepo, application: Application) :
    BaseAuthViewModel(application) {

    var _authSuccess = MutableLiveData<Boolean?>()
    val authSuccess: LiveData<Boolean?>
        get() = _authSuccess
    fun unsetAuthSuccess() {
        _authSuccess.value = null
    }
    var transactionList = MutableLiveData<List<TransactionsModel>>()
    var contactModel = MutableLiveData<ContactModel>()



}