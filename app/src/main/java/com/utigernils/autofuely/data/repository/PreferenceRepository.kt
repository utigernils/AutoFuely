package com.utigernils.autofuely.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.utigernils.autofuely.data.model.FuelType
import com.utigernils.autofuely.data.model.SortMode

class PreferenceRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("autofuely_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_FUEL_TYPE = "key_fuel_type"
        const val KEY_SORT_MODE = "key_sort_mode"
        const val KEY_BBOX_SIZE_KM = "key_bbox_size_km"
        const val KEY_HIDE_NO_PRICE_STATIONS = "key_hide_no_price_stations"
        const val KEY_MAX_PRICE_AGE_DAYS = "key_max_price_age_days"
    }

    fun registerOnChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterOnChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

    fun getFuelType(): FuelType {
        val code = prefs.getString(KEY_FUEL_TYPE, FuelType.SP95.code) ?: FuelType.SP95.code
        return FuelType.fromCode(code)
    }

    fun setFuelType(fuelType: FuelType) {
        prefs.edit().putString(KEY_FUEL_TYPE, fuelType.code).apply()
    }

    fun getSortMode(): SortMode {
        val name = prefs.getString(KEY_SORT_MODE, SortMode.PRICE.name) ?: SortMode.PRICE.name
        return try {
            SortMode.valueOf(name)
        } catch (_: Exception) {
            SortMode.PRICE
        }
    }

    fun setSortMode(sortMode: SortMode) {
        prefs.edit().putString(KEY_SORT_MODE, sortMode.name).apply()
    }

    fun getBboxSizeKm(): Int {
        return prefs.getInt(KEY_BBOX_SIZE_KM, 5)
    }

    fun setBboxSizeKm(sizeKm: Int) {
        prefs.edit().putInt(KEY_BBOX_SIZE_KM, sizeKm).apply()
    }

    fun getHideNoPriceStations(): Boolean {
        return prefs.getBoolean(KEY_HIDE_NO_PRICE_STATIONS, true)
    }

    fun setHideNoPriceStations(hide: Boolean) {
        prefs.edit().putBoolean(KEY_HIDE_NO_PRICE_STATIONS, hide).apply()
    }

    fun getMaxPriceAgeDays(): Int {
        return prefs.getInt(KEY_MAX_PRICE_AGE_DAYS, 1)
    }

    fun setMaxPriceAgeDays(days: Int) {
        prefs.edit().putInt(KEY_MAX_PRICE_AGE_DAYS, days).apply()
    }
}