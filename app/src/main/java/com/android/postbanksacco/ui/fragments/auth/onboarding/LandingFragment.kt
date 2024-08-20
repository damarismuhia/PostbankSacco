package com.android.postbanksacco.ui.fragments.auth.onboarding

import android.text.Html
import androidx.fragment.app.viewModels
import com.android.postbanksacco.databinding.FragmentLandingBinding
import com.android.postbanksacco.ui.fragments.BaseAuthFragment
import dagger.hilt.android.AndroidEntryPoint
import com.android.postbanksacco.R
import com.android.postbanksacco.utils.CacheKeys
import com.android.postbanksacco.utils.EncryptedPref
import com.android.postbanksacco.utils.extensions.navigateNext
import com.android.postbanksacco.utils.extensions.readItemFromPref
import com.android.postbanksacco.utils.makeVisible
import com.android.postbanksacco.viewmodels.AuthViewModel

@AndroidEntryPoint
class LandingFragment : BaseAuthFragment<FragmentLandingBinding, AuthViewModel>(FragmentLandingBinding::inflate) {
    override val viewModel: AuthViewModel by viewModels()

    override fun setupUI() {
        super.setupUI()
        if (EncryptedPref.readPreferences(requireContext(), CacheKeys.done_with_slider) != ("true")) {
            navigateNext(R.id.slidersFragment)
        }else {
            initViews()
        }

    }

    private fun initViews(){
        binding.apply {
            btnGetStarted.text  = if (readItemFromPref(CacheKeys.done_onboarding) != "true"){
                getString(R.string.get_started)
            }else getString(R.string.login)
            val hasLogin:Boolean = btnGetStarted.text.contentEquals(getString(R.string.login))
            btnGetStarted.setOnClickListener {
                if (hasLogin){
                        navigateNext(R.id.loginFragment)
             }else {
                    navigateNext(R.id.memberCheckFragment)
                }
            }

            if (!hasLogin){

            }
        }
    }
}