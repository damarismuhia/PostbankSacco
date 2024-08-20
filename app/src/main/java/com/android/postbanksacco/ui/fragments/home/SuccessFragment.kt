package com.android.postbanksacco.ui.fragments.home

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.postbanksacco.R
import dagger.hilt.android.AndroidEntryPoint
import com.android.postbanksacco.databinding.FragmentSuccessBinding
import com.android.postbanksacco.ui.dialog.TransactionsModel
import com.android.postbanksacco.ui.fragments.BaseAuthFragment
import com.android.postbanksacco.ui.fragments.TransactionBaseFragment
import com.android.postbanksacco.utils.CacheKeys
import com.android.postbanksacco.utils.EncryptedPref
import com.android.postbanksacco.utils.extensions.generateTransactionRef
import com.android.postbanksacco.utils.extensions.readItemFromPref
import com.android.postbanksacco.utils.extensions.setUpRecyclerAdapter
import com.android.postbanksacco.utils.extensions.showCurrentDate
import com.android.postbanksacco.viewmodels.AuthViewModel
import com.android.postbanksacco.viewmodels.MainViewModel


@AndroidEntryPoint
class SuccessFragment : TransactionBaseFragment<FragmentSuccessBinding, MainViewModel>(
    FragmentSuccessBinding::inflate){
    override val viewModel: MainViewModel by activityViewModels()
    private var fragmentType:String = ""
    private var transactionsList: MutableList<TransactionsModel> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fragmentType = it.getString("fragmentType").toString()

        }
    }

    override fun setupUI() {
        super.setupUI()
        binding.apply {
            greetings.text = "Hi ${readItemFromPref(CacheKeys.FIRST_NAME)},"
            if (fragmentType == "full") {
                subTit.text = getString(R.string.email_your_sms_confirmation_shorttly)
            }else{
                subTit.text = getString(R.string.your_sms_confirmation_shorttly)
            }
            viewModel.transactionList.observe(viewLifecycleOwner) {
                transactionsList.clear()
                if (it != null) {
                    transactionsList.addAll(it)
                    transactionsList.add(TransactionsModel(getString(R.string.date_amp_time),
                        showCurrentDate("dd-MM-yyyy | hh:mm a")
                    ))
                    transactionsList.add(TransactionsModel("Ref Id:", generateTransactionRef()))
                    recyclerViewDialogContents.setUpRecyclerAdapter(transactionsList)
                }
            }


            btnOk.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.transactionList.postValue(null)
    }





}