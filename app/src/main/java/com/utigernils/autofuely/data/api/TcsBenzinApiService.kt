package com.utigernils.autofuely.data.api

import com.utigernils.autofuely.data.model.BboxRequest
import com.utigernils.autofuely.data.model.StationBboxItem
import com.utigernils.autofuely.data.model.StationDetailRequest
import com.utigernils.autofuely.data.model.StationDetailResponse
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