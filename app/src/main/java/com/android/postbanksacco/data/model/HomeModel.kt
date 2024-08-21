package com.android.postbanksacco.data.model

import android.content.Context
import com.android.postbanksacco.R

data class HomeModel(
    val image: Int,
    val title: String,
    val code: String,
) {
    companion object {
        fun getHomeItems(context: Context): ArrayList<HomeModel> {
            return arrayListOf(
                HomeModel(
                    image = R.drawable.ic_deposit,
                    title = "Deposit", code = "deposit"
                ),
                HomeModel(
                    image = R.drawable.ft,
                    title = "Fund Transfer", code = "ft"
                ),
                HomeModel(
                    image = R.drawable.ic_airtime,
                    title = "Buy Airtime", code = "airtime"
                ),
                HomeModel(
                    image = R.drawable.ic_loans,
                    title = "Loans", code = "loans"
                ),
                HomeModel(
                    image = R.drawable.savings,
                    title = "Savings", code = "save"
                ),
                HomeModel(
                    image = R.drawable.ic_bills,
                    title ="Bill Payments", code = "bill"
                ),
                HomeModel(
                    image = R.drawable.ic_accs,
                    title = "My Accounts", code = "myAcc"
                ),
                HomeModel(
                    image = R.drawable.reqs,
                    title = "Requests", code = "requests"
                ),

            )
        }

    }
}