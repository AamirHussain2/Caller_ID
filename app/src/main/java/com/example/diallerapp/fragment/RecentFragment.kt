package com.example.diallerapp.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diallerapp.adapter.RecentAdapter
import com.example.diallerapp.databinding.FragmentRecentBinding
import com.example.diallerapp.viewmodel.RecentViewModel

class RecentFragment : Fragment() {

    private var _binding: FragmentRecentBinding? = null
    private val binding get() = _binding!!
    private val recentViewModel: RecentViewModel by viewModels()
    private lateinit var recentAdapter: RecentAdapter

    // Permission launcher
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startObservingCallLogs() // Start observing if permission is granted
            } else {
                showToast("Permission denied to read call logs")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentRecentBinding.inflate(inflater, container, false).apply {
        _binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        handlePermissions()
    }

    private fun setupRecyclerView() {
        recentAdapter = RecentAdapter()
        binding.recentRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recentAdapter
        }
    }

    private fun observeViewModel() {
        recentViewModel.recentCallLogs.observe(viewLifecycleOwner) { callLogs ->
            if (callLogs.isNotEmpty()) {
                recentAdapter.submitList(callLogs)
            } else {
                showToast("No recent calls found")
            }
        }
    }

    private fun handlePermissions() {
        if (isPermissionGranted()) {
            startObservingCallLogs()
        } else {
            requestPermission()
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_CALL_LOG
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        permissionLauncher.launch(Manifest.permission.READ_CALL_LOG)
    }

    private fun startObservingCallLogs() {
        recentViewModel.startObservingCallLogs(requireContext().contentResolver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recentViewModel.stopObservingCallLogs(requireContext().contentResolver)
        _binding = null
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
