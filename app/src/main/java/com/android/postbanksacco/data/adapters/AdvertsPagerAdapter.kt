package com.android.postbanksacco.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import com.android.postbanksacco.R
import com.android.postbanksacco.data.model.PagerData
import com.android.postbanksacco.databinding.AdvtRowBinding
import com.android.postbanksacco.databinding.SliderRowBinding


class AdvertsPagerAdapter(private val context: Context) : PagerAdapter() {
    private val pageData: IntArray = intArrayOf(R.drawable.adv,R.drawable.adv1)
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = LayoutInflater.from(context)
        val binding = AdvtRowBinding.inflate(layoutInflater, container, false)
        container.addView(binding.root)
        bindPageContent(binding, position)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount() = pageData.size

    override fun isViewFromObject(view: View, `object`: Any) = view == `object`

    private fun bindPageContent(binding: AdvtRowBinding, position: Int) {
        val currentItem = pageData[position]
        binding.clImage.setBackgroundResource(currentItem)
    }
}

