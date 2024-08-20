package com.android.postbanksacco.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import com.android.postbanksacco.R
import com.android.postbanksacco.data.model.PagerData
import com.android.postbanksacco.databinding.SliderRowBinding


class MyPagerAdapter(private val context: Context) : PagerAdapter() {
    private val pageData = listOf(
        PagerData(
            "Welcome to Postbank SACCO,manage your finances with ease and convenience.\nTujijenge pamoja."
        ),

        PagerData(
            "Access loans easily with flexible interest rates designed to fit your financial needs."
        ),

        PagerData(
            "Grow your savings with our supportive and adaptable plans"
        ),
    )


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = LayoutInflater.from(context)
        val binding = SliderRowBinding.inflate(layoutInflater, container, false)
        container.addView(binding.root)
        bindPageContent(binding, position)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount() = pageData.size

    override fun isViewFromObject(view: View, `object`: Any) = view == `object`

    private fun bindPageContent(binding: SliderRowBinding, position: Int) {
        val currentItem = pageData[position]
        binding.title.text = currentItem.title

     //  binding.logo.setImageResource(currentItem.image)


    }
}

