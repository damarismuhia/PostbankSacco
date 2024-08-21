package com.android.postbanksacco.data.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.android.postbanksacco.data.model.ServiceProviderItems
import com.android.postbanksacco.databinding.ImageTextSpinnerLayoutBinding

class ServiceProviderAdapter(context: Context, list: List<ServiceProviderItems>)  :
    ArrayAdapter<ServiceProviderItems>(context,0,list){

    override fun getView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }
    override fun getDropDownView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }
    private fun createView(position: Int, recycledView: View?, parent: ViewGroup): View {
        val name = getItem(position)
        val binding = ImageTextSpinnerLayoutBinding.inflate(LayoutInflater.from(context)
        , parent, false)
        binding.ivLogo.setImageResource(name!!.image)
        binding.tvLabel.text= name.title
        return binding.root
    }

}