package com.android.postbanksacco.data.model

import android.content.Context
import com.android.postbanksacco.R
import com.android.postbanksacco.utils.extensions.maskAccount

data class LinkedAccount(
    val accountName: String,
    val accountNumber: String,
    val isTransactional: Boolean,
) {
    override fun toString(): String {
        return accountName + "-${accountNumber.maskAccount()}"
    }
    // Computed property
    val maskedAcc: String
        get() = accountName + "-${accountNumber.maskAccount()}"
 companion object {
        fun getLinkedAccounts(): ArrayList<LinkedAccount> {
            return arrayListOf(
                LinkedAccount("FOSA Account", "FS103GT8/02", isTransactional = true),
                LinkedAccount("BOSA Account", "BS103GT8/01", isTransactional = false)
            )
        }

    }
}