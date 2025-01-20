package com.example.diallerapp.viewmodel

import android.content.ContentResolver
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.CallLog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diallerapp.model.RecentModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecentViewModel : ViewModel() {

    private val _recentCallLogs = MutableLiveData<List<RecentModel>>()
    val recentCallLogs: LiveData<List<RecentModel>> get() = _recentCallLogs

    private var contentObserver: ContentObserver? = null

    // Start observing call log changes
    fun startObservingCallLogs(contentResolver: ContentResolver) {
        fetchRecentCallLogs(contentResolver) // Initial fetch

        // Register ContentObserver for real-time updates
        contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                fetchRecentCallLogs(contentResolver) // Fetch new call logs when data changes
            }
        }
        contentResolver.registerContentObserver(CallLog.Calls.CONTENT_URI, true, contentObserver!!)
    }

    // Stop observing to prevent memory leaks
    fun stopObservingCallLogs(contentResolver: ContentResolver) {
        contentObserver?.let { contentResolver.unregisterContentObserver(it) }
    }

    // Fetch recent call logs
    private fun fetchRecentCallLogs(contentResolver: ContentResolver) {
        viewModelScope.launch {
            val callLogs = getRecentCallLogs(contentResolver)
            _recentCallLogs.postValue(callLogs)
        }
    }

    // Suspend function to fetch call logs
    private suspend fun getRecentCallLogs(contentResolver: ContentResolver): List<RecentModel> {
        return withContext(Dispatchers.IO) {
            val recentList = mutableListOf<RecentModel>()

            // Query the CallLog content provider
            val cursor = contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                arrayOf(CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER),
                null,
                null,
                "${CallLog.Calls.DATE} DESC" // Sort by date descending
            )

            cursor?.use {
                val nameIndex = it.getColumnIndex(CallLog.Calls.CACHED_NAME)
                val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)

                if (nameIndex != -1 && numberIndex != -1) {
                    while (it.moveToNext()) {
                        val name = it.getString(nameIndex)?.takeIf { cachedName ->
                            cachedName.isNotEmpty() // Only take the name if it is not null or empty
                        } ?: "Unknown" // Set "Unknown" if the name is empty or null

                        val phoneNumber = it.getString(numberIndex)
                        recentList.add(RecentModel(name, phoneNumber))
                    }
                }
            }
            recentList
        }
    }
}
