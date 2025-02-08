package com.example.diallerapp.utils

import android.content.Context
import android.provider.ContactsContract
import com.example.diallerapp.model.uicreatecontact.AddressModel

class GetAddressData {

    companion object {

        fun getAddressAndLabelFromPhoneNumber(context: Context, contactId: String?): List<AddressModel> {
            return getAddressAndLabelFromContactId(context, contactId)
        }

        private fun getAddressAndLabelFromContactId(context: Context, contactId: String?): List<AddressModel> {
            if (contactId == null) return emptyList() // Agar ID nahi mili toh empty list return karo

            val addressList = mutableListOf<AddressModel>()
            val contentResolver = context.contentResolver

            val addressUri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI
            val addressProjection = arrayOf(
                ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
                ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
                ContactsContract.CommonDataKinds.StructuredPostal.LABEL
            )

            val addressCursor = contentResolver.query(
                addressUri,
                addressProjection,
                "${ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID} = ?",
                arrayOf(contactId),
                null
            )

            addressCursor?.use {
                while (it.moveToNext()) {
                    val addressIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)
                    val typeIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE)
                    val labelIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.LABEL)

                    val address = it.getString(addressIndex)
                    val addressType = it.getInt(typeIndex)
                    var addressLabel = it.getString(labelIndex)

                    if (addressType != ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM) {
                        addressLabel = getAddressTypeLabel(addressType)
                    }

                    addressList.add(AddressModel(address, addressLabel))
                }
            }
            addressCursor?.close()

            return addressList
        }


        private fun getAddressTypeLabel(addressType: Int): String {
            return when (addressType) {
                ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME -> "Home"
                ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK -> "Work"
                ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM -> "Custom"
                ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER -> "Other"
                else -> "Unknown"
            }
        }
    }
}