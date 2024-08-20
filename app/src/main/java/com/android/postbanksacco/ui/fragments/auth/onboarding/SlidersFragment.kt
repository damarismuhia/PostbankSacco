package com.android.postbanksacco.ui.fragments.auth.onboarding

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.android.postbanksacco.R
import com.android.postbanksacco.data.adapters.MyPagerAdapter
import com.android.postbanksacco.databinding.FragmentSlidersBinding
import com.android.postbanksacco.ui.fragments.BaseGuestFragment
import com.android.postbanksacco.utils.CacheKeys
import com.android.postbanksacco.utils.EncryptedPref
import com.android.postbanksacco.utils.extensions.navigateNext
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class SlidersFragment : BaseGuestFragment<FragmentSlidersBinding>
    (FragmentSlidersBinding::inflate) {
    private lateinit var adapter:MyPagerAdapter
    lateinit var runnable: Runnable
    private val delay = 4500L //milliseconds
    private var handler = Handler()
    private var page = 0
    override fun setupUI() {
        super.setupUI()
        initViewPager()
        setupIndicators()
        setCurrentIndicator(0)
        setOnclickListener()
    }
    private fun setOnclickListener() {
        binding.btnGetStarted.setOnClickListener {
            EncryptedPref.writePreference(requireContext(), CacheKeys.done_with_slider, "true")
           navigateNext(R.id.landingFragment)
        }
    }

    private fun initViewPager() {
        val viewPager = binding.viewPager
        adapter = MyPagerAdapter(requireContext())
        viewPager.adapter = adapter
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                var currentItem = viewPager.currentItem
                if (currentItem == adapter.count) {
                    currentItem = 0
                }
                val nextItem = if (currentItem == adapter.count - 1) 0 else currentItem + 1
                viewPager.setCurrentItem(nextItem, true)

                handler.postDelayed(this, delay)
            }
        }
        // handler.postDelayed(runnable, delay)//5000L

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // Not needed for your purpose, but you can implement it if required
            }

            override fun onPageSelected(position: Int) {
                // Update items based on the currently selected page index
                // For example:
                updateBg(position)
                setCurrentIndicator(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }
    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(adapter.count)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(requireContext())
            indicators[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.active_dot
                    )
                )
                it.layoutParams = layoutParams
                binding.indicatorsContainer.addView(it)
            }
        }
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount = binding.indicatorsContainer.childCount
        for (i in 0 until childCount) {
            val imageView = binding.indicatorsContainer.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.active_dot
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.inactive_dot
                    )
                )
            }
        }
    }

    fun updateBg(pos:Int){
        when(pos){
            0 -> {
                binding.mainLayout.background =
                    AppCompatResources.getDrawable(requireContext(),R.drawable.slider2)
            }
            1 -> {
                binding.mainLayout.background =
                    AppCompatResources.getDrawable(requireContext(), R.drawable.slider)
                // Update items for the second page
            }
            2 -> {
                binding.mainLayout.background =
                    AppCompatResources.getDrawable(requireContext(), R.drawable.slider1)
            }
        }
    }
    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, delay)
    }
    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }



}