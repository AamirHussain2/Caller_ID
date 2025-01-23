package com.example.diallerapp.adapter.uicreatecontact

import android.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.diallerapp.databinding.ActivityCreateContactBinding
import com.example.diallerapp.databinding.CustomEmailUiBinding
import com.example.diallerapp.model.uicreatecontact.EmailModel

class EmailAdapter(private val activityBinding: ActivityCreateContactBinding) :
    ListAdapter<EmailModel, EmailAdapter.EmailViewHolder>(EmailDiffUtil()) {

    private var selectedPosition: Int? = null

    inner class EmailViewHolder(val binding: CustomEmailUiBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EmailModel) {
            binding.edEmail.editText?.setText(item.email)
            binding.emailAutoCompleteLabel.setText(item.emailLabel, false)


            val emailLabels = itemView.resources.getStringArray(R.array.emailAddressTypes)
            val adapter = ArrayAdapter(itemView.context, R.layout.simple_list_item_1, emailLabels)
            binding.emailAutoCompleteLabel.setAdapter(adapter)

            val defaultLabel = "Home"
            binding.emailAutoCompleteLabel.setText(defaultLabel, false)

            binding.emailAutoCompleteLabel.setOnClickListener {
                binding.emailAutoCompleteLabel.showDropDown()
            }

            binding.emailAutoCompleteLabel.setOnItemClickListener { _, _, position, _ ->
                val selectedLabel = adapter.getItem(position)

                binding.emailAutoCompleteLabel.setText(selectedLabel, false)

            }




            binding.edEmail.editText?.setOnClickListener {
                updateSelectedPosition(adapterPosition, binding)
            }

            binding.emailDeleteButton.setOnClickListener {
                removeItem(adapterPosition)
            }


            if (adapterPosition == selectedPosition) {
                binding.labelMenu.visibility = View.VISIBLE
            } else {
                binding.labelMenu.visibility = View.GONE
            }

            activityBinding.addEmailButton.setOnClickListener {
                val newList = currentList.map { it.copy() } as ArrayList
                newList.add(item)
                submitList(newList)


                activityBinding.addEmailItem.visibility = View.VISIBLE
                activityBinding.addEmailButton.visibility = View.GONE
                binding.labelMenu.visibility = View.VISIBLE

                // Set the selected position to the last item
                selectedPosition = newList.size - 1
//                notifyItemChanged(selectedPosition!!)

            }

            activityBinding.addEmailItem.setOnClickListener {
                val newList = currentList.map { it.copy() } as ArrayList
                newList.add(item)
//                selectedPosition = null
                binding.labelMenu.visibility = View.VISIBLE
                submitList(newList)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
        val binding = CustomEmailUiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EmailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    private fun updateItem(position: Int, updatedItem: EmailModel) {
        val newList = currentList.map { it.copy() } as ArrayList
        newList[position] = updatedItem
        submitList(newList)
    }

    private fun removeItem(position: Int) {
        val newList = currentList.map { it.copy() } as ArrayList
        newList.removeAt(position)
        submitList(newList)

        if (newList.isEmpty()) {
            activityBinding.addEmailItem.visibility = View.GONE
            activityBinding.addEmailButton.visibility = View.VISIBLE
        } else {
            activityBinding.addEmailItem.visibility = View.VISIBLE
            activityBinding.addEmailButton.visibility = View.GONE
        }
    }

    private fun updateSelectedPosition(position: Int, binding: CustomEmailUiBinding) {

        val previousSelectedPosition = selectedPosition
//        selectedPosition = null
        selectedPosition = position

        val updatedList = currentList.mapIndexed { index, item ->
            if (index == position) {
                binding.labelMenu.visibility = View.VISIBLE
                item.copy()
            } else if (index == previousSelectedPosition) {
                binding.labelMenu.visibility = View.GONE
                item.copy()
            } else {
                item
            }
        }

        submitList(updatedList)
    }


    class EmailDiffUtil : DiffUtil.ItemCallback<EmailModel>() {
        override fun areContentsTheSame(oldItem: EmailModel, newItem: EmailModel): Boolean {
            return oldItem.email == newItem.email && oldItem.emailLabel == newItem.emailLabel
        }

        override fun areItemsTheSame(oldItem: EmailModel, newItem: EmailModel): Boolean {
            return oldItem == newItem
        }
    }
}

