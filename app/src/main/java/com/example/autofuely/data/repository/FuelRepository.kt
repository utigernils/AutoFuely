package com.example.autofuely.data.repository

import com.example.autofuely.data.api.RetrofitClient
import com.example.autofuely.data.api.TcsBenzinApiService
import com.example.autofuely.data.model.BboxRequest
import com.example.autofuely.data.model.Filters
import com.example.autofuely.data.model.StationBboxItem
import com.example.autofuely.data.model.StationDetailRequest
import com.example.autofuely.data.model.StationDetailResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FuelRepository(
    private val apiService: TcsBenzinApiService = RetrofitClient.apiService
) {

    suspend fun fetchStationsByBbox(
        bbox: List<Double>,
        fuelCode: String,
        zoom: Int = 20
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
                val filteredStations = response.body()!!.filter { item ->
                    val id = item.id
                    // Filter out numeric bugged station IDs (e.g. 846), keeping valid string IDs (e.g. "oSUqLcS39YfZMOJIx5J3")
                    id.isNotBlank() && id.toLongOrNull() == null
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