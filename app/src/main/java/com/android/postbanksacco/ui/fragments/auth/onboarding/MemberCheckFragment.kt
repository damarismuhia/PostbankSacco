package com.android.postbanksacco.ui.fragments.auth.onboarding

import com.android.postbanksacco.R
import com.android.postbanksacco.databinding.FragmentMemberCheckBinding
import com.android.postbanksacco.ui.fragments.BaseGuestFragment
import com.android.postbanksacco.utils.extensions.navigateNext
import com.android.postbanksacco.utils.extensions.showToast

class MemberCheckFragment :
    BaseGuestFragment<FragmentMemberCheckBinding>(FragmentMemberCheckBinding::inflate) {

    override fun setupUI() {
        super.setupUI()
        binding.btnHasAccount.setOnClickListener {
           navigateNext(R.id.accountVerificationFragment)
        }
        binding.btnNewUser.setOnClickListener {
            showToast("Coming Soon")
        }
    }


}