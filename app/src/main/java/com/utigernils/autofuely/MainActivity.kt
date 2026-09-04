package com.utigernils.autofuely

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.enableEdgeToEdge
import com.utigernils.autofuely.data.model.FuelType
import com.utigernils.autofuely.data.repository.PreferenceRepository
import com.utigernils.autofuely.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceRepository: PreferenceRepository

    private val bboxSizeOptions = listOf(3, 5, 8, 10, 15, 20)
    private val maxAgeOptions = listOf(0, 1, 2, 3, 7)

    private val prefChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            PreferenceRepository.KEY_FUEL_TYPE -> updateFuelTypeUi()
            PreferenceRepository.KEY_BBOX_SIZE_KM -> updateBboxSizeUi()
            PreferenceRepository.KEY_HIDE_NO_PRICE_STATIONS -> updateHideNoPriceUi()
            PreferenceRepository.KEY_MAX_PRICE_AGE_DAYS -> updateMaxAgeUi()
        }
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineGranted || coarseGranted) {
            updatePermissionUi(true)
            Toast.makeText(this, getString(R.string.permission_granted_toast), Toast.LENGTH_SHORT).show()
        } else {
            updatePermissionUi(false)
            Toast.makeText(this, getString(R.string.permission_denied_toast), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            // Apply top padding to header
            val header = findViewById<View>(R.id.header_layout)
            header?.setPadding(
                header.paddingLeft,
                systemBars.top + (20 * resources.displayMetrics.density).toInt(),
                header.paddingRight,
                header.paddingBottom
            )
            
            // Apply bottom padding to scroll content
            val content = findViewById<View>(R.id.content_layout)
            content?.setPadding(
                content.paddingLeft,
                content.paddingTop,
                content.paddingRight,
                systemBars.bottom + (20 * resources.displayMetrics.density).toInt()
            )
            
            insets
        }

        preferenceRepository = PreferenceRepository(this)

        val hasPermission = checkLocationPermission()
        updatePermissionUi(hasPermission)

        binding.btnGrantLocation.setOnClickListener {
            requestLocationPermissions()
        }

        binding.btnGithub.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/utigernils/AutoFuely"))
            startActivity(intent)
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.tvPrivacyPolicy.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://github.com/utigernils/AutoFuely/blob/release/1.0-google-play/DATENSCHUTZERKLAERUNG.md")
            )
            startActivity(intent)
        }

        setupFuelTypeSpinner()
        setupBboxSizeSpinner()
        setupHideNoPriceSwitch()
        setupMaxAgeSpinner()
    }

    override fun onStart() {
        super.onStart()
        preferenceRepository.registerOnChangeListener(prefChangeListener)
        updateAllSettingsUi()
    }

    override fun onStop() {
        super.onStop()
        preferenceRepository.unregisterOnChangeListener(prefChangeListener)
    }

    private fun updateAllSettingsUi() {
        updateFuelTypeUi()
        updateBboxSizeUi()
        updateHideNoPriceUi()
        updateMaxAgeUi()
    }

    private fun updateFuelTypeUi() {
        val currentFuel = preferenceRepository.getFuelType()
        val fuelTypes = FuelType.entries
        val index = fuelTypes.indexOf(currentFuel).coerceAtLeast(0)
        if (binding.spinnerFuelType.selectedItemPosition != index) {
            binding.spinnerFuelType.setSelection(index)
        }
    }

    private fun updateBboxSizeUi() {
        val currentSize = preferenceRepository.getBboxSizeKm()
        val index = bboxSizeOptions.indexOf(currentSize).let { if (it >= 0) it else 4 }
        if (binding.spinnerBboxSize.selectedItemPosition != index) {
            binding.spinnerBboxSize.setSelection(index)
        }
    }

    private fun updateHideNoPriceUi() {
        val hide = preferenceRepository.getHideNoPriceStations()
        if (binding.switchHideNoPrice.isChecked != hide) {
            binding.switchHideNoPrice.isChecked = hide
        }
    }

    private fun updateMaxAgeUi() {
        val currentMaxAge = preferenceRepository.getMaxPriceAgeDays()
        val index = maxAgeOptions.indexOf(currentMaxAge).let { if (it >= 0) it else 0 }
        if (binding.spinnerMaxAge.selectedItemPosition != index) {
            binding.spinnerMaxAge.setSelection(index)
        }
    }

    private fun setupFuelTypeSpinner() {
        val fuelTypes = FuelType.entries
        val displayOptions = fuelTypes.map { getString(it.displayNameResId) }
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            displayOptions
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.spinnerFuelType.adapter = adapter

        val currentFuel = preferenceRepository.getFuelType()
        val defaultIndex = fuelTypes.indexOf(currentFuel).coerceAtLeast(0)
        binding.spinnerFuelType.setSelection(defaultIndex)

        binding.spinnerFuelType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedFuel = fuelTypes[position]
                if (preferenceRepository.getFuelType() != selectedFuel) {
                    preferenceRepository.setFuelType(selectedFuel)
                    val fuelName = getString(selectedFuel.displayNameResId)
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.fuel_type_changed, fuelName),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupHideNoPriceSwitch() {
        binding.switchHideNoPrice.isChecked = preferenceRepository.getHideNoPriceStations()
        binding.switchHideNoPrice.setOnCheckedChangeListener { _, isChecked ->
            if (preferenceRepository.getHideNoPriceStations() != isChecked) {
                preferenceRepository.setHideNoPriceStations(isChecked)
                val msg = if (isChecked) {
                    getString(R.string.hide_no_price_enabled)
                } else {
                    getString(R.string.hide_no_price_disabled)
                }
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupMaxAgeSpinner() {
        val displayOptions = maxAgeOptions.map { days ->
            if (days == 0) getString(R.string.car_settings_max_age_all) else getString(R.string.car_settings_max_age_days, days)
        }
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            displayOptions
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.spinnerMaxAge.adapter = adapter

        val currentMaxAge = preferenceRepository.getMaxPriceAgeDays()
        val defaultIndex = maxAgeOptions.indexOf(currentMaxAge).let { if (it >= 0) it else 0 }
        binding.spinnerMaxAge.setSelection(defaultIndex)

        binding.spinnerMaxAge.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedAge = maxAgeOptions[position]
                if (preferenceRepository.getMaxPriceAgeDays() != selectedAge) {
                    preferenceRepository.setMaxPriceAgeDays(selectedAge)
                    val msg = if (selectedAge == 0) {
                        getString(R.string.max_age_disabled)
                    } else {
                        getString(R.string.max_age_enabled, selectedAge)
                    }
                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
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
                        getString(R.string.bbox_size_changed, selectedSize),
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
            binding.tvLocationStatus.text = getString(com.utigernils.autofuely.R.string.permission_granted)
            binding.tvLocationStatus.setTextColor(Color.parseColor("#66BB6A"))
            binding.tvLocationStatus.visibility = View.VISIBLE
        } else {
            binding.btnGrantLocation.visibility = View.VISIBLE
            binding.tvLocationStatus.text = getString(com.utigernils.autofuely.R.string.location_permission_needed)
            binding.tvLocationStatus.setTextColor(Color.parseColor("#FFA726"))
            binding.tvLocationStatus.visibility = View.VISIBLE
        }
    }
}