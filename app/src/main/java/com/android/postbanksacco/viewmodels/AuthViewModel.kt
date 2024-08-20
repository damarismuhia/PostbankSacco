package com.android.postbanksacco.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.android.postbanksacco.data.network.ApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import com.android.postbanksacco.data.response.GeneralResponse
import com.android.postbanksacco.data.response.HomeItemsResponse
import com.android.postbanksacco.data.response.LoginResponse
import com.android.postbanksacco.data.response.SubscriboResponse
import com.android.postbanksacco.utils.resolveException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repo: GeneralRepo, application: Application) :
    BaseAuthViewModel(application) {
    fun generalPostReq(body: JSONObject,endPoint:String): LiveData<ApiResponse> {
        val myApiResponse = MutableLiveData<ApiResponse>()
        viewModelScope.launch(Dispatchers.IO)  {
            try {
                val responseBody: ResponseBody = repo.commonTransactionRequest(endPoint, body)
                val response: GeneralResponse? = repo.parseResponse(responseBody)
                if (response != null) {
                    myApiResponse.postValue(
                        ApiResponse(
                            response,
                            response.message)
                    )
                } else {
                    myApiResponse.postValue(
                        ApiResponse(null, "Unable to complete your request, Please try again later",
                        )
                    )
                }

            }
            catch (e: Exception) {
                e.printStackTrace()

                myApiResponse.postValue(ApiResponse(null, resolveException(e)))
            }
        }

        return myApiResponse
    }
    fun commonPostWithAuthRequest(body: JSONObject,endPoint:String,tkn: String): LiveData<ApiResponse> {
        val myApiResponse = MutableLiveData<ApiResponse>()
        viewModelScope.launch(Dispatchers.IO)  {
            try {
                val responseBody: ResponseBody = repo.commonPostWithAuthRequest(endPoint, body,tkn)
                val response: GeneralResponse? = repo.parseResponse(responseBody)
                if (response != null) {
                    myApiResponse.postValue(
                        ApiResponse(
                            response,
                            response.message)
                    )
                } else {
                    myApiResponse.postValue(
                        ApiResponse(null, "Unable to complete your request, Please try again later",
                        )
                    )
                }

            }catch (e:HttpException){
                if (e.code() == 401){
                    myApiResponse.postValue(ApiResponse(null, "Unauthorized access, Please login and try again!"))
                }else{
                    myApiResponse.postValue(ApiResponse(null, resolveException(e)))
                }

            }
            catch (e: Exception) {
                e.printStackTrace()

                myApiResponse.postValue(ApiResponse(null, resolveException(e)))
            }
        }

        return myApiResponse
    }
    fun loginReq(body: JSONObject): LiveData<ApiResponse> {
        val myApiResponse = MutableLiveData<ApiResponse>()
        viewModelScope.launch(Dispatchers.IO)  {
            try {
                val responseBody: ResponseBody = repo.commonTransactionRequest( "access/login",body)
                val response: LoginResponse? = repo.parseResponse(responseBody)
                if (response != null) {
                    myApiResponse.postValue(
                        ApiResponse(
                            response.data,
                            response.message)
                    )
                } else {
                    myApiResponse.postValue(
                        ApiResponse(null, "Unable to complete your request, Please try again later",
                        )
                    )
                }

            }
            catch (e: Exception) {
                e.printStackTrace()

                myApiResponse.postValue(ApiResponse(null, resolveException(e)))
            }

        }
        return myApiResponse
    }
    fun getServicesReq(tkn:String): LiveData<ApiResponse> {
        val myApiResponse = MutableLiveData<ApiResponse>()
        viewModelScope.launch(Dispatchers.IO)  {
            try {
                val responseBody: ResponseBody = repo.commonGetRequest( "service/services",tkn)
                val response: HomeItemsResponse? = repo.parseResponse(responseBody)
                if (response != null) {
                    myApiResponse.postValue(
                        ApiResponse(
                            response,
                            response.message)
                    )
                } else {
                    myApiResponse.postValue(
                        ApiResponse(null, "Unable to complete your request, Please try again later",
                        )
                    )
                }

            } catch (e:HttpException){
                if (e.code() == 401){
                    myApiResponse.postValue(ApiResponse(null, "Unauthorized access, Please login and try again!"))
                }else{
                    myApiResponse.postValue(ApiResponse(null, resolveException(e)))
                }

            }
            catch (e: Exception) {
                e.printStackTrace()

                myApiResponse.postValue(ApiResponse(null, resolveException(e)))
            }
        }
        return myApiResponse
    }
    fun getMySubscriptionsReq(endpoint:String,tkn:String): LiveData<ApiResponse> {
        val myApiResponse = MutableLiveData<ApiResponse>()
        viewModelScope.launch(Dispatchers.IO)  {
            try {
                val responseBody: ResponseBody = repo.commonGetRequest( endpoint,tkn)
                val response: SubscriboResponse? = repo.parseResponse(responseBody)
                if (response != null) {
                    myApiResponse.postValue(
                        ApiResponse(
                            response,
                            response.message)
                    )
                } else {
                    myApiResponse.postValue(
                        ApiResponse(null, "Unable to complete your request, Please try again later",
                        ))
                }
            }catch (e:HttpException){
                if (e.code() == 400){
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, SubscriboResponse::class.java)
                    myApiResponse.postValue(ApiResponse(errorResponse, errorResponse.message))
                }else if (e.code() == 401){
                    myApiResponse.postValue(ApiResponse(null, "Unauthorized access, Please login and try again!"))
                }
                else{
                    myApiResponse.postValue(ApiResponse(null, resolveException(e)))
                }
            }catch (e: Exception) {
                e.printStackTrace()

                myApiResponse.postValue(ApiResponse(null, resolveException(e)))
            }
        }

        return myApiResponse
    }
}