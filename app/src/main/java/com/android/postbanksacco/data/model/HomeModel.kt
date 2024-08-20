package com.android.postbanksacco.data.model

import android.content.Context
import com.android.postbanksacco.R

data class HomeModel(
    val image: Int,
    val title: String
) {
    companion object {
        fun getHomeItems(context: Context): ArrayList<HomeModel> {
            return arrayListOf(
                HomeModel(
                    image = R.drawable.ic_deposit,
                    title = "Deposit"
                ),
                HomeModel(
                    image = R.drawable.ft,
                    title = "Fund Transfer"
                ),
                HomeModel(
                    image = R.drawable.ic_airtime,
                    title = "Buy Airtime"
                ),
                HomeModel(
                    image = R.drawable.ic_loans,
                    title = "Loans"
                ),
                HomeModel(
                    image = R.drawable.savings,
                    title = "Savings"
                ),
                HomeModel(
                    image = R.drawable.ic_bills,
                    title ="Bill Payments"
                ),
                HomeModel(
                    image = R.drawable.ic_accs,
                    title = "My Accounts"
                ),
                HomeModel(
                    image = R.drawable.reqs,
                    title = "Requests"
                ),

            )
        }

    }
}