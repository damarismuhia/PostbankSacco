package com.android.postbanksacco.utils

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.postbanksacco.R
import com.android.postbanksacco.utils.Constants.getRootFunction
import com.scottyab.rootbeer.RootBeer
import kotlin.system.exitProcess

fun checkRooted(): Boolean {
    val checker: Int = getRootFunction
    return checker == 1
}


fun isSIMInserted(context: Context): Boolean {
    return TelephonyManager.SIM_STATE_ABSENT != (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simState

}
fun isDeviceSecured(con: Context): Boolean {
    val keyguardManager = con.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager //api 16+
    return keyguardManager.isDeviceSecure   //Return whether the keyguard is secured by a PIN, pattern or password or a SIM card is currently locked.
}
fun isVM(): Boolean {
    val radioVersion = Build.getRadioVersion()
    return radioVersion == null || radioVersion.isEmpty() || radioVersion == "1.0.0.0"
}

fun Activity.checkDeviceStatus(progressDialog: SweetAlertDialog, rootBeer: RootBeer):Boolean {
    val message: String
    return true
    /*return when {
        isVM() -> {
            message = resources.getString(R.string.emulators_des)
            showSecurityErrorDialog(progressDialog,message)
            false
        }
        checkRooted() || rootBeer.isRootedWithBusyBoxCheck -> {
            message = this.resources.getString(R.string.rooted_devices_des)
            showSecurityErrorDialog(progressDialog,message)
            false
        }
        !isDeviceSecured(this) -> {
            message = resources.getString(R.string.msg_pass_code)
            showSecurityErrorDialog(progressDialog,message)
            false
        }
        !isSIMInserted(this) -> {
            message = resources.getString(R.string.msg_missing_sim_card)
            showSecurityErrorDialog(progressDialog,message)
            false
        }
        else -> {
            true

        }
    }*/
}

private fun Activity.showSecurityErrorDialog(progressDialog:SweetAlertDialog,message: String) {
    progressDialog.setTitleText(this.resources.getString(R.string.sorry))
        .setContentText(message)
        .showCancelButton(false)
        .setCancelClickListener(null)
        .setConfirmClickListener {
            progressDialog.dismiss()
            finishAffinity()
            this.finish()
            exitProcess(0)
        }
    progressDialog.show()
}



fun EditText.disableCopyPaste() {
    this.customInsertionActionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
        }
    }
    this.customSelectionActionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
        }
    }
    this.isLongClickable = false
    this.setTextIsSelectable(false)
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
            if (after - count > 1) {
                this@disableCopyPaste.setText(s)
                this@disableCopyPaste.setSelection(s.toString().length)
            }
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
        }
    })
}
