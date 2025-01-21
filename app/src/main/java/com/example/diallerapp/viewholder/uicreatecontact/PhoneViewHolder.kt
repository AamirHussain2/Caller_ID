//package com.example.diallerapp.viewholder.uicreatecontact
//
//import android.util.Log
//import androidx.recyclerview.widget.RecyclerView
//import com.example.diallerapp.databinding.CustomPhoneUiBinding
//import com.example.diallerapp.model.uicreatecontact.PhoneModel
//
//class PhoneViewHolder(val binding: CustomPhoneUiBinding):
//    RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(item: PhoneModel){
//            binding.edPhone.editText?.setText(item.phoneNumber)
//            binding.labelMenu.editText?.setText(item.phoneLabel)
//
//
//            binding.phoneDeleteButton.setOnClickListener {
//                val newList = currentList.toMutableList()
//                Log.d("PhoneAdapter", "phoneDeleteButton: $newList")
//                newList.removeAt(position)
//                submitList(newList)
//            }
//
//            binding.addPhone.setOnClickListener {
//                val newList = currentList.toMutableList()
//                Log.d("PhoneAdapter", "newList: ${newList[0].phoneNumber}")
////            Log.d("PhoneAdapter", "newList: ${newList[1].phoneNumber}")
//                newList.add(item)
//                Log.d("PhoneAdapter", "newList: ${newList.size}")
//                submitList(newList)
//            }
//        }
//
//}