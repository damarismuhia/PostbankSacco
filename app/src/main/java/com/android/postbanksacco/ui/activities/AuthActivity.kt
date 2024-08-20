package com.android.postbanksacco.ui.activities

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.android.postbanksacco.utils.makeGone
import com.android.postbanksacco.utils.makeVisible
import dagger.hilt.android.AndroidEntryPoint
import com.android.postbanksacco.R
import com.android.postbanksacco.databinding.ActivityMainBinding

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpNavigationController()

    }
    private fun setUpNavigationController() {
        navController = (supportFragmentManager
            .findFragmentById(R.id.nav_host_auth) as NavHostFragment?)!!.navController
        setSupportActionBar(binding.mToolbar)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.landingFragment , R.id.slidersFragment-> {
                    binding.mToolbar.makeGone()
                    supportActionBar?.hide()
                }else ->{
                    binding.mToolbar.makeVisible()
                     supportActionBar?.show()
                }
            }
        }
        binding.mToolbar.setupWithNavController(navController)

//        configureBackBtn()
    }


    override fun onSupportNavigateUp(): Boolean {
        when (navController.currentDestination?.id){
            R.id.loginFragment ->{
             return  canNavUp()
            }else ->{
            return navController.navigateUp() || super.onSupportNavigateUp()
            }
        }

    }
    private fun canNavUp():Boolean{
        navController.navigate(R.id.landingFragment)
        return true
    }

    override fun onBackPressed() {
        when (navController.currentDestination?.id) {

            R.id.loginFragment -> {
                navController.navigate(R.id.landingFragment)
            }
            else -> {
                navController.navigateUp() || super.onSupportNavigateUp()
            }
        }

    }
}