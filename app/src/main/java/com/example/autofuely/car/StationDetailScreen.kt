package com.example.autofuely.car

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
import com.example.autofuely.data.model.StationDetailResponse
import com.example.autofuely.data.repository.FuelRepository
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
                errorMessage = "Fehler beim Laden der Details."
            }
            invalidate()
        }
    }

    override fun onGetTemplate(): Template {
        val header = Header.Builder()
            .setTitle("Tankstelle")
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
        val name = details?.displayName ?: details?.brand ?: "Tankstelle"
        val address = details?.formattedAddress ?: "Keine Adresse vorhanden"

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
            "Keine Preisinformationen verfügbar"
        }

        paneBuilder.addRow(
            Row.Builder()
                .setTitle("Kraftstoffpreise")
                .addText(priceText)
                .build()
        )

        // TCS Mastercard Cashback
        val cashbackText = if (details?.hasTCSMastercardCashback == true) {
            "Ja - Rabatt mit TCS Mastercard verfügbar"
        } else {
            "Nein"
        }
        paneBuilder.addRow(
            Row.Builder()
                .setTitle("TCS Mastercard Cashback")
                .addText(cashbackText)
                .build()
        )

        // Navigation Action Button
        val navLat = details?.location?.lat ?: fallbackLat
        val navLng = details?.location?.lng ?: fallbackLng
        if (navLat != null && navLng != null) {
            val navAction = Action.Builder()
                .setTitle("Navigation starten")
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