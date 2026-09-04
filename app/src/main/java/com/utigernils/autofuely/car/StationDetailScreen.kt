package com.utigernils.autofuely.car

import android.content.Intent
import android.net.Uri
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.Header
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import com.utigernils.autofuely.R
import com.utigernils.autofuely.data.model.StationDetailResponse
import com.utigernils.autofuely.data.repository.FuelRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class StationDetailScreen(
    carContext: CarContext,
    private val stationId: String,
    private val fallbackLat: Double? = null,
    private val fallbackLng: Double? = null
) : Screen(carContext) {

    private val repository = FuelRepository()

    private var detailResponse: StationDetailResponse? = null
    private var isLoading = true
    private var errorMessage: String? = null

    init {
        loadStationDetails()
    }

    private fun loadStationDetails() {
        isLoading = true
        invalidate()
        CoroutineScope(Dispatchers.Main).launch {
            val result = repository.fetchStationById(stationId)
            isLoading = false
            result.onSuccess {
                detailResponse = it
            }.onFailure {
                errorMessage = carContext.getString(R.string.car_detail_error)
            }
            invalidate()
        }
    }

    override fun onGetTemplate(): Template {
        val header = Header.Builder()
            .setTitle(carContext.getString(R.string.car_detail_title))
            .setStartHeaderAction(Action.BACK)
            .build()

        val paneBuilder = Pane.Builder()

        if (isLoading) {
            paneBuilder.setLoading(true)
            return PaneTemplate.Builder(paneBuilder.build())
                .setHeader(header)
                .build()
        }

        val details = detailResponse
        val name = details?.displayName ?: details?.brand ?: carContext.getString(R.string.car_station_default)
        val address = details?.formattedAddress ?: carContext.getString(R.string.car_detail_no_address)

        paneBuilder.addRow(
            Row.Builder()
                .setTitle(name)
                .addText(address)
                .build()
        )

        // Prices section
        val fuels = details?.fuelCollection
        val priceText = if (!fuels.isNullOrEmpty()) {
            val sb = StringBuilder()
            fuels.forEach { (fuelCode, info) ->
                val priceVal = info.displayPrice
                if (priceVal != null && priceVal > 0) {
                    sb.append("$fuelCode: CHF ${String.format(Locale.GERMANY, "%.2f", priceVal)}   ")
                }
            }
            sb.toString().trim()
        } else {
            carContext.getString(R.string.car_detail_no_prices)
        }

        paneBuilder.addRow(
            Row.Builder()
                .setTitle(carContext.getString(R.string.car_detail_fuel_prices))
                .addText(priceText)
                .build()
        )

        // TCS Mastercard Cashback
        val cashbackText = if (details?.hasTCSMastercardCashback == true) {
            carContext.getString(R.string.car_detail_tcs_yes)
        } else {
            carContext.getString(R.string.car_detail_tcs_no)
        }
        paneBuilder.addRow(
            Row.Builder()
                .setTitle(carContext.getString(R.string.car_detail_tcs_cashback))
                .addText(cashbackText)
                .build()
        )

        // Navigation Action Button
        val navLat = details?.location?.lat ?: fallbackLat
        val navLng = details?.location?.lng ?: fallbackLng
        if (navLat != null && navLng != null) {
            val navAction = Action.Builder()
                .setTitle(carContext.getString(R.string.car_detail_nav_start))
                .setOnClickListener {
                    launchNavigation(navLat, navLng)
                }
                .build()
            paneBuilder.addAction(navAction)
        }

        return PaneTemplate.Builder(paneBuilder.build())
            .setHeader(header)
            .build()
    }

    private fun launchNavigation(lat: Double, lng: Double) {
        val geoUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng")
        val googleNavUri = Uri.parse("google.navigation:q=$lat,$lng")

        val intentsToTry = listOf(
            Intent(Intent.ACTION_VIEW, geoUri),
            Intent("androidx.car.app.action.NAVIGATE", geoUri),
            Intent(Intent.ACTION_VIEW, googleNavUri)
        )

        for (intent in intentsToTry) {
            try {
                carContext.startCarApp(intent)
                return
            } catch (_: Exception) {
                // Try next intent
            }
        }

        // Fallback to startActivity
        try {
            val fallbackIntent = Intent(Intent.ACTION_VIEW, geoUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            carContext.startActivity(fallbackIntent)
        } catch (_: Exception) {
            // Ignore
        }
    }
}