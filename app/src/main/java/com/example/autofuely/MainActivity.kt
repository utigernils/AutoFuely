package com.example.autofuely

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.autofuely.data.repository.PreferenceRepository
import com.example.autofuely.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceRepository: PreferenceRepository

    private val bboxSizeOptions = listOf(3, 5, 8, 10, 15, 20)

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineGranted || coarseGranted) {
            updatePermissionUi(true)
            Toast.makeText(this, "Standortberechtigung erteilt.", Toast.LENGTH_SHORT).show()
        } else {
            updatePermissionUi(false)
            Toast.makeText(this, "Standortberechtigung wurde abgelehnt.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceRepository = PreferenceRepository(this)

        val hasPermission = checkLocationPermission()
        updatePermissionUi(hasPermission)

        binding.btnGrantLocation.setOnClickListener {
            requestLocationPermissions()
        }

        setupBboxSizeSpinner()
    }

    private fun setupBboxSizeSpinner() {
        val displayOptions = bboxSizeOptions.map { "$it km" }
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            displayOptions
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.spinnerBboxSize.adapter = adapter

        val currentSize = preferenceRepository.getBboxSizeKm()
        val defaultIndex = bboxSizeOptions.indexOf(currentSize).let { if (it >= 0) it else 4 } // default 15km
        binding.spinnerBboxSize.setSelection(defaultIndex)

        binding.spinnerBboxSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedSize = bboxSizeOptions[position]
                if (preferenceRepository.getBboxSizeKm() != selectedSize) {
                    preferenceRepository.setBboxSizeKm(selectedSize)
                    Toast.makeText(
                        this@MainActivity,
                        "Suchbereich auf $selectedSize km aktualisiert.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun checkLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    private fun requestLocationPermissions() {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun updatePermissionUi(hasPermission: Boolean) {
        if (hasPermission) {
            binding.btnGrantLocation.visibility = View.GONE
            binding.tvLocationStatus.text = getString(R.string.permission_granted)
            binding.tvLocationStatus.setTextColor(Color.parseColor("#66BB6A"))
            binding.tvLocationStatus.visibility = View.VISIBLE
        } else {
            binding.btnGrantLocation.visibility = View.VISIBLE
            binding.tvLocationStatus.text = getString(R.string.location_permission_needed)
            binding.tvLocationStatus.setTextColor(Color.parseColor("#FFA726"))
            binding.tvLocationStatus.visibility = View.VISIBLE
        }
    }
}