package com.android.postbanksacco.utils.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Base64
import android.view.View
import android.widget.TextView
import com.android.postbanksacco.R
import com.android.postbanksacco.utils.EncryptedPref
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.HashSet
import java.util.Locale
import java.util.UUID
fun String.getFirstTwoLetters(): String {
    return if (this.length >= 2) {
        this.substring(0, 2)
    } else {
        this.substring(0) // return the input itself if it's less than 2 characters
    }
}
fun String.maskPhone():String{
    return this.replace("(?<=.{5}).(?=.{3})".toRegex(), "*")
}
fun generateSessionId(con: Context): String {
    val value = UUID.randomUUID().toString()
    EncryptedPref.writePreference(con,"deviceID",value)
    return value
}
@SuppressLint("HardwareIds")
fun getAndroidId(context: Context): String {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}
fun getGreetings(con:Context): String {
    val date = Date()
    val cal = Calendar.getInstance()
    cal.time = date
    val greeting: String = when (cal[Calendar.HOUR_OF_DAY]) {
        in 12..15 -> {
            con.getString(R.string.good_afternoon)
        }
        in 16..20 -> {
            con.getString(R.string.good_evening)
        }
        in 21..23 -> {
            con.getString(R.string.good_night)
        }
        else -> {
           con.getString(R.string.good_morning)
        }
    }
    return greeting
}
fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { it.lowercase().capitalize() }

fun String.formatPhoneNumberWithCode(): String {
    val formatPhone: String = if (this.startsWith("254")) {
        this.substring(3)
    } else if (this.startsWith("+254")) {
        this.substring(4)
    } else if (this.startsWith("0")) {
        this.substring(1)
    } else {
        this
    }
    return "254${formatPhone}"
}
fun TextView.customTermsConditionsTextView(activity: Context) {
    val termsUri: Uri = Uri.parse("https://postbanksacco.co.ke/about-us-2/")
    val privacyUri: Uri = Uri.parse("https://postbanksacco.co.ke/about-us-2/")
    val spanTxt = SpannableStringBuilder(
        "By continuing, you are accepting our  "
    )
    spanTxt.append("Terms & Conditions ")
    spanTxt.setSpan(object : ClickableSpan() {
        override fun onClick(widget: View) {
            val mWebsiteIntent = Intent(Intent.ACTION_VIEW, termsUri)
            mWebsiteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(mWebsiteIntent)
        }
    }, spanTxt.length - "Terms & Conditions,".length, spanTxt.length, 0)
    spanTxt.append(" as well as our")
    spanTxt.append(" Privacy Policy")
    spanTxt.setSpan(object : ClickableSpan() {
        override fun onClick(widget: View) {
            val mWebsiteIntent = Intent(Intent.ACTION_VIEW, privacyUri)
            mWebsiteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(mWebsiteIntent)
        }
    }, spanTxt.length - " Privacy Policy".length, spanTxt.length, 0)
    this.movementMethod = LinkMovementMethod.getInstance()
    this.setText(spanTxt, TextView.BufferType.SPANNABLE)
}
fun decodeStringToByteArray(text: String): ByteArray {
    return  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        java.util.Base64.getDecoder().decode(text)
    }else{
        Base64.decode(text, Base64.DEFAULT)
    }
}

/**
 * This helper method is for the encrypted version
 * ByteArray is a data structure used to represent an array of bytes. It is a sequence of elements,
 * where each element is an 8-bit byte.
 *
 *
 * Base64 provides Base64 encoding and decoding functionality
 * Base64.NO_WRAP flag is used to remove any line breaks from the resulting string.
 * Basically, we are converting the byteArray to a Base64-encoded String
 * */
fun byteArrayToBase64String(byteArray: ByteArray): String {
    return if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
        java.util.Base64.getEncoder().encodeToString(byteArray)
    }else{
        Base64.encodeToString(byteArray, Base64.NO_WRAP)

    }
}

fun String.trimText() : String {
    return this.toString().trim()
}
fun formatDigits(wholeNumber: String): String {
    var number: Double = 0.0
    try {
        number = wholeNumber.toDouble()
    } catch (e: NumberFormatException) {
        return wholeNumber
    }
    val formatter: DecimalFormat = when (number) {
        0.00 -> {
            DecimalFormat("0.00")
        }
        "0".toDouble() -> {
            DecimalFormat("0.##")
        }
        else -> {
            DecimalFormat("#,###.00")
        }
    }

    return formatter.format(number)
}

fun String.formatActualAmount():String{
    val amnt = this.replace("-","").replace(" ","")
    return amnt.replace(",".toRegex(), "").trim()
}
fun String.formatSpaces():String{
    return this.replace(" ","").trim()
}
fun Date.dateValueFormmater(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}
fun showCurrentDate(format:String): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.now().format(DateTimeFormatter.ofPattern(format))
    } else {
        getCurrentDateTime().dateValueFormmater(format)
    }
}
fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}
fun generateTransactionRef(): String {
    val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    return (1..11)
        .map { allowedChars.random() }
        .joinToString("")
}
fun String.formatDate(): String {
    val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
    val date = inputFormatter.parse(this)
    return date.let {
        val outputFormatter = SimpleDateFormat("yyyy-MM-dd|hh:mm a", Locale.getDefault())
        outputFormatter.format(date)
    }
}
fun String.weakPin(): Boolean{
    //check if all characters are same
    val s1: MutableSet<Char> = HashSet()
    // Insert characters in the set
    for (element in this) s1.add(element)
    if (s1.size == 1) return true //pass cannot contain 4 consecutive same characters
    if (s1.size - 1 == 1) return true //pass cannot contain 3 consecutive same characters
    //Check for three consecutive digits in pin
    var prev: Char? = null
    var asc: Boolean? = null
    var streak = 0
    for (c in this.toCharArray()) {
        if (prev != null) {
            when (c - prev) {
                -1 -> if (java.lang.Boolean.FALSE == asc) streak++ else {
                    asc = false
                    streak = 2
                }
                1 -> if (java.lang.Boolean.TRUE == asc) streak++ else {
                    asc = true
                    streak = 2
                }
                else -> {
                    asc = null
                    streak = 0
                }
            }
            if (streak == 3) return true // 3 consecutive characters, pass is weak
        }
        prev = c
    }
    return false // the pass is strong
}