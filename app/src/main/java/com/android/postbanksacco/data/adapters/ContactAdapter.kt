package com.android.postbanksacco.data.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.postbanksacco.data.model.ContactModel
import com.android.postbanksacco.databinding.ItemSelectcontactBinding
import com.android.postbanksacco.utils.extensions.getFirstTwoLetters
import com.android.postbanksacco.utils.extensions.scrollAnimation
import com.android.postbanksacco.utils.interfaces.ContactCallBack

class ContactAdapter(val contactList: List<ContactModel>, private val contactCallBack: ContactCallBack): RecyclerView.Adapter<ContactAdapter.ContactAdapterViewHolder>()
{
    var lastPosition = -1
    inner class ContactAdapterViewHolder(private val itemBinding:ItemSelectcontactBinding):RecyclerView.ViewHolder(itemBinding.root){
        fun bindItems(position: Int) {
            val contact=contactList[position]
            itemBinding.textViewName.text = contact.name
            itemBinding.textViewPhone.text = contact.phonenumber
            if(contact.name.isEmpty()) {
                itemBinding.textViewNameInitials.text = "*"
            }else{
                itemBinding.textViewNameInitials.text= (contact.name).getFirstTwoLetters()
            }

            itemBinding.CLConstraint.setOnClickListener {
                contactCallBack.onItemSelected(contact)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactAdapterViewHolder {
        return ContactAdapterViewHolder(ItemSelectcontactBinding.inflate(LayoutInflater.from(parent.context)))
    }
    override fun getItemCount(): Int {
       return contactList.size
    }
    override fun onBindViewHolder(holder: ContactAdapterViewHolder, position: Int) {
        holder.bindItems(position)
        setAnimation(holder.itemView,position)
    }
    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            scrollAnimation(viewToAnimate)
            lastPosition = position
        } else if (position < lastPosition) {
            scrollAnimation(viewToAnimate)
            lastPosition = position
        }
    }

}

