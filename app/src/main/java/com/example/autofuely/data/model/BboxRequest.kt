package com.example.autofuely.data.model

import com.google.gson.annotations.SerializedName

data class BboxRequest(
    @SerializedName("zoom") val zoom: Int = 11,
    @SerializedName("pixelRatio") val pixelRatio: Int = 1,
    @SerializedName("bbox") val bbox: List<Double>,
    @SerializedName("filters") val filters: Filters
)

data class Filters(
    @SerializedName("fuel") val fuel: String,
    @SerializedName("brands") val brands: List<String> = emptyList()
)