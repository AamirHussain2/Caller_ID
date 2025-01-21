package com.example.diallerapp.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diallerapp.R
import com.example.diallerapp.adapter.uicreatecontact.AddressAdapter
import com.example.diallerapp.adapter.uicreatecontact.BirthdayAdapter
import com.example.diallerapp.adapter.uicreatecontact.EmailAdapter
import com.example.diallerapp.adapter.uicreatecontact.PhoneAdapter
import com.example.diallerapp.databinding.ActivityCreateContactBinding
import com.example.diallerapp.databinding.CustomAddressUiBinding
import com.example.diallerapp.databinding.CustomBirthdayUiBinding
import com.example.diallerapp.databinding.CustomEmailUiBinding
import com.example.diallerapp.model.uicreatecontact.AddressModel
import com.example.diallerapp.model.uicreatecontact.BirthdayModel
import com.example.diallerapp.model.uicreatecontact.EmailModel
import com.example.diallerapp.model.uicreatecontact.PhoneModel

class CreateContactActivity : AppCompatActivity() {
    private var _binding: ActivityCreateContactBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.crossButton.setOnClickListener { finish() }

        val phoneLabels = resources.getStringArray(R.array.phoneLabel)
        Log.d("phoneLabels", phoneLabels.get(0))

        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            phoneLabels
        )

        Log.d("adapter", "adapter: ${adapter.getItem(0)}")

        binding.addEmailButton.setOnClickListener {

            val view = CustomEmailUiBinding.inflate(layoutInflater)

            val email = view.edEmail.editText?.text.toString()
            val emailLabel = view.emailAutoCompleteLabel.text.toString()

            val mutableList = mutableListOf<EmailModel>()
            mutableList.add(EmailModel(email, emailLabel))
            Log.d("mutableList", "mutableList: $mutableList")


            binding.recyclerViewAddEmail.layoutManager = LinearLayoutManager(this)
            val emailAdapter = EmailAdapter()
            emailAdapter.submitList(mutableList)
            binding.recyclerViewAddEmail.adapter = emailAdapter

            binding.recyclerViewAddEmail.visibility = View.VISIBLE
            binding.addEmailButton.visibility = View.GONE

        }

        binding.addAddressButton.setOnClickListener {

            val view = CustomAddressUiBinding.inflate(layoutInflater)

            val address = view.edAddress.editText?.text.toString()
            val addressLabel = view.addressAutoCompleteLabel.text.toString()

            val mutableList = mutableListOf<AddressModel>()
            mutableList.add(AddressModel(address, addressLabel))

            val addressAdapter = AddressAdapter()
            binding.recyclerViewAddAddress.layoutManager = LinearLayoutManager(this)
            addressAdapter.submitList(mutableList)
            binding.recyclerViewAddAddress.adapter = addressAdapter

            binding.recyclerViewAddAddress.visibility = View.VISIBLE
            binding.addAddressButton.visibility = View.GONE

        }

        binding.addBirthButton.setOnClickListener {

            val view = CustomBirthdayUiBinding.inflate(layoutInflater)
            Log.d("AddBirthButton", "Button Clicked: $view")

            val date = view.autoCompleteDatePicker.text.toString()
            val birthdayLabel = view.autoCompleteTextView.text.toString()

            Log.d("AddBirthButton", "date: $date")
            Log.d("AddBirthButton", "birthdayLabel: $birthdayLabel")

            val mutableList = mutableListOf<BirthdayModel>()
            mutableList.add(BirthdayModel(date, birthdayLabel))

            val birthdayAdapter = BirthdayAdapter()
            binding.recyclerViewAddBirthday.layoutManager = LinearLayoutManager(this)
            birthdayAdapter.submitList(mutableList)
            binding.recyclerViewAddBirthday.adapter = birthdayAdapter

            binding.recyclerViewAddBirthday.visibility = View.VISIBLE
            binding.addBirthButton.visibility = View.GONE

        }

//        setAddPhoneButton()

        binding.addLabelButton.setOnClickListener {
            binding.addLabelButton.visibility = View.GONE
            binding.addToLabelConstraintLayout.visibility = View.VISIBLE
        }


//        binding.addPhoneButton.setOnClickListener {
//            setAddPhoneButton()
//        }
        setAddPhoneButton()

    }

    private fun setAddPhoneButton() {
        val mutableList = mutableListOf<PhoneModel>()
        mutableList.add(PhoneModel("1233" , "home"))

        binding.recyclerViewAddPhone.layoutManager = LinearLayoutManager(this)
        val phoneAdapter = PhoneAdapter(binding)
        phoneAdapter.submitList(mutableList)
        binding.recyclerViewAddPhone.adapter = phoneAdapter

        binding.recyclerViewAddPhone.visibility = View.VISIBLE
        binding.addPhoneButton.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
