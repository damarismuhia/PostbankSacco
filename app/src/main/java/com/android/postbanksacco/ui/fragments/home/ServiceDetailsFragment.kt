package com.android.postbanksacco.ui.fragments.home

import android.os.Bundle
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import com.android.postbanksacco.R
import com.android.postbanksacco.data.response.HomeItemsData
import com.android.postbanksacco.databinding.FragmentServiceDetailsBinding
import com.android.postbanksacco.ui.fragments.BaseAuthFragment
import com.android.postbanksacco.utils.CacheKeys
import com.android.postbanksacco.utils.EncryptedPref
import com.android.postbanksacco.utils.KEndpoints
import com.android.postbanksacco.utils.extensions.formatDigits
import com.android.postbanksacco.utils.extensions.navigateToHome
import com.android.postbanksacco.utils.extensions.readItemFromPref
import com.android.postbanksacco.utils.extensions.showErrorDialog
import com.android.postbanksacco.utils.extensions.showLoadingAlert
import com.android.postbanksacco.viewmodels.AuthViewModel
import org.json.JSONObject

@AndroidEntryPoint
class ServiceDetailsFragment : BaseAuthFragment<FragmentServiceDetailsBinding, AuthViewModel>(FragmentServiceDetailsBinding::inflate){
    override val viewModel: AuthViewModel by viewModels()
    private lateinit var services:HomeItemsData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            services =
                it.getParcelable<HomeItemsData>("service") as HomeItemsData

        }
    }
    private lateinit var progressDialog: SweetAlertDialog
    override fun setupUI() {
        super.setupUI()
        progressDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        setUpData()
        val name = EncryptedPref.readPreferences(requireContext(), CacheKeys.firstName)

    }

    private fun setUpData() {
        binding.apply {
            setUpFragmentTit()
            tvservice.text = services.serviceName
            val amnt = formatDigits(services.pricing)
            binding.tvPriceVal.text = "Ksh $amnt"
            binding.tvPercentVal.text = "${services.discountPercent}"
            val originalPrice = services.pricing.toDouble()
            val discountedAmnt = (services.discountPercent.toDouble())
            val finalDisAmnt = originalPrice - discountedAmnt
            tvDiscountVal.text = "Ksh $finalDisAmnt"
            binding.btnSubscribe.setOnClickListener {
//                showTransactionConfirmDialog(
//                    "Please confirm your subscription to ${services.serviceName}. You will be charged Ksh $finalDisAmnt for this subscription.\n"){proceed->
//                    if (proceed){
//                        subscribeReq(finalDisAmnt.toString())
//                    }
//                }
            }
        }
    }
    private fun setUpFragmentTit(){
        binding.fragmentTitle.apply {
            fragmentTitle.text = getString(R.string.service_details)
            fragmentTitleDesc.text = "View detailed pricing and discount for our subscription service."
        }
    }
    private fun subscribeReq(amount:String) {
        binding.apply {
            if (!progressDialog.isShowing) {
                showLoadingAlert(progressDialog)
            }
            val tk = "Bearer${EncryptedPref.readPreferences(requireContext(),CacheKeys.jt)}"
            val body = JSONObject()
                .put("subscriberEmail",readItemFromPref(CacheKeys.email))
                .put("serviceName",services.serviceName)
                .put("amountPaid",amount)
            viewModel.commonPostWithAuthRequest(body, KEndpoints.subscribe,tk).observe(viewLifecycleOwner) {
                when (it.message?.lowercase()) {
                    "success" -> {
                        progressDialog.dismiss()
                        val args = Bundle().apply {
                            putString("servicesName",tvservice.text.toString())
                            putString("amountPaid",tvDiscountVal.text.toString())
                        }
                        navigateToHome(R.id.successFragment,args)
                    }
                    else -> {
                        showErrorDialog(progressDialog,it.message)
                    }
                }
            }
        }
    }




}