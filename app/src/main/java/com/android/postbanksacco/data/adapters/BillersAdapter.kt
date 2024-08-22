package com.android.postbanksacco.data.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.postbanksacco.data.model.HomeModel
import com.android.postbanksacco.databinding.BillRowBinding
import com.android.postbanksacco.databinding.TransferRowBinding
import com.android.postbanksacco.utils.interfaces.OnMenuItemClick

class BillersAdapter(val items: ArrayList<HomeModel>, private val callBack: OnMenuItemClick) :
    RecyclerView.Adapter<BillersAdapter.LoanDashboardViewHolder>() {
    inner class LoanDashboardViewHolder(val itemBinding: BillRowBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindViews(position: Int) {
            val currentItems = items[position]
            itemBinding.apply {
                viewLine.setImageResource(currentItems.image)
                tvLoantitle.text = currentItems.title
            }
            itemBinding.ClLoanProduct.setOnClickListener {
                callBack.navigateTo(position, currentItems)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanDashboardViewHolder {
        val binding = BillRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoanDashboardViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: LoanDashboardViewHolder, position: Int) =
        holder.bindViews(position)


}