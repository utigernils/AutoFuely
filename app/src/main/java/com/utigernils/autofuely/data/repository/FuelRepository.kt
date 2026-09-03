package com.utigernils.autofuely.data.repository

import com.utigernils.autofuely.data.api.RetrofitClient
import com.utigernils.autofuely.data.api.TcsBenzinApiService
import com.utigernils.autofuely.data.model.BboxRequest
import com.utigernils.autofuely.data.model.Filters
import com.utigernils.autofuely.data.model.StationBboxItem
import com.utigernils.autofuely.data.model.StationDetailRequest
import com.utigernils.autofuely.data.model.StationDetailResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FuelRepository(
    private val apiService: TcsBenzinApiService = RetrofitClient.apiService
) {

    suspend fun fetchStationsByBbox(
        bbox: List<Double>,
        fuelCode: String,
        zoom: Int = 20,
        hideNoPrice: Boolean = true,
        maxAgeDays: Int = 0
    ): Result<List<StationBboxItem>> = withContext(Dispatchers.IO) {
        try {
            val request = BboxRequest(
                zoom = zoom,
                pixelRatio = 1,
                bbox = bbox,
                filters = Filters(fuel = fuelCode)
            )
            val response = apiService.getStationsByBbox(request)
            if (response.isSuccessful && response.body() != null) {
                val now = System.currentTimeMillis()
                val maxAgeMs = maxAgeDays * 24 * 60 * 60 * 1000L

                val filteredStations = response.body()!!.filter { item ->
                    val id = item.id
                    val hasValidId = id.isNotBlank() && id.toLongOrNull() == null
                    val hasValidPrice = !hideNoPrice || (item.price != null && item.price > 0.0)

                    val timestampMs = item.lastPriceUpdate?.toEpochMillis()
                        ?: item.lastCachedPriceRefresh?.toEpochMillis()
                    val hasValidAge = if (maxAgeDays > 0 && timestampMs != null) {
                        (now - timestampMs) <= maxAgeMs
                    } else {
                        true
                    }

                    hasValidId && hasValidPrice && hasValidAge
                }
                Result.success(filteredStations)
            } else {
                Result.failure(Exception("API Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchStationById(id: String): Result<StationDetailResponse> = withContext(Dispatchers.IO) {
        try {
            val request = StationDetailRequest(id = id)
            val response = apiService.getStationById(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("API Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}