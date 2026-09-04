package com.utigernils.autofuely.car

import android.content.SharedPreferences
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.Header
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.utigernils.autofuely.data.model.FuelType
import com.utigernils.autofuely.data.repository.PreferenceRepository

class FuelTypeSelectScreen(carContext: CarContext) : Screen(carContext), DefaultLifecycleObserver {

    private val preferenceRepository = PreferenceRepository(carContext)

    private val prefChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == PreferenceRepository.KEY_FUEL_TYPE) {
            invalidate()
        }
    }

    init {
        lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        preferenceRepository.registerOnChangeListener(prefChangeListener)
    }

    override fun onStop(owner: LifecycleOwner) {
        preferenceRepository.unregisterOnChangeListener(prefChangeListener)
    }

    override fun onGetTemplate(): Template {
        val currentType = preferenceRepository.getFuelType()
        val listBuilder = ItemList.Builder()

        FuelType.entries.forEach { fuelType ->
            val isSelected = fuelType == currentType
            val titleText = if (isSelected) "✔  ${fuelType.displayName}" else "    ${fuelType.displayName}"

            val row = Row.Builder()
                .setTitle(titleText)
                .addText(if (isSelected) "Aktuell ausgewählt" else "Antippen zum Auswählen")
                .setOnClickListener {
                    preferenceRepository.setFuelType(fuelType)
                    screenManager.pop()
                }
                .build()

            listBuilder.addItem(row)
        }

        val header = Header.Builder()
            .setTitle("Kraftstoffart")
            .setStartHeaderAction(Action.BACK)
            .build()

        return ListTemplate.Builder()
            .setHeader(header)
            .setSingleList(listBuilder.build())
            .build()
    }
}