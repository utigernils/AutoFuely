package com.example.autofuely.data.model

import com.google.gson.annotations.SerializedName

data class StationDetailRequest(
    @SerializedName("id") val id: String
)

data class StationDetailResponse(
    @SerializedName("id") val id: String,
    @SerializedName("brand") val brand: String?,
    @SerializedName("location") val location: LocationData?,
    @SerializedName("isDeleted") val isDeleted: Boolean?,
    @SerializedName("displayName") val displayName: String?,
    @SerializedName("fuelCollection") val fuelCollection: Map<String, FuelPriceInfo>?,
    @SerializedName("formattedAddress") val formattedAddress: String?,
    @SerializedName("hasTCSMastercardCashback") val hasTCSMastercardCashback: Boolean?
)

data class LocationData(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double
)

data class FuelPriceInfo(
    @SerializedName("displayPrice") val displayPrice: Double?
)