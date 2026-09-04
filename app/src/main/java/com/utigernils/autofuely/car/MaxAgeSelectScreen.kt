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
import com.utigernils.autofuely.R
import com.utigernils.autofuely.data.repository.PreferenceRepository

class MaxAgeSelectScreen(carContext: CarContext) : Screen(carContext), DefaultLifecycleObserver {

    private val preferenceRepository = PreferenceRepository(carContext)
    private val options = listOf(0, 1, 2, 3, 7)

    private val prefChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == PreferenceRepository.KEY_MAX_PRICE_AGE_DAYS) {
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
        val currentMaxAge = preferenceRepository.getMaxPriceAgeDays()
        val listBuilder = ItemList.Builder()

        options.forEach { days ->
            val isSelected = days == currentMaxAge
            val label = if (days == 0) {
                carContext.getString(R.string.car_max_age_all)
            } else {
                carContext.getString(R.string.car_settings_max_age_days, days)
            }
            
            val titleText = if (isSelected) {
                carContext.getString(R.string.car_select_current, label)
            } else {
                carContext.getString(R.string.car_select_not_current, label)
            }

            val subtitle = if (isSelected) {
                carContext.getString(R.string.car_current_setting)
            } else {
                carContext.getString(R.string.car_tap_to_select)
            }

            val row = Row.Builder()
                .setTitle(titleText)
                .addText(subtitle)
                .setOnClickListener {
                    preferenceRepository.setMaxPriceAgeDays(days)
                    screenManager.pop()
                }
                .build()

            listBuilder.addItem(row)
        }

        val header = Header.Builder()
            .setTitle(carContext.getString(R.string.car_settings_max_age))
            .setStartHeaderAction(Action.BACK)
            .build()

        return ListTemplate.Builder()
            .setHeader(header)
            .setSingleList(listBuilder.build())
            .build()
    }
}
