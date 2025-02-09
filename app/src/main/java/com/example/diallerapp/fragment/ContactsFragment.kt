package com.example.diallerapp.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.platform.LocalGraphicsContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diallerapp.activities.CreateContactActivity
import com.example.diallerapp.adapter.ContentAdapter
import com.example.diallerapp.databinding.FragmentContactsBinding
import com.example.diallerapp.viewmodel.ContactsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private lateinit var contentAdapter: ContentAdapter
    private val contactsViewModel: ContactsViewModel by viewModels()

    // Required Permissions List
    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS
    )

    // Step 1: Check if all permissions are granted
    private fun hasPermissions(): Boolean {
        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Step 2: Register Multi-Permission Request
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.all { it.value }

            if (allGranted) {
                fetchContacts() // All permissions granted, proceed
            } else {
                val permanentlyDenied = permissions.keys.any { permission ->
                    !shouldShowRequestPermissionRationale(permission)
                }

                if (permanentlyDenied) {
                    showSettingsDialog() // "Don't Ask Again" case
                } else {
                    showPermissionDeniedMessage() // User denied but didn't select "Don't Ask Again"
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createContactButton.setOnClickListener {
            startActivity(Intent(requireContext(), CreateContactActivity::class.java))
        }

        setupRecyclerView()
        checkAndRequestPermissions()
    }

    private fun setupRecyclerView() {
        contentAdapter = ContentAdapter(requireContext())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = contentAdapter
    }

    // Step 3: Check and Request Permissions
    private fun checkAndRequestPermissions() {
        if (hasPermissions()) {
            fetchContacts()
        } else {
            if (requiredPermissions.any { shouldShowRequestPermissionRationale(it) }) {
                showPermissionExplanation()
            } else {
                permissionLauncher.launch(requiredPermissions)
            }
        }
    }

    // Step 4: Fetch Contacts if Permissions Granted
    private fun fetchContacts() {
        CoroutineScope(Dispatchers.IO).launch {
            contactsViewModel.fetchContacts(requireActivity().contentResolver)

            withContext(Dispatchers.Main) {
                contactsViewModel.contacts.observe(viewLifecycleOwner) { contacts ->
                    contentAdapter.submitList(contacts.toList())
                }
            }
        }
    }

    // Step 5: Show Explanation if Permission is Denied Initially
    private fun showPermissionExplanation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Contacts Permission Required")
            .setMessage("This app needs access to your contacts to display them.")
            .setPositiveButton("OK") { _, _ ->
                permissionLauncher.launch(requiredPermissions)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Step 6: Show Message if Permission is Denied
    private fun showPermissionDeniedMessage() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Denied")
            .setMessage("Contacts permission is required to access contacts. Please allow it in settings.")
            .setPositiveButton("Retry") { _, _ ->
                permissionLauncher.launch(requiredPermissions)
            }
            .setNegativeButton("Cancel", null)
            .show()

    }

    // Step 7: Open Settings if "Don't Ask Again" is Selected
    private fun showSettingsDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Enable Permission from Settings")
            .setMessage("You have permanently denied this permission. Please enable it from settings.")
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireContext().packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        Log.d("TAG","onResume Called")
        // Check if permissions are granted when returning from settings
        if (hasPermissions()) {
            fetchContacts()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

}
