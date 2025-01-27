package com.example.diallerapp.adapter.uicreatecontact

import android.R
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.diallerapp.databinding.ActivityCreateContactBinding
import com.example.diallerapp.databinding.CustomPhoneUiBinding
import com.example.diallerapp.model.uicreatecontact.PhoneModel

class PhoneAdapter(private val activityBinding: ActivityCreateContactBinding) :
    ListAdapter<PhoneModel, PhoneAdapter.PhoneViewHolder>(PhoneDiffUtil()) {

    var selectedPosition: Int? = null

    inner class PhoneViewHolder(val binding: CustomPhoneUiBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PhoneModel) {
            binding.edPhone.editText?.setText(item.phoneNumber)
            binding.phoneAutoComplete.setText(item.phoneLabel, false)


            val phoneLabels = itemView.resources.getStringArray(R.array.phoneTypes)
            val adapter = ArrayAdapter(itemView.context, R.layout.simple_list_item_1, phoneLabels)
            binding.phoneAutoComplete.setAdapter(adapter)

            val defaultLabel = "Mobile"
            binding.phoneAutoComplete.setText(defaultLabel, false)

            binding.phoneAutoComplete.setOnClickListener {
                binding.phoneAutoComplete.showDropDown()
            }

            binding.phoneAutoComplete.setOnItemClickListener { _, _, position, _ ->
                val selectedLabel = adapter.getItem(position)

                binding.phoneAutoComplete.setText(selectedLabel, false)

            }



            binding.edPhone.editText?.setOnFocusChangeListener { v, hasFocus ->
                Log.d("hasfocus", "bind: $hasFocus")
                if (hasFocus) {
                    Log.d("hasfocus", "bind: true -> $adapterPosition")
                    binding.labelMenu.visibility = View.VISIBLE
                } else {
                    Log.d("hasfocus", "bind: false -> $adapterPosition")
                    binding.labelMenu.visibility = View.GONE
                }
            }
//            binding.edPhone.editText?.setOnClickListener {
//                updateSelectedPosition(adapterPosition, binding)
//            }

            binding.phoneDeleteButton.setOnClickListener {
                removeItem(adapterPosition)
            }


            binding.labelMenu.visibility = if (item.isSelected) View.VISIBLE else View.GONE


//            activityBinding.addPhoneButton.setOnClickListener {
//                val newList = currentList.map { it.copy() } as ArrayList
//                newList.add(PhoneModel(binding.edPhone.editText?.text.toString(), binding.phoneAutoComplete.text.toString()))
//                submitList(newList)
//
//                activityBinding.addPhoneItem.visibility = View.VISIBLE
//                activityBinding.addPhoneButton.visibility = View.GONE
//                binding.labelMenu.visibility = View.VISIBLE
//
//                selectedPosition = newList.size - 1
////                notifyItemChanged(selectedPosition!!)
//            }

//            activityBinding.addPhoneItem.setOnClickListener {
//                val newList = currentList.map { it.copy() } as ArrayList
//                newList.add(item)
//                selectedPosition = null
//                binding.labelMenu.visibility = View.VISIBLE
//                submitList(newList)
//            }

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
        holder.bind(getItem(position))
    }


    private fun updateItem(position: Int, updatedItem: PhoneModel) {
        val newList = currentList.map { it.copy() } as ArrayList
        newList[position] = updatedItem
        submitList(newList)
    }

    private fun removeItem(position: Int) {
        val newList = currentList.map { it.copy() } as ArrayList
        newList.removeAt(position)
        submitList(newList)

        if (newList.isEmpty()) {
            activityBinding.addPhoneItem.visibility = View.GONE
            activityBinding.addPhoneButton.visibility = View.VISIBLE
        } else {
            activityBinding.addPhoneItem.visibility = View.VISIBLE
            activityBinding.addPhoneButton.visibility = View.GONE
        }
    }

    private fun updateSelectedPosition(position: Int, binding: CustomPhoneUiBinding) {
        val updatedList = currentList.mapIndexed { index, item ->
            Log.d("updateSelectedPosition", "position: $position")
            if (index == position) {
                Log.d("updateSelectedPosition", "if: $position")
                binding.labelMenu.visibility = View.VISIBLE
                item.copy(isSelected = true)
            } else {
                Log.d("updateSelectedPosition", "else: $position")
                binding.labelMenu.visibility = View.GONE
                item.copy(isSelected = false)
            }
        }

        submitList(updatedList)
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

