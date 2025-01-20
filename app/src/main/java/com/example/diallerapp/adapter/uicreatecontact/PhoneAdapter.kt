package com.example.diallerapp.adapter.uicreatecontact

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.diallerapp.databinding.CustomPhoneUiBinding
import com.example.diallerapp.model.uicreatecontact.PhoneModel
import com.example.diallerapp.viewholder.uicreatecontact.PhoneViewHolder

class PhoneAdapter:
    ListAdapter<PhoneModel, PhoneViewHolder>(PhoneDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneViewHolder {
        val binding = CustomPhoneUiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhoneViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhoneViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)

        holder.binding.phoneDeleteButton.setOnClickListener {
            val newList = currentList.toMutableList()
            Log.d("PhoneAdapter", "phoneDeleteButton: $newList")
            newList.removeAt(position)
            submitList(newList)
        }

        holder.binding.addPhone.setOnClickListener {
            val newList = currentList.toMutableList()
            Log.d("PhoneAdapter", "newList: ${newList[0].phoneNumber}")
//            Log.d("PhoneAdapter", "newList: ${newList[1].phoneNumber}")
            newList.add(item)
            Log.d("PhoneAdapter", "newList: ${newList.size}")
            submitList(newList)
        }
    }

    class PhoneDiffUtil: DiffUtil.ItemCallback<PhoneModel>(){
        override fun areContentsTheSame(oldItem: PhoneModel, newItem: PhoneModel): Boolean {
            return oldItem.phoneNumber == newItem.phoneNumber
        }

        override fun areItemsTheSame(oldItem: PhoneModel, newItem: PhoneModel): Boolean {
            return oldItem == newItem
        }

    }
}


//        holder.binding.phoneAutoComplete.setAdapter(adapter)

//        // Handling default selection
//        val defaultPosition = adapter.getPosition("Mobile")
//        Log.d("PhoneAdapter", "defaultPosition: $defaultPosition")
//        if (defaultPosition >= 0) {
//            val defaultValue = adapter.getItem(defaultPosition) ?: "Unknown"
//            holder.binding.phoneAutoComplete.setText(defaultValue, false)
//            holder.binding.phoneAutoComplete.setSelection(defaultPosition)
//        } else {
//            // Handle invalid position
//            holder.binding.phoneAutoComplete.setText("Unknown", false)
//        }