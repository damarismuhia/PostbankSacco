package com.android.postbanksacco.data.response
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuthData(
    var memberName: String = "",
    var memberId: String = "",
    var email: String = "",
    var firstName: String = "",
    var iDNumber: String = "",
    var mobileNumber: String = "",
    var isRegistered: Boolean = false,
    var isAccountFound: Boolean = false,
):Parcelable
 fun simulateLookUpResponse(phone:String):AuthData{
    val response = AuthData()
    if (phone == "254718194920"){
        response.memberName = "Damaris Muhia"
        response.firstName = "Damaris"
        response.email = "muhiadamaris12@gmail.com"
        response.isAccountFound = true
    }else if(phone == "254728506150"){
        response.memberName = "Alex Kiburu"
        response.firstName = "Alex"
        response.email = "kiburualex@gmail.com"
        response.isAccountFound = true
    }else{
       response.isAccountFound = false
    }
    return response
}
fun simulateLoginResponse(phone:String,pin:String):Boolean{
    return if (phone == "254718194920" && pin == "0852"){
        true
    }else phone == "254728506150"  && pin == "2580"
}