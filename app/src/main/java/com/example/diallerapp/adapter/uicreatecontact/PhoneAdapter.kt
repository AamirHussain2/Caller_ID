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
import com.example.diallerapp.databinding.CustomPhoneUiBinding
import com.example.diallerapp.databinding.ActivityCreateContactBinding
import com.example.diallerapp.model.uicreatecontact.PhoneModel

class PhoneAdapter(val activityAddPhoneBinding: ActivityCreateContactBinding) :
    ListAdapter<PhoneModel, PhoneAdapter.PhoneViewHolder>(PhoneDiffUtil()) {

    inner class PhoneViewHolder(private val binding: CustomPhoneUiBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PhoneModel) {
            // Set initial values from the PhoneModel
            binding.edPhone.editText?.setText(item.phoneNumber).toString()
            binding.phoneAutoComplete.setText(item.phoneLabel, false).toString()

            Log.d("com.example.diallerapp.adapter.uicreatecontact.PhoneAdapter", "phoneNumber: ${item.phoneNumber}, phoneLabel: ${item.phoneLabel}")

            binding.labelMenu.visibility = if (item.isLabelVisible) View.VISIBLE else View.GONE

            // Set dropdown menu for phone labels
            val phoneLabels = itemView.resources.getStringArray(R.array.phoneTypes)
            val adapter = ArrayAdapter(itemView.context, R.layout.simple_list_item_1, phoneLabels)
            binding.phoneAutoComplete.setAdapter(adapter)

            // Update PhoneModel when phone label is selected
            binding.phoneAutoComplete.setOnItemClickListener { _, _, position, _ ->
                val selectedLabel = adapter.getItem(position)
                item.phoneLabel = selectedLabel ?: ""
                binding.phoneAutoComplete.setText(selectedLabel, false)
            }

            // Update PhoneModel when phone number is edited
            binding.edPhone.editText?.addTextChangedListener {
                item.phoneNumber = it.toString()
            }

            // Handle focus changes to toggle label visibility
            binding.edPhone.editText?.setOnFocusChangeListener { _, hasFocus ->
                item.isLabelVisible = hasFocus
                binding.labelMenu.visibility = if (hasFocus) View.VISIBLE else View.GONE
            }

            // Delete button logic
            binding.phoneDeleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val newList = currentList.toMutableList()
                    newList.removeAt(position)
                    submitList(newList)

                    if (newList.isEmpty()) {
                        activityAddPhoneBinding.addPhoneButton.visibility = View.VISIBLE
                        activityAddPhoneBinding.addPhoneItem.visibility = View.GONE
                    } else {
                        activityAddPhoneBinding.addPhoneButton.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneViewHolder {
        val binding = CustomPhoneUiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhoneViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhoneViewHolder, position: Int) {

        holder.bind(getItem(position))
    }

    class PhoneDiffUtil : DiffUtil.ItemCallback<PhoneModel>() {
        override fun areContentsTheSame(oldItem: PhoneModel, newItem: PhoneModel): Boolean {
            return oldItem.phoneNumber == newItem.phoneNumber &&
                    oldItem.phoneLabel == newItem.phoneLabel &&
                    oldItem.isLabelVisible == newItem.isLabelVisible
        }

        override fun areItemsTheSame(oldItem: PhoneModel, newItem: PhoneModel): Boolean {
            return oldItem == newItem
        }
    }
}
