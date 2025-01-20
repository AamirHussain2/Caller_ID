package com.example.diallerapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.diallerapp.databinding.CustomRecentUiBinding
import com.example.diallerapp.model.RecentModel
import com.example.diallerapp.viewholder.RecentViewHolder

class RecentAdapter : ListAdapter<RecentModel, RecentViewHolder>(RecentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        val binding = CustomRecentUiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        val contact = getItem(position)
        holder.bindRecentLog(contact)
    }
}

class RecentDiffCallback : DiffUtil.ItemCallback<RecentModel>() {
    override fun areItemsTheSame(oldItem: RecentModel, newItem: RecentModel): Boolean {
        Log.d("DiffUtil", "oldItem areItemsTheSame: ${oldItem.phoneNumber} : newItem: ${newItem.phoneNumber}")
        return oldItem.phoneNumber == newItem.phoneNumber
    }

    override fun areContentsTheSame(oldItem: RecentModel, newItem: RecentModel): Boolean {
        // Replace with actual content comparison logic
        Log.d("DiffUtil", "oldItem areContentsTheSame: $oldItem : newItem: $newItem")
        return oldItem == newItem
    }
}
