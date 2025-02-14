package com.example.diallerapp.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import com.example.diallerapp.model.uicreatecontact.AddressModel
import com.example.diallerapp.model.uicreatecontact.BirthdayModel
import com.example.diallerapp.model.uicreatecontact.EmailModel
import com.example.diallerapp.model.uicreatecontact.PhoneModel
import java.io.ByteArrayOutputStream

class UpdateContactData {
    companion object {

        fun updateContact(
            context: Context,
            contentResolver: ContentResolver,
            contactId: String,
            contactProfilePic: Bitmap?,
            firstName: String,
            sureName: String,
            companyName: String,
            phoneList: List<PhoneModel>,
            emailList: List<EmailModel>,
            addressList: List<AddressModel>,
            birthdayList: List<BirthdayModel>,
            labelName: List<String>
        ) {
            try {
                val rawContactId = getRawContactId(contentResolver, contactId) ?: return

                // set name and sureName
                val nameValues = ContentValues().apply {
                    put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName)
                    put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, sureName)
                }
                updateField(
                    contentResolver,
                    rawContactId,
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                    nameValues
                )

                //set company
                val companyValues = ContentValues().apply {
                    put(ContactsContract.CommonDataKinds.Organization.COMPANY, companyName)
                }
                updateField(
                    contentResolver,
                    rawContactId,
                    ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE,
                    companyValues
                )

                // set phone
                insertOrUpdatePhone(contentResolver, rawContactId, phoneList)
                //set email
                insertOrUpdateEmail(contentResolver, rawContactId, emailList)
                //set address
                insertOrUpdateAddress(contentResolver, rawContactId, addressList)
                //set birthday
                insertOrUpdateBirthday(contentResolver, rawContactId, birthdayList)

                // set label name (Groups)
                updateLabelField(contentResolver, rawContactId, labelName)

                // set profile pic
                if (contactProfilePic != null) {
                    val profilePicValues = ContentValues().apply {
                        put(
                            ContactsContract.CommonDataKinds.Photo.PHOTO,
                            convertImageToByteArray(contactProfilePic)
                        )
                    }
                    updateField(
                        contentResolver,
                        rawContactId,
                        ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE,
                        profilePicValues
                    )
                }

                contentResolver.notifyChange(ContactsContract.Contacts.CONTENT_URI, null)
                Toast.makeText(context, "Contact Updated", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                Toast.makeText(context, "Error updating contact: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        private fun updateLabelField(
            contentResolver: ContentResolver,
            rawContactId: String,
            labelName: List<String>
        ) {
            for (label in labelName) {
                val groupId = getGroupId(contentResolver, label)
                if (groupId != -1L) {
                    val selection =
                        "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?"
                    val selectionArgs = arrayOf(
                        rawContactId,
                        ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE
                    )

                    val groupValues = ContentValues().apply {
                        put(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID, groupId)
                    }

                    val rowsUpdated = contentResolver.update(
                        ContactsContract.Data.CONTENT_URI,
                        groupValues,
                        selection,
                        selectionArgs
                    )

                    if (rowsUpdated == 0) {

                        groupValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                        groupValues.put(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE
                        )
                        contentResolver.insert(ContactsContract.Data.CONTENT_URI, groupValues)
                    }
                }
            }
        }

        private fun getGroupId(
            contentResolver: ContentResolver,
            groupName: String
        ): Long {
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

        private fun insertOrUpdatePhone(
            contentResolver: ContentResolver,
            rawContactId: String,
            phoneList: List<PhoneModel>
        ) {
            val existingNumbers = mutableListOf<String>()
            val cursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                arrayOf(rawContactId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE),
                null
            )

            cursor?.use {
                while (it.moveToNext()) {
                    existingNumbers.add(it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)))
                }
            }

            // Delete removed phone numbers
            for (existingNumber in existingNumbers) {

                if (phoneList.none { it.phoneNumber == existingNumber }) {

                    val deleteSelection =
                        "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ? AND ${ContactsContract.CommonDataKinds.Phone.NUMBER} = ?"

                    val deleteArgs = arrayOf(
                        rawContactId,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                        existingNumber
                    )
                    contentResolver.delete(
                        ContactsContract.Data.CONTENT_URI,
                        deleteSelection,
                        deleteArgs
                    )
                }
            }

            // Insert or update phone numbers
            for (phone in phoneList) {

                val selection =
                    "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ? AND ${ContactsContract.CommonDataKinds.Phone.NUMBER} = ?"

                val selectionArgs = arrayOf(
                    rawContactId,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                    phone.phoneNumber
                )

                val values = ContentValues().apply {
                    put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone.phoneNumber)
                    put(
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        mapPhoneLabelToType(phone.phoneLabel)
                    )
                }

                val updateCursor = contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    arrayOf(ContactsContract.Data._ID),
                    selection,
                    selectionArgs,
                    null
                )
                if (updateCursor != null && updateCursor.moveToFirst()) {
                    contentResolver.update(
                        ContactsContract.Data.CONTENT_URI,
                        values,
                        selection,
                        selectionArgs
                    )
                } else {
                    values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId.toLong())
                    values.put(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    )
                    contentResolver.insert(ContactsContract.Data.CONTENT_URI, values)
                }
                updateCursor?.close()
            }
        }

        private fun insertOrUpdateEmail(
            contentResolver: ContentResolver,
            rawContactId: String,
            emailList: List<EmailModel>
        ) {
            val existingEmail = mutableListOf<String>()

            val cursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS),
                "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                arrayOf(rawContactId, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE),
                null
            )

            cursor?.use {
                while (it.moveToNext()) {
                    existingEmail.add(it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS)))
                }
            }

            // Delete removed email
            for (existingNumber in existingEmail) {
                if (emailList.none { it.email == existingNumber }) {

                    val deleteSelection =
                        "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ? AND ${ContactsContract.CommonDataKinds.Email.ADDRESS} = ?"

                    val deleteArgs = arrayOf(
                        rawContactId,
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                        existingNumber
                    )
                    contentResolver.delete(
                        ContactsContract.Data.CONTENT_URI,
                        deleteSelection,
                        deleteArgs
                    )
                }
            }

            for (email in emailList) {

                val selection =
                    "${ContactsContract.Data.RAW_CONTACT_ID} = ?" +
                            " AND ${ContactsContract.Data.MIMETYPE} = ?" +
                            " AND ${ContactsContract.CommonDataKinds.Email.ADDRESS} = ?"

                val selectionArgs = arrayOf(
                    rawContactId,
                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                    email.email
                )

                val values = ContentValues().apply {
                    put(ContactsContract.CommonDataKinds.Email.ADDRESS, email.email)
                    put(
                        ContactsContract.CommonDataKinds.Email.TYPE,
                        mapEmailLabelToType(email.emailLabel)
                    )
                }

                val updateCursor = contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    arrayOf(ContactsContract.Data._ID),
                    selection,
                    selectionArgs,
                    null
                )
                if (updateCursor != null && updateCursor.moveToFirst()) {
                    contentResolver.update(
                        ContactsContract.Data.CONTENT_URI,
                        values,
                        selection,
                        selectionArgs
                    )
                } else {
                    values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId.toLong())
                    values.put(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
                    )
                    contentResolver.insert(ContactsContract.Data.CONTENT_URI, values)
                }
                updateCursor?.close()

            }
        }

        private fun insertOrUpdateAddress(
            contentResolver: ContentResolver,
            rawContactId: String,
            addressList: List<AddressModel>
        ) {

            val existingAddress = mutableListOf<String>()
            val cursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS),
                "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                arrayOf(
                    rawContactId,
                    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE
                ),
                null
            )

            cursor?.use {
                while (it.moveToNext()) {
                    existingAddress.add(it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)))
                }
            }

            // Delete removed address
            for (existingNumber in existingAddress) {
                if (addressList.none { it.address == existingNumber }) {

                    val deleteSelection =
                        "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ? AND ${ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS} = ?"

                    val deleteArgs = arrayOf(
                        rawContactId,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE,
                        existingNumber
                    )
                    contentResolver.delete(
                        ContactsContract.Data.CONTENT_URI,
                        deleteSelection,
                        deleteArgs
                    )
                }
            }

            for (address in addressList) {

                val selection =
                    "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ? AND ${ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS} = ?"
                val selectionArgs = arrayOf(
                    rawContactId,
                    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE,
                    address.address
                )

                val values = ContentValues().apply {
                    put(
                        ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
                        address.address
                    )
                    put(
                        ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
                        mapAddressLabelToType(address.addressLabel)
                    )
                }

                val updateCursor = contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    arrayOf(ContactsContract.Data._ID),
                    selection,
                    selectionArgs,
                    null
                )
                if (updateCursor != null && updateCursor.moveToFirst()) {
                    contentResolver.update(
                        ContactsContract.Data.CONTENT_URI,
                        values,
                        selection,
                        selectionArgs
                    )
                } else {
                    values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId.toLong())
                    values.put(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE
                    )
                    contentResolver.insert(ContactsContract.Data.CONTENT_URI, values)
                }
                updateCursor?.close()
            }
        }

        private fun insertOrUpdateBirthday(
            contentResolver: ContentResolver,
            rawContactId: String,
            birthdayList: List<BirthdayModel>
        ) {

            val existingBirthday = mutableListOf<String>()

            val cursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Event.START_DATE),
                "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                arrayOf(rawContactId, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE),
                null
            )

            cursor?.use {
                while (it.moveToNext()) {
                    existingBirthday.add(it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Event.START_DATE)))
                }
            }

            // Delete removed birthday
            for (existingNumber in existingBirthday) {

                if (birthdayList.none { it.birthdayDatePicker == existingNumber }) {

                    val deleteSelection =
                        "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ? AND ${ContactsContract.CommonDataKinds.Event.START_DATE} = ?"

                    val deleteArgs = arrayOf(
                        rawContactId,
                        ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
                        existingNumber
                    )
                    contentResolver.delete(
                        ContactsContract.Data.CONTENT_URI,
                        deleteSelection,
                        deleteArgs
                    )
                }
            }


            for (birthday in birthdayList) {

                val selection =
                    "${ContactsContract.Data.RAW_CONTACT_ID} = ?" +
                            " AND ${ContactsContract.Data.MIMETYPE} = ?" +
                            " AND ${ContactsContract.CommonDataKinds.Event.START_DATE} = ?"

                val selectionArgs = arrayOf(
                    rawContactId,
                    ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
                    birthday.birthdayDatePicker
                )

                val values = ContentValues().apply {
                    put(
                        ContactsContract.CommonDataKinds.Event.START_DATE,
                        birthday.birthdayDatePicker
                    )
                    put(
                        ContactsContract.CommonDataKinds.Event.TYPE,
                        mapBirthdayLabelToType(birthday.birthdayLabel)
                    )
                }

                val updateCursor = contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    arrayOf(ContactsContract.Data._ID),
                    selection,
                    selectionArgs,
                    null
                )
                if (updateCursor != null && updateCursor.moveToFirst()) {
                    contentResolver.update(
                        ContactsContract.Data.CONTENT_URI,
                        values,
                        selection,
                        selectionArgs
                    )
                } else {
                    values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId.toLong())
                    values.put(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE
                    )
                    contentResolver.insert(ContactsContract.Data.CONTENT_URI, values)
                }
                updateCursor?.close()


            }
        }

        private fun getRawContactId(
            contentResolver: ContentResolver,
            contactId: String
        ): String? {
            val projection = arrayOf(ContactsContract.RawContacts._ID)
            val selection = "${ContactsContract.RawContacts.CONTACT_ID} = ?"
            val selectionArgs = arrayOf(contactId)

            val cursor = contentResolver.query(
                ContactsContract.RawContacts.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
            )
            return if (cursor != null && cursor.moveToFirst()) {
                val rawContactId = cursor.getString(0)
                cursor.close()
                rawContactId
            } else {
                cursor?.close()
                null
            }
        }

        private fun updateField(
            contentResolver: ContentResolver,
            rawContactId: String,
            mimeType: String,
            values: ContentValues
        ) {
            val selection =
                "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?"
            val args = arrayOf(rawContactId, mimeType)
            val rowsUpdated =
                contentResolver.update(ContactsContract.Data.CONTENT_URI, values, selection, args)

            if (rowsUpdated == 0) {
                values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId.toLong())
                values.put(ContactsContract.Data.MIMETYPE, mimeType)
                contentResolver.insert(ContactsContract.Data.CONTENT_URI, values)
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
