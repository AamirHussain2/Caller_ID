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
            if (contactId.isNullOrEmpty()) return emptyList() // Prevent null access

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

            if (addressCursor == null) {
                return emptyList() // Prevent crash if query fails
            }

            addressCursor.use { cursor ->
                while (cursor.moveToNext()) {
                    val addressIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)
                    val typeIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE)
                    val labelIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.LABEL)

                    if (addressIndex == -1 || typeIndex == -1 || labelIndex == -1) continue // Skip if columns are missing

                    val address = cursor.getString(addressIndex) ?: ""
                    val addressType = cursor.getInt(typeIndex)
                    var addressLabel = cursor.getString(labelIndex) ?: ""

                    if (addressType != ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM) {
                        addressLabel = getAddressTypeLabel(addressType)
                    }

                    addressList.add(AddressModel(address, addressLabel))
                }
            }
            addressCursor.close()

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