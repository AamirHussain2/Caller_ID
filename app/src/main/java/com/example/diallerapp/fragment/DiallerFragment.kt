//package com.example.diallerapp.fragments
//
//import android.content.Intent
//import android.os.Bundle
//import android.telecom.TelecomManager
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import com.example.diallerapp.adapter.ContentAdapter
//import com.example.diallerapp.databinding.FragmentDiallerBinding
//import com.example.diallerapp.utils.DiallerUI
//import com.example.diallerapp.viewmodel.ContactsViewModel
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment
//
//class DiallerFragment : BottomSheetDialogFragment() {
//
//    private var _binding: FragmentDiallerBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var contentAdapter: ContentAdapter
//    private lateinit var diallerUI: DiallerUI
//    private val contactsViewModel: ContactsViewModel by viewModels()
//
//    // Register permission launcher for making calls
//    private val callPermissionLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//            if (isGranted) {
//                diallerUI.makeCall() // Proceed with the call if permission is granted
//                Toast.makeText(requireContext(), "Permission granted...", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(requireContext(), "Permission denied to make calls", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentDiallerBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        diallerUI = DiallerUI(requireContext(), binding, callPermissionLauncher)
//
//        // Observe LiveData and update RecyclerView when data changes
//        contactsViewModel.contacts.observe(viewLifecycleOwner) { contacts ->
//            contentAdapter.updateList(contacts)
//        }
//
//        // Handle incoming phone number from arguments or savedInstanceState
//        arguments?.getString("phone_number")?.let {
//            binding.phoneNumberInput.setText(it)
//        } ?: savedInstanceState?.getString("phone_number")?.let {
//            binding.phoneNumberInput.setText(it)
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        offerReplacingDefaultDialer()
//    }
//
//    // Check and offer replacing the default dialer
//    private fun offerReplacingDefaultDialer() {
//        if (requireContext().getSystemService(TelecomManager::class.java).defaultDialerPackage != requireContext().packageName) {
//            Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
//                .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, requireContext().packageName)
//                .let(::startActivity)
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    companion object {
//        const val TAG = "ModalBottomSheet"
//    }
//}
