package com.android.postbanksacco.data.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.postbanksacco.data.model.HomeModel
import com.android.postbanksacco.databinding.TransferRowBinding
import com.android.postbanksacco.utils.interfaces.OnMenuItemClick

class ListItemsAdapter(val items: ArrayList<HomeModel>, private val callBack: OnMenuItemClick) :
    RecyclerView.Adapter<ListItemsAdapter.LoanDashboardViewHolder>() {
    inner class LoanDashboardViewHolder(val itemBinding: TransferRowBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindViews(position: Int) {
            val currentItems = items[position]
            itemBinding.apply {
                viewLine.setImageResource(currentItems.image)
                tvLoantitle.text = currentItems.title
                textLimit.text = currentItems.code
            }
            itemBinding.ClLoanProduct.setOnClickListener {
                callBack.navigateTo(position, currentItems)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanDashboardViewHolder {
        val binding = TransferRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoanDashboardViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: LoanDashboardViewHolder, position: Int) =
        holder.bindViews(position)


}