package com.android.postbanksacco.ui.fragments.home.deposit

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.postbanksacco.R
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


@AndroidEntryPoint
class DepositFragment : TransactionBaseFragment<FragmentDepositBinding, MainViewModel>(
    FragmentDepositBinding::inflate) {
    override val viewModel: MainViewModel by activityViewModels ()
    private lateinit var progressDialog: SweetAlertDialog
    private var cardSelected:Int = 1
    private var lookUpDialog: Dialog? = null
    private lateinit var lookUpBinding: LookupDialogBinding
    override fun setupUI() {
        super.setupUI()
        progressDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        initViews()
    }

    private fun initViews() {
        binding.apply {
            etAmount.addTextChangedListener(textWatcher)
            etPhonenumber.addTextChangedListener(textWatcher)
            etPhonenumber.setText(readItemFromPref(CacheKeys.PHONE_NUMBER))
            spMno.setMnoData()
            spAccount.populateTransactional(true)
            toggleFields()
            showLookUpDialog()
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
            tvOwn.text = getString(R.string.own_account)
            tvOthers.text = getString(R.string.other_account)
            llOwn.setOnClickListener {
                setOwnAcciIsSelected()
            }
            llOther.setOnClickListener {
               setOtherAccAsSelected()
                binding.apply {
                    lookUpDialog?.show()
                    tlAccount.makeGone()

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
           binding.apply {
               tlAccount.makeVisible()
               tlAccNo.makeGone()
               tlAccName.makeGone()
               if (lookUpDialog?.isShowing == true){
                   lookUpDialog?.dismiss()
               }
           }
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
    private fun showLookUpDialog() {
        lookUpDialog = Dialog(requireContext())
        lookUpDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        lookUpBinding = LookupDialogBinding.inflate(layoutInflater)
        lookUpBinding.apply {
            ivClose.setOnClickListener {
                lookUpDialog?.dismiss()
                setOwnAcciIsSelected()
                cardSelected = 1
            }
            btnSubmit.setOnClickListener {
                if (!progressDialog.isShowing){
                    showLoadingAlert(progressDialog)
                }
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    if (etAccount.text.toString().contains("1")){
                        progressDialog.dismiss()
                        binding.apply {
                            etAccNo.setText(lookUpBinding.etAccount.text.toString().uppercase())
                            binding.etAccName.setText("Joe Makuchu")
                            tlAccNo.makeVisible()
                            tlAccName.makeVisible()
                        }

                        setOtherAccAsSelected()

                        lookUpDialog?.dismiss()
                    }else{
                        showErrorDialog(progressDialog, "The member account number doesn't exist")
                    }
                }
            }
        }
        lookUpDialog?.setContentView(lookUpBinding.root)
        val width = (resources.displayMetrics.widthPixels * 0.92).toInt()
        lookUpDialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        lookUpDialog?.setCancelable(false)
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
                add(TransactionsModel("Amount:", "$currency ${formatDigits(etAmount.text.toString())}"))
                add(TransactionsModel("Deposit From:", "${spMno.text}-${etPhonenumber.text.toString()}"))
                if (cardSelected == 1){
                    add(TransactionsModel(getString(R.string.deposit_to), spAccount.text.toString()))
                }else{
                    add(TransactionsModel(getString(R.string.deposit_to), "${etAccName.text}-${etAccNo.text.toString()}"))
                }
                add(TransactionsModel("Charges:", "$currency ${formatDigits(chargeAmnt)}"))
                add(TransactionsModel("Excise Duty:", "$currency ${formatDigits(chargeAmnt)}"))
            }
            showTransactionConfirmDialog(getString(R.string.confirm_deposit_to_acc),
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
            putString("fragmentType", "deposit")
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
            fragmentTitle.text = getString(R.string.depositto_acc)
            fragmentTitleDesc.text = getString(R.string.how_much_deposit)
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
                tlAccount.error = null
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog.isShowing) progressDialog.cancel()
    }
}