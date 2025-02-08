package com.example.diallerapp.adapter.uicreatecontact

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import androidx.fragment.app.FragmentManager
import com.example.diallerapp.R
import com.example.diallerapp.activities.CreateContactActivity
import com.example.diallerapp.databinding.ActivityCreateContactBinding
import com.example.diallerapp.databinding.CustomBirthdayUiBinding
import com.example.diallerapp.model.uicreatecontact.BirthdayModel
import com.example.diallerapp.utils.DialogUtils

class BirthdayAdapter(
    val activityAddBirthdayBinding: ActivityCreateContactBinding,
    val supportFragmentManager: FragmentManager,
    val context: CreateContactActivity
) :
    ListAdapter<BirthdayModel, BirthdayAdapter.BirthdayViewHolder>(BirthdayDiffUtil()) {

    inner class BirthdayViewHolder(private val binding: CustomBirthdayUiBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BirthdayModel) {

            binding.birthdayAutoCompleteDatePicker.setText(item.birthdayDatePicker).toString()
            binding.birthdayAutoCompleteLabel.setText(item.birthdayLabel, false).toString()

            Log.d(
                "BirthdayAdapter",
                "birthday: ${item.birthdayDatePicker}, birthdayLabel: ${item.birthdayLabel}"
            )

            binding.labelMenu.visibility = if (item.isLabelVisible) View.VISIBLE else View.GONE


            val birthdayLabels = itemView.resources.getStringArray(R.array.birthdayLabel)
            val adapter =
                ArrayAdapter(itemView.context, android.R.layout.simple_list_item_1, birthdayLabels)
            binding.birthdayAutoCompleteLabel.setAdapter(adapter)


            binding.birthdayAutoCompleteLabel.setOnItemClickListener { _, _, position, _ ->
                val selectedLabel = adapter.getItem(position)
                item.birthdayLabel = selectedLabel ?: ""
                binding.birthdayAutoCompleteLabel.setText(selectedLabel, false)
            }

            if (adapterPosition == currentList.size - 1) {
                DialogUtils.selectedDate.observe(context) { selectedDate ->
                    item.birthdayDatePicker = selectedDate

                    Log.d(
                        "BirthdayAdapter",
                        "Selected date: $selectedDate \n item: ${item.birthdayDatePicker}"
                    )
                    binding.birthdayAutoCompleteDatePicker.setText(selectedDate)
                }
            }

            binding.birthdayAutoCompleteDatePicker.setOnClickListener {
                if (adapterPosition == currentList.indexOf(currentList[adapterPosition])){
                    item.isLabelVisible = true
                }
                currentList[adapterPosition].isLabelVisible = item.isLabelVisible
                binding.labelMenu.visibility = if (item.isLabelVisible) View.VISIBLE else View.GONE

                DialogUtils.showDialogDatePicker(supportFragmentManager)

                DialogUtils.selectedDate.removeObservers(context)

                Log.d("TAG", "adapterPosition: $adapterPosition \n current size: ${currentList.size}")
                if (adapterPosition == currentList.indexOf(item)) {
                    DialogUtils.selectedDate.observe(context) { selectedDate ->
                        item.birthdayDatePicker = selectedDate
                        Log.d(
                            "BirthdayAdapter",
                            "Selected date...: $selectedDate \n item: ${item.birthdayDatePicker}"
                        )
                        binding.birthdayAutoCompleteDatePicker.setText(selectedDate)

                    }
                }
            }

            binding.birthdayDeleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val newList = currentList.toMutableList()
                    newList.removeAt(position)
                    submitList(newList)

                    if (newList.isEmpty()) {
                        activityAddBirthdayBinding.addBirthdayButton.visibility = View.VISIBLE
                        activityAddBirthdayBinding.addBirthdayItem.visibility = View.GONE
                    } else {
                        activityAddBirthdayBinding.addBirthdayButton.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirthdayViewHolder {
        val binding =
            CustomBirthdayUiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BirthdayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BirthdayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BirthdayDiffUtil : DiffUtil.ItemCallback<BirthdayModel>() {
        override fun areContentsTheSame(oldItem: BirthdayModel, newItem: BirthdayModel): Boolean {
            return oldItem.birthdayDatePicker == newItem.birthdayDatePicker &&
                    oldItem.birthdayLabel == newItem.birthdayLabel &&
                    oldItem.isLabelVisible == newItem.isLabelVisible
        }

        override fun areItemsTheSame(oldItem: BirthdayModel, newItem: BirthdayModel): Boolean {
            return oldItem == newItem
        }
    }
}
