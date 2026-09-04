package com.utigernils.autofuely.data.model

import androidx.annotation.StringRes
import com.utigernils.autofuely.R

enum class FuelType(val code: String, @param:StringRes val displayNameResId: Int) {
    DIESEL("DIESEL", R.string.fuel_diesel),
    SP95("SP95", R.string.fuel_sp95),
    SP98("SP98", R.string.fuel_sp98);

    companion object {
        fun fromCode(code: String): FuelType {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: DIESEL
        }
    }
}