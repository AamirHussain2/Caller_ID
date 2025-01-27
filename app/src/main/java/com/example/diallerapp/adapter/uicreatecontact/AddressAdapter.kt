package com.example.diallerapp.adapter.uicreatecontact

import android.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.diallerapp.databinding.ActivityCreateContactBinding
import com.example.diallerapp.databinding.CustomAddressUiBinding
import com.example.diallerapp.model.uicreatecontact.AddressModel

class AddressAdapter(private val activityBinding: ActivityCreateContactBinding) :
    ListAdapter<AddressModel, AddressAdapter.AddressViewHolder>(AddressDiffUtil()) {

    private var selectedPosition: Int? = null

    inner class AddressViewHolder(val binding: CustomAddressUiBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AddressModel) {
            binding.edAddress.editText?.setText(item.address)
            binding.addressAutoCompleteLabel.setText(item.addressLabel, false)


            val addressLabels = itemView.resources.getStringArray(R.array.emailAddressTypes)
            val adapter = ArrayAdapter(itemView.context, R.layout.simple_list_item_1, addressLabels)
            binding.addressAutoCompleteLabel.setAdapter(adapter)

            val defaultLabel = "Home"
            binding.addressAutoCompleteLabel.setText(defaultLabel, false)

            binding.addressAutoCompleteLabel.setOnClickListener {
                binding.addressAutoCompleteLabel.showDropDown()
            }

            binding.addressAutoCompleteLabel.setOnItemClickListener { _, _, position, _ ->
                val selectedLabel = adapter.getItem(position)

                binding.addressAutoCompleteLabel.setText(selectedLabel, false)

            }


            binding.edAddress.editText?.setOnClickListener {
                updateSelectedPosition(adapterPosition, binding)
            }

            binding.addressDeleteButton.setOnClickListener {
                removeItem(adapterPosition)
            }


            if (adapterPosition == selectedPosition) {
                binding.labelMenu.visibility = View.VISIBLE
            } else {
                binding.labelMenu.visibility = View.GONE
            }

//            activityBinding.addAddressButton.setOnClickListener {
//                val newList = currentList.map { it.copy() } as ArrayList
//                newList.add(item)
//                submitList(newList)
//
//
//                activityBinding.addAddressItem.visibility = View.VISIBLE
//                activityBinding.addAddressButton.visibility = View.GONE
//                binding.labelMenu.visibility = View.VISIBLE
//
//                // Set the selected position to the last item
//                selectedPosition = newList.size - 1
////                notifyItemChanged(selectedPosition!!)
//
//            }

//            activityBinding.addAddressItem.setOnClickListener {
//                val newList = currentList.map { it.copy() } as ArrayList
//                newList.add(item)
////                selectedPosition = null
//                binding.labelMenu.visibility = View.VISIBLE
//                submitList(newList)
//
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = CustomAddressUiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    private fun removeItem(position: Int) {
        val newList = currentList.map { it.copy() } as ArrayList
        newList.removeAt(position)
        submitList(newList)

        if (newList.isEmpty()) {
            activityBinding.addAddressItem.visibility = View.GONE
            activityBinding.addAddressButton.visibility = View.VISIBLE
        } else {
            activityBinding.addAddressItem.visibility = View.VISIBLE
            activityBinding.addAddressButton.visibility = View.GONE
        }
    }

    private fun updateSelectedPosition(position: Int, binding: CustomAddressUiBinding) {

        val previousSelectedPosition = selectedPosition
        selectedPosition = position

        val updatedList = currentList.mapIndexed { index, item ->
            if (index == position) {
                binding.labelMenu.visibility = View.VISIBLE
                item.copy()
            } else if (index == previousSelectedPosition) {
                binding.labelMenu.visibility = View.GONE
                item.copy()
            } else {
                item
            }
        }

        submitList(updatedList)
    }


    class AddressDiffUtil : DiffUtil.ItemCallback<AddressModel>() {
        override fun areContentsTheSame(oldItem: AddressModel, newItem: AddressModel): Boolean {
            return oldItem.address == newItem.address && oldItem.addressLabel == newItem.addressLabel
        }

        override fun areItemsTheSame(oldItem: AddressModel, newItem: AddressModel): Boolean {
            return oldItem == newItem
        }
    }
}

