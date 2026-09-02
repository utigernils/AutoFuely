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
    @SerializedName("type") val type: String? = null,
    @SerializedName("displayPrice") val displayPrice: Double? = null,
    @SerializedName("isDeleted") val isDeleted: Boolean? = null,
    @SerializedName("fiability") val fiability: FiabilityInfo? = null,
    @SerializedName("lastCachedPriceRefresh") val lastCachedPriceRefresh: TimestampInfo? = null
)

data class FiabilityInfo(
    @SerializedName("level") val level: String? = null,
    @SerializedName("score") val score: Double? = null,
    @SerializedName("lastPriceUpdate") val lastPriceUpdate: TimestampInfo? = null,
    @SerializedName("numberOfRecentPriceUpdates") val numberOfRecentPriceUpdates: Int? = null
)

data class TimestampInfo(
    @SerializedName("_seconds") val seconds: Long? = null,
    @SerializedName("seconds") val sec: Long? = null,
    @SerializedName("_nanoseconds") val nanoseconds: Long? = null
) {
    fun toEpochMillis(): Long? {
        val s = seconds ?: sec
        return if (s != null) s * 1000L else null
    }
}