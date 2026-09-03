package com.utigernils.autofuely.data.model

enum class FuelType(val code: String, val displayName: String) {
    DIESEL("DIESEL", "Diesel"),
    SP95("SP95", "Bleifrei 95 (SP95)"),
    SP98("SP98", "Bleifrei 98 (SP98)");

    companion object {
        fun fromCode(code: String): FuelType {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: DIESEL
        }
    }
}