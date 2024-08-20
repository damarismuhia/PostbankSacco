package com.android.postbanksacco.ui.dialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.android.postbanksacco.R
import com.android.postbanksacco.databinding.LayoutConfirmDialogBinding
import com.android.postbanksacco.utils.extensions.setDialogLayoutParams
import com.android.postbanksacco.utils.extensions.setUpRecyclerAdapter

class TransactionConfirmationDialog(context: Context) : Dialog(context) {
    private lateinit var dialogAdapter: TransactionDialogAdapter
    private lateinit var binding: LayoutConfirmDialogBinding
    // Use this instance of the interface to deliver action events
    private var mCancelClickListener: ((view: View) -> Unit)? = null
    private var mConfirmClickListener: ((view: View) -> Unit)? = null
    private var dialogDetailCommons: List<TransactionsModel> = listOf()
    private var confirmTit:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onSaveInstanceState()
       requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = LayoutConfirmDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.window?.setBackgroundDrawableResource(android.R.color.transparent)
        this.setDialogLayoutParams()
        setCancelable(false)
        binding.tvTit.text = confirmTit
        binding.recyclerViewDialogContents.setUpRecyclerAdapter(dialogDetailCommons)

        binding.buttonConfirm.setOnClickListener {
            if (mConfirmClickListener != null) {
                mConfirmClickListener!!.invoke(it)
            } else {
                this.dismiss()
            }
        }
        binding.buttonCancel.setOnClickListener {
            if (mCancelClickListener != null) {
                mCancelClickListener!!.invoke(it)
            } else {
                this.dismiss()
            }
        }
    }
    fun setCancelClickListener(listener: (Any) -> Unit) {
        mCancelClickListener = listener
    }

    fun setConfirmClickListener(listener: (Any) -> Unit) {
        mConfirmClickListener = listener

    }

    //dialog builder

    // set up the recycler adapter


        fun transactionDialog(
            list: List<TransactionsModel>,
            tit:String
        ): TransactionConfirmationDialog {
            this.dialogDetailCommons = list
            this.confirmTit = tit
            return this
        }
    companion object{
        private var transDialog: TransactionConfirmationDialog? = null
        fun Fragment.showTransactionConfirmDialog(tit: String,list: List<TransactionsModel>, completionHandler:(Boolean)->Unit){
            if (transDialog == null) {
                transDialog = TransactionConfirmationDialog(requireContext())
            }
            transDialog?.transactionDialog(list,tit)
            transDialog?.setConfirmClickListener {
                transDialog?.dismiss()
                completionHandler.invoke(true)
                transDialog = null
            }
            transDialog?.setCancelClickListener {
                transDialog?.dismiss()
                completionHandler.invoke(false)
                transDialog = null
            }
            transDialog?.show()
        }
    }


    //  cancel button ClickListener with listener
/*    private fun cancelButtonClickListener(callBacks: ConfirmDialogCallBacks) =
        with(cancelButton) {
            setClickListenerToDialogIcon {
                callBacks.cancel()
                dialog?.dismiss()
            }
        }

    //  confirm button ClickListener with listener
    private fun confirmButtonClickListener(callBacks: ConfirmDialogCallBacks) =
        with(confirmButton) {
            setClickListenerToDialogIcon{
                callBacks.confirm()
                dialog?.dismiss()
            }

        }

    fun setCallbacks(dialogCallBacks: ConfirmDialogCallBacks){
        confirmButtonClickListener(dialogCallBacks)
        cancelButtonClickListener(dialogCallBacks)
    }

    //  view click listener as extension function
    *//**This function takes a single parameter, func, which is a lambda function that takes no arguments
     * and returns nothing (() -> Unit).
     * Lambda function syntax { argumentList -> codeBody } Where argumentList is a comma-separated list of zero or more arguments
     * that the lambda function can take, and codeBody is the block of code that the lambda function executes.*//*
    private fun View.setClickListenerToDialogIcon(func: (() -> Unit)?) =
        setOnClickListener {
            func?.invoke()
            dialog?.dismiss()
        }*/

}
