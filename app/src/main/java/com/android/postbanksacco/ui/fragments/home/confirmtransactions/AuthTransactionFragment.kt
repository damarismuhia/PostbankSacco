package com.android.postbanksacco.ui.fragments.home.confirmtransactions

import android.annotation.SuppressLint
import android.security.keystore.KeyPermanentlyInvalidatedException
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.postbanksacco.R
import com.android.postbanksacco.data.response.simulateLoginResponse
import com.android.postbanksacco.databinding.FragmentAuthTransactionBinding
import com.android.postbanksacco.ui.fragments.TransactionBaseFragment
import com.android.postbanksacco.utils.CacheKeys
import com.android.postbanksacco.utils.CryptographyManager
import com.android.postbanksacco.utils.EncryptedPref
import com.android.postbanksacco.utils.extensions.byteArrayToBase64String
import com.android.postbanksacco.utils.extensions.capitalizeWords
import com.android.postbanksacco.utils.extensions.decodeStringToByteArray
import com.android.postbanksacco.utils.extensions.getGreetings
import com.android.postbanksacco.utils.extensions.readItemFromPref
import com.android.postbanksacco.utils.extensions.showErrorDialog
import com.android.postbanksacco.utils.extensions.showLoadingAlert
import com.android.postbanksacco.utils.extensions.showToast
import com.android.postbanksacco.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class AuthTransactionFragment : TransactionBaseFragment<FragmentAuthTransactionBinding, MainViewModel>(FragmentAuthTransactionBinding::inflate) {
    override val viewModel: MainViewModel by activityViewModels()
    private var pinCount: Int = 2
    private var one1: String? = null
    private var isDone = false
    private var two2: String? = null
    private var three3: String? = null
    private var four4: String? = null
    private var mConfirmPin: String? = null

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    lateinit var biometricManager: BiometricManager
    lateinit var cryptoObjectData: String
    private var readyToEncrypt: Boolean = false
    private var enrollFingerPrint: Boolean = false
    private lateinit var cryptographyManager: CryptographyManager
    private lateinit var secretKeyName: String
    private lateinit var ciphertext: ByteArray
    private lateinit var initializationVector: ByteArray
    private lateinit var progressDialog: SweetAlertDialog

    override fun setupUI() {
        super.setupUI()
        progressDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        cryptographyManager = CryptographyManager()
        secretKeyName = getString(R.string.secret_key_name)
        biometricPrompt = createBiometricPrompt()
        promptInfo = createPromptInfo()
        biometricManager = BiometricManager.from(requireContext())
        initUI()
    }

    private fun initUI() {
        binding.apply {
            tvTitle.text =  "Hello ${readItemFromPref(CacheKeys.FIRST_NAME).capitalizeWords()},"

            binding.btnOne.setOnClickListener { controlPinPad2("1") }
            binding.btnTwo.setOnClickListener { controlPinPad2("2") }
            binding.btnThree.setOnClickListener { controlPinPad2("3") }
            binding.btnFour.setOnClickListener { controlPinPad2("4") }
            binding.btnFive.setOnClickListener { controlPinPad2("5") }
            binding.btnSix.setOnClickListener { controlPinPad2("6") }
            binding.btnSeven.setOnClickListener { controlPinPad2("7") }
            binding.btnEight.setOnClickListener { controlPinPad2("8") }
            binding.btnNine.setOnClickListener { controlPinPad2("9") }
            binding.btnZero.setOnClickListener { controlPinPad2("0") }
            binding.btnDelete.setOnClickListener { deletePinEntry() }
            btnUseFingerprint.setOnClickListener{
                EncryptedPref.writePreference(requireContext(),
                    CacheKeys.fingerPrintClickedToEnroll,"true")
                authWithFingerPrint()
            }

        }
    }

    private fun controlPinPad2(entry: String) {
        binding.apply {
            if (one1 == null) {
                one1 = entry
                pin1.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.active_pin)
            } else if (two2 == null) {
                two2 = entry
                pin2.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.active_pin)
            } else if (three3 == null) {
                three3 = entry
                pin3.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.active_pin)
            } else if (four4 == null) {
                four4 = entry
                pin4.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.active_pin)
            }
            if (mConfirmPin == null) {
                mConfirmPin = entry
            } else {
                mConfirmPin += entry
            }
            if (mConfirmPin?.length == 4) {
                loginAndEcrypt()

            }
        }
    }

    private fun loginAndEcrypt(){
        if (enrollFingerPrint) {
            authenticateToEncrypt()
        } else {
            authReq()
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun deletePinEntry() {
        binding.apply {
            if (mConfirmPin != null && mConfirmPin!!.length > 0) {
                mConfirmPin = mConfirmPin!!.substring(0, mConfirmPin!!.length - 1)
            }
            when {
                four4 != null -> {
                    binding.pin4.background = resources.getDrawable(R.drawable.ic_inactive_pin)
                    four4 = null
                    isDone = false

                }

                three3 != null -> {
                    binding.pin3.background = resources.getDrawable(R.drawable.ic_inactive_pin)
                    three3 = null
                }

                two2 != null -> {
                    binding.pin2.background = resources.getDrawable(R.drawable.ic_inactive_pin)
                    two2 = null
                }

                one1 != null -> {
                    binding.pin1.background = resources.getDrawable(R.drawable.ic_inactive_pin)
                    one1 = null
                }
            }
        }
    }


    private fun proceedOnSuccess() {

        findNavController().navigateUp()
    }


    private fun authReq() {
        viewModel.unsetAuthSuccess()
        if (!progressDialog.isShowing) {
            showLoadingAlert(progressDialog)
        }
        val phone = readItemFromPref(CacheKeys.PHONE_NUMBER)
        CoroutineScope(Dispatchers.Main).launch {
            if (!progressDialog.isShowing) {
                showLoadingAlert(progressDialog)
            }
            delay(2500)
            if (progressDialog.isShowing) {

                if (simulateLoginResponse(phone,mConfirmPin!!)){
                    pinCount = 2
                    EncryptedPref.writePreference(requireContext(), CacheKeys.done_onboarding, "true")
                    progressDialog.dismiss()
                    if (enrollFingerPrint) {
                        EncryptedPref.writePreference(requireContext(),
                            CacheKeys.setIsFingerPrintEnrolled,"true")
                        if (this@AuthTransactionFragment::cryptoObjectData.isInitialized){
                            EncryptedPref.writePreference(requireContext(),
                                CacheKeys.secureFinger,cryptoObjectData)
                        }
                    }
                    viewModel._authSuccess.postValue(true)
                    proceedOnSuccess()
                }else{
                    viewModel.unsetAuthSuccess()
                    clearPin()
                    if (pinCount == 0){
                        showErrorDialog(progressDialog, "Your account is blocked! Kindly contact customer care for assistance.")
                    }else{
                        showErrorDialog(progressDialog, "Wrong Credentials! You have $pinCount trials remaining")
                        pinCount -=1
                    }

                }


            }
        }
    }


    private fun authWithFingerPrint() {
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                biometricPrompt = createBiometricPrompt()
                promptInfo = createPromptInfo()
                Timber.e("ApiResults -%s", "BIOMETRIC_SUCCESS")
                val isFingerprintSet = EncryptedPref.readPreferences(requireContext(), CacheKeys.setIsFingerPrintEnrolled)
                Timber.e("isFinger printSet -%s", isFingerprintSet)
                if (isFingerprintSet != "true") {
                    showToast("Please Enter your PIN to Enable Finger Print")
                    enrollFingerPrint = true
                    Timber.e("isFinger ARE YOU NEW -%s", isFingerprintSet)
                } else{
                    Timber.e("loginWithFingerPrint :::::authenticateToDecrypt" )
                    enrollFingerPrint = false
                    authenticateToDecrypt()
                }
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Timber.e("biometricManager.canAuthenticate() -%s", "BIOMETRIC_ERROR_NO_HARDWARE")
                showToast(getString(R.string.error_msg_no_biometric_hardware))
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                showToast(
                    getString(R.string.error_msg_biometric_hw_unavailable))
                Timber.e("biometricManager.canAuthenticate() -%s", "BIOMETRIC_ERROR_HW_UNAVAILABLE")
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                showToast(
                    getString(R.string.error_msg_biometric_not_setup))
                Timber.e("biometricManager.canAuthenticate() $", "BIOMETRIC_ERROR_NONE_ENROLLED")
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                showToast(
                    getString(R.string.error_msg_biometric_hw_unavailable))
                Timber.e(
                    "biometricManager.canAuthenticate() -%s",
                    "BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED"
                )
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                showToast(
                    getString(R.string.error_msg_biometric_hw_unavailable))
                Timber.e("biometricManager.canAuthenticate() -%s", "BIOMETRIC_ERROR_UNSUPPORTED")

            }
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                showToast(
                    getString(R.string.error_msg_biometric_hw_unavailable))
                Timber.e("biometricManager.canAuthenticate() -%s", "BIOMETRIC_STATUS_UNKNOWN")
            }
        }
    }
    private fun createBiometricPrompt(): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(requireContext())
        val callback = object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Timber.e("$errorCode ::onAuthenticationError here $errString")
                if (EncryptedPref.readPreferences(requireContext(), CacheKeys.setIsFingerPrintEnrolled) != "true" && errorCode == 13){
                    authReq()
                }
            }
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Timber.e("Authentication failed for an unknown reason")
            }


            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Timber.e("Authentication was successful")
                if (enrollFingerPrint) {
                    cryptoObjectData = processData(result.cryptoObject)
                    authReq()
                } else {
                    cryptoObjectData = processData(result.cryptoObject)
                    mConfirmPin = cryptoObjectData
                    authReq()
                }

            }
        }
        //The API requires the client/Activity context for displaying the prompt view
        return BiometricPrompt(this, executor, callback)
    }

    private fun createPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.prompt_info_title)) // e.g. "Sign in"
            .setSubtitle(getString(R.string.prompt_info_subtitle)) // e.g. "Biometric for My App"
            .setDescription(getString(R.string.prompt_info_description)) // e.g. "Confirm biometric to continue"
            //.setConfirmationRequired(false)
            .setNegativeButtonText(getString(R.string.prompt_info_use_app_password)) // e.g. "Use Account Password"
            // Also note that setDeviceCredentialAllowed and setNegativeButtonText are
            // incompatible so that if you uncomment one you must comment out the other
            .build()
    }


    private fun authenticateToEncrypt() {
        readyToEncrypt = true
        if (BiometricManager.from(requireContext())
                .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager
                .BIOMETRIC_SUCCESS
        ) {
            try {
                val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
                biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
            } catch (e: KeyPermanentlyInvalidatedException) {
                cryptographyManager.deleteEntry()
                Timber.e("UuthenticateToEncrypt Error:::: ${e.localizedMessage}")
                EncryptedPref.writePreference(requireContext(), CacheKeys.setIsFingerPrintEnrolled,"false")
                EncryptedPref.writePreference(requireContext(), CacheKeys.secureLoginIVData,"")
                loginAndEcrypt()
            }
        }
    }

    private fun authenticateToDecrypt() {
        readyToEncrypt = false
        val iv = EncryptedPref.readPreferences(requireContext(), CacheKeys.secureLoginIVData)
        if (!iv.isNullOrEmpty()) {
            initializationVector = decodeStringToByteArray(iv)
            if (BiometricManager.from(requireContext())
                    .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager
                    .BIOMETRIC_SUCCESS
            ) {
                try {
                    val cipher = cryptographyManager.getInitializedCipherForDecryption(
                        secretKeyName,
                        initializationVector
                    )
                    biometricPrompt.authenticate(
                        promptInfo,
                        BiometricPrompt.CryptoObject(cipher)
                    )
                } catch (e: Exception) {
                    Timber.e("CATCH ON DECRYPT FINGER::::: ${e.message}")
                    EncryptedPref.writePreference(requireContext(),
                        CacheKeys.setIsFingerPrintEnrolled,"false")
                    EncryptedPref.writePreference(requireContext(), CacheKeys.secureLoginIVData,"")
                    showToast("Please Enter your PIN to Enable Finger Print")
                }
            }
        } else {
            Timber.e(" FINGER:","IV NULLUTY ON DECRYPT FINGER:::::")
            enrollFingerPrint = true
            EncryptedPref.writePreference(requireContext(), CacheKeys.setIsFingerPrintEnrolled,"false")
            showToast("Please Enter your PIN to Enable Finger Print")
        }

    }

    private fun processData(cryptoObject: BiometricPrompt.CryptoObject?): String {
        return if (readyToEncrypt) {
            val text = mConfirmPin.toString()
            val encryptedData = cryptographyManager.encryptData(text, cryptoObject?.cipher!!)
            ciphertext = encryptedData.ciphertext
            initializationVector = encryptedData.initializationVector
            val ivInBase64 = byteArrayToBase64String(initializationVector)
            EncryptedPref.writePreference(requireContext(), CacheKeys.secureLoginIVData,ivInBase64)
            //return String(ciphertext, Charset.forName("UTF-8"))
            byteArrayToBase64String(ciphertext)
        } else {
            val rawData = EncryptedPref.readPreferences(requireContext(), CacheKeys.secureFinger)
            val data = decodeStringToByteArray(rawData.toString())
            cryptographyManager.decryptData(data, cryptoObject?.cipher!!)
        }
    }

    private fun clearPin() {
        binding.apply {
            pin1.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_inactive_pin)
            one1 = null
            pin2.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_inactive_pin)
            two2 = null
            pin3.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_inactive_pin)
            three3 = null
            pin4.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_inactive_pin)
            four4 = null
            mConfirmPin = null
        }
    }


}