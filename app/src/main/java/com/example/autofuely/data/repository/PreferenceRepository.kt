package com.example.autofuely.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.autofuely.data.model.FuelType
import com.example.autofuely.data.model.SortMode

class PreferenceRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("autofuely_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FUEL_TYPE = "key_fuel_type"
        private const val KEY_SORT_MODE = "key_sort_mode"
        private const val KEY_BBOX_SIZE_KM = "key_bbox_size_km"
    }

    fun getFuelType(): FuelType {
        val code = prefs.getString(KEY_FUEL_TYPE, FuelType.DIESEL.code) ?: FuelType.DIESEL.code
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
        return prefs.getInt(KEY_BBOX_SIZE_KM, 15)
    }

    fun setBboxSizeKm(sizeKm: Int) {
        prefs.edit().putInt(KEY_BBOX_SIZE_KM, sizeKm).apply()
    }
}