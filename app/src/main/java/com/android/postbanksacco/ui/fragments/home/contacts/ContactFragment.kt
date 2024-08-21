package com.android.postbanksacco.ui.fragments.home.contacts

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.android.postbanksacco.R
import com.android.postbanksacco.data.adapters.ContactAdapter
import com.android.postbanksacco.data.model.ContactModel
import com.android.postbanksacco.databinding.FragmentContactBinding
import com.android.postbanksacco.ui.fragments.TransactionBaseFragment
import com.android.postbanksacco.utils.CacheKeys
import com.android.postbanksacco.utils.extensions.formatContact
import com.android.postbanksacco.utils.extensions.readItemFromPref
import com.android.postbanksacco.utils.extensions.showToast
import com.android.postbanksacco.utils.interfaces.ContactCallBack
import com.android.postbanksacco.utils.makeVisible
import com.android.postbanksacco.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class ContactFragment : TransactionBaseFragment<FragmentContactBinding,MainViewModel>(FragmentContactBinding::inflate),
    ContactCallBack {
    private lateinit var contactAdapter: ContactAdapter
    private val displayList = ArrayList<ContactModel>()
    private val filteredList = ArrayList<ContactModel>()
    override val viewModel: MainViewModel by activityViewModels ()
    // Single Permission Contract
    private val askContactPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                getContactList()
            } else {
                binding.tvNoContacts.makeVisible()
                binding.tvNoContacts.text = "You didn't grant the permission to display your contacts."
                showToast("You have disabled a contacts permission, We wont be able to display your contacts.")
            }
        }

    override fun setupUI() {
        super.setupUI()
        requestContactPermission()
        configureViews()
        searchContact()
        setUpFragmentTit()
    }

    private fun searchContact() {
        binding.searchEt.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) {
                    displayList.clear()
                    binding.tvNoContacts.visibility = View.GONE
                    val search = newText.lowercase(Locale.US)
                    filteredList.forEach {
                        if (it.name.lowercase(Locale.US).contains(search)) {
                            displayList.add(it)
                        }
                    }
                    if (displayList.isEmpty()) {
                        binding.tvNoContacts.visibility = View.VISIBLE
                        binding.tvNoContacts.text ="$newText Not Found"
                    } else {
                        binding.tvNoContacts.visibility = View.GONE
                    }
                    binding.rvContact.adapter?.notifyDataSetChanged()
                } else {
                    binding.tvNoContacts.visibility = View.GONE
                    displayList.clear()
                    displayList.addAll(filteredList)
                    binding.rvContact.adapter?.notifyDataSetChanged()
                }
                return true
            }
        })
    }
    private fun configureViews(){
        binding.apply {
            btnAdd.setOnClickListener {
                findNavController().navigateUp()
            }
        }

    }
    private fun requestContactPermission() {
        // Check if the READ_CONTACTS permission has been granted
        if ((ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_CONTACTS
            )) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is already available, show contact List
            getContactList()
        } else {
            // Permission is missing and must be requested.
            askContactPermission.launch(Manifest.permission.READ_CONTACTS)
        }
    }
    private val PROJECTION = arrayOf(
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )
    private fun getContactList() {
        //  binding.loadingContacts.visibility=View.VISIBLE
        val cr: ContentResolver = requireActivity().contentResolver
        val cursor: Cursor? = cr.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            PROJECTION,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        if (cursor != null) {
            val mobileNoSet = HashSet<String>()
            try {
                val nameIndex: Int = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val numberIndex: Int =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                while (cursor.moveToNext()) {
                    val  name = cursor.getString(nameIndex)
                    val number = cursor.getString(numberIndex).replace(" ", "")
                    if (!mobileNoSet.contains(number)) {
                        val contact = formatContact(number)
                        displayList.add(ContactModel(name, contact))
                        filteredList.add(ContactModel(name, contact))
                        mobileNoSet.add(number)
                    }
                }
            } finally {
                cursor.close()
                contactAdapter = ContactAdapter(displayList, this)
                binding.rvContact.adapter = contactAdapter
                binding.rvContact.layoutManager = GridLayoutManager(requireActivity(), 1)
                val phone = readItemFromPref(CacheKeys.PHONE_NUMBER)
                displayList.add(0, ContactModel("Myself", phone))
                contactAdapter.notifyItemInserted(0)
            }
        }
    }
    private fun setUpFragmentTit(){
        binding.fragmentTitle.apply {
            fragmentTitle.text = getString(R.string.buy_airtime)
            fragmentTitleDesc.text = getString(R.string.who_would_you_like_airtime)
        }
    }
    private  fun navigateWithOptions(cont: ContactModel){
        viewModel.contactModel.postValue(cont)
        this.findNavController().popBackStack()
    }
    override fun onItemSelected(cont: ContactModel) {
        navigateWithOptions(cont)
    }
}