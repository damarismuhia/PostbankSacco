package com.android.postbanksacco.utils.extensions

import android.R
import android.app.DatePickerDialog
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.postbanksacco.data.adapters.ServiceProviderAdapter
import com.android.postbanksacco.data.model.LinkedAccount
import com.android.postbanksacco.data.model.ServiceProviderItems
import com.android.postbanksacco.ui.dialog.TransactionDialogAdapter
import com.android.postbanksacco.ui.dialog.TransactionsModel
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun EditText.pickRegDob(format:String) {
    val dateListener: DatePickerDialog.OnDateSetListener
    val myCalendar = Calendar.getInstance()
    val currYear = myCalendar[Calendar.YEAR]
    val currMonth = myCalendar[Calendar.MONTH]
    val currDay = myCalendar[Calendar.DAY_OF_MONTH]
    dateListener =
        DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            myCalendar[Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = monthOfYear
            myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            var isDateOk = true
            if (currYear - year < 18) isDateOk = false else if (currYear == 18) {
                if (currMonth - monthOfYear < 0) isDateOk = false
                if (currMonth == monthOfYear && currDay - dayOfMonth < 0) isDateOk = false
            }
            if (isDateOk) {
                val date =
                    SimpleDateFormat(format, Locale.US).format(myCalendar.time)
                this.setText(date)
            } else {
                Toasty.error(
                    this.context,
                    "Age should not be less than 18 years",
                    Toast.LENGTH_SHORT,
                    true
                ).show()
            }
        }
    myCalendar.add(Calendar.YEAR, -18)
    DatePickerDialog(
        this.context, dateListener, myCalendar[Calendar.YEAR],
        myCalendar[Calendar.MONTH],
        myCalendar[Calendar.DAY_OF_MONTH]
    ).show()
}
 fun EditText.showDatePicker() {
    val calendar = Calendar.getInstance()
    val dialog = DatePickerDialog(this.context, { _, year, month, day_of_month ->
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = month
        calendar[Calendar.DAY_OF_MONTH] = day_of_month
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        this.setText(sdf.format(calendar.time))
    }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
    dialog.datePicker.maxDate = calendar.timeInMillis
    dialog.show()
}

fun AutoCompleteTextView.setMnoData() {
    val serviceProviderAdapter = ServiceProviderAdapter(this.context, mnoOptions())
    this.setAdapter(serviceProviderAdapter)
    this.setText(mnoOptions()[0].title, false)
    this.setOnItemClickListener { _, _, position, _ ->
        val selectedItem = serviceProviderAdapter.getItem(position)
        this.setText(selectedItem?.title, false)
    }
}

fun AutoCompleteTextView.populateTransactional(isDebitable:Boolean,isCreditable:Boolean=false) {
    val list = LinkedAccount.getLinkedAccounts()
    val sourceAccList = if (isDebitable) {
        list.filter { linkedAccount -> linkedAccount.isTransactional }
    } else if (isCreditable) {
        list.filter { linkedAccount -> !linkedAccount.isTransactional }
    }else{
        list
    }

    val typeAdapter = ArrayAdapter(this.context, R.layout.simple_list_item_1, sourceAccList)
    this.setAdapter(typeAdapter)
    this.onItemClickListener =
        AdapterView.OnItemClickListener { parent, arg1, position, arg3 ->
            val selected = parent.adapter.getItem(position) as LinkedAccount
            this.setText(selected.maskedAcc,false)
        }
}

fun EditText.setEditTextEndIcon(leftLogo: Int?,rightLogo: Int?) {
    this.setCompoundDrawablesWithIntrinsicBounds(
        leftLogo?.let { ContextCompat.getDrawable(this.context, it) },
        null,
        rightLogo?.let { ContextCompat.getDrawable(this.context, it) },
        null
    )
}

fun RecyclerView.setUpRecyclerAdapter(detailCommons: List<TransactionsModel>) {
    val dialogAdapter = TransactionDialogAdapter()
    val linearLayoutManager = GridLayoutManager(context,1)
    this.apply {
        layoutManager = linearLayoutManager
        dialogAdapter.submitList(detailCommons)
        adapter = dialogAdapter
    }
}