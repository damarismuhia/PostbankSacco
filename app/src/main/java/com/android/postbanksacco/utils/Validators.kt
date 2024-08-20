package com.android.postbanksacco.utils

import android.util.Patterns
import com.android.postbanksacco.MainApp
import com.android.postbanksacco.R

object Validators {
    const val validInput = "valid"
    fun isEmailValid(email: String): String {
        var message = validInput
        message = if (email.trim().isNotEmpty()) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                MainApp.applicationContext()!!.getString(R.string.valid_email_address)
            }else{
                return message
            }
        }else{
            "Enter your email address"
        }
        return message
    }
    fun validatePassword(password: String): String {
        if (password.isEmpty()) return "Password cannot be empty"
        if (password.length < 8) return "Password must be at least 8 characters long"

        val hasUpperCase = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

        return when {
            !hasUpperCase -> "Password must contain at least one uppercase letter"
            !hasDigit -> "Password must contain at least one digit"
            !hasSpecialChar -> "Password must contain at least one special character"
            else -> Validators.validInput
        }
    }
    fun validPhoneNUmber(phoneNumber: String,type:String = "normal"): String {
        var message = validInput
        if (phoneNumber.trim().isNotEmpty()) {
            if (phoneNumber.startsWith("0") && phoneNumber.trim().length < 10 ){
                message = if (type =="checkout") {
                    "Enter a valid checkout phone number e.g(0712345678)"
                }else{
                    "Enter a valid phone number e.g(0712345678)"
                }
            }else if (phoneNumber.startsWith("254") && phoneNumber.trim().length < 12 ){
                message = if (type =="checkout") {
                    "Enter a valid checkout phone number e.g(254712345678)"
                }else{
                    "Enter a valid phone number e.g(254712345678)"
                }
            }else if(phoneNumber.trim().length < 9) {
                message = if (type =="checkout") {
                    "Enter a valid checkout phone number e.g(712345678)"
                }else{
                    "Enter a valid phone number e.g(712345678)"
                }

            }

        } else {
            message = if (type =="checkout") {
                "Enter a valid checkout phone number e.g(0712345678)"
            }else{
                "Enter a valid phone number e.g(0712345678)"
            }
        }
        return message
    }


}