package com.android.postbanksacco.data.adapters
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.postbanksacco.R
import com.android.postbanksacco.data.response.MinistatementModel
import com.android.postbanksacco.databinding.MinistatemetRowBinding
import com.android.postbanksacco.utils.extensions.capitalizeWords

class MinistatementAdapter(val items: ArrayList<MinistatementModel>,val context:Context) :
    RecyclerView.Adapter<MinistatementAdapter.MinistatementViewHolder>() {
    inner class MinistatementViewHolder(val itemBinding: MinistatemetRowBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindViews(position: Int) {
            val currentItems = items[position]
            itemBinding.apply {
                if (currentItems.transactionType=="C"){
                    val img: Drawable = ContextCompat.getDrawable(context, R.drawable.mini_up)!!
                    viewLine.setImageDrawable(img)
                    tvAmount.setTextColor(ContextCompat.getColor(context,R.color.green))
                }else{
                    val img: Drawable = ContextCompat.getDrawable(context, R.drawable.mini_down)!!
                    viewLine.setImageDrawable(img)
                    tvAmount.setTextColor(ContextCompat.getColor(context,R.color.red))
                }
                tvTitle.text = currentItems.narration
                if (currentItems.amount.isNotEmpty()){
                    tvAmount.text= currentItems.amount.trim()
                }
                if (currentItems.date.isNotEmpty()){
                    textDate.text = currentItems.date
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MinistatementViewHolder {
        val binding = MinistatemetRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MinistatementViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MinistatementViewHolder, position: Int) =
        holder.bindViews(position)
}