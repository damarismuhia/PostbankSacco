package com.android.postbanksacco.data.model

import android.content.Context
import android.os.Parcelable
import com.android.postbanksacco.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeModel(
    val image: Int,
    val title: String,
    val code: String,
):Parcelable {
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

        fun getFTItems():ArrayList<HomeModel>{
            return arrayListOf(
                HomeModel(R.drawable.momo,
                    "Mobile Money Transfer",
                    "Send money to mobile money"),
                HomeModel(R.drawable.internal_ft,
                    "Internal Transfer",
                    "Transfer money to own and other member accounts.",),
            )
        }
        fun getMobileMoneyItems():ArrayList<HomeModel>{
            return arrayListOf(
                HomeModel(R.drawable.ic_mpesa,
                    "Send to M-PESA",
                    "Make transfer to M-PESA"),
                HomeModel(R.drawable.airm,
                    "Send to Airtel Money",
                    "Make transfer to Airtel Money",),
            )
        }

    }
}