package com.example.diallerapp.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.provider.ContactsContract
import java.io.ByteArrayOutputStream

class UpdateContactData {

    companion object{

        // Function to Update Contact
        fun updateContact(
            contentResolver: ContentResolver,
            contactId: Long,
            contactProfilePic: Bitmap?,
            firstName: String,
            sureName: String,
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
            val where = "${ContactsContract.Data.CONTACT_ID} = ?"
            val whereArgs = arrayOf(contactId.toString())

            // Update Name
            val nameValues = ContentValues().apply {
                put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName)
                put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, sureName)
            }
            contentResolver.update(ContactsContract.Data.CONTENT_URI, nameValues, where, whereArgs)

            // Update Phone Number
            val phoneValues = ContentValues().apply {
                put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
                put(ContactsContract.CommonDataKinds.Phone.TYPE, mapPhoneLabelToType(phoneLabel))
            }
            contentResolver.update(ContactsContract.Data.CONTENT_URI, phoneValues, where, whereArgs)

            // Update Email
            val emailValues = ContentValues().apply {
                put(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
                put(ContactsContract.CommonDataKinds.Email.TYPE, mapEmailLabelToType(emailLabel))
            }
            contentResolver.update(ContactsContract.Data.CONTENT_URI, emailValues, where, whereArgs)

            // Update Birthday
            val birthdayValues = ContentValues().apply {
                put(ContactsContract.CommonDataKinds.Event.START_DATE, birthdayDatePicker)
                put(ContactsContract.CommonDataKinds.Event.TYPE, mapBirthdayLabelToType(birthdayLabel))
            }
            contentResolver.update(ContactsContract.Data.CONTENT_URI, birthdayValues, where, whereArgs)

            // Update Address
            val addressValues = ContentValues().apply {
                put(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, address)
                put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, mapAddressLabelToType(addressLabel))
            }
            contentResolver.update(ContactsContract.Data.CONTENT_URI, addressValues, where, whereArgs)

            // Update Labels
            val labelValues = ContentValues().apply {
                put(ContactsContract.CommonDataKinds.Note.NOTE, labelName.joinToString(", "))
            }
            contentResolver.update(ContactsContract.Data.CONTENT_URI, labelValues, where, whereArgs)


            // Update Profile Picture
            if (contactProfilePic != null) {
                val profilePicValues = ContentValues().apply {
                    put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    put(ContactsContract.CommonDataKinds.Photo.PHOTO, convertImageToByteArray(contactProfilePic))
                }

                val rowsUpdated = contentResolver.update(
                    ContactsContract.Data.CONTENT_URI,
                    profilePicValues,
                    "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                    arrayOf(contactId.toString(), ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                )

                // Agar koi existing profile picture nahi hai to insert karein
                if (rowsUpdated == 0) {
                    val insertPicValues = ContentValues().apply {
                        put(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                        put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                        put(ContactsContract.CommonDataKinds.Photo.PHOTO, convertImageToByteArray(contactProfilePic))
                    }
                    contentResolver.insert(ContactsContract.Data.CONTENT_URI, insertPicValues)
                }
            }

        }

        private fun convertImageToByteArray(image: Bitmap): ByteArray {
            val outPutStream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, outPutStream)
            return outPutStream.toByteArray()
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