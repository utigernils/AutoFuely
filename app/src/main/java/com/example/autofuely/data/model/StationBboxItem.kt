package com.example.autofuely.data.model

import com.google.gson.annotations.SerializedName

data class StationBboxItem(
    @SerializedName("id") val id: String,
    @SerializedName("brand") val brand: String?,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("displayName") val displayName: String?,
    @SerializedName("formattedAddress") val formattedAddress: String?,
    @SerializedName("isCheapest") val isCheapest: Boolean?,
    @SerializedName("price") val price: Double?,
    @SerializedName("fuel") val fuel: String?,
    @SerializedName("fiability") val fiability: String?,
    @SerializedName("cluster") val cluster: Boolean?
) {
    fun getReliabilityLabel(): String {
        return when (fiability?.uppercase()) {
            "CONFIDENT" -> "Zuverlässig"
            "OLD_LAST_UPDATE" -> "Älteres Update"
            "UNRELIABLE" -> "Unbestätigt"
            else -> fiability ?: "K.A."
        }
    }
}