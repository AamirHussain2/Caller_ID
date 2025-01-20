package com.example.diallerapp.utils

import android.telecom.Call
import android.telecom.VideoProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.diallerapp.activities.CallActivity

object OngoingCall {

    // Use MutableStateFlow instead of BehaviorSubject
    private val _state = MutableStateFlow<Int?>(null)
    private val state: StateFlow<Int?> = _state

    private val callback = object : Call.Callback() {

        override fun onStateChanged(call: Call, newState: Int) {
            // Update the state in the flow
            _state.value = newState
        }
    }

    var call: Call? = null
        set(value) {
            // Unregister callback if previous call exists
            field?.unregisterCallback(callback)
            field = value
            value?.let {
                // Register the callback for the new call
                it.registerCallback(callback)
                // Emit initial state when the call is set
                _state.value = it.state
            }
        }

    suspend fun answer() {
        // Answer the call using the IO dispatcher
        withContext(Dispatchers.IO) {
            call?.answer(VideoProfile.STATE_AUDIO_ONLY)
        }
    }

    suspend fun hangup() {
        // Disconnect the call using the IO dispatcher
        withContext(Dispatchers.IO) {
            call?.disconnect()
        }
    }

    fun observeState(owner: LifecycleOwner, block: suspend (Int?) -> Unit) {
        // Use lifecycleScope to collect the state safely
        owner.lifecycleScope.launch {
            state.collect {
                block(it)
            }
        }
    }
}
