package com.example.diallerapp.activities

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.graphics.createBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diallerapp.R
import com.example.diallerapp.adapter.uicreatecontact.AddToLabelAdapter
import com.example.diallerapp.adapter.uicreatecontact.AddressAdapter
import com.example.diallerapp.adapter.uicreatecontact.BirthdayAdapter
import com.example.diallerapp.adapter.uicreatecontact.EmailAdapter
import com.example.diallerapp.adapter.uicreatecontact.PhoneAdapter
import com.example.diallerapp.databinding.ActivityCreateContactBinding
import com.example.diallerapp.databinding.CustomAddToLabelDialogBinding
import com.example.diallerapp.model.uicreatecontact.AddToLabelModel

import com.example.diallerapp.model.uicreatecontact.AddressModel
import com.example.diallerapp.model.uicreatecontact.BirthdayModel
import com.example.diallerapp.model.uicreatecontact.EmailModel
import com.example.diallerapp.model.uicreatecontact.PhoneModel
import com.example.diallerapp.utils.DialogUtils
import com.example.diallerapp.utils.SaveContactData
import kotlin.properties.Delegates


class CreateContactActivity : AppCompatActivity() {
    private var _binding: ActivityCreateContactBinding? = null
    private val binding get() = _binding!!

    private val permissions = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.READ_PHONE_STATE
    )

    private lateinit var phoneAdapter: PhoneAdapter
    private lateinit var emailAdapter: EmailAdapter
    private lateinit var addressAdapter: AddressAdapter
    private lateinit var birthdayAdapter: BirthdayAdapter
    private lateinit var firstName: String
    private lateinit var sureName: String
    private lateinit var companyName: String
    private lateinit var labelName: List<String>

    private val mutableList = mutableListOf<AddToLabelModel>()

    private lateinit var dialog: Dialog
    private lateinit var phoneList: List<PhoneModel>
    private lateinit var emailList: List<EmailModel>
    private lateinit var addressList: List<AddressModel>
    private lateinit var birthdayList: List<BirthdayModel>
    private lateinit var contactId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        birthdayAdapter = BirthdayAdapter(binding, supportFragmentManager, this)
        addressAdapter = AddressAdapter(binding)
        phoneAdapter = PhoneAdapter(binding)
        emailAdapter = EmailAdapter(binding)

        getIntentData()


        binding.crossButton.setOnClickListener { finish() }

        binding.addLabelButton.setOnClickListener {

            Log.d("CreateContactActivity", "mutableList: $mutableList")

            dialog = Dialog(this)
            val dialogBinding = CustomAddToLabelDialogBinding.inflate(LayoutInflater.from(this))
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setContentView(dialogBinding.root)

            dialogBinding.btnOk.isEnabled = false


            dialogBinding.recyclerViewAddToLabelDialog.layoutManager = LinearLayoutManager(this)

            val addToLabelAdapter = AddToLabelAdapter(this)

            mutableList.add(AddToLabelModel("item 1"))
            mutableList.add(AddToLabelModel("item 2"))
            mutableList.add(AddToLabelModel("item 3"))
            mutableList.add(AddToLabelModel("item 4"))
            mutableList.add(AddToLabelModel("item 5"))
            mutableList.add(AddToLabelModel("item 6"))
            mutableList.add(AddToLabelModel("item 7"))
            mutableList.add(AddToLabelModel("item 8"))
            mutableList.add(AddToLabelModel("item 9"))
            mutableList.add(AddToLabelModel("item 10"))

            dialogBinding.recyclerViewAddToLabelDialog.adapter = addToLabelAdapter
            addToLabelAdapter.submitList(mutableList)

            // Function to check if any item is checked
            fun updateOkButtonState() {
                val isAnyItemChecked = addToLabelAdapter.currentList.any { it.isChecked }
                dialogBinding.btnOk.isEnabled = isAnyItemChecked
            }

            // Listener for checkbox state changes
            addToLabelAdapter.setOnItemCheckedChangeListener {
                updateOkButtonState()
            }

            dialogBinding.btnOk.setOnClickListener {

                val selectedText = addToLabelAdapter.currentList.filter { it.isChecked }
                    .joinToString(", ") { it.textViewLabel }

                binding.autoComplete.setText(selectedText) // EditText me text set karna

                binding.addToLabelConstraintLayout.visibility = View.VISIBLE
                binding.addLabelButton.visibility = View.GONE

                dialog.dismiss()
            }

            dialogBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            Log.d("CreateContactActivity", "addToLabelAdapter: ${addToLabelAdapter.currentList}")

            dialog.show()

        }

        binding.autoComplete.setOnClickListener {
            if (::dialog.isInitialized) {
                dialog.show()
            }
        }

        binding.addToLabelDelete.setOnClickListener {
            binding.addLabelButton.visibility = View.VISIBLE
            binding.addToLabelConstraintLayout.visibility = View.GONE
        }


        setAddPhoneButton()

        setAddEmailButton()

        setAddAddressButton()

        setAddBirthdayButton()

        setSaveButton()

    }

    private fun getIntentData() {

        // get intent data for text field
        intent.apply {
            firstName = getStringExtra("contact_name") ?: ""
            sureName = getStringExtra("contact_sureName") ?: ""
            companyName = getStringExtra("contact_company") ?: ""

            // set data with view
            binding.apply {
                edFirstName.editText?.setText(firstName)
                edSureName.editText?.setText(sureName)
                edCompany.editText?.setText(companyName)
            }
        }

        // get intent data for recycler view
        contactId = intent.getStringExtra("contact_id") ?: ""
        Log.d("contId", "intent contact id: $contactId")
        phoneList = intent.getParcelableArrayListExtra<PhoneModel>("phone_list") as? List<PhoneModel> ?: emptyList()
        emailList = intent.getParcelableArrayListExtra<EmailModel>("email_list") as? List<EmailModel> ?: emptyList()
        addressList = intent.getParcelableArrayListExtra<AddressModel>("address_list") as? List<AddressModel> ?: emptyList()
        birthdayList = intent.getParcelableArrayListExtra<BirthdayModel>("birthday_list") as? List<BirthdayModel> ?: emptyList()


    }


    private fun setAddPhoneButton() {

        val phoneMutableList = mutableListOf<PhoneModel>()

        if (phoneList.isNotEmpty()) {

            phoneMutableList.addAll(phoneList.map { it.copy(isLabelVisible = true) })

        } else {
            Log.d("newContactPhone", "mutableList: $phoneMutableList")
            phoneMutableList.add(PhoneModel("", "", isLabelVisible = false))
        }

        // Set up the Visibility
        binding.apply {
            recyclerViewAddPhone.visibility = View.VISIBLE
            addPhoneItem.visibility = View.VISIBLE
            addPhoneButton.visibility = View.GONE
        }

        // Set up the RecyclerView
        binding.recyclerViewAddPhone.apply {
            layoutManager = LinearLayoutManager(this@CreateContactActivity)
            adapter = phoneAdapter
        }

        // Submit the updated list to the adapter
        phoneAdapter.submitList(phoneMutableList.toList())

        // Set up the Add Phone Button
        binding.addPhoneButton.setOnClickListener {
            val newList = phoneAdapter.currentList.toMutableList()
            newList.add(PhoneModel("", "", isLabelVisible = true))
            phoneAdapter.submitList(newList.toList())

            binding.apply {
                addPhoneItem.visibility = View.VISIBLE
                addPhoneButton.visibility = View.GONE
            }

        }

        // Set up the Add Phone Item Button
        binding.addPhoneItem.setOnClickListener {
            val newPhoneItemList =
                phoneAdapter.currentList.map { it.copy(isLabelVisible = false) }.toMutableList()
            newPhoneItemList.add(PhoneModel("", "", isLabelVisible = true))
            phoneAdapter.submitList(newPhoneItemList.toList())
        }

    }

    private fun setAddEmailButton() {

        val emailMutableList = mutableListOf<EmailModel>()

        if (emailList.isNotEmpty()) {
            emailMutableList.addAll(emailList.map { it.copy(isLabelVisible = true) })

            // Set up the Visibility
            binding.apply {
                recyclerViewAddEmail.visibility = View.VISIBLE
                addEmailItem.visibility = View.VISIBLE
                addEmailButton.visibility = View.GONE
            }

            // Set up the RecyclerView
            binding.recyclerViewAddEmail.apply {
                layoutManager = LinearLayoutManager(this@CreateContactActivity)
                adapter = emailAdapter
            }

            // Submit the updated list to the adapter
            emailAdapter.submitList(emailMutableList.toList())
        } else {

            binding.addEmailButton.setOnClickListener {

//            val emailMutableList = mutableListOf<EmailModel>()
//
//            if (emailList.isNotEmpty()) {
//
//                emailMutableList.addAll(emailList.map { it.copy(isLabelVisible = true) })
//
//            }else{
//                Log.d("newContactEmail", "mutableList: $emailMutableList")
//                emailMutableList.add(EmailModel("", "", isLabelVisible = true))
//            }
                emailMutableList.add(EmailModel("", "", isLabelVisible = true))

                // Set up the Visibility
                binding.apply {
                    recyclerViewAddEmail.visibility = View.VISIBLE
                    addEmailItem.visibility = View.VISIBLE
                    addEmailButton.visibility = View.GONE
                }

                // Set up the RecyclerView
                binding.recyclerViewAddEmail.apply {
                    layoutManager = LinearLayoutManager(this@CreateContactActivity)
                    adapter = emailAdapter
                }

                // Submit the updated list to the adapter
                emailAdapter.submitList(emailMutableList.toList())
            }
        }


        binding.addEmailItem.setOnClickListener {

            val newEmailItemList =
                emailAdapter.currentList.map { it.copy(isLabelVisible = false) }.toMutableList()

            newEmailItemList.add(EmailModel("", "", isLabelVisible = true))

            emailAdapter.submitList(newEmailItemList.toList())
        }

    }

    private fun setAddBirthdayButton() {
        val birthdayMutableList = mutableListOf<BirthdayModel>()

        if (birthdayList.isNotEmpty()) {
            birthdayMutableList.addAll(birthdayList.map { it.copy(isLabelVisible = true) })

            // Set up the Visibility
            binding.apply {
                recyclerViewAddBirthday.visibility = View.VISIBLE
                addBirthdayItem.visibility = View.VISIBLE
                addBirthdayButton.visibility = View.GONE
            }

            // Set up the RecyclerView
            binding.recyclerViewAddBirthday.apply {
                layoutManager = LinearLayoutManager(this@CreateContactActivity)
                adapter = birthdayAdapter
            }

            // Submit the updated list to the adapter
            birthdayAdapter.submitList(birthdayMutableList.toList())

        } else {
            binding.addBirthdayButton.setOnClickListener {

//                val birthdayMutableList = mutableListOf<BirthdayModel>()

//                if (birthdayList.isNotEmpty()) {

//                    birthdayMutableList.addAll(birthdayList.map { it.copy(isLabelVisible = true) })
//
//                } else {
//                    Log.d("newContactBirthday", "mutableList: $birthdayMutableList")
//                    birthdayMutableList.add(BirthdayModel("", "", isLabelVisible = true))
//                }
                birthdayMutableList.add(BirthdayModel("", "", isLabelVisible = true))

                // Set up the Visibility
                binding.apply {
                    recyclerViewAddBirthday.visibility = View.VISIBLE
                    addBirthdayItem.visibility = View.VISIBLE
                    addBirthdayButton.visibility = View.GONE
                }

                // Set up the RecyclerView
                binding.recyclerViewAddBirthday.apply {
                    layoutManager = LinearLayoutManager(this@CreateContactActivity)
                    adapter = birthdayAdapter
                }

                // Submit the updated list to the adapter
                birthdayAdapter.submitList(birthdayMutableList.toList())

                binding.recyclerViewAddBirthday.post {
                    DialogUtils.showDialogDatePicker(supportFragmentManager)
                }

            }

        }

        binding.addBirthdayItem.setOnClickListener {

            val newBirthdayItemList =
                birthdayAdapter.currentList.map { it.copy(isLabelVisible = false) }.toMutableList()

            newBirthdayItemList.add(BirthdayModel("", "", isLabelVisible = true))

            birthdayAdapter.submitList(newBirthdayItemList.toList())

            DialogUtils.showDialogDatePicker(supportFragmentManager)
        }

    }

    private fun setAddAddressButton() {
        val addressMutableList = mutableListOf<AddressModel>()

        if (addressList.isNotEmpty()) {

            addressMutableList.addAll(addressList.map { it.copy(isLabelVisible = true) })

            binding.apply {

                recyclerViewAddAddress.visibility = View.VISIBLE
                addAddressItem.visibility = View.VISIBLE
                addAddressButton.visibility = View.GONE
            }

            binding.recyclerViewAddAddress.apply {
                layoutManager = LinearLayoutManager(this@CreateContactActivity)
                adapter = addressAdapter
            }

            addressAdapter.submitList(addressMutableList.toList())

        } else {
            binding.addAddressButton.setOnClickListener {

//                val addressMutableList = mutableListOf<AddressModel>()

//                if (addressList.isNotEmpty()) {
//
//                    addressMutableList.addAll(addressList.map { it.copy(isLabelVisible = true) })
//
//                } else {
//                    Log.d("newContactAddress", "mutableList: $addressMutableList")
//                    addressMutableList.add(AddressModel("", "", isLabelVisible = true))
//                }

                addressMutableList.add(AddressModel("", "", isLabelVisible = true))

                binding.apply {

                    recyclerViewAddAddress.visibility = View.VISIBLE
                    addAddressItem.visibility = View.VISIBLE
                    addAddressButton.visibility = View.GONE
                }

                binding.recyclerViewAddAddress.apply {
                    layoutManager = LinearLayoutManager(this@CreateContactActivity)
                    adapter = addressAdapter
                }

                addressAdapter.submitList(addressMutableList.toList())


            }

        }

        binding.addAddressItem.setOnClickListener {

            val newAddressItemList =
                addressAdapter.currentList.map { it.copy(isLabelVisible = false) }.toMutableList()
//            newList.add(AddressModel("", "", isLabelVisible = true))

            newAddressItemList.add(AddressModel("", "", isLabelVisible = true))

            addressAdapter.submitList(newAddressItemList.toList())
        }

    }

    private fun setSaveButton() {

        binding.saveButton.setOnClickListener {

            firstName = binding.edFirstName.editText?.text.toString().trim()
            sureName = binding.edSureName.editText?.text.toString().trim()
            companyName = binding.edCompany.editText?.text.toString().trim()
            labelName = listOf(binding.autoComplete.text.toString())

            // Collect phone numbers
            val allPhoneData = phoneAdapter.currentList

            // Collect emails
            val allEmailData = emailAdapter.currentList

            // Collect address
            val allAddressData = addressAdapter.currentList

            //Collect birthday
            val allBirthdayData = birthdayAdapter.currentList

            handlePermissionsAndSaveContact(
                phoneList = allPhoneData,
                emailList = allEmailData,
                addressList = allAddressData,
                birthdayList = allBirthdayData
            )

            Log.d("AddPhoneActivity", "All phone data: $allPhoneData")
            Log.d("AddPhoneActivity", "All phone data size: ${allPhoneData.size}")
        }
    }

    private fun handlePermissionsAndSaveContact(
        phoneList: List<PhoneModel>,
        emailList: List<EmailModel>,
        addressList: List<AddressModel>,
        birthdayList: List<BirthdayModel>
    ) {
        if (permissions.all {
                ActivityCompat.checkSelfPermission(
                    this,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }) {
//            phoneList.forEach { phoneModel ->
//                emailList.forEach { emailModel ->
//                    addressList.forEach { addressModel ->
//                        birthdayList.forEach { birthdayModel ->
//
//                            SaveContactData.insertOrUpdateContact(
//                                this,
//                                contentResolver,
//                                phoneNumber = phoneModel.phoneNumber,
//                                phoneLabel = phoneModel.phoneLabel,
//                                email = emailModel.email,
//                                emailLabel = emailModel.emailLabel,
//                                address = addressModel.address,
//                                addressLabel = addressModel.addressLabel,
//                                birthdayDatePicker = birthdayModel.birthdayDatePicker,
//                                birthdayLabel = birthdayModel.birthdayLabel,
//                                contactProfilePic = SaveContactData.convertDrawableResToBitmap(
//                                    this,
//                                    R.drawable.ic_launcher_background
//                                ),
//                                firstName = firstName,
//                                sureName = sureName,
//                                companyName = companyName,
//                                labelName = labelName
//                            )
//                        }
//                    }
//                }
//            }

            saveContactData(
                phoneList = phoneList,
                emailList = emailList,
                addressList = addressList,
                birthdayList = birthdayList
            )
        } else {
            requestMultiplePermissionsLauncher.launch(permissions)
        }
    }


    private val requestMultiplePermissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.values.all {
                    it
                }) {
                // Save data after getting permissions
                saveContactData(
                    phoneAdapter.currentList.toMutableList(),
                    emailAdapter.currentList.toMutableList(),
                    addressAdapter.currentList.toMutableList(),
                    birthdayAdapter.currentList.toMutableList()
                )
            } else {
                Toast.makeText(
                    this,
                    "All permissions are required to save contacts!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun saveContactData(
        phoneList: List<PhoneModel>,
        emailList: List<EmailModel>,
        addressList: List<AddressModel>,
        birthdayList: List<BirthdayModel>
    ) {
        try {
            phoneList.forEach { phoneModel ->
                emailList.forEach { emailModel ->
                    addressList.forEach { addressModel ->
                        birthdayList.forEach { birthdayModel ->

                            SaveContactData.insertOrUpdateContact(
                                context = this,
                                contactId = contactId,
                                contentResolver = contentResolver,
                                phoneNumber = phoneModel.phoneNumber,
                                phoneLabel = phoneModel.phoneLabel,
                                email = emailModel.email,
                                emailLabel = emailModel.emailLabel,
                                address = addressModel.address,
                                addressLabel = addressModel.addressLabel,
                                birthdayDatePicker = birthdayModel.birthdayDatePicker,
                                birthdayLabel = birthdayModel.birthdayLabel,
                                contactProfilePic = SaveContactData.convertDrawableResToBitmap(
                                    this,
                                    R.drawable.ic_launcher_background
                                ),
                                firstName = firstName,
                                sureName = sureName,
                                companyName = companyName,
                                labelName = labelName
                            )
                        }
                    }
                }
            }

        }catch (e: Exception){
            Log.e("CreateContactActivity", "Error saving contact: ${e.message}")
            Toast.makeText(this, "Error saving contact: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        finish()
    }


    override fun onDestroy() {
        super.onDestroy()

        _binding?.apply {
            recyclerViewAddPhone.adapter = null
            recyclerViewAddEmail.adapter = null
            recyclerViewAddAddress.adapter = null
            recyclerViewAddBirthday.adapter = null
        }

        _binding = null
    }
}