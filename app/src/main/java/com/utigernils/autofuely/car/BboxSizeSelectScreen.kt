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

class BboxSizeSelectScreen(carContext: CarContext) : Screen(carContext) {

    private val preferenceRepository = PreferenceRepository(carContext)
    private val options = listOf(3, 5, 8, 10, 15, 20)

    override fun onGetTemplate(): Template {
        val currentSize = preferenceRepository.getBboxSizeKm()
        val listBuilder = ItemList.Builder()

        options.forEach { sizeKm ->
            val isSelected = sizeKm == currentSize
            val titleText = if (isSelected) "✔  $sizeKm km" else "    $sizeKm km"

            val row = Row.Builder()
                .setTitle(titleText)
                .addText(if (isSelected) "Aktueller Suchbereich" else "Antippen zum Auswählen")
                .setOnClickListener {
                    preferenceRepository.setBboxSizeKm(sizeKm)
                    screenManager.pop()
                }
                .build()

            listBuilder.addItem(row)
        }

        val header = Header.Builder()
            .setTitle("Suchbereich")
            .setStartHeaderAction(Action.BACK)
            .build()

        return ListTemplate.Builder()
            .setHeader(header)
            .setSingleList(listBuilder.build())
            .build()
    }
}