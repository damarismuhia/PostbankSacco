package com.android.postbanksacco.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.Task

import javax.inject.Inject

/**
 * BroadcastReceiver to wait for SMS messages. This can be registered either
 * in the AndroidManifest or at runtime.  Should filter Intents on
 * SmsRetriever.SMS_RETRIEVED_ACTION.
 */
class SmsBroadcastReceiver @Inject constructor() : BroadcastReceiver() {
    var isDeviceAndroidVersionAbove13 = false
    private var otpListener: OTPReceiveListener? = null

    var smsBroadcastReceiverListener: SmsBroadcastReceiverListener? = null
    fun initUserConsentListener(smsBroadcastReceiverListener: SmsBroadcastReceiverListener) {
        isDeviceAndroidVersionAbove13 = Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2
        Log.e("OTPReceiveListener -%s", "Success")
        this.smsBroadcastReceiverListener = smsBroadcastReceiverListener
    }

    fun setOTPListener(otpListener: OTPReceiveListener?) {
        isDeviceAndroidVersionAbove13 = Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2
        this.otpListener = otpListener
    }

    override fun onReceive(context: Context, intent: Intent) {
       /* if (intent.action == SmsRetriever.SMS_RETRIEVED_ACTION && !isDeviceAndroidVersionAbove13) {
            val extras = intent.extras
            val status = extras!![SmsRetriever.EXTRA_STATUS] as Status?
            when (status?.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val sms = extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String?
                    sms?.let {
                        val p = Pattern.compile("\\d+")
                        val m = p.matcher(it)
                        if (m.find()) {
                            val otp = m.group()
                            if (otpListener != null) {
                                otpListener?.onOTPReceived(otp)
                            }
                        }
                    }
                }

            }*/
      //  }else{
            if (intent.action == SmsRetriever.SMS_RETRIEVED_ACTION) {
                val extras = intent.extras
                val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status
                when (smsRetrieverStatus.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT).also {
                            if (this.smsBroadcastReceiverListener != null) {
                                smsBroadcastReceiverListener?.onSuccess(it)
                            } else {
                                Log.e("Ooops smsBroadcastReceiverListener  -%s", "Is very Null")
                            }
                        }
                    }

                    CommonStatusCodes.TIMEOUT -> {
                        if (this.smsBroadcastReceiverListener != null) {
                            smsBroadcastReceiverListener?.onFailure()
                        }
                    }
                }
            }
       // }
    }
    fun startSmsUserConsent(context: Context) {
        SmsRetriever.getClient(context).also {
            //We can add user phone number or leave it blank

            it.startSmsUserConsent("CBK")
                .addOnSuccessListener {
                    Log.d("","LISTENING_SUCCESS")
                }
                .addOnFailureListener {
                    Log.d("","LISTENING_FAILURE")
                }
        }
    }

    fun startSMSRetriever(ctx: Context) {
        val client: SmsRetrieverClient = SmsRetriever.getClient(ctx)
        // Starts SmsRetriever, which waits for ONE matching SMS message until timeout
// (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
// action SmsRetriever#SMS_RETRIEVED_ACTION.
        val task: Task<Void> = client.startSmsRetriever()
        // Listen for success/failure of the start Task. If in a background thread, this
// can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener {
            Log.e("addOnSuccessListener Message -%s", "Sms listener started!")
        }
        task.addOnFailureListener { e ->
            Log.e(
                "addOnFailureListener Message -%s",
                "Failed to start sms retriever: ${e.message}"
            )
        }
    }

    interface OTPReceiveListener {
        fun onOTPReceived(otp: String?)
    }
    interface SmsBroadcastReceiverListener {
        fun onSuccess(intent: Intent?)
        fun onFailure()
    }
}