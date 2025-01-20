package com.example.diallerapp.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diallerapp.activities.CreateContactActivity
import com.example.diallerapp.adapter.ContentAdapter
import com.example.diallerapp.databinding.FragmentContactsBinding
import com.example.diallerapp.viewmodel.ContactsViewModel
import com.google.android.material.search.SearchView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private lateinit var contentAdapter: ContentAdapter
    private val contactsViewModel: ContactsViewModel by viewModels()

    // Register permission launcher for contacts permission
    private val contactsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                fetchContacts() // Fetch contacts if permission is granted
            } else {
                Toast.makeText(requireContext(), "Permission denied to read contacts", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createContactButton.setOnClickListener {
            startActivity(Intent(requireContext(), CreateContactActivity::class.java))
        }
        setupRecyclerView()

//        contactsViewModel.contacts.observe(viewLifecycleOwner) { contacts ->
//            contentAdapter.updateList(contacts)
//        }

        checkPermissions()
//        handleSearchView()
    }

//    private fun handleSearchView() {
//        // Show SearchView when SearchBar is clicked
//        binding.searchBar.setOnClickListener {
//            Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
//
//        }
//
//    }

    private fun setupRecyclerView() {
        contentAdapter = ContentAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = contentAdapter
    }

    private fun checkPermissions() {
        // Check if contacts permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            fetchContacts()  // Fetch contacts if permission is granted
        } else {
            contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)  // Request contacts permission if not granted
        }
    }

    private fun fetchContacts() {
        CoroutineScope(Dispatchers.IO).launch {
            contactsViewModel.fetchContacts(requireActivity().contentResolver)

            withContext(Dispatchers.Main){
                contactsViewModel.contacts.observe(viewLifecycleOwner) { contacts ->
                    contentAdapter.submitList(contacts)
                }
            }
        }
    }

}