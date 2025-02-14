package com.example.diallerapp.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.ContactsContract
import android.widget.Toast
import com.example.diallerapp.model.uicreatecontact.AddressModel
import com.example.diallerapp.model.uicreatecontact.BirthdayModel
import com.example.diallerapp.model.uicreatecontact.EmailModel
import com.example.diallerapp.model.uicreatecontact.PhoneModel
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
            phoneList: List<PhoneModel>,
            emailList: List<EmailModel>,
            addressList: List<AddressModel>,
            birthdayList: List<BirthdayModel>,
            labelName: List<String>
        ) {
            val resolver: ContentResolver = contentResolver
            val values = ContentValues()

            try {
                val rawContactUri: Uri? = resolver.insert(ContactsContract.RawContacts.CONTENT_URI, values)
                val rawContactId: Long = rawContactUri?.lastPathSegment?.toLong() ?: -1

                // Insert Name
                val nameValues = ContentValues().apply {
                    put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                    put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName)
                    put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, sureName)
                }
                resolver.insert(ContactsContract.Data.CONTENT_URI, nameValues)

                // Insert Company Name
                val companyValues = ContentValues().apply {
                    put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                    put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    put(ContactsContract.CommonDataKinds.Organization.COMPANY, companyName)
                }
                resolver.insert(ContactsContract.Data.CONTENT_URI, companyValues)

                // Insert Multiple Phone Numbers
                for (phone in phoneList) {
                    val phoneValues = ContentValues().apply {
                        put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                        put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone.phoneNumber)
                        put(ContactsContract.CommonDataKinds.Phone.TYPE, mapPhoneLabelToType(phone.phoneLabel))
                    }
                    resolver.insert(ContactsContract.Data.CONTENT_URI, phoneValues)
                }

                // Insert Multiple Emails
                for (email in emailList) {
                    val emailValues = ContentValues().apply {
                        put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                        put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        put(ContactsContract.CommonDataKinds.Email.ADDRESS, email.email)
                        put(ContactsContract.CommonDataKinds.Email.TYPE, mapEmailLabelToType(email.emailLabel))
                    }
                    resolver.insert(ContactsContract.Data.CONTENT_URI, emailValues)
                }

                // Insert Multiple Birthdays
                for (birthday in birthdayList) {
                    val birthdayValues = ContentValues().apply {
                        put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                        put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)
                        put(ContactsContract.CommonDataKinds.Event.START_DATE, birthday.birthdayDatePicker)
                        put(ContactsContract.CommonDataKinds.Event.TYPE, mapBirthdayLabelToType(birthday.birthdayLabel))
                    }
                    resolver.insert(ContactsContract.Data.CONTENT_URI, birthdayValues)
                }

                // Insert Multiple Addresses
                for (address in addressList) {
                    val addressValues = ContentValues().apply {
                        put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                        put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                        put(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, address.address)
                        put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, mapAddressLabelToType(address.addressLabel))
                    }
                    resolver.insert(ContactsContract.Data.CONTENT_URI, addressValues)
                }

                // Insert label Name
                for (label in labelName) {
                    val groupValues = ContentValues().apply {
                        put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                        put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE)
                        put(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID, getGroupId(context, contentResolver, label))
                    }
                    resolver.insert(ContactsContract.Data.CONTENT_URI, groupValues)
                }


                // Insert Profile Picture
                if (contactProfilePic != null) {
                    val profilePicValues = ContentValues().apply {
                        put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                        put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                        put(ContactsContract.CommonDataKinds.Photo.PHOTO, convertImageToByteArray(contactProfilePic))
                    }
                    resolver.insert(ContactsContract.Data.CONTENT_URI, profilePicValues)
                }

                Toast.makeText(context, "Contact Saved", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                Toast.makeText(context, "Error saving contact: $e", Toast.LENGTH_SHORT).show()
            }
        }

        private fun getGroupId(context: Context, contentResolver: ContentResolver, groupName: String): Long {
            val cursor = contentResolver.query(
                ContactsContract.Groups.CONTENT_URI,
                arrayOf(ContactsContract.Groups._ID),
                "${ContactsContract.Groups.TITLE} = ?",
                arrayOf(groupName),
                null
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    return it.getLong(it.getColumnIndexOrThrow(ContactsContract.Groups._ID))
                }
            }

            // Group nahi mila, to naya create karo
            val values = ContentValues().apply {
                put(ContactsContract.Groups.TITLE, groupName)
                put(ContactsContract.Groups.GROUP_VISIBLE, 1)
            }
            val newGroupUri = contentResolver.insert(ContactsContract.Groups.CONTENT_URI, values)
            return newGroupUri?.lastPathSegment?.toLong() ?: -1
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

        private fun convertImageToByteArray(image: Bitmap): ByteArray {
            val outPutStream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, outPutStream)
            return outPutStream.toByteArray()
        }
    }
}