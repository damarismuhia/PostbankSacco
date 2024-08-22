package com.android.postbanksacco.data.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.postbanksacco.data.model.HomeModel
import com.android.postbanksacco.data.response.HomeItemsData
import com.android.postbanksacco.utils.interfaces.OnMenuItemClick
import com.android.postbanksacco.databinding.HomeRowBinding
import com.android.postbanksacco.utils.extensions.formatDigits
import com.android.postbanksacco.utils.extensions.getFirstTwoLetters

class HomeMenuItemsAdapter(
    private val data: ArrayList<HomeModel>,
    private val menuItemClick: OnMenuItemClick
) :
    RecyclerView.Adapter<HomeMenuItemsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = HomeRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(position)
    }
    private fun handleNavigation(position: Int,objc:HomeModel) {
        menuItemClick.navigateTo(position,objc)
    }

    override fun getItemCount() = data.size

    inner class MyViewHolder(private val binding: HomeRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position:Int) {
            val currentItem = data[position]
            binding.tvName.text = currentItem.title
            binding.ivIcon.setImageResource(currentItem.image)

            binding.layout.setOnClickListener { handleNavigation(position,currentItem) }

        }
    }
}
