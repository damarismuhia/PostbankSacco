package com.android.postbanksacco.ui.fragments.home.ft.mobilemoney

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
import com.android.postbanksacco.data.model.HomeModel
import com.android.postbanksacco.databinding.FragmentHomeFTBinding
import com.android.postbanksacco.ui.fragments.TransactionBaseFragment
import com.android.postbanksacco.utils.extensions.navigateNext
import com.android.postbanksacco.utils.extensions.navigateWithArgs
import com.android.postbanksacco.utils.interfaces.OnMenuItemClick
import com.android.postbanksacco.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MobileMoneyFragment : TransactionBaseFragment<FragmentHomeFTBinding, MainViewModel>(
    FragmentHomeFTBinding::inflate), OnMenuItemClick {
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
        itemAdapter = ListItemsAdapter(HomeModel.getMobileMoneyItems(),this)
        binding.rvMini.setHasFixedSize(true)
        binding.rvMini.apply {
            layoutManager = GridLayoutManager(requireActivity(), 1)
            adapter = itemAdapter
        }
    }
    private fun setUpFragmentTit(){
        binding.fragmentTitle.apply {
            fragmentTitle.text = getString(R.string.momo_transfer)
            fragmentTitleDesc.text = getString(R.string.where_to_momo)
        }
    }

    override fun navigateTo(pos: Int, obj: HomeModel) {
        val args = Bundle().apply {
            putParcelable("momoData", obj)
        }
        navigateWithArgs(R.id.momoFtFragment, args)

    }


}