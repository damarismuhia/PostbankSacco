package com.android.postbanksacco.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.scottyab.rootbeer.RootBeer
import dagger.hilt.android.AndroidEntryPoint
import com.android.postbanksacco.R
import com.android.postbanksacco.databinding.ActivityHomeBinding
import com.android.postbanksacco.utils.extensions.showExitDialog
import com.android.postbanksacco.utils.makeGone
import com.android.postbanksacco.utils.makeVisible
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var progressDialog: SweetAlertDialog
    @Inject
    lateinit var rootBeer: RootBeer
    private val countdownHandler = Handler(Looper.getMainLooper())
    private var mTime: Long = 120000 //timeout after 2 minutes = 120000 milliseconds
    private var IDLE_TIMEOUT: Long = 120000//120000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressDialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
        progressDialog.setCancelable(false)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpNavigationController()
        reStartHandler()

    }

    private fun setUpNavigationController() {
        navController = (supportFragmentManager
            .findFragmentById(R.id.navhost_dashboard_module) as NavHostFragment?)!!.navController
        setSupportActionBar(binding.mToolbar)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    binding.appBar.makeGone()
                    supportActionBar?.hide()
                }
                else -> {
                    supportActionBar?.show()
                    binding.appBar.makeVisible()
                }
            }
        }

        binding.mToolbar.setupWithNavController(navController)

    }
    override fun onUserInteraction() {
        super.onUserInteraction()
        mTime = IDLE_TIMEOUT // Reset time to 2 minutes
        reStartHandler()
    }
    private fun reStartHandler() {
        // If there is an existing callback, remove it
        countdownHandler.removeCallbacksAndMessages(null)

        // Create a new callback to reduce time every second
        countdownHandler.post(object : Runnable {
            override fun run() {
                // Reduce time by 1000 milliseconds (1 second)
                mTime -= 1000

                // Check if time has reached 0
                if (mTime <= 0) {
                    navToLogin()
                } else {
                    // Timber.e("INITIT TIMEOUT CALLED:::::::: $mTime")
                    // Continue the countdown
                    // Schedule the next iteration after 1 second
                    /**The postDelayed() method  post code that should be run in the future.
                     * The Runnable contains the code you want to run in its run() method,
                     * long specifies the number of milliseconds you wish to delay the code by.
                     * The code will run as soon as possible after the delay.*/
                    countdownHandler.postDelayed(this, 1000)
                }
            }
        })

    }
    private fun navToLogin(){
        startActivity(Intent(this,AuthActivity::class.java))

    }



    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


    override fun onBackPressed() {
        val currentDestination = navController.currentDestination?.id
        if (currentDestination == R.id.homeFragment) {
            showExitDialog()
        } else {
            navController.navigateUp()
        }
    }
    override fun onResume() {
        super.onResume()
        reStartHandler()
    }

    override fun onDestroy() {
        super.onDestroy()
        countdownHandler.removeCallbacksAndMessages(null)
    }



}