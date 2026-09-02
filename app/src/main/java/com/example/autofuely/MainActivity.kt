package com.example.autofuely

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.autofuely.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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

        val hasPermission = checkLocationPermission()
        updatePermissionUi(hasPermission)

        binding.btnGrantLocation.setOnClickListener {
            requestLocationPermissions()
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
            binding.tvLocationStatus.visibility = View.VISIBLE
        } else {
            binding.btnGrantLocation.visibility = View.VISIBLE
            binding.tvLocationStatus.visibility = View.GONE
        }
    }
}