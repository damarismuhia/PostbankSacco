package com.android.postbanksacco.ui.fragments.home.ft

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.postbanksacco.R
import com.android.postbanksacco.data.adapters.ListItemsAdapter
import com.android.postbanksacco.data.adapters.MinistatementAdapter
import com.android.postbanksacco.data.model.HomeModel
import com.android.postbanksacco.data.response.MinistatementModel
import com.android.postbanksacco.databinding.FragmentHomeFTBinding
import com.android.postbanksacco.databinding.FragmentMiniStatementBinding
import com.android.postbanksacco.ui.fragments.TransactionBaseFragment
import com.android.postbanksacco.utils.extensions.navigateNext
import com.android.postbanksacco.utils.extensions.showLoadingAlert
import com.android.postbanksacco.utils.interfaces.OnMenuItemClick
import com.android.postbanksacco.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFTFragment : TransactionBaseFragment<FragmentHomeFTBinding, MainViewModel>(
    FragmentHomeFTBinding::inflate),OnMenuItemClick{
    private lateinit var itemAdapter: ListItemsAdapter
    override val viewModel: MainViewModel by activityViewModels()

    private lateinit var progressDialog: SweetAlertDialog
    override fun setupUI() {
        super.setupUI()
        progressDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        setUpData()
    }

    private fun setUpData() {
        setUpFragmentTit()
        populateMenus()
        binding.apply {

        }
    }
    private  fun populateMenus(){
        itemAdapter = ListItemsAdapter(HomeModel.getFTItems(),this@HomeFTFragment)
        binding.rvMini.setHasFixedSize(true)
        binding.rvMini.apply {
            layoutManager = GridLayoutManager(requireActivity(), 1)
            adapter = itemAdapter
        }
    }
    private fun setUpFragmentTit(){
        binding.fragmentTitle.apply {
            fragmentTitle.text = getString(R.string.fund_transfer)
            fragmentTitleDesc.text = getString(R.string.where_to_ft)
        }
    }

    override fun navigateTo(pos: Int, obj: HomeModel) {
        when(pos){
           0->{
               navigateNext(R.id.mobileMoneyFragment)
           }
           1->{
               navigateNext(R.id.internalFtFragment)
           }
        }

    }


}