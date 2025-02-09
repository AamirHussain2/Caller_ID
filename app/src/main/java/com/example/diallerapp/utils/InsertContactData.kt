package com.example.diallerapp.utils

import android.accounts.Account
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import java.io.ByteArrayOutputStream

class InsertContactData {

    companion object{
        fun insertContact(
            context: Context,
            contentResolver: ContentResolver,
            contactProfilePic: Bitmap?,
            firstName: String = "",
            sureName: String = "",
            companyName: String = "",
            phoneNumber: String = "",
            phoneLabel: String = "",
            email: String = "",
            emailLabel: String = "",
            birthdayDatePicker: String = "",
            birthdayLabel: String = "",
            address: String = "",
            addressLabel: String = "",
            labelName: List<String>
        ) {

//            if (firstName.isBlank() || phoneNumber.isBlank()) {
//                Toast.makeText(context, "Name and Number are required!", Toast.LENGTH_SHORT).show()
//                return
//            }

            val resolver: ContentResolver = contentResolver

            val values = ContentValues()

            try {
                val rawContactUri: Uri? = resolver.insert(ContactsContract.RawContacts.CONTENT_URI, values)
                val rawContactId: Long = rawContactUri?.lastPathSegment?.toLong() ?: -1

                // Insert Name
                val nameValues = ContentValues().apply {
                    put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                    put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName)  // First Name
                    put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, sureName)  // Last Name (Surname)
                }
                resolver.insert(ContactsContract.Data.CONTENT_URI, nameValues)

                // Add organization (company name)
                    val companyValues = ContentValues().apply {
                        put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                        put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                        put(ContactsContract.CommonDataKinds.Organization.COMPANY, companyName)
                    }
                    resolver.insert(ContactsContract.Data.CONTENT_URI, companyValues)

                // Add label
                val labelValues = ContentValues().apply {
                    val labelText = labelName.joinToString(", ") // Convert list to comma-separated text

                    put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                    put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
                    put(ContactsContract.CommonDataKinds.Note.NOTE, labelText )
                }
                resolver.insert(ContactsContract.Data.CONTENT_URI, labelValues)
                Log.d("SaveData", "Note added: This is a custom note for the contact.")


                // Insert Number
                val phoneValues = ContentValues().apply {
                    put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                    put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
                    put(ContactsContract.CommonDataKinds.Phone.TYPE, mapPhoneLabelToType(phoneLabel))
                }
                resolver.insert(ContactsContract.Data.CONTENT_URI, phoneValues)

                // Insert Email
                    val emailValues = ContentValues().apply {
                        put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                        put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        put(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
                        put(ContactsContract.CommonDataKinds.Email.TYPE, mapEmailLabelToType(emailLabel))
                    }
                    resolver.insert(ContactsContract.Data.CONTENT_URI, emailValues)
                    Log.d("SaveData", "Email added: ${mapEmailLabelToType(emailLabel)}")


                // Insert Birthday
                    val birthdayValues = ContentValues().apply {
                        put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                        put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)
                        put(ContactsContract.CommonDataKinds.Event.START_DATE, birthdayDatePicker)
                        put(ContactsContract.CommonDataKinds.Event.TYPE, mapBirthdayLabelToType(birthdayLabel))
                    }
                    resolver.insert(ContactsContract.Data.CONTENT_URI, birthdayValues)


                // Insert Address
                    val addressValues = ContentValues().apply {
                        put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                        put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                        put(ContactsContract.CommonDataKinds.StructuredPostal.CITY, address)
                        put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, mapAddressLabelToType(addressLabel))
                    }
                    resolver.insert(ContactsContract.Data.CONTENT_URI, addressValues)
                    Log.d("SaveData", "Address added: ${mapAddressLabelToType(addressLabel)}")


                // Insert ProfilePic
                if (contactProfilePic.toString().isNotBlank()) {
                    val profilePicValues = ContentValues().apply {
                        put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                        put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                        put(ContactsContract.CommonDataKinds.Photo.PHOTO,
                            contactProfilePic?.let { convertImageToByteArray(it) })
                    }
                    resolver.insert(ContactsContract.Data.CONTENT_URI, profilePicValues)
                }
                Toast.makeText(context, "Contact Saved", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error saving contact: $e", Toast.LENGTH_SHORT).show()
            }
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

        private fun saveToAccounts(accountType: String, context: Context): Account? {
            val accounts = AccountManager.get(context).getAccountsByType(accountType)
            return if (accounts.isNotEmpty()) {
                Log.d("saveToAccounts", "saveToAccounts: ${accounts[0]}")
                accounts[0]
            } else {
                null
            }
        }

        private fun convertImageToByteArray(image: Bitmap): ByteArray {
            val outPutStream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, outPutStream)
            return outPutStream.toByteArray()
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        fun convertDrawableResToBitmap(context: Context, drawableRes: Int): Bitmap {
            val drawable = context.getDrawable(drawableRes)!!
            val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 1
            val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 1

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }
    }
}