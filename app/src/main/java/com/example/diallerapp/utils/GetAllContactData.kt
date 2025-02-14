package com.example.diallerapp.utils

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import com.example.diallerapp.model.uicreatecontact.AddressModel
import com.example.diallerapp.model.uicreatecontact.BirthdayModel
import com.example.diallerapp.model.uicreatecontact.EmailModel
import com.example.diallerapp.model.uicreatecontact.PhoneModel

class GetAllContactData{

    companion object {

         // Function to get Contact ID from Phone Number
        fun getContactIdFromPhoneNumber(context: Context, phoneNumber: String): String? {
            val contentResolver = context.contentResolver
            val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            val phoneProjection = arrayOf(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)

            val phoneCursor = contentResolver.query(
                phoneUri,
                phoneProjection,
                "${ContactsContract.CommonDataKinds.Phone.NUMBER} = ?",
                arrayOf(phoneNumber),
                null
            )

            var contactId: String? = null
            phoneCursor?.use {
                if (it.moveToFirst()) {
                    val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)

                    if (idIndex != -1) {
                        contactId = it.getString(idIndex)

                    } else {
                        Log.e("GetAllContactData", "CONTACT_ID column not found")
                    }
                } else {
                    Log.e("GetAllContactData", "No contact found for phone number: $phoneNumber")
                }
            }
            phoneCursor?.close()

            return contactId
        }

        // Function to get First Name directly from Phone Number
        fun getFirstNameFromPhoneNumber(context: Context, phoneNumber: String): String?{
            val contactId = getContactIdFromPhoneNumber(context, phoneNumber)
            return getFirstNameFromContactId(context, contactId)
        }

        // Function to get First Name using Contact ID
        private fun getFirstNameFromContactId(context: Context, contactId: String?): String? {
            if (contactId == null) return null // Agar ID nahi mili toh null return karo

            val contentResolver = context.contentResolver
            var firstName: String? = null

            val nameUri = ContactsContract.Data.CONTENT_URI
            val nameProjection = arrayOf(
                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME
            )

            val nameCursor = contentResolver.query(
                nameUri,
                nameProjection,
                "${ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                arrayOf(contactId, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE),
                null
            )

            nameCursor?.use {
                if (it.moveToFirst()) {
                    val firstNameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)
                    firstName = it.getString(firstNameIndex)
                }
            }
            nameCursor?.close()

            return firstName
        }

        // Function to get Sure Name directly from Phone Number
        fun getSureNameFromPhoneNumber(context: Context, phoneNumber: String): String? {
            val contactId = getContactIdFromPhoneNumber(context, phoneNumber)
            return getSureNameFromContactId(context, contactId)
        }

        // Function to get Sure Name using Contact ID
        private fun getSureNameFromContactId(context: Context, contactId: String?): String? {
            if (contactId == null) return null  // Agar contact ID nahi mili to return null
            val contentResolver = context.contentResolver

            // Step 2: Contact ID se Surname Dhoondo
            val nameUri = ContactsContract.Data.CONTENT_URI
            val nameProjection = arrayOf(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)

            val nameCursor = contentResolver.query(
                nameUri,
                nameProjection,
                "${ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID} = ?",
                arrayOf(contactId),
                null
            )

            var surname: String? = null
            nameCursor?.use {
                if (it.moveToFirst()) {
                    val surnameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)
                    surname = it.getString(surnameIndex)
                }
            }
            nameCursor?.close()

            return surname

        }

        // Function to get Label Name directly from Phone Number
        fun getLabelNameFromPhoneNumber(context: Context, phoneNumber: String): String? {
            val contactId = getContactIdFromPhoneNumber(context, phoneNumber)
            return getLabelNameFromContactId(context, contactId)
        }

        // Function to get Label Name using Contact ID
        private fun getLabelNameFromContactId(context: Context, contactId: String?): String? {
            if (contactId == null) return null
            val contentResolver = context.contentResolver

            val labelUri = ContactsContract.Data.CONTENT_URI
            val labelProjection = arrayOf(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID)

            val labelCursor = contentResolver.query(
                labelUri,
                labelProjection,
                "${ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                arrayOf(contactId, ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE),
                null
            )

            var groupId: Long? = null
            labelCursor?.use {
                if (it.moveToFirst()) {
                    val groupIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID)
                    groupId = it.getLong(groupIndex)
                }
            }
            labelCursor?.close()

            return groupId?.let { getGroupNameFromId(context, it) }
        }

        // Function to get Group Name from Group ID
        private fun getGroupNameFromId(context: Context, groupId: Long): String? {
            val contentResolver = context.contentResolver

            val groupUri = ContactsContract.Groups.CONTENT_URI
            val groupProjection = arrayOf(ContactsContract.Groups.TITLE)

            val groupCursor = contentResolver.query(
                groupUri,
                groupProjection,
                "${ContactsContract.Groups._ID} = ?",
                arrayOf(groupId.toString()),
                null
            )

            var groupName: String? = null
            groupCursor?.use {
                if (it.moveToFirst()) {
                    val groupIndex = it.getColumnIndex(ContactsContract.Groups.TITLE)
                    groupName = it.getString(groupIndex)
                }
            }
            groupCursor?.close()

            return groupName
        }


        // Function to get Company Name directly from Phone Number
        fun getCompanyNameFromPhoneNumber(context: Context, phoneNumber: String): String? {
            val contactId = getContactIdFromPhoneNumber(context, phoneNumber)
            return getCompanyNameFromContactId(context, contactId)

        }

        private fun getCompanyNameFromContactId(context: Context, contactId: String?): String? {
            if (contactId == null) return null // Agar ID nahi mili toh null return karo

            val contentResolver = context.contentResolver
            var companyName: String? = null

            val organizationUri = ContactsContract.Data.CONTENT_URI
            val organizationProjection = arrayOf(
                ContactsContract.CommonDataKinds.Organization.COMPANY
            )

            val organizationCursor = contentResolver.query(
                organizationUri,
                organizationProjection,
                "${ContactsContract.CommonDataKinds.Organization.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                arrayOf(contactId, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE),
                null
            )

            organizationCursor?.use {
                if (it.moveToFirst()) {
                    val companyIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY)
                    companyName = it.getString(companyIndex)
                }
            }
            organizationCursor?.close()

            return companyName
        }

        // Function to get birthday and label directly from Phone Number
        fun getBirthdayAndLabelFromPhoneNumber(context: Context, phoneNumber: String): List<BirthdayModel>{
            val contactId = getContactIdFromPhoneNumber(context, phoneNumber)
            if (contactId == null) {
                Log.e("GetAllContactData", "No Contact ID found for phone number: $phoneNumber")
                return emptyList() // Return an empty list instead of crashing
            }
            return GetBirthdayData.getBirthdayAndLabelFromPhoneNumber(context, contactId)
        }

        // Function to get address and label directly from Phone Number
        fun getAddressAndLabelFromPhoneNumber(context: Context, phoneNumber: String): List<AddressModel>{

            val contactId = getContactIdFromPhoneNumber(context, phoneNumber)
            if (contactId == null) {
                Log.e("GetAllContactData", "No Contact ID found for phone number: $phoneNumber")
                return emptyList() // Return an empty list instead of crashing
            }
            return GetAddressData.getAddressAndLabelFromPhoneNumber(context, contactId)
        }

        // Function to get Phone and Label directly from Phone Number (Combines both functions)
        fun getPhoneAndLabelFromPhoneNumber(context: Context, phoneNumber: String): List<PhoneModel>{
            val contactId = getContactIdFromPhoneNumber(context, phoneNumber)

            val list =  GetPhoneData.getPhoneAndLabelFromPhoneNumber(context, contactId)

            return list

        }

        // Function to get Email and Label directly from Phone Number
        fun getEmailAndLabelFromPhoneNumber(context: Context, phoneNumber: String): List<EmailModel>{

            val contactId = getContactIdFromPhoneNumber(context, phoneNumber)

            return GetEmailData.getEmailAndLabelFromPhoneNumber(context, contactId)

        }

    }
}
