package com.android.postbanksacco.data.response

import android.content.Context
import com.android.postbanksacco.R
import com.android.postbanksacco.data.model.HomeModel
import com.google.gson.annotations.SerializedName

data class MinistatementModel(
    val narration: String,
    val amount: String,
    val date: String,
    val transactionType: String
) {
    companion object {
        fun getMini(): ArrayList<MinistatementModel> {
            return arrayListOf(
                MinistatementModel("Send to M-PESA","- Ksh 2,006.00","21/08/2024 | 12:35pm","D"),
                MinistatementModel("Loan Repayment","- Ksh 4,406.00","21/08/2024 | 12:35pm","D"),
                MinistatementModel("Deposit to Account","+ Ksh 9,001.00","10/08/2024 | 2:00am","C"),
                MinistatementModel("Airtime Purchase","- Ksh 46.00","09/08/2024 | 2:35pm","D"),
                MinistatementModel("Internal Transfer","- Ksh 639.00","01/07/2024 | 9:35am","D"),
                )
        }

    }
}