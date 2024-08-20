package com.android.postbanksacco.ui.fragments.home

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import com.android.postbanksacco.R
import com.android.postbanksacco.data.adapters.MySubsAdapter
import com.android.postbanksacco.data.response.SubscriboData
import com.android.postbanksacco.data.response.SubscriboResponse
import com.android.postbanksacco.databinding.FragmentViewMySubscriptionBinding
import com.android.postbanksacco.ui.fragments.BaseAuthFragment
import com.android.postbanksacco.utils.CacheKeys
import com.android.postbanksacco.utils.EncryptedPref
import com.android.postbanksacco.utils.extensions.readItemFromPref
import com.android.postbanksacco.utils.extensions.showLoadingAlert
import com.android.postbanksacco.utils.interfaces.OnSubsItemClick
import com.android.postbanksacco.utils.makeGone
import com.android.postbanksacco.utils.makeVisible
import com.android.postbanksacco.viewmodels.AuthViewModel

@AndroidEntryPoint
class ViewMySubscriptionFragment : BaseAuthFragment<FragmentViewMySubscriptionBinding, AuthViewModel>(FragmentViewMySubscriptionBinding::inflate),
OnSubsItemClick {
    private lateinit var mySubsAdapter: MySubsAdapter
    override val viewModel: AuthViewModel by viewModels()
    private var mySubList = arrayListOf<SubscriboData>()

    private lateinit var progressDialog: SweetAlertDialog
    override fun setupUI() {
        super.setupUI()
        progressDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        setUpData()
        populateMySubs()

    }
    private fun populateMySubs() {
        binding.apply {

            if (!progressDialog.isShowing) {
                mySubRv.makeGone()
                showLoadingAlert(progressDialog)
            }
            val tk = "Bearer${EncryptedPref.readPreferences(requireContext(),CacheKeys.jt)}"
            viewModel.getMySubscriptionsReq("subscription/subscriptions/${readItemFromPref(CacheKeys.email)}",tk).observe(viewLifecycleOwner) {
                when (it.message?.lowercase()) {
                    "success" -> {
                        mySubRv.makeVisible()
                        progressDialog.dismiss()
                        if (it.data != null){
                            val data = it.data as SubscriboResponse
                            popMySubRecler(data.data)

                        }


                    }
                    else -> {
                        progressDialog.dismiss()
                        if (it.data != null){
                            val data = it.data as SubscriboResponse
                            popMySubRecler(data.data)
                        }else{
                            tvActiveSubs.makeVisible()
                            tvActiveSubs.text  = getString(R.string.unable_to_fetch_data)
                        }
                    }
                }
            }
        }
    }
    private  fun popMySubRecler(data: List<SubscriboData>){
        binding.apply {
            if(data.isEmpty()){
                tvActiveSubs.makeVisible()
                tvActiveSubs.text  = getString(R.string.active_subscriptions_will_appear_here)
            }else{
                mySubList.clear()
                mySubList.addAll(data)
            }
            mySubRv.adapter?.notifyDataSetChanged()
        }
    }



    private fun setUpData() {
        mySubsAdapter = MySubsAdapter(mySubList,this)
        binding.mySubRv.setHasFixedSize(true)
        binding.mySubRv.apply {
            layoutManager = GridLayoutManager(requireActivity(), 1)
            adapter = mySubsAdapter
        }
        setUpFragmentTit()
    }
    private fun setUpFragmentTit(){
        binding.fragmentTitle.apply {
            fragmentTitle.text = getString(R.string.my_subscriptions)
            fragmentTitleDesc.text = getString(R.string.here_is_a_list_of_all_your_subscriptions)

        }
    }

    override fun navigateTo(service: SubscriboData) {
    }


}