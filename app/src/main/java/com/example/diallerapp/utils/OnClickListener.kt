package com.example.diallerapp.utils

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.diallerapp.activities.CallActivity
import com.example.diallerapp.databinding.ActivityCallBinding
import com.example.diallerapp.databinding.CustomRecievedUiBinding
import kotlinx.coroutines.launch

class OnClickListener {

    companion object {

        fun clickAnswer(binding: ActivityCallBinding, lifecycleScope: LifecycleCoroutineScope) {
            binding.answer.setOnClickListener {
                lifecycleScope.launch {
                    OngoingCall.answer() // Answer the call using coroutines
                }
            }
        }

        fun clickHangup(
            binding: ActivityCallBinding,
            lifecycleScope: LifecycleCoroutineScope
        ) {
            binding.hangup.setOnClickListener {
                lifecycleScope.launch {
                    OngoingCall.hangup() // Hang up using coroutines
                }
            }
        }

        fun clickAnswer(binding: CustomRecievedUiBinding, lifecycleScope: LifecycleCoroutineScope) {
            binding.answer.setOnClickListener {
                lifecycleScope.launch {
                    OngoingCall.answer() // Answer the call using coroutines
                }
            }
        }

        fun clickHangup(
            binding: CustomRecievedUiBinding,
            lifecycleScope: LifecycleCoroutineScope
        ) {
            binding.hangup.setOnClickListener {
                lifecycleScope.launch {
                    OngoingCall.hangup() // Hang up using coroutines
                }
            }
        }
    }
}