package com.android.postbanksacco.ui.fragments.home.airtime

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.postbanksacco.R
import com.android.postbanksacco.databinding.FragmentAirtimeBinding
import com.android.postbanksacco.databinding.FragmentDepositBinding
import com.android.postbanksacco.databinding.LookupDialogBinding
import com.android.postbanksacco.ui.dialog.TransactionConfirmationDialog.Companion.showTransactionConfirmDialog
import com.android.postbanksacco.ui.dialog.TransactionsModel
import com.android.postbanksacco.ui.fragments.TransactionBaseFragment
import com.android.postbanksacco.utils.CacheKeys
import com.android.postbanksacco.utils.Validators
import com.android.postbanksacco.utils.extensions.chargeAmnt
import com.android.postbanksacco.utils.extensions.currency
import com.android.postbanksacco.utils.extensions.formatDigits
import com.android.postbanksacco.utils.extensions.navigateNext
import com.android.postbanksacco.utils.extensions.navigateToHome
import com.android.postbanksacco.utils.extensions.populateTransactional
import com.android.postbanksacco.utils.extensions.readItemFromPref
import com.android.postbanksacco.utils.extensions.setMnoData
import com.android.postbanksacco.utils.extensions.showErrorDialog
import com.android.postbanksacco.utils.extensions.showLoadingAlert
import com.android.postbanksacco.utils.makeGone
import com.android.postbanksacco.utils.makeVisible
import com.android.postbanksacco.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class AirtimeFragment : TransactionBaseFragment<FragmentAirtimeBinding, MainViewModel>(
    FragmentAirtimeBinding::inflate) {
    override val viewModel: MainViewModel by activityViewModels ()
    private lateinit var progressDialog: SweetAlertDialog
    private var cardSelected:Int = 1
    private var selectedContact:String = ""
    override fun setupUI() {
        super.setupUI()
        progressDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        initViews()
        Timber.e("Card Selected is:: $cardSelected")
        if (cardSelected == 1){
            setOwnAcciIsSelected()
        }else{
            setOtherAccAsSelected()
        }
    }

    private fun initViews() {
        binding.apply {
            etAmount.addTextChangedListener(textWatcher)
            etPhonenumber.addTextChangedListener(textWatcher)
            if (cardSelected == 1){
                etPhonenumber.setText(readItemFromPref(CacheKeys.PHONE_NUMBER))
            }else{
                etPhonenumber.setText(selectedContact)
            }

            spMno.setMnoData()
            spAccount.populateTransactional(true)
            toggleFields()
            viewModel.contactModel.observe(viewLifecycleOwner){
                if (it!=null){
                    etPhonenumber.setText(it.phonenumber)
                    selectedContact = it.phonenumber
                }
            }
            btnSubmit.setOnClickListener {
                getCharges()
            }
            viewModel.authSuccess.observe(viewLifecycleOwner) {
                if (it == true) {
                    depositReq()
                    viewModel.unsetAuthSuccess()
                }
            }

        }
        setUpFragmentTit()

    }
    private fun toggleFields(){
        binding.llSelect.apply {
            llOwn.setOnClickListener {
                setOwnAcciIsSelected()
            }
            llOther.setOnClickListener {
                setOtherAccAsSelected()
                binding.apply {
                    navigateNext(R.id.contactFragment)
                }
            }
        }
    }
    private  fun setOwnAcciIsSelected(){
        binding.llSelect.apply {
            cardSelected = 1
            llOwn.background = ContextCompat.getDrawable(requireContext(),R.drawable.selected_rb_bg)
            tvOwn.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            ivOwn.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
            ivOwn.setImageResource(R.drawable.active_dot)

            llOther.background = ContextCompat.getDrawable(requireContext(),R.drawable.unselected_rb_bg)
            ivOther.setImageResource(R.drawable.inactive_dot)
            tvOthers.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
            ivOther.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.grey)
        }
    }
    private fun setOtherAccAsSelected(){
        binding.llSelect.apply {
            cardSelected = 2
            llOther.background = ContextCompat.getDrawable(requireContext(),R.drawable.selected_rb_bg)
            tvOthers.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            ivOther.setImageResource(R.drawable.active_dot)
            ivOther.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)


            llOwn.background = ContextCompat.getDrawable(requireContext(),R.drawable.unselected_rb_bg)
            ivOwn.setImageResource(R.drawable.inactive_dot)
            tvOwn.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
            ivOwn.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.grey)
        }
    }
    private fun getCharges(){
        if (!progressDialog.isShowing){
            progressDialog.show()
        }
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            if (progressDialog.isShowing) {
                progressDialog.dismiss()

                showConfirmDialog()
            }
        }
    }
    private fun showConfirmDialog() {
        binding.apply {
            val dialogList = mutableListOf<TransactionsModel>().apply {
                add(TransactionsModel("Amount:", "$currency ${formatDigits(etAmount.text.toString())}"))
                add(TransactionsModel(getString(R.string.buy_for), "${spMno.text}-${etPhonenumber.text.toString()}"))
                add(TransactionsModel(getString(R.string.source_account), spAccount.text.toString()))
                add(TransactionsModel("Charges:", "$currency ${formatDigits(chargeAmnt)}"))
                add(TransactionsModel("Excise Duty:", "$currency ${formatDigits(chargeAmnt)}"))
            }
            showTransactionConfirmDialog(getString(R.string.confirm_airtime),
                dialogList
            ) { confirmClicked ->
                if (confirmClicked) {
                    viewModel.transactionList.postValue(dialogList)
                    navigateNext(R.id.authTransactionFragment)
                }
            }
        }
    }
    private  fun depositReq(){
        val args = Bundle().apply {
            putString("fragmentType", "airtime")
        }
        if (!progressDialog.isShowing){
            progressDialog.show()
        }
        CoroutineScope(Dispatchers.Main).launch {
            delay(1500)
            if (progressDialog.isShowing) {
                progressDialog.dismiss()
                navigateToHome(R.id.successFragment,args)
            }
        }
    }

    private fun setUpFragmentTit(){
        binding.fragmentTitle.apply {
            fragmentTitle.text = getString(R.string.buy_airtime)
            fragmentTitleDesc.text = getString(R.string.how_much_airtime)
        }
    }
    private fun validData(): Boolean {
        val amount = binding.etAmount.text.toString().trim()
        val email = binding.spAccount.text.toString().trim()
        val validEmail = Validators.isEmailValid(email)

        return true
//        return if (name.isEmpty() || name.length < 3) {
//            binding.tlNames.error = "Enter a valid first name"
//            false
//        } else if (validEmail != Validators.validInput) {
//            binding.tlEmail.error = validEmail
//            false
//        }else{
//            registerUser()
//            true
//        }


    }
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // No action needed before text changed
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // No action needed as text changes
        }

        override fun afterTextChanged(s: Editable?) {
            // Clear error when user starts typing
            binding.apply {
                tlAmount.error = null
                tlAccount.error = null
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog.isShowing) progressDialog.cancel()
    }
}