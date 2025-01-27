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
import com.example.diallerapp.databinding.CustomBirthdayUiBinding
import com.example.diallerapp.model.uicreatecontact.BirthdayModel

class BirthdayAdapter(private val activityBinding: ActivityCreateContactBinding) :
    ListAdapter<BirthdayModel, BirthdayAdapter.BirthdayViewHolder>(BirthdayDiffUtil()) {

//    private var selectedPosition: Int? = null

    inner class BirthdayViewHolder(val binding: CustomBirthdayUiBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BirthdayModel) {
            Log.d("datePicker", "Binding item: $item.birthdayDatePicker")
            binding.birthdayAutoCompleteDatePicker.setText(item.birthdayDatePicker)
            binding.birthdayAutoCompleteLabel.setText(item.birthdayLabel, false)


            val emailLabels = itemView.resources.getStringArray(R.array.emailAddressTypes)
            val adapter = ArrayAdapter(itemView.context, R.layout.simple_list_item_1, emailLabels)
            binding.birthdayAutoCompleteLabel.setAdapter(adapter)

            val defaultLabel = "Home"
            binding.birthdayAutoCompleteLabel.setText(defaultLabel, false)

            binding.birthdayAutoCompleteLabel.setOnClickListener {
                binding.birthdayAutoCompleteLabel.showDropDown()
            }

            binding.birthdayAutoCompleteLabel.setOnItemClickListener { _, _, position, _ ->
                val selectedLabel = adapter.getItem(position)

                binding.birthdayAutoCompleteLabel.setText(selectedLabel, false)

            }




            binding.birthdayAutoCompleteDatePicker.setOnClickListener {
//                updateSelectedPosition(adapterPosition, binding)
            }

            binding.birthdayDeleteButton.setOnClickListener {
                removeItem(adapterPosition)
            }


//            if (adapterPosition == selectedPosition) {
//                binding.labelMenu.visibility = View.VISIBLE
//            } else {
//                binding.labelMenu.visibility = View.GONE
//            }

            activityBinding.addBirthdayButton.setOnClickListener {
                val newList = currentList.map { it.copy() } as ArrayList
                newList.add(item)
                submitList(newList)


                activityBinding.addBirthdayItem.visibility = View.VISIBLE
                activityBinding.addBirthdayButton.visibility = View.GONE
                binding.labelMenu.visibility = View.VISIBLE

                // Set the selected position to the last item
//                selectedPosition = newList.size - 1
//                notifyItemChanged(selectedPosition!!)

            }

            activityBinding.addBirthdayItem.setOnClickListener {
                val newList = currentList.map { it.copy() } as ArrayList
                newList.add(item)
//                selectedPosition = null
                binding.labelMenu.visibility = View.VISIBLE
                submitList(newList)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirthdayViewHolder {
        val binding = CustomBirthdayUiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BirthdayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BirthdayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun removeItem(position: Int) {
        val newList = currentList.map { it.copy() } as ArrayList
        newList.removeAt(position)
        submitList(newList)

        if (newList.isEmpty()) {
            activityBinding.addBirthdayItem.visibility = View.GONE
            activityBinding.addBirthdayButton.visibility = View.VISIBLE
        } else {
            activityBinding.addBirthdayItem.visibility = View.VISIBLE
            activityBinding.addBirthdayButton.visibility = View.GONE
        }
    }

//    private fun updateSelectedPosition(position: Int, binding: CustomBirthdayUiBinding) {
//
//        val previousSelectedPosition = selectedPosition
////        selectedPosition = null
//        selectedPosition = position
//
//        val updatedList = currentList.mapIndexed { index, item ->
//            if (index == position) {
//                binding.labelMenu.visibility = View.VISIBLE
//                item.copy()
//            } else if (index == previousSelectedPosition) {
//                binding.labelMenu.visibility = View.GONE
//                item.copy()
//            } else {
//                item
//            }
//        }
//
//        submitList(updatedList)
//    }


    class BirthdayDiffUtil : DiffUtil.ItemCallback<BirthdayModel>() {
        override fun areContentsTheSame(oldItem: BirthdayModel, newItem: BirthdayModel): Boolean {
            return oldItem.birthdayDatePicker == newItem.birthdayDatePicker && oldItem.birthdayLabel == newItem.birthdayLabel
        }

        override fun areItemsTheSame(oldItem: BirthdayModel, newItem: BirthdayModel): Boolean {
            return oldItem == newItem
        }
    }
}

