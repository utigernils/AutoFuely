package com.utigernils.autofuely.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class LocationHelper(context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Default location: Central Switzerland (Lucerne area)
    val defaultLocation: Location = Location("default").apply {
        latitude = 47.0502
        longitude = 8.3093
    }

    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Location = suspendCancellableCoroutine { continuation ->
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (continuation.isActive) {
                        continuation.resume(location ?: defaultLocation)
                    }
                }
                .addOnFailureListener {
                    if (continuation.isActive) {
                        continuation.resume(defaultLocation)
                    }
                }
        } catch (_: Exception) {
            if (continuation.isActive) {
                continuation.resume(defaultLocation)
            }
        }
    }

    fun calculateBbox(location: Location, edgeLengthKm: Double = 15.0): List<Double> {
        val halfKm = edgeLengthKm / 2.0
        val deltaLat = halfKm / 111.0
        val cosLat = cos(Math.toRadians(location.latitude)).coerceAtLeast(0.1)
        val deltaLng = halfKm / (111.0 * cosLat)

        val minLng = location.longitude - deltaLng
        val minLat = location.latitude - deltaLat
        val maxLng = location.longitude + deltaLng
        val maxLat = location.latitude + deltaLat

        return listOf(minLng, minLat, maxLng, maxLat)
    }

    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }
}