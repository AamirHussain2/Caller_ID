package com.example.diallerapp.utils

import android.content.Context
import android.provider.ContactsContract
import com.example.diallerapp.model.uicreatecontact.EmailModel

class GetEmailData {

    companion object{

        // Function to get Email and Label using Contact ID

        private fun getEmailAndLabelFromContactId(context: Context, contactId: String?): List<EmailModel> {
            if (contactId == null) return emptyList() // Agar ID nahi mili to empty list return karo

            val emailList = mutableListOf<EmailModel>()
            val contentResolver = context.contentResolver

            val emailUri = ContactsContract.CommonDataKinds.Email.CONTENT_URI
            val emailProjection = arrayOf(
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.TYPE,
                ContactsContract.CommonDataKinds.Email.LABEL
            )

            val emailCursor = contentResolver.query(
                emailUri,
                emailProjection,
                "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} = ?",
                arrayOf(contactId),
                null
            )

            emailCursor?.use {
                while (it.moveToNext()) {
                    val emailIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
                    val typeIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE)
                    val labelIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL)

                    val email = it.getString(emailIndex)
                    val emailType = it.getInt(typeIndex)
                    var emailLabel = it.getString(labelIndex)

                    emailLabel = if (emailType != ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM) {
                        getEmailTypeLabel(emailType)
                    }else{
                        "Custom"
                    }

                    emailList.add(EmailModel(email, emailLabel ?: "Unknown"))
                }
            }
            emailCursor?.close()

            return emailList
        }


        // Function to get Email and Label directly from Phone Number (Combines both functions)

        fun getEmailAndLabelFromPhoneNumber(context: Context, contactId: String?): List<EmailModel> {
            return getEmailAndLabelFromContactId(context, contactId)
        }

        // Function to get Email Type Label
        private fun getEmailTypeLabel(emailType: Int): String {
            return when (emailType) {
                ContactsContract.CommonDataKinds.Email.TYPE_HOME -> "Home"
                ContactsContract.CommonDataKinds.Email.TYPE_WORK -> "Work"
                ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM -> "Custom"
                ContactsContract.CommonDataKinds.Email.TYPE_OTHER -> "Other"
                else -> "Unknown"
            }
        }
    }

}