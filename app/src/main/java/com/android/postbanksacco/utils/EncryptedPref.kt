package com.android.postbanksacco.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object EncryptedPref {
    private const val PREFS_NAME = "subTracker-android-app"
    private fun getSecretSharedPref(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    fun writePreference(context: Context, key: String, value: String) {
        getSecretSharedPref(context).edit().putString(key, value).apply()
    }
    fun readPreferences(context: Context, key: String): String? {
        return getSecretSharedPref(context).getString(key, "")
    }

    fun clearSharedPref(context: Context,key: String){
        val sharedPreferences = getSecretSharedPref(context)
        sharedPreferences.edit().remove(key).apply()
    }

}