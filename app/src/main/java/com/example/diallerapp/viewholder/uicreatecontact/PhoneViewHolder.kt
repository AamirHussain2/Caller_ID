package com.example.diallerapp.viewholder.uicreatecontact

import androidx.recyclerview.widget.RecyclerView
import com.example.diallerapp.databinding.CustomPhoneUiBinding
import com.example.diallerapp.model.uicreatecontact.PhoneModel

class PhoneViewHolder(val binding: CustomPhoneUiBinding):
    RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PhoneModel){
            binding.edPhone.editText?.setText(item.phoneNumber)
            binding.labelMenu.editText?.setText(item.phoneLabel)

        }

}