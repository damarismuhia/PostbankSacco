package com.android.postbanksacco.ui.fragments.home.enquiries

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.postbanksacco.R
import com.android.postbanksacco.data.adapters.MinistatementAdapter
import com.android.postbanksacco.data.response.MinistatementModel
import com.android.postbanksacco.databinding.FragmentMiniStatementBinding
import com.android.postbanksacco.ui.fragments.TransactionBaseFragment
import com.android.postbanksacco.utils.extensions.showLoadingAlert
import com.android.postbanksacco.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MiniStatementFragment : TransactionBaseFragment<FragmentMiniStatementBinding, MainViewModel>(FragmentMiniStatementBinding::inflate){
    private lateinit var itemAdapter: MinistatementAdapter
    override val viewModel: MainViewModel by activityViewModels()

    private lateinit var progressDialog: SweetAlertDialog
    override fun setupUI() {
        super.setupUI()
        progressDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        setUpData()


    }

    private fun setUpData() {
        setUpFragmentTit()
        fetchMini()
        binding.apply {

        }
    }
    private  fun fetchMini(){
        if (!progressDialog.isShowing) {
            showLoadingAlert(progressDialog)
        }
        CoroutineScope(Dispatchers.Main).launch {
            delay(2500)
            if (progressDialog.isShowing) {
                progressDialog.dismiss()
                itemAdapter = MinistatementAdapter(MinistatementModel.getMini(), requireContext())
                binding.rvMini.setHasFixedSize(true)
                binding.rvMini.apply {
                    layoutManager = GridLayoutManager(requireActivity(), 1)
                    adapter = itemAdapter
                }
            }

        }
    }
    private fun setUpFragmentTit(){
        binding.fragmentTitle.apply {
            fragmentTitle.text = getString(R.string.ministatement)
            fragmentTitleDesc.text = "FOSA Account Recent Transactions"
        }
    }




}