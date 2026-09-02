package com.example.autofuely.data.api

import com.example.autofuely.data.model.BboxRequest
import com.example.autofuely.data.model.StationBboxItem
import com.example.autofuely.data.model.StationDetailRequest
import com.example.autofuely.data.model.StationDetailResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TcsBenzinApiService {

    @POST("benzinGetStationByBbox")
    suspend fun getStationsByBbox(
        @Body request: BboxRequest
    ): Response<List<StationBboxItem>>

    @POST("benzinGetStationById")
    suspend fun getStationById(
        @Body request: StationDetailRequest
    ): Response<StationDetailResponse>
}