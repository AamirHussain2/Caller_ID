package com.example.diallerapp.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.provider.ContactsContract
import android.util.Log
import java.io.ByteArrayOutputStream

class UpdateContactData {
    companion object {

        fun updateContact(
            contentResolver: ContentResolver,
            contactId: String,
            contactProfilePic: Bitmap?,
            firstName: String,
            sureName: String,
            companyName: String,
            phoneNumber: String,
            phoneLabel: String,
            email: String,
            emailLabel: String,
            birthdayDatePicker: String,
            birthdayLabel: String,
            address: String,
            addressLabel: String,
            labelName: List<String>
        ) {
            val where = "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?"
            val whereArgs = { mimetype: String -> arrayOf(contactId.toString(), mimetype) }

            updateField(contentResolver, where, whereArgs(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)) {
                put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName)
                put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, sureName)
            }

            updateField(contentResolver, where, whereArgs(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)) {
                put(ContactsContract.CommonDataKinds.Organization.COMPANY, companyName)
            }

            updateField(contentResolver, where, whereArgs(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
                put(ContactsContract.CommonDataKinds.Phone.TYPE, mapPhoneLabelToType(phoneLabel))
            }

            updateField(contentResolver, where, whereArgs(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                put(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
                put(ContactsContract.CommonDataKinds.Email.TYPE, mapEmailLabelToType(emailLabel))
            }

            updateField(contentResolver, where, whereArgs(ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)) {
                put(ContactsContract.CommonDataKinds.Event.START_DATE, birthdayDatePicker)
                put(ContactsContract.CommonDataKinds.Event.TYPE, mapBirthdayLabelToType(birthdayLabel))
            }

            updateField(contentResolver, where, whereArgs(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)) {
                put(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, address)
                put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, mapAddressLabelToType(addressLabel))
            }

            updateField(contentResolver, where, whereArgs(ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)) {
                put(ContactsContract.CommonDataKinds.Note.NOTE, labelName.joinToString(", "))
            }

            // Update Profile Picture
            if (contactProfilePic != null) {
                updateField(contentResolver, where, whereArgs(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)) {
                    put(ContactsContract.CommonDataKinds.Photo.PHOTO, convertImageToByteArray(contactProfilePic))
                }
            }
        }

        // ✅ Function to Update Only if Existing Data is Found
        private fun updateField(contentResolver: ContentResolver, where: String, whereArgs: Array<String>, valuesBuilder: ContentValues.() -> Unit) {
            val values = ContentValues().apply(valuesBuilder)
            val rowsUpdated = contentResolver.update(ContactsContract.Data.CONTENT_URI, values, where, whereArgs)

            if (rowsUpdated > 0) {
                Log.d("UpdateContactData", "Updated: ${whereArgs[1]}")
            } else {
                Log.e("UpdateContactData", "Update Failed (No Match Found): ${whereArgs[1]}")
            }
        }

        private fun convertImageToByteArray(image: Bitmap): ByteArray {
            val outputStream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            return outputStream.toByteArray()
        }

        private fun mapPhoneLabelToType(phoneLabel: String): Int {
            return when (phoneLabel) {
                "Mobile" -> ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                "Home" -> ContactsContract.CommonDataKinds.Phone.TYPE_HOME
                "Work" -> ContactsContract.CommonDataKinds.Phone.TYPE_WORK
                "Home fax" -> ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME
                "Work fax" -> ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK
                "Pager" -> ContactsContract.CommonDataKinds.Phone.TYPE_PAGER
                "Other" -> ContactsContract.CommonDataKinds.Phone.TYPE_OTHER
                "Custom" -> ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM
                else -> ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM
            }
        }

        private fun mapEmailLabelToType(emailLabel: String): Int {
            return when (emailLabel) {
                "Home" -> ContactsContract.CommonDataKinds.Email.TYPE_HOME
                "Work" -> ContactsContract.CommonDataKinds.Email.TYPE_WORK
                "Other" -> ContactsContract.CommonDataKinds.Email.TYPE_OTHER
                "Custom" -> ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM
                else -> ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM
            }
        }

        private fun mapBirthdayLabelToType(birthdayLabel: String): Int {
            return when (birthdayLabel) {
                "Birthday" -> ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY
                "Anniversary" -> ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY
                "Other" -> ContactsContract.CommonDataKinds.Event.TYPE_OTHER
                "Custom" -> ContactsContract.CommonDataKinds.Event.TYPE_CUSTOM
                else -> ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM
            }
        }

        private fun mapAddressLabelToType(addressLabel: String): Int {
            return when (addressLabel) {
                "Home" -> ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME
                "Work" -> ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK
                "Other" -> ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER
                "Custom" -> ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM
                else -> ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM
            }
        }
    }
}
