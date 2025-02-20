package com.example.diallerapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.diallerapp.databinding.CustomContactsUiBinding
import com.example.diallerapp.model.ContactModel
import com.example.diallerapp.viewholder.ContactViewHolder

class ContactAdapter(private val context: Context) : ListAdapter<ContactModel, ContactViewHolder>(ContactDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(CustomContactsUiBinding.inflate(LayoutInflater.from(parent.context), parent, false), context)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bindContactLog(getItem(position))
    }

//    override fun onBindViewHolder(holder: ContactViewHolder, position: Int, payloads: MutableList<Any>) {
//        if (payloads.isEmpty()) {
//            onBindViewHolder(holder, position)
//        } else {
//            holder.bindContactLog(getItem(position))
//        }
//    }

}

class ContactDiffCallback : DiffUtil.ItemCallback<ContactModel>() {
    override fun areItemsTheSame(oldItem: ContactModel, newItem: ContactModel): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: ContactModel, newItem: ContactModel): Any? {
        return super.getChangePayload(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItem: ContactModel, newItem: ContactModel): Boolean {
        return oldItem.phoneNumber == newItem.phoneNumber &&
                oldItem.name == newItem.name &&
                oldItem.photo.contentEquals(newItem.photo)
    }
}
