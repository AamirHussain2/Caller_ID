package com.example.diallerapp.viewmodel

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diallerapp.model.ContactModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
            var cursor: Cursor? = null

            try {
                cursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID // Use CONTACT_ID instead of _ID
                    ),
                    null,
                    null,
                    null
                )

                if (cursor == null) {
                    Log.e("ContactsViewModel", "Cursor is null. No contacts found.")
                    return@withContext contactList
                }

                // Ensure valid column indexes
                val displayNameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val phoneNumberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val contactIdIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID) // Use CONTACT_ID

                if (displayNameIndex == -1 || phoneNumberIndex == -1 || contactIdIndex == -1) {
                    Log.e("ContactsViewModel", "One or more column indexes are invalid.")
                    return@withContext contactList
                }

                while (cursor.moveToNext()) {
                    val name = cursor.getString(displayNameIndex) ?: "Unknown"
                    val phoneNumber = cursor.getString(phoneNumberIndex) ?: "No Number"
                    val contactId = cursor.getLong(contactIdIndex)

                    contactList.add(ContactModel(name, phoneNumber, contactId))
                }
            } catch (e: Exception) {
                Log.e("ContactsViewModel", "Error fetching contacts: ${e.message}")
            } finally {
                cursor?.close() // Always close cursor to prevent memory leaks
            }

            return@withContext contactList
        }
    }

}
