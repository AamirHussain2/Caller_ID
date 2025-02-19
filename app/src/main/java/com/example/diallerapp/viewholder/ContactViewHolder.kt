package com.example.diallerapp.viewholder

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.diallerapp.R
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

        if (contact.photo != null) {
            val bitmap = BitmapFactory.decodeByteArray(contact.photo, 0, contact.photo.size)
            Log.d("IMAGE", "bindContactLog->bitmap: $bitmap")
            binding.profileImage.setImageBitmap(bitmap)
        }else{
            binding.profileImage.setImageResource(R.drawable.ic_contact_image)
        }

        binding.root.setOnClickListener {

//            var profilePicBitmap: Bitmap? = null

            val number = binding.contactPhone.text.toString()
            val name = binding.contactName.text.toString()


            val intent = Intent(binding.root.context, CreateContactActivity::class.java)

            // get image
            val contactImage = GetAllContactData.getContactImageFromPhoneNumber(context, number)
//            val bitmap = BitmapFactory.decodeByteArray(contactImage, 0, contactImage!!.size) ?: null
//            Log.d("IMAGE", "after click ->bindContactLog->bitmap: $bitmap")

            // get First Name
            val firstName = GetAllContactData.getFirstNameFromPhoneNumber(context, number)

            // get SureName
            val sureName = GetAllContactData.getSureNameFromPhoneNumber(context, number)?: ""

            // get Company
            val companyName = GetAllContactData.getCompanyNameFromPhoneNumber(context, number)?: ""

            val contactId: String? = GetAllContactData.getContactIdFromPhoneNumber(context, number)?: ""
            Log.d("ContactsViewModel", "Contact ID->contact view holder: $contactId")


            // get All Contact Data
            val phoneList = GetAllContactData.getPhoneAndLabelFromPhoneNumber(context, number)

            // get Address
            val addressList = GetAllContactData.getAddressAndLabelFromPhoneNumber(context, number)

            // get Email
            val emailList = GetAllContactData.getEmailAndLabelFromPhoneNumber(context, number)

            // get Birthday
            val birthdayList = GetAllContactData.getBirthdayAndLabelFromPhoneNumber(context, number)

            //get label
            val groupLabel = GetAllContactData.getLabelNameFromPhoneNumber(context, number)


            intent.putExtra("contact_name", name)
            intent.putExtra("contact_sureName", sureName)
            intent.putExtra("contact_company", companyName)
            intent.putExtra("contact_id", contactId)
            intent.putExtra("group_label", groupLabel)
            intent.putExtra("contact_image", contactImage)
            intent.putParcelableArrayListExtra("phone_list", phoneList as ArrayList<PhoneModel>)
            intent.putParcelableArrayListExtra("email_list", emailList as ArrayList<EmailModel>)
            intent.putParcelableArrayListExtra("address_list", addressList as ArrayList<AddressModel>)
            intent.putParcelableArrayListExtra("birthday_list", birthdayList as ArrayList<BirthdayModel>)

            binding.root.context.startActivity(intent)

        }

    }




}