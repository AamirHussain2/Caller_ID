package com.example.diallerapp.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.widget.Toast
import com.example.diallerapp.model.uicreatecontact.AddressModel
import com.example.diallerapp.model.uicreatecontact.BirthdayModel
import com.example.diallerapp.model.uicreatecontact.EmailModel
import com.example.diallerapp.model.uicreatecontact.PhoneModel

class SaveContactData {

    companion object{

        fun insertOrUpdateContact(
            context: Context,
            contentResolver: ContentResolver,
            contactId: String?,
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
            if (contactId.isNullOrEmpty()) {
                Log.d("contId", "if-> Contact ID is empty, inserting new contact: $contactId")
                Log.d("contId", "phone list: $phoneList")
                Log.d("TESTING", "insertContact...: $phoneList")

                // Insert new contact with lists
                InsertContactData.insertContact(
                    context = context,
                    contentResolver = contentResolver,
                    contactProfilePic = contactProfilePic,
                    firstName = firstName,
                    sureName = sureName,
                    companyName = companyName,
                    phoneList = phoneList,
                    emailList = emailList,
                    addressList = addressList,
                    birthdayList = birthdayList,
                    labelName = labelName
                )
                Toast.makeText(context, "New Contact Saved", Toast.LENGTH_SHORT).show()

            } else {
                Log.d("contId", "else-> Updating contact with ID: $contactId")
                Log.d("contId", "phone list: $phoneList")
                Log.d("TESTING", "updateContact...: $phoneList")


                // Update existing contact with lists
                UpdateContactData.updateContact(
                    context = context,
                    contactId = contactId,
                    contentResolver = contentResolver,
                    contactProfilePic = contactProfilePic,
                    firstName = firstName,
                    sureName = sureName,
                    companyName = companyName,
                    phoneList = phoneList,
                    emailList = emailList,
                    addressList = addressList,
                    birthdayList = birthdayList,
                    labelName = labelName
                )
                Toast.makeText(context, "Contact Updated", Toast.LENGTH_SHORT).show()
            }
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        fun convertDrawableResToBitmap(context: Context, drawableRes: Int): Bitmap? {
            val drawable = context.getDrawable(drawableRes) ?: return null
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