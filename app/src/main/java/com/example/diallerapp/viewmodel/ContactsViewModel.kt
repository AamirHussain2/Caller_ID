package com.example.diallerapp.viewmodel

import android.content.ContentResolver
import android.database.Cursor
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


class ContactsViewModel : ViewModel() {

    private val _contacts = MutableLiveData<List<ContactModel>>()
    val contacts: LiveData<List<ContactModel>> get() = _contacts

    fun fetchContacts(contentResolver: ContentResolver) {
        viewModelScope.launch {
            val contactList = fetchContactsFromPhoneList(contentResolver)
            Log.d("ContactsViewModel", "Contacts fetched: $contactList\n")
            _contacts.postValue(contactList)
        }
    }

    private suspend fun fetchContactsFromPhoneList(
        contentResolver: ContentResolver
    ): List<ContactModel> {
        return withContext(Dispatchers.IO) {
            val contactList = mutableListOf<ContactModel>()
            val cursor: Cursor? = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
            )

            cursor?.let {
                val displayNameIndex =
                    it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val phoneNumberIndex =
                    it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                // Ensure column indexes are valid (not -1)
                if (displayNameIndex != -1 && phoneNumberIndex != -1) {
                    while (it.moveToNext()) {
                        val name = it.getString(displayNameIndex)
                        val phoneNumber = it.getString(phoneNumberIndex)
                        contactList.add(ContactModel(name, phoneNumber))
                    }
                }
                it.close()
            }
            contactList
        }
    }
}
