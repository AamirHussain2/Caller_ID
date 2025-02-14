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

    private lateinit var dialog: Dialog
    private lateinit var addToLabelAdapter: AddToLabelAdapter

    private lateinit var updatedPhoneList: List<PhoneModel>
    private lateinit var updatedEmailList: List<EmailModel>
    private lateinit var updatedAddressList: List<AddressModel>
    private lateinit var updatedBirthdayList: List<BirthdayModel>
    private lateinit var updatedContactId: String
    private lateinit var updatedGroupLabel: String

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



        setAddToLabel()

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
        updatedContactId = intent.getStringExtra("contact_id") ?: ""
        Log.d("contId", "get intent contact id: $updatedContactId")

        updatedPhoneList = intent.getParcelableArrayListExtra<PhoneModel>("phone_list") as? List<PhoneModel> ?: emptyList()
        Log.d("TESTING", "updatedPhoneList...: $updatedPhoneList")

        updatedEmailList = intent.getParcelableArrayListExtra<EmailModel>("email_list") as? List<EmailModel> ?: emptyList()
        updatedAddressList = intent.getParcelableArrayListExtra<AddressModel>("address_list") as? List<AddressModel> ?: emptyList()
        updatedBirthdayList = intent.getParcelableArrayListExtra<BirthdayModel>("birthday_list") as? List<BirthdayModel> ?: emptyList()

        updatedGroupLabel = intent.getStringExtra("group_label") ?: ""
        Log.d("contId", "get intent group label: $updatedGroupLabel")
    }

    private fun setAddToLabel(){
        val groupLabelMutableList = mutableListOf<AddToLabelModel>()

        if (updatedGroupLabel.isNotEmpty()) {
            binding.apply {

                autoComplete.setText(updatedGroupLabel)
                addToLabelConstraintLayout.visibility = View.VISIBLE
                addLabelButton.visibility = View.GONE

                groupLabelMutableList.add(AddToLabelModel(updatedGroupLabel))

                autoComplete.setOnClickListener {
                    showAddToLabelDialog(groupLabelMutableList)
                }
            }
        }

        binding.addLabelButton.setOnClickListener {
                showAddToLabelDialog(groupLabelMutableList)

//            val dialogBinding = CustomAddToLabelDialogBinding.inflate(LayoutInflater.from(this))
//
//            dialog.apply {
//                window?.setBackgroundDrawableResource(android.R.color.transparent)
//                setContentView(dialogBinding.root)
//            }
//
//            addToLabelAdapter = AddToLabelAdapter(this)
//
//            groupLabelMutableList.apply {
//                add(AddToLabelModel("item 1"))
//                add(AddToLabelModel("item 2"))
//                add(AddToLabelModel("item 3"))
//                add(AddToLabelModel("item 4"))
//                add(AddToLabelModel("item 5"))
//                add(AddToLabelModel("item 6"))
//                add(AddToLabelModel("item 7"))
//                add(AddToLabelModel("item 8"))
//                add(AddToLabelModel("item 9"))
//                add(AddToLabelModel("item 10"))
//            }
//
//            // set adapter
//            dialogBinding.apply {
//                btnOk.isEnabled = false
//                recyclerViewAddToLabelDialog.layoutManager = LinearLayoutManager(this@CreateContactActivity)
//                recyclerViewAddToLabelDialog.adapter = addToLabelAdapter
//            }
//
//            addToLabelAdapter.submitList(groupLabelMutableList.toList())
//
//            // Listener for checkbox state changes
//            addToLabelAdapter.setOnItemCheckedChangeListener {
//                // To check if any item is checked
//                val isAnyItemChecked = addToLabelAdapter.currentList.any { it.isChecked }
//                dialogBinding.btnOk.isEnabled = isAnyItemChecked
//            }
//
//            dialogBinding.apply {
//
//                btnOk.setOnClickListener {
//
//                    val selectedText = addToLabelAdapter.currentList.filter { it.isChecked }
//                        .joinToString(", ") { it.textViewLabel }
//
//
//                    binding.apply {
//                        autoComplete.setText(selectedText)
//
//                        addToLabelConstraintLayout.visibility = View.VISIBLE
//                        addLabelButton.visibility = View.GONE
//                    }
//
//                    dialog.dismiss()
//                }
//
//                btnCancel.setOnClickListener {
//                    dialog.dismiss()
//                }
//            }
//            Log.d("CreateContactActivity", "addToLabelAdapter: ${addToLabelAdapter.currentList}")
//
//            dialog.show()
        }

        // click view of autoComplete
        binding.apply {

            autoComplete.setOnClickListener {
                showAddToLabelDialog(groupLabelMutableList)
            }

            // delete button
            addToLabelDelete.setOnClickListener {
                addLabelButton.visibility = View.VISIBLE
                addToLabelConstraintLayout.visibility = View.GONE
            }
        }

    }

    private fun showAddToLabelDialog(groupLabelMutableList: MutableList<AddToLabelModel>) {
        dialog = Dialog(this)

        val dialogBinding = CustomAddToLabelDialogBinding.inflate(LayoutInflater.from(this))

        dialog.apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setContentView(dialogBinding.root)
        }

        val addToLabelAdapter = AddToLabelAdapter(this)

        groupLabelMutableList.apply {
            add(AddToLabelModel("item 1"))
            add(AddToLabelModel("item 2"))
            add(AddToLabelModel("item 3"))
            add(AddToLabelModel("item 4"))
            add(AddToLabelModel("item 5"))
            add(AddToLabelModel("item 6"))
            add(AddToLabelModel("item 7"))
            add(AddToLabelModel("item 8"))
            add(AddToLabelModel("item 9"))
            add(AddToLabelModel("item 10"))
        }

        dialogBinding.apply {
            btnOk.isEnabled = false
            recyclerViewAddToLabelDialog.layoutManager = LinearLayoutManager(this@CreateContactActivity)
            recyclerViewAddToLabelDialog.adapter = addToLabelAdapter
        }

        addToLabelAdapter.submitList(groupLabelMutableList.toList())

        // Listener for checkbox state changes
        addToLabelAdapter.setOnItemCheckedChangeListener {
            val isAnyItemChecked = addToLabelAdapter.currentList.any { it.isChecked }
            dialogBinding.btnOk.isEnabled = isAnyItemChecked
        }

        dialogBinding.apply {

            btnOk.setOnClickListener {
                val selectedText = addToLabelAdapter.currentList
                    .filter { it.isChecked }
                    .joinToString(", ") { it.textViewLabel }

                binding.apply {
                    autoComplete.setText(selectedText)
                    addToLabelConstraintLayout.visibility = View.VISIBLE
                    addLabelButton.visibility = View.GONE
                }

                dialog.dismiss()
            }

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }

        Log.d("CreateContactActivity", "addToLabelAdapter: ${addToLabelAdapter.currentList}")

        dialog.show()
    }

    private fun setAddPhoneButton() {

        val phoneMutableList = mutableListOf<PhoneModel>()

        if (updatedPhoneList.isNotEmpty()) {

            phoneMutableList.addAll(updatedPhoneList.map { it.copy(isLabelVisible = true) })

        } else {
            Log.d("newContactPhone", "mutableList: $phoneMutableList")
            phoneMutableList.add(PhoneModel("", "", isLabelVisible = false))
            Log.d("DEBUG", "Phone List Size...: ${phoneMutableList.size}")

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
            val newPhoneItemList = phoneAdapter.currentList.map { it.copy(isLabelVisible = false) }.toMutableList()
            Log.d("DEBUG", "newPhoneItemList: $newPhoneItemList")
            newPhoneItemList.add(PhoneModel("", "", isLabelVisible = true))
            Log.d("DEBUG", "Phone List: ${newPhoneItemList.size} \n ${newPhoneItemList.forEach { it.phoneNumber + it.phoneLabel }}")

            phoneAdapter.submitList(newPhoneItemList.toList())
        }

    }

    private fun setAddEmailButton() {

        val emailMutableList = mutableListOf<EmailModel>()

        if (updatedEmailList.isNotEmpty()) {
            emailMutableList.addAll(updatedEmailList.map { it.copy(isLabelVisible = true) })

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

        if (updatedBirthdayList.isNotEmpty()) {
            birthdayMutableList.addAll(updatedBirthdayList.map { it.copy(isLabelVisible = true) })

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

        if (updatedAddressList.isNotEmpty()) {

            addressMutableList.addAll(updatedAddressList.map { it.copy(isLabelVisible = true) })

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
                ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }) {

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
                    phoneAdapter.currentList,
                    emailAdapter.currentList,
                    addressAdapter.currentList,
                    birthdayAdapter.currentList
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
            val contactProfilePic = SaveContactData.convertDrawableResToBitmap(
                this,
                R.drawable.ic_launcher_background
            )

            SaveContactData.insertOrUpdateContact(
                context = this,
                contactId = updatedContactId,
                contentResolver = contentResolver,
                phoneList = phoneList,
                emailList = emailList,
                addressList = addressList,
                birthdayList = birthdayList,
                contactProfilePic = contactProfilePic,
                firstName = firstName,
                sureName = sureName,
                companyName = companyName,
                labelName = labelName
            )

            Toast.makeText(this, "Contact Saved Successfully!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
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