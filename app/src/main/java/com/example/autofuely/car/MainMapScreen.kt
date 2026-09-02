package com.example.autofuely.car

import android.location.Location
import android.text.Spannable
import android.text.SpannableString
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.CarLocation
import androidx.car.app.model.Distance
import androidx.car.app.model.DistanceSpan
import androidx.car.app.model.ItemList
import androidx.car.app.model.Metadata
import androidx.car.app.model.Place
import androidx.car.app.model.PlaceListMapTemplate
import androidx.car.app.model.PlaceMarker
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.autofuely.data.model.SortMode
import com.example.autofuely.data.model.StationBboxItem
import com.example.autofuely.data.repository.FuelRepository
import com.example.autofuely.data.repository.PreferenceRepository
import com.example.autofuely.util.BrandIconLoader
import com.example.autofuely.util.LocationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainMapScreen(carContext: CarContext) : Screen(carContext), DefaultLifecycleObserver {

    private val repository = FuelRepository()
    private val preferenceRepository = PreferenceRepository(carContext)
    private val locationHelper = LocationHelper(carContext)
    private val brandIconLoader = BrandIconLoader(carContext)

    private var currentLocation: Location = locationHelper.defaultLocation
    private var stations: List<StationBboxItem> = emptyList()
    private var brandIconsMap: Map<String, CarIcon> = emptyMap()

    private var isLoading = true
    private var errorMessage: String? = null
    private var autoRefreshJob: Job? = null

    init {
        lifecycle.addObserver(this)
        loadData()
    }

    override fun onStart(owner: LifecycleOwner) {
        startAutoRefresh()
    }

    override fun onStop(owner: LifecycleOwner) {
        stopAutoRefresh()
    }

    private fun startAutoRefresh() {
        stopAutoRefresh()
        autoRefreshJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                delay(60_000) // Auto refresh every 60 seconds
                loadData()
            }
        }
    }

    private fun stopAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = null
    }

    private fun loadData() {
        isLoading = true
        invalidate()

        CoroutineScope(Dispatchers.Main).launch {
            currentLocation = locationHelper.getLastKnownLocation()
            val bbox = locationHelper.calculateBbox(currentLocation)
            val selectedFuel = preferenceRepository.getFuelType()

            val result = repository.fetchStationsByBbox(bbox, selectedFuel.code)

            result.onSuccess { list ->
                stations = list
                errorMessage = null
                loadBrandIcons(list)
            }.onFailure {
                stations = emptyList()
                errorMessage = "Fehler beim Laden der Tankstellen."
            }

            isLoading = false
            invalidate()
        }
    }

    private fun loadBrandIcons(list: List<StationBboxItem>) {
        CoroutineScope(Dispatchers.Main).launch {
            val icons = mutableMapOf<String, CarIcon>()
            withContext(Dispatchers.IO) {
                list.forEach { station ->
                    val brand = station.brand
                    if (!brand.isNullOrEmpty() && !icons.containsKey(brand.lowercase())) {
                        val icon = brandIconLoader.getBrandIcon(brand)
                        icons[brand.lowercase()] = icon
                    }
                }
            }
            brandIconsMap = icons
            invalidate()
        }
    }

    override fun onGetTemplate(): Template {
        val selectedFuel = preferenceRepository.getFuelType()
        val currentSortMode = preferenceRepository.getSortMode()

        // ActionStrip
        val actionStripBuilder = ActionStrip.Builder()

        // Action 1: Fuel Type Selector
        actionStripBuilder.addAction(
            Action.Builder()
                .setTitle(selectedFuel.code)
                .setOnClickListener {
                    screenManager.push(FuelTypeSelectScreen(carContext) {
                        loadData()
                    })
                }
                .build()
        )

        // Action 2: Sort Mode Toggle
        actionStripBuilder.addAction(
            Action.Builder()
                .setTitle(if (currentSortMode == SortMode.PRICE) "CHF Günstigste" else "📍 Nächste")
                .setOnClickListener {
                    val nextSortMode = if (currentSortMode == SortMode.PRICE) SortMode.DISTANCE else SortMode.PRICE
                    preferenceRepository.setSortMode(nextSortMode)
                    invalidate()
                }
                .build()
        )

        val templateBuilder = PlaceListMapTemplate.Builder()
            .setTitle("Tankstellen")
            .setHeaderAction(Action.APP_ICON)
            .setActionStrip(actionStripBuilder.build())

        if (isLoading) {
            templateBuilder.setLoading(true)
            return templateBuilder.build()
        }

        val sortedStations = sortStations(stations, currentSortMode)
        val itemListBuilder = ItemList.Builder()

        if (sortedStations.isEmpty()) {
            itemListBuilder.setNoItemsMessage(
                errorMessage ?: "Keine Tankstellen in diesem Bereich gefunden."
            )
        } else {
            // Max 6 items recommended for Car App POI lists
            val displayList = sortedStations.take(6)

            displayList.forEach { station ->
                val distKm = locationHelper.calculateDistanceKm(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    station.latitude,
                    station.longitude
                )

                val name = station.displayName ?: station.brand ?: "Tankstelle"
                val priceFormatted = if (station.price != null && station.price > 0) {
                    "CHF ${String.format(Locale.GERMANY, "%.2f", station.price)}"
                } else {
                    "Preis k.A."
                }

                // Line 1: Price and Distance (with DistanceSpan)
                val distanceObj = if (distKm < 1.0) {
                    Distance.create((distKm * 1000).toInt().coerceAtLeast(1).toDouble(), Distance.UNIT_METERS)
                } else {
                    Distance.create(distKm, Distance.UNIT_KILOMETERS_P1)
                }
                val distanceSpan = DistanceSpan.create(distanceObj)

                val distancePlaceholder = "0 km"
                val line1Spannable = SpannableString("$priceFormatted  •  $distancePlaceholder").apply {
                    val start = length - distancePlaceholder.length
                    val end = length
                    setSpan(distanceSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                }

                // Line 2: Reliability Label
                val line2 = station.getReliabilityLabel()

                val brandKey = station.brand?.trim()?.lowercase() ?: ""
                val icon = brandIconsMap[brandKey] ?: brandIconLoader.fallbackIcon

                // Place marker for map: Set icon on PlaceMarker instead of Row.setImage()
                val carLocation = CarLocation.create(station.latitude, station.longitude)
                val markerBuilder = PlaceMarker.Builder()
                    .setIcon(icon, PlaceMarker.TYPE_ICON)

                if (station.isCheapest == true) {
                    markerBuilder.setColor(CarColor.GREEN)
                }

                val place = Place.Builder(carLocation)
                    .setMarker(markerBuilder.build())
                    .build()

                val row = Row.Builder()
                    .setTitle(name)
                    .addText(line1Spannable)
                    .addText(line2)
                    .setMetadata(Metadata.Builder().setPlace(place).build())
                    .setOnClickListener {
                        screenManager.push(StationDetailScreen(carContext, station.id))
                    }
                    .build()

                itemListBuilder.addItem(row)
            }
        }

        templateBuilder.setItemList(itemListBuilder.build())
        return templateBuilder.build()
    }

    private fun sortStations(list: List<StationBboxItem>, mode: SortMode): List<StationBboxItem> {
        return when (mode) {
            SortMode.PRICE -> list.sortedBy { it.price ?: Double.MAX_VALUE }
            SortMode.DISTANCE -> list.sortedBy {
                locationHelper.calculateDistanceKm(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    it.latitude,
                    it.longitude
                )
            }
        }
    }
}