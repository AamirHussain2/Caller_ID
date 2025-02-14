package com.example.diallerapp.adapter.uicreatecontact

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.R
import android.util.Log
import androidx.core.widget.addTextChangedListener
import com.example.diallerapp.databinding.ActivityCreateContactBinding
import com.example.diallerapp.databinding.CustomAddressUiBinding
import com.example.diallerapp.model.uicreatecontact.AddressModel

class AddressAdapter(val activityAddAddressBinding: ActivityCreateContactBinding) :
    ListAdapter<AddressModel, AddressAdapter.AddressViewHolder>(AddressDiffUtil()) {

    inner class AddressViewHolder(private val binding: CustomAddressUiBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AddressModel) {
            // Set initial values from the AddressModel
            binding.edAddress.editText?.setText(item.address).toString()
            binding.addressAutoCompleteLabel.setText(item.addressLabel, false).toString()


            binding.labelMenu.visibility = if (item.isLabelVisible) View.VISIBLE else View.GONE

            // Set dropdown menu for address labels
            val addressLabels = itemView.resources.getStringArray(R.array.emailAddressTypes)
            val adapter = ArrayAdapter(itemView.context, R.layout.simple_list_item_1, addressLabels)
            binding.addressAutoCompleteLabel.setAdapter(adapter)

            // Update AddressModel when address label is selected
            binding.addressAutoCompleteLabel.setOnItemClickListener { _, _, position, _ ->
                val selectedLabel = adapter.getItem(position)
                item.addressLabel = selectedLabel ?: ""
                binding.addressAutoCompleteLabel.setText(selectedLabel, false)
            }

            // Update Address Model when address is edited
            binding.edAddress.editText?.addTextChangedListener {
                item.address = it.toString()
            }

            // Handle focus changes to toggle label visibility
            binding.edAddress.editText?.setOnFocusChangeListener { _, hasFocus ->
                item.isLabelVisible = hasFocus
                binding.labelMenu.visibility = if (hasFocus) View.VISIBLE else View.GONE
            }

            // Delete button logic
            binding.addressDeleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val newList = currentList.toMutableList()
                    newList.removeAt(position)
                    submitList(newList)

                    if (newList.isEmpty()) {
                        activityAddAddressBinding.addAddressButton.visibility = View.VISIBLE
                        activityAddAddressBinding.addAddressItem.visibility = View.GONE
                    } else {
                        activityAddAddressBinding.addAddressButton.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = CustomAddressUiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AddressDiffUtil : DiffUtil.ItemCallback<AddressModel>() {
        override fun areContentsTheSame(oldItem: AddressModel, newItem: AddressModel): Boolean {
            return oldItem.address == newItem.address &&
                    oldItem.addressLabel == newItem.addressLabel &&
                    oldItem.isLabelVisible == newItem.isLabelVisible
        }

        override fun areItemsTheSame(oldItem: AddressModel, newItem: AddressModel): Boolean {
            return oldItem == newItem
        }
    }
}
