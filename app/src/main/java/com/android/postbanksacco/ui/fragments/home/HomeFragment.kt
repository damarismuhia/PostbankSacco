package com.android.postbanksacco.ui.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import com.android.postbanksacco.R
import com.android.postbanksacco.data.adapters.AdvertsPagerAdapter
import com.android.postbanksacco.data.adapters.HomeMenuItemsAdapter
import com.android.postbanksacco.data.adapters.MySubsAdapter
import com.android.postbanksacco.data.model.HomeModel
import com.android.postbanksacco.data.response.HomeItemsData
import com.android.postbanksacco.data.response.HomeItemsResponse
import com.android.postbanksacco.data.response.SubscriboData
import com.android.postbanksacco.data.response.SubscriboResponse
import com.android.postbanksacco.databinding.FragmentHomeBinding
import com.android.postbanksacco.databinding.FullStatementBottomsheetBinding
import com.android.postbanksacco.ui.activities.AuthActivity
import com.android.postbanksacco.ui.dialog.TransactionConfirmationDialog.Companion.showTransactionConfirmDialog
import com.android.postbanksacco.ui.dialog.TransactionsModel
import com.android.postbanksacco.ui.fragments.BaseAuthFragment
import com.android.postbanksacco.ui.fragments.TransactionBaseFragment
import com.android.postbanksacco.utils.CacheKeys
import com.android.postbanksacco.utils.EncryptedPref
import com.android.postbanksacco.utils.extensions.autoPlayAdvertisement
import com.android.postbanksacco.utils.extensions.capitalizeWords
import com.android.postbanksacco.utils.extensions.chargeAmnt
import com.android.postbanksacco.utils.extensions.currency
import com.android.postbanksacco.utils.extensions.formatDigits
import com.android.postbanksacco.utils.extensions.fosaBal
import com.android.postbanksacco.utils.extensions.navigateNext
import com.android.postbanksacco.utils.extensions.navigateToHome
import com.android.postbanksacco.utils.extensions.navigateWithArgs
import com.android.postbanksacco.utils.extensions.pickRegDob
import com.android.postbanksacco.utils.extensions.readItemFromPref
import com.android.postbanksacco.utils.extensions.showDatePicker
import com.android.postbanksacco.utils.extensions.showLoadingAlert
import com.android.postbanksacco.utils.interfaces.OnMenuItemClick
import com.android.postbanksacco.utils.interfaces.OnSubsItemClick
import com.android.postbanksacco.utils.makeVisible
import com.android.postbanksacco.utils.makeGone
import com.android.postbanksacco.viewmodels.AuthViewModel
import com.android.postbanksacco.viewmodels.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : TransactionBaseFragment<FragmentHomeBinding, MainViewModel>(FragmentHomeBinding::inflate),
    OnMenuItemClick {
    private lateinit var homeMenuItemsAdapter: HomeMenuItemsAdapter
    override val viewModel: MainViewModel by activityViewModels()
    private var mBottomSheetDialog: BottomSheetDialog? = null
    private lateinit var  statementBinding :FullStatementBottomsheetBinding

    private lateinit var progressDialog: SweetAlertDialog
    override fun setupUI() {
        super.setupUI()
        progressDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        setUpData()
        val name = EncryptedPref.readPreferences(requireContext(), CacheKeys.FIRST_NAME)
        binding.tvGreetings.text = "Welcome back ${name?.capitalizeWords()},"


    }

    private fun setUpData() {
        homeMenuItemsAdapter = HomeMenuItemsAdapter(HomeModel.getHomeItems(requireContext()),this)
        binding.dashBoardRv.setHasFixedSize(true)
        binding.dashBoardRv.apply {
            layoutManager = GridLayoutManager(requireActivity(), 3)
            adapter = homeMenuItemsAdapter
        }
        loadAdvs()
        binding.apply {
            ivProfile.setOnClickListener {
                startActivity(Intent(requireContext(),AuthActivity::class.java))
            }
            btnViewBal.setOnClickListener {
                getCharges("mini")
            }
            btnState.setOnClickListener {
                setupFullStatementBottomSheet()
            }
            tvShowHideBalance.setOnClickListener {
                Timber.e("Tracking BUTTON TAP")
                if (tvShowHideBalance.text == getString(R.string.show_balance)) {
                    getCharges("bi")
                }else {
                    Timber.e("Go here for false Tracking BALANCE")
                    tvShowHideBalance.text = getString(R.string.show_balance)
                    tvAvailBalValue.text = "✽✽✽✽✽"
                }
            }

        viewModel.authSuccess.observe(viewLifecycleOwner) {
            if (it == true) {
                Timber.e("Tracking BALANCE")
                tvShowHideBalance.text = getString(R.string.hide_balance)
                tvAvailBalValue.text = fosaBal
                viewModel.unsetAuthSuccess()
            }
        }
        }
    }
    private fun getCharges(type: String){
        if (!progressDialog.isShowing){
            progressDialog.show()
        }
        CoroutineScope(Dispatchers.Main).launch {
            delay(1100)
            if (progressDialog.isShowing) {
                progressDialog.dismiss()

                showDialog(type)
            }
        }
    }
    private fun setupFullStatementBottomSheet() {
         statementBinding = FullStatementBottomsheetBinding.inflate(LayoutInflater.from(context))
        statementBinding.apply {
            etEmail.setText(readItemFromPref(CacheKeys.email))
            etStartDate.setOnClickListener { etStartDate.showDatePicker() }
            etEndDate.setOnClickListener { etEndDate.showDatePicker() }
            etEndDate.setCurrentDate()
        }
        statementBinding.btnSubmit.setOnClickListener {
            getCharges("full")
        }
        mBottomSheetDialog = BottomSheetDialog(requireContext(),R.style.AccountsBaseBottomSheetDialog)
        mBottomSheetDialog = BottomSheetDialog(requireContext())
        mBottomSheetDialog!!.setContentView(statementBinding.root)
        mBottomSheetDialog!!.show()
        mBottomSheetDialog!!.setOnDismissListener {
            mBottomSheetDialog = null
        }
    }
    private fun EditText.setCurrentDate(){
        val cal: Calendar = Calendar.getInstance()
        val currentTime: Date = cal.time
        cal.add(Calendar.DAY_OF_YEAR, -30)

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.UK)
        val formattedendDate = formatter.format(currentTime)
        this.setText(formattedendDate)
    }

    private fun showDialog(type:String) {
        Timber.e("Tracking BUTTON TAP 1")
        val dialogList = mutableListOf<TransactionsModel>().apply {
            add(TransactionsModel("Account:", "${binding.textacName.text}"))
            if (type == "full"){
                add(TransactionsModel("Email Address:", "${statementBinding.etEmail.text}"))
            }
            add(TransactionsModel("Charges:", "$currency ${formatDigits(chargeAmnt)}"))
            add(TransactionsModel("Excise Duty:", "$currency ${formatDigits(chargeAmnt)}"))
        }
        val tit:String = if (type == "bi"){
            getString(R.string.confirm_balance_enquiry)
        }else if (type == "mini"){
            getString(R.string.confirm_mini_enquiry)
        }else{
            getString(R.string.confirm_full_enquiry)
        }
        showTransactionConfirmDialog(tit,
            dialogList
        ) { confirmClicked ->
            if (confirmClicked) {
                if (type == "bi") {
                    navigateNext(R.id.authTransactionFragment)
                }else if (type == "mini") {
                    navigateNext(R.id.miniStatementFragment)
                }else{
                    viewModel.transactionList.postValue(dialogList)
                    val args = Bundle().apply {
                        putString("fragmentType", "full")
                    }
                    if (!progressDialog.isShowing){
                        progressDialog.show()
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500)
                        if (progressDialog.isShowing) {
                            progressDialog.dismiss()
                            mBottomSheetDialog?.dismiss()
                            navigateToHome(R.id.successFragment,args)
                        }
                    }

                }

            }
        }
    }
    private fun loadAdvs(){
    val fragAd =
        AdvertsPagerAdapter(
            requireContext()
        )
    binding.pager.adapter = fragAd
    fragAd.notifyDataSetChanged()
   binding.indicatorsContainer.setupWithViewPager(binding.pager, true)
    autoPlayAdvertisement(binding.pager)

}
    override fun navigateTo(obj: HomeModel) {
        when(obj.code.lowercase()){
            "deposit"->{
                navigateNext(R.id.depositFragment)
            }
            "airtime"->{
                navigateNext(R.id.airtimeFragment)
            }
        }
    }




}