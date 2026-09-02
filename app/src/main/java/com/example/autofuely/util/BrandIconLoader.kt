package com.example.autofuely.util

import android.content.Context
import android.graphics.Bitmap
import androidx.car.app.model.CarIcon
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.autofuely.R
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

class BrandIconLoader(private val context: Context) {
    private val iconCache = ConcurrentHashMap<String, CarIcon>()

    val fallbackIcon: CarIcon by lazy {
        CarIcon.Builder(
            IconCompat.createWithResource(context, R.drawable.ic_gas_station)
        ).build()
    }

    suspend fun getBrandIcon(brand: String?): CarIcon = withContext(Dispatchers.IO) {
        if (brand.isNullOrEmpty()) return@withContext fallbackIcon

        val brandKey = brand.trim().lowercase()
        iconCache[brandKey]?.let { return@withContext it }

        val iconUrl = "https://benzin.tcs.ch/images/brands/icons/$brandKey.webp"
        try {
            val imageLoader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(iconUrl)
                .allowHardware(false)
                .size(128, 128)
                .build()

            val result = imageLoader.execute(request)
            if (result is SuccessResult) {
                val drawable = result.drawable
                val bitmap: Bitmap = drawable.toBitmap(width = 128, height = 128)
                val carIcon = CarIcon.Builder(
                    IconCompat.createWithBitmap(bitmap)
                ).build()
                iconCache[brandKey] = carIcon
                return@withContext carIcon
            }
        } catch (e: Exception) {
            // Fallback on error or 404
        }

        return@withContext fallbackIcon
    }
}