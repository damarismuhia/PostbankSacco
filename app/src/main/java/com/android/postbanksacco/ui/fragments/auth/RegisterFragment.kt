package com.android.postbanksacco.ui.fragments.auth

import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import com.android.postbanksacco.R
import com.android.postbanksacco.databinding.FragmentRegisterBinding
import com.android.postbanksacco.ui.fragments.BaseAuthFragment
import com.android.postbanksacco.utils.CacheKeys
import com.android.postbanksacco.utils.EncryptedPref
import com.android.postbanksacco.utils.KEndpoints
import com.android.postbanksacco.utils.Validators
import com.android.postbanksacco.utils.disableCopyPaste
import com.android.postbanksacco.utils.extensions.getAndroidId
import com.android.postbanksacco.utils.extensions.maskPhone
import com.android.postbanksacco.utils.extensions.navigateNext
import com.android.postbanksacco.utils.extensions.navigateWithArgs
import com.android.postbanksacco.utils.extensions.pickRegDob
import com.android.postbanksacco.utils.extensions.readItemFromPref
import com.android.postbanksacco.utils.extensions.showActionDialog
import com.android.postbanksacco.utils.extensions.showErrorDialog
import com.android.postbanksacco.utils.extensions.showLoadingAlert
import com.android.postbanksacco.utils.extensions.showSuccessAlertDialog
import com.android.postbanksacco.viewmodels.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

@AndroidEntryPoint
class RegisterFragment : BaseAuthFragment<FragmentRegisterBinding, AuthViewModel>(
    FragmentRegisterBinding::inflate) {
    override val viewModel: AuthViewModel by viewModels()
    private lateinit var progressDialog: SweetAlertDialog
    override fun setupUI() {
        super.setupUI()
        progressDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        initViews()
    }

    private fun initViews() {
    binding.apply {
        binding.etDob.setOnClickListener { binding.etDob.pickRegDob("yyyy-MM-dd") }
        etEmail.addTextChangedListener(textWatcher)
        etName.addTextChangedListener(textWatcher)
        btnReg.setOnClickListener {validData()}
    }
        setUpFragmentTit()
 }
    private fun setUpFragmentTit(){
        binding.fragmentTitle.apply {
            fragmentTitle.text = getString(R.string.register)
            fragmentTitleDesc.text = getString(R.string.fill_the_details)
        }
    }
    private fun validData(): Boolean {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val validEmail = Validators.isEmailValid(email)
        return if (name.isEmpty() || name.length < 3) {
            binding.tlNames.error = "Enter a valid first name"
            false
        }else{
            registerUser()
            return true
        }

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
                    showActionDialog("Account Registered Successfully","Dear ${etName.text}, you can use a 4-digit default PIN sent to ${readItemFromPref(CacheKeys.PHONE_NUMBER).maskPhone()} to access your account")
                    {activate->
                        if (activate){
                            EncryptedPref.writePreference(requireContext(), CacheKeys.done_onboarding, "true")
                            navigateNext(R.id.loginFragment)
                        }
                    }
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
                tlEmail.error = null
                tlNames.error = null
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog.isShowing) progressDialog.cancel()
    }
}