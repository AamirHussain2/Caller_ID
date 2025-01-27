package com.example.diallerapp.activities

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diallerapp.R
import com.example.diallerapp.adapter.uicreatecontact.AddressAdapter
import com.example.diallerapp.adapter.uicreatecontact.BirthdayAdapter
import com.example.diallerapp.adapter.uicreatecontact.EmailAdapter
import com.example.diallerapp.adapter.uicreatecontact.PhoneAdapter
import com.example.diallerapp.databinding.ActivityCreateContactBinding
import com.example.diallerapp.databinding.CustomEmailUiBinding
import com.example.diallerapp.databinding.CustomPhoneUiBinding
import com.example.diallerapp.model.uicreatecontact.AddressModel
import com.example.diallerapp.model.uicreatecontact.BirthdayModel
import com.example.diallerapp.model.uicreatecontact.EmailModel
import com.example.diallerapp.model.uicreatecontact.PhoneModel
import com.example.diallerapp.utils.DialogUtils
import com.google.android.material.datepicker.MaterialDatePicker

class CreateContactActivity : AppCompatActivity() {
    private var _binding: ActivityCreateContactBinding? = null
    private val binding get() = _binding!!

//    lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.crossButton.setOnClickListener { finish() }

//        val phoneLabels = resources.getStringArray(R.array.phoneLabel)
//        Log.d("phoneLabels", phoneLabels.get(0))
//
//        adapter = ArrayAdapter(
//            this,
//            android.R.layout.simple_list_item_1,
//            phoneLabels
//        )

        binding.addEmailButton.setOnClickListener {

            val view = CustomEmailUiBinding.inflate(layoutInflater)

            val email = view.edEmail.editText?.text.toString()
            val emailLabel = view.emailAutoCompleteLabel.text.toString()

            val mutableList = mutableListOf<EmailModel>()
            mutableList.add(EmailModel(email, emailLabel))
            Log.d("mutableList", "mutableList: $mutableList")


            binding.recyclerViewAddEmail.layoutManager = LinearLayoutManager(this)
            val emailAdapter = EmailAdapter(binding)
            emailAdapter.submitList(mutableList)
            binding.recyclerViewAddEmail.adapter = emailAdapter

            binding.recyclerViewAddEmail.visibility = View.VISIBLE
            binding.addEmailItem.visibility = View.VISIBLE
            binding.addEmailButton.visibility = View.GONE

        }

        binding.addAddressButton.setOnClickListener {

//            val view = CustomAddressUiBinding.inflate(layoutInflater)
//
//            val address = view.edAddress.editText?.text.toString()
//            val addressLabel = view.addressAutoCompleteLabel.text.toString()

            val mutableList = mutableListOf<AddressModel>()
            mutableList.add(AddressModel("", ""))

            val addressAdapter = AddressAdapter(binding)
            binding.recyclerViewAddAddress.layoutManager = LinearLayoutManager(this)
            addressAdapter.submitList(mutableList)
            binding.recyclerViewAddAddress.adapter = addressAdapter

            binding.recyclerViewAddAddress.visibility = View.VISIBLE
            binding.addAddressButton.visibility = View.GONE
            binding.addAddressItem.visibility = View.VISIBLE

        }

        binding.addBirthdayButton.setOnClickListener {

            DialogUtils.showDialogDatePicker(supportFragmentManager)

            val mutableList = mutableListOf<BirthdayModel>()
            val birthdayAdapter = BirthdayAdapter(binding)

            DialogUtils.selectedDate.observe(this) { selectedDate ->
                mutableList.add(BirthdayModel(selectedDate, ""))
                birthdayAdapter.submitList(mutableList)

            }

            binding.recyclerViewAddBirthday.layoutManager = LinearLayoutManager(this)
//            birthdayAdapter.submitList(mutableList)
            binding.recyclerViewAddBirthday.adapter = birthdayAdapter
            binding.recyclerViewAddBirthday.visibility = View.VISIBLE

            binding.addBirthdayButton.visibility = View.GONE
            binding.addBirthdayItem.visibility = View.VISIBLE
        }


        binding.addLabelButton.setOnClickListener {
            binding.addLabelButton.visibility = View.GONE
            binding.addToLabelConstraintLayout.visibility = View.VISIBLE
        }

        setAddPhoneButton()

    }

    private fun setAddPhoneButton() {

        val view = CustomPhoneUiBinding.inflate(layoutInflater)

        val mutableList = mutableListOf<PhoneModel>()
        mutableList.add(PhoneModel("" , ""))

        binding.recyclerViewAddPhone.layoutManager = LinearLayoutManager(this)

        val phoneAdapter = PhoneAdapter(binding)

        phoneAdapter.submitList(mutableList)
        binding.recyclerViewAddPhone.adapter = phoneAdapter

        binding.recyclerViewAddPhone.visibility = View.VISIBLE
        binding.addPhoneItem.visibility = View.VISIBLE
        binding.addPhoneButton.visibility = View.GONE


        binding.addPhoneButton.setOnClickListener {
            val newList = phoneAdapter.currentList.map { it.copy() } as ArrayList
            newList.add(PhoneModel("", ""))
            phoneAdapter.submitList(newList)

            binding.addPhoneItem.visibility = View.VISIBLE
            binding.addPhoneButton.visibility = View.GONE
            view.labelMenu.visibility = View.VISIBLE

            phoneAdapter.selectedPosition = newList.size - 1
        }

        binding.addPhoneItem.setOnClickListener {
            val newList = phoneAdapter.currentList.map { it.copy() } as ArrayList
            newList.add(PhoneModel("", ""))
            phoneAdapter.selectedPosition = null
            view.labelMenu.visibility = View.VISIBLE
            phoneAdapter.submitList(newList)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}