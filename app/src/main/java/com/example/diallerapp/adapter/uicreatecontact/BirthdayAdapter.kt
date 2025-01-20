package com.example.diallerapp.adapter.uicreatecontact

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.diallerapp.databinding.CustomBirthdayUiBinding
import com.example.diallerapp.model.uicreatecontact.BirthdayModel
import com.example.diallerapp.viewholder.uicreatecontact.BirthdayViewHolder

class BirthdayAdapter: ListAdapter<BirthdayModel, BirthdayViewHolder>(DiffBirthdayCallback()) {

    init {
        Log.d("addBirthButton", "show adapter class")
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

        val item = getItem(position)
        holder.bind(item)

    }


    class DiffBirthdayCallback: DiffUtil.ItemCallback<BirthdayModel>() {
        override fun areContentsTheSame(oldItem: BirthdayModel, newItem: BirthdayModel): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areItemsTheSame(oldItem: BirthdayModel, newItem: BirthdayModel): Boolean {
            return oldItem == newItem
        }

    }
}