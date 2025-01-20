package com.example.diallerapp.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.example.diallerapp.databinding.CustomContactsUiBinding
import com.example.diallerapp.model.ContactModel

class ContactViewHolder(private val binding: CustomContactsUiBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindContactLog(contact: ContactModel) {
        binding.contactName.text = contact.name
        binding.contactPhone.text = contact.phoneNumber
    }
}