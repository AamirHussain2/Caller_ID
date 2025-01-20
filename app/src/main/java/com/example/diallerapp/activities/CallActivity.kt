package com.example.diallerapp.activities

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.telecom.Call
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.diallerapp.R
import com.example.diallerapp.utils.OngoingCall
import com.example.diallerapp.databinding.ActivityCallBinding
import com.example.diallerapp.databinding.CustomRecievedUiBinding
import com.example.diallerapp.utils.CallForegroundService
import com.example.diallerapp.utils.OnClickListener
import java.util.Locale

class CallActivity : AppCompatActivity() {

    private var _binding: ActivityCallBinding? = null
    private val binding get() = _binding!!

    private var number: String? = null

    private var startTimeMillis: Long = 0
    private var elapsedTimeMillis: Long = 0
    private var timerRunning: Boolean = false
    private val timerHandler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCallBinding.inflate(layoutInflater)

        number = intent.data?.schemeSpecificPart

        // Observing state changes using lifecycleScope
        OngoingCall.observeState(this) { state ->
            state?.let {
                Log.d("TAG", "State changed: $it")
                updateUi(it)

                // Check if the call is disconnected
                if (it == Call.STATE_DISCONNECTED) {
                    stopService(Intent(this, CallForegroundService::class.java))
                    stopTimer()  // Stop timer when the call is disconnected
                    finish() // Close the activity if the call is disconnected
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()

        OnClickListener.clickAnswer(binding, lifecycleScope)
        OnClickListener.clickHangup(binding, lifecycleScope)
    }

    @SuppressLint("SetTextI18n")
    private fun updateUi(state: Int) {
//        Log.d("TAG", "Updating UI with state: ${Constants.asString(state)}: $state")

        // Contact name fetch
        val contactName = getContactName(number) ?: "Unknown"
        binding.callInfo.text = getString(R.string.call_info, contactName, number ?: "")

        Log.d("CALL INFO",getString(R.string.call_info, contactName, number ?: "") )

        // Show hangup button when call is dialing, ringing, or active
        binding.hangup.visibility = if (state in listOf(Call.STATE_DIALING, Call.STATE_RINGING, Call.STATE_ACTIVE)) View.VISIBLE else View.GONE

        // Handle timer visibility and start/stop logic based on call state
        setCallState(state)
    }

    private fun setCallState(state: Int) {
        when (state) {
            Call.STATE_ACTIVE, Call.STATE_CONNECTING -> {
                // Switch to active call layout
                setContentView(binding.root)

                // Start the foreground service
                val serviceIntent = Intent(this, CallForegroundService::class.java)
                startService(serviceIntent)

                binding.timer.visibility = View.VISIBLE  // Make the timer visible when the call is active
                startTimer()  // Start timer only when the call is active
            }
            Call.STATE_RINGING -> {

                val customBinding = CustomRecievedUiBinding.inflate(layoutInflater, null, false)

                setContentView(customBinding.root)
                setupRingingLayout(customBinding)

                binding.timer.visibility = View.GONE
                stopTimer()
            }
            else -> {
                binding.timer.visibility = View.GONE
                stopTimer()  // Stop timer when call is not active
            }
        }
    }


    private fun setupRingingLayout(binding: CustomRecievedUiBinding) {
        // Contact name fetch
        val contactName = getContactName(number) ?: getString(R.string.unknown_contact)
        binding.callInfo.text = getString(R.string.call_info, contactName, number ?: "")

        OnClickListener.clickAnswer(binding, lifecycleScope)
        OnClickListener.clickHangup(binding, lifecycleScope)
    }

    // Function to get contact name by number
    private fun getContactName(phoneNumber: String?): String? {
        if (phoneNumber.isNullOrEmpty()) return null

        val contentResolver: ContentResolver = contentResolver

        val cursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER),
            ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
            arrayOf(phoneNumber),
            null
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            if (nameIndex >= 0 && it.moveToFirst()) {
                return it.getString(nameIndex)
            }
        }

        Log.d("TAG", "Contact not found for number: $phoneNumber") // Log statement for debugging
        return null
    }

    private fun startTimer() {
        if (timerRunning) return // Prevent starting the timer multiple times

        startTimeMillis = System.currentTimeMillis() - elapsedTimeMillis
        timerRunning = true

        // Update every second
        timerRunnable = object : Runnable {
            override fun run() {
                elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis
                val seconds = (elapsedTimeMillis / 1000).toInt()
                val minutes = seconds / 60
                val displaySeconds = seconds % 60

                // Extra check for long-running timer
                val formattedTime = if (seconds >= 3600) {
                    val hours = seconds / 3600
                    String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes % 60, displaySeconds)
                } else {
                    String.format(Locale.getDefault(), "%02d:%02d", minutes, displaySeconds)
                }

                binding.timer.text = formattedTime
                timerHandler.postDelayed(this, 1000)  // Update every second
            }
        }
        timerHandler.post(timerRunnable!!)  // Start the timer
    }

    private fun stopTimer() {
        timerRunnable?.let {
            timerHandler.removeCallbacks(it)
        }
        timerRunning = false
        timerRunnable = null  // Reset the timerRunnable after stopping the timer
    }

    companion object {
        fun start(context: Context, call: Call) {
            Intent(context, CallActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(call.details.handle)
                .let(context::startActivity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val serviceIntent = Intent(this, CallForegroundService::class.java)
        startService(serviceIntent)
        stopTimer()  // Stop timer if the activity is destroyed
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        Log.d("test", "onPause")

        val serviceIntent = Intent(this, CallForegroundService::class.java)
        startService(serviceIntent)
    }

    override fun onResume() {
        super.onResume()
        Log.d("test", "onResume")

        val serviceIntent = Intent(this, CallForegroundService::class.java)
        startService(serviceIntent)
    }
}
