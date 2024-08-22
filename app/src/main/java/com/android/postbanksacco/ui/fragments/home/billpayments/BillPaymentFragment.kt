package com.android.postbanksacco.ui.fragments.home.billpayments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.postbanksacco.R
import com.android.postbanksacco.data.model.HomeModel
import com.android.postbanksacco.data.response.AuthData
import com.android.postbanksacco.databinding.FragmentBillPaymentBinding
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

@AndroidEntryPoint
class BillPaymentFragment : TransactionBaseFragment<FragmentBillPaymentBinding, MainViewModel>(
    FragmentBillPaymentBinding::inflate) {
    override val viewModel: MainViewModel by activityViewModels ()
    private lateinit var progressDialog: SweetAlertDialog
    private lateinit var billData: HomeModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            billData =
                it.getParcelable<AuthData>("billData") as HomeModel
        }
    }
    override fun setupUI() {
        super.setupUI()
        progressDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        initViews()
    }

    private fun initViews() {
        binding.apply {
            llSource.tvFrom.text = getString(R.string.pay_from)
            etAmount.addTextChangedListener(textWatcher)
            llSource.spDebitAcc.populateTransactional(true)
            decideToShowFields()
            btnSubmit.setOnClickListener {
                if (tlPreAccount.isVisible){
                    simulatePresentment()
                }else{
                    getCharges()
                }

            }
            viewModel.authSuccess.observe(viewLifecycleOwner) {
                if (it == true) {
                    showFieldsAfterPresentment()
                    postPaymentRequest()
                    viewModel.unsetAuthSuccess()
                }
            }

        }
        setUpFragmentTit()

    }
    private fun decideToShowFields(){
        binding.apply {
            if (billData.code.contains("internet") || billData.code.contains("kenya")){
                showFieldsAfterPresentment()
                etCurrentAmount.setText("0.0")
                etMeterNo.isEnabled = true
                etAccName.isEnabled = true
            }else{
                clFields.makeGone()
                tlPreAccount.makeVisible()
            }
        }
    }
    private fun simulatePresentment(){
        if (!progressDialog.isShowing){
            showLoadingAlert(progressDialog)
        }
        CoroutineScope(Dispatchers.Main).launch {
            delay(800)
            if (progressDialog.isShowing) {
                progressDialog.dismiss()
                showFieldsAfterPresentment()

                binding.apply {
                    fragmentTitle.fragmentTitleDesc.text = getString(R.string.provide_biller_payment)
                    tlCurrentAmount.makeVisible()
                    etAccName.isEnabled = false
                    etMeterNo.isEnabled = false
                    etCurrentAmount.setText("850.00")
                    etAccName.setText("Jane Doe")
                    etMeterNo.setText(etPreAccount.text.toString())
                }

            }
        }
    }
    private fun showFieldsAfterPresentment(){
        binding.apply {
            tlPreAccount.makeGone()
            clFields.makeVisible()

        }
    }


    private fun getCharges(){
        if (!progressDialog.isShowing){
            showLoadingAlert(progressDialog)
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
                add(TransactionsModel(getString(R.string.amont), "$currency ${formatDigits(etAmount.text.toString())}"))
                add(TransactionsModel(getString(R.string.pay_from), llSource.spDebitAcc.text.toString()))
                add(TransactionsModel("Charges:", "$currency ${formatDigits(chargeAmnt)}"))
                add(TransactionsModel("Excise Duty:", "$currency ${formatDigits(chargeAmnt)}"))
            }
            showTransactionConfirmDialog(String.format(getString(R.string.confirm_send),"${billData.title} Payment"),
                dialogList
            ) { confirmClicked ->
                if (confirmClicked) {
                    viewModel.transactionList.postValue(dialogList)
                    navigateNext(R.id.authTransactionFragment)
                }
            }
        }
    }
    private  fun postPaymentRequest(){
        val args = Bundle().apply {
            putString("fragmentType", "bill")
        }
        if (!progressDialog.isShowing){
            showLoadingAlert(progressDialog)
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
            fragmentTitle.text = billData.title
            if (binding.tlPreAccount.isVisible){
                fragmentTitleDesc.text = String.format(getString(R.string.provide_biller_acc),billData.title)
            }else{
                fragmentTitleDesc.text = getString(R.string.provide_biller_payment)
            }

        }
    }
    private fun validData(): Boolean {
        val amount = binding.etAmount.text.toString().trim()

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



    private fun registerUser() {

        binding.apply {
            if (!progressDialog.isShowing) {
                showLoadingAlert(progressDialog)
            }

            CoroutineScope(Dispatchers.Main).launch {
                delay(2500)
                if (progressDialog.isShowing) {
                    progressDialog.dismiss()

                }
            }
        }
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
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog.isShowing) progressDialog.cancel()
    }
}