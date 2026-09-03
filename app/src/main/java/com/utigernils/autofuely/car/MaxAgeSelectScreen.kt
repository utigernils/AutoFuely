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

class MaxAgeSelectScreen(carContext: CarContext) : Screen(carContext) {

    private val preferenceRepository = PreferenceRepository(carContext)
    private val options = listOf(0, 1, 2, 3, 7)

    override fun onGetTemplate(): Template {
        val currentMaxAge = preferenceRepository.getMaxPriceAgeDays()
        val listBuilder = ItemList.Builder()

        options.forEach { days ->
            val isSelected = days == currentMaxAge
            val label = if (days == 0) "Alle anzeigen (kein Filter)" else "Maximal $days ${if (days == 1) "Tag" else "Tage"} alt"
            val titleText = if (isSelected) "✔  $label" else "    $label"

            val row = Row.Builder()
                .setTitle(titleText)
                .addText(if (isSelected) "Aktuelle Einstellung" else "Antippen zum Auswählen")
                .setOnClickListener {
                    preferenceRepository.setMaxPriceAgeDays(days)
                    screenManager.pop()
                }
                .build()

            listBuilder.addItem(row)
        }

        val header = Header.Builder()
            .setTitle("Max. Alter der Daten")
            .setStartHeaderAction(Action.BACK)
            .build()

        return ListTemplate.Builder()
            .setHeader(header)
            .setSingleList(listBuilder.build())
            .build()
    }
}
