package com.android.postbanksacco.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.android.postbanksacco.utils.Inflate
import com.android.postbanksacco.viewmodels.BaseAuthViewModel

/**
 * Plain base fragment - to used bu fragments without viewModel
 *
 * @param VB viewBinding layout to inflate
 * @property inflate the layout inflater
 * @constructor Create empty Plain base fragment
 */
abstract class BaseAuthFragment<VB : ViewBinding, VM: BaseAuthViewModel>(
    private val inflate: Inflate<VB>
) : Fragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!
    protected abstract val viewModel: VM


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Setup UI
     *
     */
    protected open fun setupUI() = Unit


}



