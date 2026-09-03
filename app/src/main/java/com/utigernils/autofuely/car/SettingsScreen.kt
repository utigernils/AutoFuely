package com.utigernils.autofuely.car

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.Header
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import com.utigernils.autofuely.data.repository.PreferenceRepository

class SettingsScreen(carContext: CarContext) : Screen(carContext) {

    private val preferenceRepository = PreferenceRepository(carContext)

    override fun onGetTemplate(): Template {
        val currentFuel = preferenceRepository.getFuelType()
        val currentSize = preferenceRepository.getBboxSizeKm()
        val hideNoPrice = preferenceRepository.getHideNoPriceStations()

        val listBuilder = ItemList.Builder()

        // 1. Kraftstoffart
        listBuilder.addItem(
            Row.Builder()
                .setTitle("Kraftstoffart")
                .addText(currentFuel.displayName)
                .setOnClickListener {
                    screenManager.push(FuelTypeSelectScreen(carContext))
                }
                .build()
        )

        // 2. Suchbereich
        listBuilder.addItem(
            Row.Builder()
                .setTitle("Suchbereich")
                .addText("$currentSize km Bounding Box")
                .setOnClickListener {
                    screenManager.push(BboxSizeSelectScreen(carContext))
                }
                .build()
        )

        // 3. Nur mit Preis anzeigen
        val priceToggleText = if (hideNoPrice) "Aktiv (ohne Preis ausblenden)" else "Inaktiv (alle anzeigen)"
        listBuilder.addItem(
            Row.Builder()
                .setTitle("Nur Tankstellen mit Preis")
                .addText(priceToggleText)
                .setOnClickListener {
                    preferenceRepository.setHideNoPriceStations(!hideNoPrice)
                    invalidate()
                }
                .build()
        )

        val header = Header.Builder()
            .setTitle("Einstellungen")
            .setStartHeaderAction(Action.BACK)
            .build()

        return ListTemplate.Builder()
            .setHeader(header)
            .setSingleList(listBuilder.build())
            .build()
    }
}