package com.example.diallerapp.utils

import android.telecom.Call
import android.telecom.InCallService
import com.example.diallerapp.activities.CallActivity.Companion.start

class CallService : InCallService() {
    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        OngoingCall.call = call
        start(this, call)
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        OngoingCall.call = null
    }
}