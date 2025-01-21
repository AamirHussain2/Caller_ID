package com.example.diallerapp.adapter.uicreatecontact

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.diallerapp.databinding.ActivityCreateContactBinding
import com.example.diallerapp.databinding.CustomPhoneUiBinding
import com.example.diallerapp.model.uicreatecontact.PhoneModel

class PhoneAdapter(private val activityBinding: ActivityCreateContactBinding) :
    ListAdapter<PhoneModel, PhoneAdapter.PhoneViewHolder>(PhoneDiffUtil()) {

    inner class PhoneViewHolder(val binding: CustomPhoneUiBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PhoneModel) {


            binding.edPhone.editText?.setText(item.phoneNumber)
            binding.labelMenu.editText?.setText(item.phoneLabel)
            val currentList = currentList.toMutableList()
//            currentList.add(PhoneModel("1", "work"))
            Log.d("CurrentList", "newList: $currentList \n size: ${currentList.size}")

            binding.phoneDeleteButton.setOnClickListener {
//                if(currentList.isNotEmpty()){
                if(adapterPosition != -1 && currentList.isNotEmpty()){
                    val newList = currentList.map { it.copy() } as ArrayList
                    Log.d("PhoneAdapter", "phoneDeleteButton: $newList")


                    Log.d("PhoneAdapter", "bind: if")
                    newList.removeAt(adapterPosition)
                    if (newList.isEmpty()) {
                        Log.d("PhoneAdapter", "bind: currentList.isEmpty()")
                        activityBinding.addPhoneButton.visibility = View.VISIBLE
                    }
                    Log.d("PhoneAdapter", "phoneDeleteButton: $newList")
                    submitList(newList)
                }else{
                    Log.d("PhoneAdapter", "bind: else")
                    activityBinding.addPhoneButton.visibility = View.VISIBLE
//                    binding.root.visibility = View.GONE
                }

            }

            binding.addPhone.setOnClickListener {
                val newList = currentList.map { it.copy() } as ArrayList
                Log.d("addPhone", "before newList: ${newList.size}")
//            Log.d("PhoneAdapter", "newList: ${newList[1].phoneNumber}")

                newList.add(item)
                Log.d("addPhone", "after newList: ${newList.size}")
                submitList(newList)
            }

        }

    }

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

    }

    class PhoneDiffUtil : DiffUtil.ItemCallback<PhoneModel>() {
        override fun areContentsTheSame(oldItem: PhoneModel, newItem: PhoneModel): Boolean {
            return oldItem.phoneNumber == newItem.phoneNumber && oldItem.phoneLabel == newItem.phoneLabel
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