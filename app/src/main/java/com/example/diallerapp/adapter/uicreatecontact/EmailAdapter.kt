package com.example.diallerapp.adapter.uicreatecontact

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.diallerapp.databinding.CustomEmailUiBinding
import com.example.diallerapp.model.uicreatecontact.EmailModel
import com.example.diallerapp.viewholder.uicreatecontact.EmailViewHolder

class EmailAdapter: ListAdapter<EmailModel, EmailViewHolder>(DiffEmailCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
        val binding = CustomEmailUiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EmailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
        Log.d("EmailAdapter", "onBindViewHolder: position: $position")
        Log.d("EmailAdapter", "onBindViewHolder: holder: $holder")
        val item = getItem(position)
        holder.bind(item)

//        holder.binding.emailDeleteButton.setOnClickListener {
//            val newList = currentList.toMutableList()
//            Log.d("EmailAdapter", "emailDeleteButton: $newList")
//            newList.removeAt(position)
//            submitList(newList)
//        }

        holder.binding.addEmail.setOnClickListener {
            val newList = currentList.toMutableList()
            Log.d("EmailAdapter", "newList: $newList")
            newList.add(item)
            Log.d("EmailAdapter", "newList: ${newList.size}")
            submitList(newList)
        }
    }

    class DiffEmailCallback: DiffUtil.ItemCallback<EmailModel>(){
        override fun areContentsTheSame(oldItem: EmailModel, newItem: EmailModel): Boolean {
            return oldItem.number == newItem.number
        }

        override fun areItemsTheSame(oldItem: EmailModel, newItem: EmailModel): Boolean {
            return  oldItem == newItem
        }

    }
}