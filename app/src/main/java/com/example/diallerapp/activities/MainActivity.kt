package com.example.diallerapp.activities

import android.content.Intent
import android.os.Bundle
import android.telecom.TelecomManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.diallerapp.R
import com.example.diallerapp.databinding.ActivityMainBinding
import com.example.diallerapp.databinding.FragmentDiallerBinding
import com.example.diallerapp.utils.DiallerUI
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var diallerUI: DiallerUI
    private lateinit var fragmentDiallerBinding: FragmentDiallerBinding

    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.containerView) as NavHostFragment).navController
    }

    private val callPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            val message = if (isGranted) {
                diallerUI.makeCall()
                "Permission granted..."
            } else {
                "Permission denied to make calls"
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        handleIncomingIntent()

    }


    private fun setupUI() {
        binding.bottomNavigationView.setupWithNavController(navController)
        binding.fabButton.setOnClickListener { showDialerBottomSheet() }
    }

    private fun handleIncomingIntent() {
        intent.data?.schemeSpecificPart?.let {
            fragmentDiallerBinding.phoneNumberInput.setText(it)
        }
    }

    private fun showDialerBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this).apply {
            fragmentDiallerBinding = FragmentDiallerBinding.inflate(layoutInflater)
            setContentView(fragmentDiallerBinding.root)
            show()
        }

        diallerUI = DiallerUI(this, fragmentDiallerBinding, callPermissionLauncher, bottomSheetDialog)
    }

    override fun onStart() {
        super.onStart()
        offerReplacingDefaultDialer()
    }

    private fun offerReplacingDefaultDialer() {
        val telecomManager = getSystemService(TelecomManager::class.java)
        if (telecomManager?.defaultDialerPackage != packageName) {
            Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
                putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
                startActivity(this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null

    }
}
