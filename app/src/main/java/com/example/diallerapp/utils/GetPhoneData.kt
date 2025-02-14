package com.example.diallerapp.utils

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import com.example.diallerapp.model.uicreatecontact.PhoneModel

class GetPhoneData {

    companion object {

        // Function to get Phone and Label directly from Phone Number (Combines both functions)

        fun getPhoneAndLabelFromPhoneNumber(context: Context, contactId: String?) : List<PhoneModel>{

            val list = getPhoneAndLabelFromContactId(context, contactId)

            Log.d("PHONE", "getPhoneAndLabelFromContactId: $list")

            return list
        }

        // Function to get Phone and Label using Contact ID

        private fun getPhoneAndLabelFromContactId(context: Context, contactId: String?): List<PhoneModel> {
            Log.d("PHONE", "contactId->getPhoneAndLabelFromContactId: $contactId")
            if (contactId == null) return emptyList() // Agar ID nahi mili to empty list return karo

            val phoneList = mutableListOf<PhoneModel>()
            val contentResolver = context.contentResolver

            val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            val phoneProjection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.LABEL
            )

            val phoneCursor = contentResolver.query(
                phoneUri,
                phoneProjection,
                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                arrayOf(contactId),
                null
            )

            phoneCursor?.use {
                while (it.moveToNext()) {
                    val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val typeIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)
                    val labelIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL)

                    val phoneNumber = it.getString(numberIndex)
                    val phoneType = it.getInt(typeIndex)

                   var phoneLabel = if (phoneType != ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM) {
                        getPhoneTypeLabel(phoneType)
                    }else{
                        "Custom"
                    }

                    phoneList.add(PhoneModel(phoneNumber, phoneLabel ?: "Unknown"))
                }
            }
            phoneCursor?.close()

            return phoneList
        }

        // Function to get Phone Type Label
        private fun getPhoneTypeLabel(phoneType: Int): String {
            return when (phoneType) {
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> "Mobile"
                ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> "Home"
                ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> "Work"
                ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM -> "Custom"
                ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK -> "Work Fax"
                ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME -> "Home Fax"
                ContactsContract.CommonDataKinds.Phone.TYPE_PAGER -> "Pager"
                ContactsContract.CommonDataKinds.Phone.TYPE_OTHER -> "Other"
                else -> "Unknown"
            }
        }
    }
}