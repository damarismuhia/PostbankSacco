package com.android.postbanksacco.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import cn.pedant.SweetAlert.SweetAlertDialog
import com.scottyab.rootbeer.RootBeer
import dagger.hilt.android.AndroidEntryPoint
import com.android.postbanksacco.R
import com.android.postbanksacco.databinding.ActivitySplashBinding
import com.android.postbanksacco.utils.checkDeviceStatus
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var progressDialog:SweetAlertDialog
    @Inject
    lateinit var rootBeer: RootBeer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressDialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
        progressDialog.setCancelable(false)
        if (checkDeviceStatus(progressDialog, rootBeer)) {
            binding = ActivitySplashBinding.inflate(layoutInflater)
            setContentView(binding.root)
            val animationZoomOut = AnimationUtils.loadAnimation(this, R.anim.anim_fade_in)
            binding.logo.startAnimation(animationZoomOut)

            binding.logo.animate().setDuration(1000).alpha(1f).withEndAction {
                launchActivity()
            }
        }

    }
    private fun launchActivity() {
        startActivity(Intent(this, AuthActivity::class.java))
        this.finish()
    }
}
