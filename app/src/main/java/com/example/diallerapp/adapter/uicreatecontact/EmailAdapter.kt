package com.example.diallerapp.adapter.uicreatecontact

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.R
import android.util.Log
import androidx.core.widget.addTextChangedListener
import com.example.diallerapp.databinding.ActivityCreateContactBinding
import com.example.diallerapp.databinding.CustomEmailUiBinding
import com.example.diallerapp.model.uicreatecontact.EmailModel

class EmailAdapter(val activityAddEmailBinding: ActivityCreateContactBinding) :
    ListAdapter<EmailModel, EmailAdapter.EmailViewHolder>(EmailDiffUtil()) {

    inner class EmailViewHolder(private val binding: CustomEmailUiBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EmailModel) {
            // Set initial values from the EmailModel
            binding.edEmail.editText?.setText(item.email).toString()
            binding.emailAutoCompleteLabel.setText(item.emailLabel, false).toString()

            Log.d("com.example.diallerapp.adapter.uicreatecontact.EmailAdapter", "email: ${item.email}, emailLabel: ${item.emailLabel}")

            binding.labelMenu.visibility = if (item.isLabelVisible) View.VISIBLE else View.GONE

            // Set dropdown menu for email labels
            val emailLabels = itemView.resources.getStringArray(R.array.emailAddressTypes)
            val adapter = ArrayAdapter(itemView.context, R.layout.simple_list_item_1, emailLabels)
            binding.emailAutoCompleteLabel.setAdapter(adapter)

            // Update EmailModel when email label is selected
            binding.emailAutoCompleteLabel.setOnItemClickListener { _, _, position, _ ->
                val selectedLabel = adapter.getItem(position)
                item.emailLabel = selectedLabel ?: ""
                binding.emailAutoCompleteLabel.setText(selectedLabel, false)
            }

            // Update Email Model when email is edited
            binding.edEmail.editText?.addTextChangedListener {
                item.email = it.toString()
            }

            // Handle focus changes to toggle label visibility
            binding.edEmail.editText?.setOnFocusChangeListener { _, hasFocus ->
                item.isLabelVisible = hasFocus
                binding.labelMenu.visibility = if (hasFocus) View.VISIBLE else View.GONE
            }

            // Delete button logic
            binding.emailDeleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val newList = currentList.toMutableList()
                    newList.removeAt(position)
                    submitList(newList.toList())

                    if (newList.isEmpty()) {
                        activityAddEmailBinding.addEmailButton.visibility = View.VISIBLE
                        activityAddEmailBinding.addEmailItem.visibility = View.GONE
                    } else {
                        activityAddEmailBinding.addEmailButton.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
        val binding = CustomEmailUiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EmailDiffUtil : DiffUtil.ItemCallback<EmailModel>() {
        override fun areContentsTheSame(oldItem: EmailModel, newItem: EmailModel): Boolean {
            return oldItem.email == newItem.email &&
                    oldItem.emailLabel == newItem.emailLabel &&
                    oldItem.isLabelVisible == newItem.isLabelVisible
        }

        override fun areItemsTheSame(oldItem: EmailModel, newItem: EmailModel): Boolean {
            return oldItem == newItem
        }
    }
}
