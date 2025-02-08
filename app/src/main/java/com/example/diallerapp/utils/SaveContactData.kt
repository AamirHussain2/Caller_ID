package com.example.diallerapp.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.provider.ContactsContract
import android.widget.Toast

class SaveContactData {

    companion object{

        fun insertOrUpdateContact(
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
            val contactId = getContactIdByPhoneNumber(contentResolver, phoneNumber)

            if (contactId != null) {
                // Contact exists, update it
                UpdateContactData.updateContact(
                    contentResolver = contentResolver,
                    contactId = contactId,
                    contactProfilePic = contactProfilePic,
                    firstName = firstName,
                    sureName = sureName,
                    phoneNumber = phoneNumber,
                    phoneLabel = phoneLabel,
                    email = email,
                    emailLabel = emailLabel,
                    birthdayDatePicker = birthdayDatePicker,
                    birthdayLabel = birthdayLabel,
                    address = address,
                    addressLabel = addressLabel,
                    labelName = labelName
                )
                Toast.makeText(context, "Contact Updated", Toast.LENGTH_SHORT).show()
            } else {
                // Contact does not exist, insert new
                InsertContactData.insertContact(
                    context = context,
                    contentResolver = contentResolver,
                    contactProfilePic = contactProfilePic,
                    firstName = firstName,
                    sureName = sureName,
                    companyName = companyName,
                    phoneNumber = phoneNumber,
                    phoneLabel = phoneLabel,
                    email = email,
                    emailLabel = emailLabel,
                    birthdayDatePicker = birthdayDatePicker,
                    birthdayLabel = birthdayLabel,
                    address = address,
                    addressLabel = addressLabel,
                    labelName = labelName
                )
                Toast.makeText(context, "New Contact Saved", Toast.LENGTH_SHORT).show()
            }
        }

        // Function to get Contact ID by Phone Number
        private fun getContactIdByPhoneNumber(contentResolver: ContentResolver, phoneNumber: String): Long? {
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Phone.CONTACT_ID),
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                arrayOf(phoneNumber),
                null
            )

            var contactId: Long? = null
            cursor?.use {
                if (it.moveToFirst()) {
                    contactId = it.getLong(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                }
            }
            cursor?.close()
            return contactId
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