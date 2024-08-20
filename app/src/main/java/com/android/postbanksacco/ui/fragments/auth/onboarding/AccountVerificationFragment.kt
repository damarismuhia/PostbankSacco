package com.android.postbanksacco.ui.fragments.auth.onboarding

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.postbanksacco.BuildConfig
import com.android.postbanksacco.R
import com.android.postbanksacco.data.response.AuthData
import com.android.postbanksacco.data.response.simulateLookUpResponse
import com.android.postbanksacco.databinding.FragmentAccountVerificationBinding
import com.android.postbanksacco.ui.fragments.BaseAuthFragment
import com.android.postbanksacco.utils.CacheKeys
import com.android.postbanksacco.utils.EncryptedPref
import com.android.postbanksacco.utils.Validators
import com.android.postbanksacco.utils.extensions.customTermsConditionsTextView
import com.android.postbanksacco.utils.extensions.formatPhoneNumberWithCode
import com.android.postbanksacco.utils.extensions.navigateWithArgs
import com.android.postbanksacco.utils.extensions.showActionDialog
import com.android.postbanksacco.utils.extensions.showErrorDialog
import com.android.postbanksacco.utils.extensions.showLoadingAlert
import com.android.postbanksacco.utils.extensions.showToast
import com.android.postbanksacco.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountVerificationFragment : BaseAuthFragment<FragmentAccountVerificationBinding, AuthViewModel>
    (FragmentAccountVerificationBinding::inflate) {
    override val viewModel: AuthViewModel by viewModels()
    private lateinit var progressDialog: SweetAlertDialog
    override fun setupUI() {
        super.setupUI()
        initData()
        progressDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
    }
    private fun initData() {
        hardCodeData()
        setUpFragmentTitle()
        setClickListeners()
        binding.apply {
            etPhonenumber.addTextChangedListener(textWatcher)
            etMemberId.addTextChangedListener(textWatcher)
            etNationalId.addTextChangedListener(textWatcher)
            tvAcceptTermsAndConditions.customTermsConditionsTextView(requireActivity())
        }

    }

    private fun hardCodeData() {
        if (BuildConfig.DEBUG) {
            binding.etMemberId.setText("S4027")
            binding.etPhonenumber.setText("0718194920")
            binding.etNationalId.setText("34189136")
        }
    }
    private fun validData(): Boolean {
        val memberId = binding.etMemberId.text.toString().trim()
        val nationalId = binding.etNationalId.text.toString().trim()
        val validPhone = Validators.validPhoneNUmber(binding.etPhonenumber.text.toString().trim())
        if (!validPhone.contentEquals("valid")) {
            binding.phoneTF.error = (validPhone)
            return false
        } else if (memberId.isEmpty() || memberId.length < 5) {
            binding.tfMemberNo.error = "Enter a valid Staff Id"
            return false
        } else if (nationalId.isEmpty()) {
            binding.tilNationalId.error = "Enter National Id"
            return false
        }else if(!binding.termsCheckBox.isChecked){
            showToast("Please accept Terms & Conditions and Privacy Policy to proceed ")
            return false
        }else {
            return true
        }
    }
    private fun doAccountLookUp() {
        binding.apply {
            if (!progressDialog.isShowing) {
                showLoadingAlert(progressDialog)
            }
            CoroutineScope(Dispatchers.Main).launch {
                delay(3000)
                val ph = etPhonenumber.text.toString().trim().formatPhoneNumberWithCode()
                if (progressDialog.isShowing) {
                    val authData = simulateLookUpResponse(ph)
                    authData.mobileNumber = ph
                    authData.memberId = etMemberId.text.toString().trim()
                    authData.iDNumber = etNationalId.text.toString().trim()
                    if (authData.isAccountFound){
                        progressDialog.dismiss()
                        if (!authData.isRegistered){
                            showActionDialog("Account not Activated","Would you like to activate it?", logo = R.drawable.activate_icon){activate->
                                if (activate){
                                    saveData(authData)
                                    val args = Bundle().apply {
                                        putParcelable("authData", authData)
                                    }
                                    navigateWithArgs(R.id.deviceVerificationFragment, args)
                                }
                            }
                        }else{
                            saveData(authData)
                            val args = Bundle().apply {
                                putParcelable("authData", authData)
                            }
                            navigateWithArgs(R.id.deviceVerificationFragment, args)
                        }

                    }else {
                        showErrorDialog(progressDialog,"Account Not Found")
                    }
                }
            }
        }
    }

    private fun saveData(data: AuthData){
        EncryptedPref.writePreference(requireContext(),CacheKeys.MEMBER_ID,data.memberId ?: "")
        EncryptedPref.writePreference(requireContext(),CacheKeys.PHONE_NUMBER,data.mobileNumber ?: "")
        EncryptedPref.writePreference(requireContext(),CacheKeys.FIRST_NAME, data.firstName)
        EncryptedPref.writePreference(requireContext(),CacheKeys.Full_NAME, data.memberName)
        EncryptedPref.writePreference(requireContext(),CacheKeys.email, data.email)
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
                tilNationalId.error = null
                tfMemberNo.error = null
                phoneTF.error = null
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog.isShowing) progressDialog.cancel()
    }

    private fun setUpFragmentTitle() {
        binding.fragmentTitle.apply {
            fragmentTitle.text = getString(R.string.accountLookUp_txt)
            fragmentTitleDesc.text = getString(R.string.account_lookUp_desc_txt)
        }
    }

    private fun setClickListeners() {
        binding.btnContinue.setOnClickListener {
            if (validData()) {
                doAccountLookUp()
            }
        }
    }
}