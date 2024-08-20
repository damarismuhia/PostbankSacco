package com.android.postbanksacco.ui.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.postbanksacco.databinding.ItemDialogBinding

class TransactionDialogAdapter : ListAdapter<TransactionsModel, TransactionDialogAdapter.ViewHolder>(
     DIFF_UTIL
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDialogBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bindViews(position)

   inner class ViewHolder(val itemBinding: ItemDialogBinding) : RecyclerView.ViewHolder(itemBinding.root){
       fun bindViews(position: Int){
           val dialogDetail = getItem(position)
           itemBinding.textViewlabel.text = dialogDetail.label
           itemBinding.textViewContent.text = dialogDetail.content
       }
    }



}
private val DIFF_UTIL = object : DiffUtil.ItemCallback<TransactionsModel>() {
    override fun areItemsTheSame(oldItem: TransactionsModel, newItem: TransactionsModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: TransactionsModel, newItem: TransactionsModel): Boolean {
        return oldItem == newItem
    }

}