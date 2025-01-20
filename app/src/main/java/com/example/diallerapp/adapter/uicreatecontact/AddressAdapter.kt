package com.example.diallerapp.adapter.uicreatecontact

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.diallerapp.databinding.CustomAddressUiBinding
import com.example.diallerapp.model.uicreatecontact.AddressModel
import com.example.diallerapp.viewholder.uicreatecontact.AddressViewHolder

class AddressAdapter: ListAdapter<AddressModel, AddressViewHolder>(DiffAddressCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = CustomAddressUiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    class DiffAddressCallback: DiffUtil.ItemCallback<AddressModel>(){
        override fun areContentsTheSame(oldItem: AddressModel, newItem: AddressModel): Boolean {
            return oldItem.address == newItem.address
        }

        override fun areItemsTheSame(oldItem: AddressModel, newItem: AddressModel): Boolean {
            return oldItem == newItem
        }

    }
}