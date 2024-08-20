package com.android.postbanksacco.ui.fragments.auth.onboarding

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.postbanksacco.R
import com.android.postbanksacco.data.response.AuthData
import com.android.postbanksacco.databinding.FragmentDeviceVerificationBinding
import com.android.postbanksacco.ui.fragments.BaseAuthFragment
import com.android.postbanksacco.utils.CacheKeys
import com.android.postbanksacco.utils.SmsBroadcastReceiver
import com.android.postbanksacco.utils.extensions.maskPhone
import com.android.postbanksacco.utils.extensions.navigateNext
import com.android.postbanksacco.utils.extensions.navigateWithArgs
import com.android.postbanksacco.utils.extensions.readItemFromPref
import com.android.postbanksacco.utils.extensions.showActionDialog
import com.android.postbanksacco.utils.extensions.showLoadingAlert
import com.android.postbanksacco.utils.makeGone
import com.android.postbanksacco.utils.makeVisible
import com.android.postbanksacco.viewmodels.AuthViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class DeviceVerificationFragment : BaseAuthFragment<FragmentDeviceVerificationBinding,
        AuthViewModel>(FragmentDeviceVerificationBinding::inflate), SmsBroadcastReceiver.OTPReceiveListener,
    SmsBroadcastReceiver.SmsBroadcastReceiverListener {
    override val viewModel: AuthViewModel by viewModels()
    private lateinit var authData:AuthData
    private var countdownTimer: CountDownTimer? = null

    @Inject
    lateinit var smsBroadcastReceiver: SmsBroadcastReceiver
    private lateinit var progressDialog: SweetAlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            authData =
                it.getParcelable<AuthData>("authData") as AuthData

            Log.e("","Auth Data is $authData")
        }
    }
    override fun setupUI() {
        super.setupUI()
        progressDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        binding.fragmentTitle.apply {
            fragmentTitle.text = getString(R.string.verification_code)
            val number = readItemFromPref(CacheKeys.PHONE_NUMBER).maskPhone()
            fragmentTitleDesc.text = String.format(getString(R.string.We_have_sent_verification_code_to),number)
        }
//        runBlocking {
//            launch {
//                delay(1000)
        deviceVerificationReq()
//            }
//        }

        registerReceiver()
       // initCountdownTimer()
    }
    private fun deviceVerificationReq() {
        binding.apply {
            if (!progressDialog.isShowing) {
                showLoadingAlert(progressDialog)
            }
            CoroutineScope(Dispatchers.Main).launch {
                delay(3000)
                if (progressDialog.isShowing) {
                    progressDialog.dismiss()
                    initCountdownTimer()
                    //startActivity(Intent(requireContext(), LoginActivity::class.java))
                }
                }
            }

        }


    private fun initCountdownTimer() {
        countdownTimer =  object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                context?.let {
                    val seconds = (millisUntilFinished / 1000) % 60
                    val timeLeftFormatted =
                        java.lang.String.format(Locale.getDefault(), "%02d", seconds)
                    binding.tv1.text = String.format(getString(R.string.resend_code_in),timeLeftFormatted)
                    if (timeLeftFormatted == "55"){
                        simulateOtpEntry()

                    }
                    binding.resendCode.makeGone()
                    binding.resendCode.setTextColor(requireContext().getColor(R.color.grey_100))

                    binding.resendCode.setOnClickListener {
                        binding.resendCode.isClickable = false
                    }
                }
            }

            override fun onFinish() {
                reactivateSender()
            }
        }.start()

    }
    private fun simulateOtpEntry() {
        val otp = "123456"
        CoroutineScope(Dispatchers.Main).launch {
            binding.etOne.setText(otp[0].toString())
            delay(400)
            binding.etTwo.setText(otp[1].toString())
            delay(400)
            binding.etThree.setText(otp[2].toString())
            delay(400)
            binding.etFour.setText(otp[3].toString())
            delay(400)
            binding.etFive.setText(otp[4].toString())
            delay(400)
            binding.etSix.setText(otp[5].toString())
            if (!authData.isRegistered){
                navigateNext(R.id.registerFragment)
            }else{
                navigateNext(R.id.loginFragment)
            }

    }

    }

    fun reactivateSender(){
        binding.tv1.apply {
            textSize = 18F
            text = "Didn’t get the code?"
        }
        binding.resendCode.setTextColor(requireContext().getColor(R.color.colorPrimary))
        binding.resendCode.makeVisible()
        binding.resendCode.setOnClickListener {
            registerReceiver()
            deviceVerificationReq()
        }
    }

    private fun registerReceiver() {
        try {
            val intentFilter = IntentFilter()
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requireActivity().registerReceiver(smsBroadcastReceiver, intentFilter,
                    Context.RECEIVER_EXPORTED
                )
            }else {
                requireActivity().registerReceiver(smsBroadcastReceiver, intentFilter,
                )
            }

            smsBroadcastReceiver.initUserConsentListener(this)
            smsBroadcastReceiver.startSmsUserConsent(requireContext())
        } catch (e: Exception) {
            Log.e("Exception Message -%s", e.message ?: "")
        }

    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if ((result.resultCode == Activity.RESULT_OK) && (result.data != null)) {
                //That gives all message to us. We need to get the code from inside with regex
                val message = result.data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                val regex = Regex("\\d{6}")
                if (regex.containsMatchIn(message!!)) {
                    val code = fetchVerificationCode(message)

                }

            }else{
                Toasty.error(requireContext(),"You won't be able to proceed. \nKindly allow the app to read the code to proceed. You can request the code again after the session has expired by clicking resend",
                    Toast.LENGTH_LONG,true).show()
                //  findNavController(R.id.navHostSample).navigateUp()
                Timber.e("USER CANCED THE DIALOG")
            }
        }
    private fun fetchVerificationCode(message: String): String {
        return Regex("(\\d{6})").find(message)?.value ?: ""
    }



    override fun onStop() {
        super.onStop()
        countdownTimer?.cancel()
        (requireContext()).unregisterReceiver(smsBroadcastReceiver)
        Timber.e("Called On onStop")
        clearFields()
    }


    override fun onOTPReceived(otp: String?) {
        if (!otp.isNullOrBlank()) {
            //verifyOtp(otp)
        }
    }
    private fun clearFields(){
        binding.etOne.setText("")
        binding.etTwo.setText("")
        binding.etThree.setText("")
        binding.etFour.setText("")
        binding.etFive.setText("")
        binding.etSix.setText("")
    }

    override fun onSuccess(intent: Intent?) {
        startForResult.launch(intent)
    }

    override fun onFailure() {
        Timber.e("Error on receice::: Userconsent")
    }

}