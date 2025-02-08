package com.example.diallerapp.utils

import android.content.Context
import android.provider.ContactsContract
import com.example.diallerapp.model.uicreatecontact.AddressModel
import com.example.diallerapp.model.uicreatecontact.BirthdayModel

class GetBirthdayData {

    companion object {


        fun getBirthdayAndLabelFromPhoneNumber(context: Context, contactId: String?): List<BirthdayModel> {

            return getBirthdayAndLabelFromContactId(context, contactId)

        }

        private fun getBirthdayAndLabelFromContactId(context: Context, contactId: String?): List<BirthdayModel> {
            if (contactId == null) return emptyList() // Agar ID nahi mili toh empty list return karo

            val birthdayList = mutableListOf<BirthdayModel>()
            val contentResolver = context.contentResolver

            val eventUri = ContactsContract.Data.CONTENT_URI
            val eventProjection = arrayOf(
                ContactsContract.CommonDataKinds.Event.START_DATE,
                ContactsContract.CommonDataKinds.Event.TYPE,
                ContactsContract.CommonDataKinds.Event.LABEL
            )

            val eventCursor = contentResolver.query(
                eventUri,
                eventProjection,
                "${ContactsContract.CommonDataKinds.Event.CONTACT_ID} = ? AND ${ContactsContract.CommonDataKinds.Event.MIMETYPE} = ?",
                arrayOf(contactId, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE),
                null
            )

            eventCursor?.use {
                while (it.moveToNext()) {
                    val dateIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE)
                    val typeIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Event.TYPE)
                    val labelIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Event.LABEL)

                    val birthdayDate = it.getString(dateIndex) ?: "Unknown Date"
                    val eventType = it.getInt(typeIndex)
                    var eventLabel = it.getString(labelIndex)

                    if (eventType != ContactsContract.CommonDataKinds.Event.TYPE_CUSTOM) {
                        eventLabel = getEventTypeLabel(eventType)
                    }

                    birthdayList.add(BirthdayModel(birthdayDate, eventLabel))
                }
            }
            eventCursor?.close()

            return birthdayList
        }
        private fun getEventTypeLabel(eventType: Int): String {
            return when (eventType) {
                ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY -> "Birthday"
                ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY -> "Anniversary"
                ContactsContract.CommonDataKinds.Event.TYPE_OTHER -> "Other"
                else -> "Unknown"
            }
        }

    }
}