package com.example.diallerapp.viewholder.uicreatecontact

import androidx.recyclerview.widget.RecyclerView
import com.example.diallerapp.databinding.CustomEmailUiBinding
import com.example.diallerapp.model.uicreatecontact.EmailModel

class EmailViewHolder(val binding: CustomEmailUiBinding):
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: EmailModel) {

        binding.edEmail.editText?.setText(item.number)

        binding.labelMenu.editText?.setText(item.phoneLabel)


    }

}