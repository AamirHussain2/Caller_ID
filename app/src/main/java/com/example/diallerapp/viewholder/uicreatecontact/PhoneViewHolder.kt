//package com.example.diallerapp.viewholder.uicreatecontact
//
//import android.util.Log
//import android.view.View
//import androidx.recyclerview.widget.RecyclerView
//import com.example.diallerapp.adapter.uicreatecontact.PhoneAdapter
//import com.example.diallerapp.databinding.ActivityCreateContactBinding
//import com.example.diallerapp.databinding.CustomPhoneUiBinding
//import com.example.diallerapp.model.uicreatecontact.PhoneModel
//
//class PhoneViewHolder(val binding: CustomPhoneUiBinding) :
//    RecyclerView.ViewHolder(binding.root) {
//
//    private var selectedPosition: Int? = null
//
//    fun bind(
//        item: PhoneModel,
//        activityBinding: ActivityCreateContactBinding,
//        currentList: List<PhoneModel>,
//        adapter: PhoneAdapter
//    ) {
//
//        binding.edPhone.editText?.setText(item.phoneNumber)
//        binding.labelMenu.editText?.setText(item.phoneLabel)
//
//        // Set labelMenu visibility based on the current selected position
//        if (adapterPosition == selectedPosition) {
//            binding.labelMenu.visibility = View.VISIBLE
//        } else {
//            binding.labelMenu.visibility = View.GONE
//        }
//
//        binding.phoneDeleteButton.setOnClickListener {
//            val position = adapterPosition
//            if (position != RecyclerView.NO_POSITION && position < currentList.size) {
//
//                val newList = ArrayList(currentList)
//
//                newList.removeAt(position)
//
//                adapter.submitList(newList)
//
//                if (newList.isEmpty()) {
//                    activityBinding.addPhoneButton.visibility = View.VISIBLE
//                } else {
//                    activityBinding.addPhoneButton.visibility = View.GONE
//                }
//            } else {
//                Log.e("PhoneAdapter", "Invalid adapterPosition: $position")
//            }
//        }
//
//        activityBinding.addPhoneButton.setOnClickListener {
//
//            val newList = currentList.map { it.copy() } as ArrayList
//            newList.add(item)
//            binding.addPhone.visibility = View.GONE
//            adapter.submitList(newList)
//            activityBinding.addPhoneButton.visibility = View.GONE
//        }
//
//        activityBinding.addPhoneItem.setOnClickListener {
//            val newList = currentList.map { it.copy() } as ArrayList
//            newList.add(item)
//            Log.d("PhoneAdapter", "newList: ${newList.size}")
//            // Reset selectedPosition when a new item is added
//            selectedPosition = null
//            adapter.submitList(newList)
//            // Notify entire list for UI update
//            adapter.notifyItemRangeChanged(0, newList.size)
//            binding.addPhone.visibility = View.GONE
//
//        }
//
//        binding.edPhone.editText?.setOnClickListener {
//            val previousPosition = selectedPosition
//            selectedPosition = adapterPosition
//
//            // Update previous and current positions
//            if (previousPosition != null && previousPosition != selectedPosition) {
//                adapter.notifyItemChanged(previousPosition)
//            }
//            adapter.notifyItemChanged(selectedPosition!!)
//        }
//    }
//}