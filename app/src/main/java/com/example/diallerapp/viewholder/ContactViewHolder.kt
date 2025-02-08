package com.example.diallerapp.viewholder

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.diallerapp.activities.CreateContactActivity
import com.example.diallerapp.databinding.CustomContactsUiBinding
import com.example.diallerapp.model.ContactModel
import com.example.diallerapp.model.uicreatecontact.AddressModel
import com.example.diallerapp.model.uicreatecontact.BirthdayModel
import com.example.diallerapp.model.uicreatecontact.EmailModel
import com.example.diallerapp.model.uicreatecontact.PhoneModel
import com.example.diallerapp.utils.GetAllContactData


class ContactViewHolder(private val binding: CustomContactsUiBinding, private val context: Context) :
    RecyclerView.ViewHolder(binding.root) {
    fun bindContactLog(contact: ContactModel) {
        binding.contactName.text = contact.name
        binding.contactPhone.text = contact.phoneNumber


        binding.root.setOnClickListener {

            val number = binding.contactPhone.text.toString()
            val name = binding.contactName.text.toString()
            Log.d("ContactViewHolder", "number: $number")
            Log.d("ContactViewHolder", "name: $name")

            val intent = Intent(binding.root.context, CreateContactActivity::class.java)

            // get First Name
            val firstName = GetAllContactData.getFirstNameFromPhoneNumber(context, number)
            Log.d("ContactEmail", "firstName: $firstName")

            // get SureName
            val sureName = GetAllContactData.getSureNameFromPhoneNumber(context, number)
            Log.d("ContactEmail", "sureName: $sureName")

            // get Company
            val companyName = GetAllContactData.getCompanyNameFromPhoneNumber(context, number)
            Log.d("ContactEmail", "companyName: $companyName")

            // get All Contact Data
            val phoneList = GetAllContactData.getPhoneAndLabelFromPhoneNumber(context, number)
            Log.d("ContactEmail", "phoneList: $phoneList")

            for (phoneModel in phoneList) {
//                Log.d("ContactPhone", "Phone: ${phoneModel.phoneNumber}, Label: ${phoneModel.phoneLabel}")
            }

            // get Address
            val addressList = GetAllContactData.getAddressAndLabelFromPhoneNumber(context, number)
            Log.d("ContactEmail", "addressList: $addressList")

            for (addressModel in addressList) {
                Log.d("ContactEmail", "Address: ${addressModel.address}, Label: ${addressModel.addressLabel}")
            }

            // get Email
            val emailList = GetAllContactData.getEmailAndLabelFromPhoneNumber(context, number)
            Log.d("ContactEmail", "emailList: $emailList")
//            for ((email, label) in emailList) {
//                Log.d("ContactEmail", "Email: $email, Label: $label")
//            }
            for (emailModel in emailList) {
                val email = emailModel.email
                val label = emailModel.emailLabel
                Log.d("ContactEmail", "Email: $email, Label: $label")
            }

            // get Birthday
            val birthdayList = GetAllContactData.getBirthdayAndLabelFromPhoneNumber(context, number)
            Log.d("ContactEmail", "birthdayList: $birthdayList")

            for (birthdayModel in birthdayList) {
                Log.d("ContactEmail", "Birthday: ${birthdayModel.birthdayDatePicker}, Label: ${birthdayModel.birthdayLabel}")
            }


            intent.putExtra("contact_name", name)
            intent.putExtra("contact_sureName", sureName)
            intent.putExtra("contact_company", companyName)
            intent.putParcelableArrayListExtra("phone_list", phoneList as ArrayList<PhoneModel>)
            intent.putParcelableArrayListExtra("email_list", emailList as ArrayList<EmailModel>)
            intent.putParcelableArrayListExtra("address_list", addressList as ArrayList<AddressModel>)
            intent.putParcelableArrayListExtra("birthday_list", birthdayList as ArrayList<BirthdayModel>)

            binding.root.context.startActivity(intent)

        }

    }




}