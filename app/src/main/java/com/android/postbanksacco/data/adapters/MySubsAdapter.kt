package com.android.postbanksacco.data.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.postbanksacco.data.response.SubscriboData
import com.android.postbanksacco.databinding.HomeRowBinding
import com.android.postbanksacco.utils.extensions.formatDigits
import com.android.postbanksacco.utils.extensions.getFirstTwoLetters
import com.android.postbanksacco.utils.interfaces.OnSubsItemClick
import com.android.postbanksacco.utils.makeGone

class MySubsAdapter(
    private val data: ArrayList<SubscriboData>,
    private val menuItemClick: OnSubsItemClick
) :
    RecyclerView.Adapter<MySubsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = HomeRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(position)
    }
    private fun handleNavigation(serviceDescription: SubscriboData) {
        menuItemClick.navigateTo(serviceDescription)
    }

    override fun getItemCount() = data.size

    inner class MyViewHolder(private val binding: HomeRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position:Int) {
            val currentItem = data[position]
            binding.tvName.text = currentItem.serviceName
            val amnt = formatDigits(currentItem.amountPaid)
//            binding.tvNarration.text = "Amount Paid: Ksh $amnt"
//           // binding.tvDate.text = "Discount: ${currentItem.discountPercent}%"
//            binding.radioBtn.makeGone()
            binding.layout.setOnClickListener { handleNavigation(currentItem) }

        }
    }
}
