package com.example.diallerapp.viewmodel

import android.content.ContentResolver
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diallerapp.model.ContactModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream


class ContactsViewModel : ViewModel() {

    private val _contacts = MutableLiveData<List<ContactModel>>()
    val contacts: LiveData<List<ContactModel>> get() = _contacts

    fun fetchContacts(contentResolver: ContentResolver) {
        viewModelScope.launch {
            val contactList = fetchContactsFromPhoneList(contentResolver)

            _contacts.postValue(contactList)
        }
    }

    private suspend fun fetchContactsFromPhoneList(
        contentResolver: ContentResolver
    ): List<ContactModel> {
        return withContext(Dispatchers.IO) {
            val contactList = mutableListOf<ContactModel>()
            val contactMap = mutableMapOf<Long, MutableList<String>>() // To store multiple numbers for a contact
            var cursor: Cursor? = null

            try {
                cursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                        ContactsContract.Contacts.PHOTO_URI
                    ),
                    null,
                    null,
                    null
                )

                if (cursor == null) {
                    Log.e("ContactsViewModel", "Cursor is null. No contacts found.")
                    return@withContext contactList
                }

                val displayNameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val phoneNumberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val contactIdIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val photoIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)

                if (displayNameIndex == -1 || phoneNumberIndex == -1 || contactIdIndex == -1) {
                    Log.e("ContactsViewModel", "One or more column indexes are invalid.")
                    return@withContext contactList
                }

                while (cursor.moveToNext()) {
                    val name = cursor.getString(displayNameIndex) ?: ""
                    val phoneNumber = cursor.getString(phoneNumberIndex) ?: ""
                    val contactId = cursor.getLong(contactIdIndex)
                    val photoUri = cursor.getString(photoIndex)

                    // Check if this contactId already exists in map
                    if (contactMap.containsKey(contactId)) {
                        // If contact exists, just add the new phone number to list
                        contactMap[contactId]?.add(phoneNumber)
                    } else {
                        // If contact does not exist, create a new entry and add to list
                        contactMap[contactId] = mutableListOf(phoneNumber)

                        // Process and store contact photo
                        val contactImage: ByteArray? = photoUri?.let {
                            val imageStream = contentResolver.openInputStream(Uri.parse(it))
                            val bitmap = BitmapFactory.decodeStream(imageStream)
                            val byteArrayOutputStream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                            imageStream?.close()
                            byteArrayOutputStream.toByteArray()
                        }

                        // Add only one phone number (first occurrence) to UI list
                        contactList.add(ContactModel(name, phoneNumber, contactId, contactImage))
                    }
                }
            } catch (e: Exception) {
                Log.e("ContactsViewModel", "Error fetching contacts: ${e.message}")
            } finally {
                cursor?.close()
            }

            return@withContext contactList
        }
    }


}
