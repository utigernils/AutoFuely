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
    private val stationId: String
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
            .setTitle("Tankstellen Details")
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
        if (details == null) {
            paneBuilder.addRow(
                Row.Builder()
                    .setTitle("Fehler")
                    .addText(errorMessage ?: "Keine Daten gefunden")
                    .build()
            )
            return PaneTemplate.Builder(paneBuilder.build())
                .setHeader(header)
                .build()
        }

        val name = details.displayName ?: details.brand ?: "Tankstelle"
        val address = details.formattedAddress ?: "Keine Adresse vorhanden"

        paneBuilder.addRow(
            Row.Builder()
                .setTitle(name)
                .addText(address)
                .build()
        )

        // Prices section
        val fuels = details.fuelCollection
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
        val cashbackText = if (details.hasTCSMastercardCashback == true) {
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
        val lat = details.location?.lat
        val lng = details.location?.lng
        if (lat != null && lng != null) {
            val navAction = Action.Builder()
                .setTitle("Navigation starten")
                .setOnClickListener {
                    launchNavigation(lat, lng, name)
                }
                .build()
            paneBuilder.addAction(navAction)
        }

        return PaneTemplate.Builder(paneBuilder.build())
            .setHeader(header)
            .build()
    }

    private fun launchNavigation(lat: Double, lng: Double, name: String) {
        val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(${Uri.encode(name)})")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            carContext.startCarApp(intent)
        } catch (e: Exception) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                carContext.startActivity(intent)
            } catch (ex: Exception) {
                // Ignore
            }
        }
    }
}