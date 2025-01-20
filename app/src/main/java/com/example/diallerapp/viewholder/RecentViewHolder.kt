package com.example.diallerapp.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.example.diallerapp.databinding.CustomRecentUiBinding
import com.example.diallerapp.model.RecentModel

class RecentViewHolder(private val binding: CustomRecentUiBinding):
    RecyclerView.ViewHolder(binding.root) {

    fun bindRecentLog(contact: RecentModel) {
        binding.contactName.text = contact.name
        binding.contactPhone.text = contact.phoneNumber

    }
}