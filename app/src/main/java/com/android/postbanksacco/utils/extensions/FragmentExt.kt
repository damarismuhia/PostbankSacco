package com.android.postbanksacco.utils.extensions
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.postbanksacco.R
import com.android.postbanksacco.data.model.ServiceProviderItems
import com.android.postbanksacco.databinding.ActionDialogBinding
import com.android.postbanksacco.utils.EncryptedPref
import com.android.postbanksacco.utils.makeGone
import com.android.postbanksacco.utils.makeVisible
import java.util.Objects
var currency:String = "Ksh"
var chargeAmnt:String = "0"
var chargedAmnt:String = "10"
var duty:String = "2"
var fosaBal:String = "Ksh 45,765.00"
fun getNavOptions(): NavOptions {
    return NavOptions.Builder()
        .setEnterAnim(R.anim.slide_in_right)
        .setExitAnim(R.anim.slide_out_left)
        .setPopEnterAnim(R.anim.slide_in_left)
        .setPopExitAnim(R.anim.slide_out_right)
        .build()
}
fun Fragment.navigateNext(navId:Int){
    findNavController().navigate(navId,null, getNavOptions())
}
fun Fragment.navigateWithArgs(navId:Int,args:Bundle){
    findNavController().navigate(navId,args, getNavOptions())
}
fun Fragment.navigateToHome(destination:Int,args:Bundle) {
    val navOptions = NavOptions.Builder()
        .setPopUpTo(R.id.homeFragment, false)
        .setEnterAnim(R.anim.slide_in_right)
        .setExitAnim(R.anim.slide_out_left)
        .setPopEnterAnim(R.anim.slide_in_left)
        .setPopExitAnim(R.anim.slide_out_right)
        .build()
    findNavController().navigate(destination, args, navOptions)
}
private var pagerPosition = 0
fun autoPlayAdvertisement(viewPager: ViewPager) {
    Handler().postDelayed({
        if (pagerPosition == Objects.requireNonNull(viewPager.adapter)!!.count - 1
        ) {
            pagerPosition = 0
            viewPager.currentItem = pagerPosition
        } else {
            viewPager.currentItem = 1.let {
                pagerPosition += it;
                pagerPosition
            }
        }
        autoPlayAdvertisement(viewPager)
    }, 4000)
}

fun Fragment.readItemFromPref(key:String) : String{
   return EncryptedPref.readPreferences(requireContext(),key) ?: ""
}
fun Fragment.showToast(msg: String) {
    Toast.makeText(this.requireContext(), msg, Toast.LENGTH_LONG).show()
}
fun Fragment.showActionDialog(tit: String,subTit:String,logo:Int=R.drawable.success, completionHandler:(Boolean)->Unit) {
    val dialog = Dialog(requireContext())
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    val activateBinding = ActionDialogBinding.inflate(LayoutInflater.from(context))
    activateBinding.apply {
        tvTitle.text = tit
        tvContent.text = subTit
        ivLogo.setImageResource(logo)
        if (logo == R.drawable.success){
            btnCancel.makeGone()
            btnAct.makeGone()
            btnok.makeVisible()
        }
        btnAct.setOnClickListener {
            completionHandler.invoke(true)
            dialog.dismiss()
        }
        btnok.setOnClickListener {
            completionHandler.invoke(true)
            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            completionHandler.invoke(false)
            dialog.dismiss()
        }
    }
    dialog.setContentView(activateBinding.root)
    dialog.setDialogLayoutParams()
    dialog.show()
    dialog.setCancelable(false)
}
fun Fragment.showLoadingAlert(progressDialog: SweetAlertDialog) {
    if (!progressDialog.isShowing) {
        progressDialog.setTitleText("Sending request")
            .setContentText("Please Wait......")
            .showCancelButton(false)
            .setCancelClickListener(null)
            .setConfirmClickListener(null)
            .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.progressHelper.barColor =
            ContextCompat.getColor(requireActivity(), R.color.colorPrimary)
        progressDialog.progressHelper.rimColor =
            ContextCompat.getColor(requireActivity(), R.color.colorPrimary)
        progressDialog.setCancelable(false)
        progressDialog.show()
    }
}
fun Fragment.showErrorDialog(progressDialog: SweetAlertDialog,error:String?,canShowAlert:Boolean= false, canNavigateUp: Boolean = false){
    val defaultError = "Your Request failed, please try again later"
    val progressType = if (error ==  "no internet detected" || canShowAlert) SweetAlertDialog.WARNING_TYPE else SweetAlertDialog.ERROR_TYPE
    val errMsg = if (error ==  "no internet detected") "Please check your internet and try again" else error ?: defaultError
    showEAlertDialog(progressDialog,"",errMsg,progressType){
        if (canNavigateUp){
            findNavController().navigateUp()
        }
    }

}
fun mnoOptions() : MutableList<ServiceProviderItems> {
    return mutableListOf(
        ServiceProviderItems(R.drawable.ic_mpesa, "M-PESA", "safaricom"),

        ServiceProviderItems(R.drawable.airtel, "AIRTEL MONEY", "airtel"),
    )

}
fun topupOptions() : MutableList<ServiceProviderItems> {
    return mutableListOf(
            ServiceProviderItems(R.drawable.saf, "Safaricom", "SAFARICOM_TOPUP"),
            ServiceProviderItems(R.drawable.airtel, "Airtel", "AIRTEL_TOPUP"),

    )

}



fun showSuccessAlertDialog(
    progressDialog: SweetAlertDialog,
    tit: String = "Success",
    message: String,
    completionHandler: (Boolean) -> Unit
) {
    progressDialog
        .setTitleText(tit)
        .setContentText(message)
        .setConfirmText("OK")
        .showCancelButton(false)
        .setCancelClickListener(null)
        .setConfirmClickListener { progressDialog.dismiss()
            completionHandler.invoke(true)
        }
        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
}
private fun showEAlertDialog(progressDialog: SweetAlertDialog,tit:String = "",message: String,progressType:Int = SweetAlertDialog.ERROR_TYPE,
                             completionHandler: (Boolean) -> Unit) {
    progressDialog
        .setTitleText(tit)
        .setContentText(message)
        .setConfirmText("OK")
        .showCancelButton(false)
        .setCancelClickListener(null)
        .setConfirmClickListener {
            progressDialog.dismiss()
            completionHandler.invoke(true)
        }
        .changeAlertType(progressType)
}
fun Dialog.setDialogLayoutParams() {
    val lp = WindowManager.LayoutParams()
    lp.copyFrom(this.window?.attributes)
    lp.width = WindowManager.LayoutParams.MATCH_PARENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
    window?.attributes = lp
}
fun Activity.showExitDialog() {
    val builder = AlertDialog.Builder(this)
        .setTitle("Confirm Exit!")
        .setMessage("Are you sure you want to exit?")
        .setPositiveButton("YES") { _, which ->
            this.finishAffinity()
        }
        .setNegativeButton("CANCEL") { dialog, which ->
            dialog.dismiss()
        }
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setCancelable(false)
    builder.show()
}

fun Fragment.showDialog(tit: String,body:String,okBtn:String,canShowIcon:Boolean = true,completionHandler: (Boolean) -> Unit) {
    val builder = AlertDialog.Builder(requireContext())
        .setTitle(tit)
        .setMessage(body)
        .setPositiveButton(okBtn) { _, which ->
            completionHandler.invoke(true)
        }
        .setNegativeButton("CANCEL") { dialog, which ->
            completionHandler.invoke(false)
            dialog.dismiss()
        }
        .setCancelable(false)
    if (canShowIcon) builder.setIcon(android.R.drawable.ic_dialog_alert)
    builder.show()
}
