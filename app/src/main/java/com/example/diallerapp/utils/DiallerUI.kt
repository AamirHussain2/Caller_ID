package com.example.diallerapp.utils

import android.Manifest.permission.CALL_PHONE
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.PermissionChecker
import com.example.diallerapp.databinding.FragmentDiallerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class DiallerUI(
    private val context: Context,
    private val binding: FragmentDiallerBinding,
    private val callPermissionLauncher: ActivityResultLauncher<String>,
    private val bottomSheetDialog: BottomSheetDialog
) {

    init {
        setupButtons()
//        setupEditText()
    }

    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

    private fun setupButtons() {
        with(binding) {
            button1.setOnClickListener {
                onNumberClicked("1"); toneGenerator.startTone(
                ToneGenerator.TONE_DTMF_1,
                100
            )
            }
            button2.setOnClickListener {
                onNumberClicked("2"); toneGenerator.startTone(
                ToneGenerator.TONE_DTMF_2,
                100
            )
            }
            button3.setOnClickListener {
                onNumberClicked("3"); toneGenerator.startTone(
                ToneGenerator.TONE_DTMF_3,
                100
            )
            }
            button4.setOnClickListener {
                onNumberClicked("4"); toneGenerator.startTone(
                ToneGenerator.TONE_DTMF_4,
                100
            )
            }
            button5.setOnClickListener {
                onNumberClicked("5"); toneGenerator.startTone(
                ToneGenerator.TONE_DTMF_5,
                100
            )
            }
            button6.setOnClickListener {
                onNumberClicked("6"); toneGenerator.startTone(
                ToneGenerator.TONE_DTMF_6,
                100
            )
            }
            button7.setOnClickListener {
                onNumberClicked("7"); toneGenerator.startTone(
                ToneGenerator.TONE_DTMF_7,
                100
            )
            }
            button8.setOnClickListener {
                onNumberClicked("8"); toneGenerator.startTone(
                ToneGenerator.TONE_DTMF_8,
                100
            )
            }
            button9.setOnClickListener {
                onNumberClicked("9"); toneGenerator.startTone(
                ToneGenerator.TONE_DTMF_9,
                100
            )
            }
            button0.setOnClickListener {
                onNumberClicked("0"); toneGenerator.startTone(
                ToneGenerator.TONE_DTMF_0,
                100
            )
            }
            asteriskSign.setOnClickListener {
                onSpecialCharClicked("*"); toneGenerator.startTone(
                ToneGenerator.TONE_DTMF_S,
                100
            )
            }
            hashSign.setOnClickListener {
                onSpecialCharClicked("#"); toneGenerator.startTone(
                ToneGenerator.TONE_DTMF_P,
                100
            )
            }
            callButton.setOnClickListener {
                onCallButtonClicked(); toneGenerator.startTone(
                ToneGenerator.TONE_PROP_ACK,
                100
            )
                bottomSheetDialog.dismiss()
            }
            delete.setOnClickListener {
                onDeleteButtonClicked(); toneGenerator.startTone(
                ToneGenerator.TONE_PROP_NACK,
                100
            )
            }
        }
    }

    private fun setupEditText() {
        binding.phoneNumberInput.isFocusable = false
        binding.phoneNumberInput.setOnEditorActionListener { _, _, _ ->
            onCallButtonClicked()
            true
        }
    }

    private fun onNumberClicked(number: String) {
        val currentText = binding.phoneNumberInput.text.toString()
        binding.phoneNumberInput.setText(currentText.plus(number))
    }

    private fun onSpecialCharClicked(char: String) {
        val currentText: String = binding.phoneNumberInput.text.toString()
        binding.phoneNumberInput.setText(currentText.plus(char))
    }

    private fun onCallButtonClicked() {
        val phoneNumber = binding.phoneNumberInput.text.toString().trim()
        if (phoneNumber.isNotEmpty()) {
            makeCall() // Call method to initiate the call
            Toast.makeText(context, "Calling $phoneNumber", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to delete the last character from the phone number input
    private fun onDeleteButtonClicked() {
        val currentText: String = binding.phoneNumberInput.text.toString()

        if (currentText.isNotEmpty()) {
            binding.phoneNumberInput.setText(currentText.dropLast(1)) // Removes the last character
        }
    }

    fun makeCall() {
        val phoneNumber = binding.phoneNumberInput.text.toString().trim()
        if (phoneNumber.isNotEmpty()) {
            // Check if permission is granted
            if (PermissionChecker.checkSelfPermission(
                    context,
                    CALL_PHONE
                ) == PermissionChecker.PERMISSION_GRANTED
            ) {
                val uri = Uri.parse("tel:$phoneNumber")
                context.startActivity(Intent(Intent.ACTION_CALL, uri))
                binding.phoneNumberInput.setText("") // Clear input after call
            } else {
                // Request permission using the launcher
                callPermissionLauncher.launch(CALL_PHONE)
            }
        } else {
            Toast.makeText(context, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
        }

    }
}
