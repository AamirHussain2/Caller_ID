package com.example.diallerapp.utils

import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import com.google.android.material.datepicker.MaterialDatePicker

class DialogUtils {
    companion object {
        var selectedDate: MutableLiveData<String> = MutableLiveData()

        fun showDialogDatePicker(fragmentManager: FragmentManager) {
            Log.d("DatePickerActivity", "DatePicker button clicked")

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.show(fragmentManager, "DATE_PICKER")

            datePicker.addOnPositiveButtonClickListener {
                Log.d("DatePickerActivity", "Date selected: ${datePicker.headerText}")
                selectedDate.postValue(datePicker.headerText)
                Log.d("DatePickerActivity", "Selected date: $selectedDate")
            }

        }

//        fun getSelectedDate(): String? {
//            Log.d("DatePickerActivity", "Getting selected datelkjlkjlkjlj: $selectedDate")
//            return selectedDate
//        }
    }
}
