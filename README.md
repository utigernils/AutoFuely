# AutoFuely 

**AutoFuely** is a Proof-of-Concept (PoC) **Android Auto Point-of-Interest (POI) App** designed to find and display the cheapest nearby gas station prices across Switzerland in real-time.

Powered by official TCS Benzinpreis API data, **AutoFuely** seamlessly integrates into your car's display via the official `androidx.car.app` library, allowing drivers to quickly compare fuel prices, view data freshness, and launch turn-by-turn navigation directly from the vehicle head unit.

> [!NOTE]  
> Fuely is currently only available in German.

---

## Screenshots

Here is a preview of the app in action on both Android Auto and the Smartphone companion app:

| Android Auto UI (Car Display) | Smartphone Companion App |
| :---: | :---: |
| <img width="800" height="480" alt="Screenshot 2026-09-02 193856" src="https://github.com/user-attachments/assets/0531e8f0-3400-4ac1-a7bd-ab3e0e1b4e59" />| <img width="569" height="1271" alt="image" src="https://github.com/user-attachments/assets/4e8a1972-05d7-4c0c-bb1a-e56452681b34" />|

---

## Features

### Android Auto Experience
* **Interactive Map & Station List (`PlaceListMapTemplate`):** Displays nearby gas stations on the map alongside a sorted list.
* **Full-Color Brand Logos:** Custom map pins featuring full-color brand logos (Agrola, BP, Shell, Avia, Eni, Migrol, Coop, etc.).
* **Vehicle Location Anchor:** Displays a prominent **Blue Dot Marker** on the map indicating your car's exact GPS location.
* **Live Update Time & Freshness Indicators:** Displays relative update timestamps (*"vor 15 Min."*, *"vor 2 Std."*, *"vor 1 Tag"*) with dynamic color coding:
  * 🟢 **Green:** Updated within the last 24 hours.
  * 🟡 **Yellow:** Updated 24 to 48 hours ago.
  * 🔴 **Red:** Updated over 48 hours ago or outdated.
* **1-Minute Automatic Background Refresh:** Automatically updates gas station prices every 60 seconds while open on the car display.
* **Sorting Toggle:** One-tap toggle in the header action bar to switch sorting between **Günstigste** (price ascending) and **Nächste** (distance ascending).
* **Detailed Station Info (`PaneTemplate`):** Tapping any station reveals full details: formatted address, prices for available fuel types (`DIESEL`, `SP95`, `SP98`), and TCS Mastercard Cashback availability.
* **"Navigation starten" Button:** One-click action launching turn-by-turn navigation directly in Google Maps or Waze.
* **In-Car Settings Menu:** Access app settings (Fuel type, Search radius, Price filter) via the in-car cog wheel button.

### Smartphone Companion App
* **Sleek Dark Theme:** Material 3 UI styled to match the dark automotive aesthetic.
* **Search Area (Bounding Box) Selector:** Custom dropdown allowing drivers to adjust the search radius edge length (**3 km**, **5 km**, **8 km**, **10 km**, **15 km**, **20 km**).
* **Fuel Type Selector:** Set your preferred fuel type (**Diesel**, **Bleifrei 95**, **Bleifrei 98**).
* **No-Price Filter Toggle:** Easily hide gas stations that do not have valid price data.
* **Permission Management:** One-tap location permission manager to grant GPS access required by Android Auto.

---

## How It Works

### Architecture & Data Flow
1. **Location & Bounding Box:** `LocationHelper` retrieves GPS coordinates from Google Play Services Location and computes a dynamic bounding box `[min_lng, min_lat, max_lng, max_lat]` based on the user-selected search radius.
2. **TCS Benzin API Integration:**
   * `POST /benzinGetStationByBbox`: Fetches all active gas stations within the visible map area for the selected fuel type.
   * `POST /benzinGetStationById`: Concurrently prefetches detailed price collections and Firestore update timestamps (`lastPriceUpdate`) for visible stations.
3. **Data Filtering:** Automatically filters out legacy numeric station IDs (e.g. `846`) and stations without price information (when configured).
4. **Android Auto Rendering:** Converts station data into native `PlaceListMapTemplate` and `PaneTemplate` structures with `DistanceSpan` and `ForegroundCarColorSpan` for automotive compliance.

---

## Installation

Pre-built APK releases are available directly on GitHub.

### Step 1: Download the APK
1. Go to the [**GitHub Releases**](../../releases) page of this repository.
2. Download the latest `app-debug.apk` or release build.

### Step 2: Install on Smartphone
1. Open the downloaded `.apk` file on your Android smartphone.
2. If prompted, enable "Install from unknown sources" in your Android security settings.
3. Complete the installation and open **AutoFuely**.
4. Tap **"Standortberechtigung erteile"** in the app to grant GPS permissions.

### Step 3: Enable Android Auto
1. Connect your smartphone to your vehicle via USB cable or wireless Android Auto.
2. If using the Android Auto Emulator / Desktop Head Unit (DHU) or custom APKs, ensure **Developer mode** is enabled in Android Auto settings:
   * Go to **Settings > Android Auto** on your phone.
   * Tap the **Version** number 10 times to unlock Developer Mode.
   * Open the top-right 3-dot menu and check **"Unknown sources"**.
3. Launch **AutoFuely** from your car's launcher menu.

---

## Tech Stack & Dependencies

* **Language:** Kotlin
* **UI Frameworks:** Jetpack Material 3 (Phone App) & `androidx.car.app:app:1.7.0` (Android Auto)
* **Networking:** Retrofit 2 + Gson Converter + OkHttp 4
* **Image Loading:** Coil (`io.coil-kt:coil:2.7.0`)
* **Location:** Google Play Services Location (`com.google.android.gms:play-services-location:21.4.0`)
* **Coroutines:** Kotlin Coroutines Android (`kotlinx-coroutines-android:1.8.1`)

---

## License

This project is open-source under the MIT License. Data provided by TCS Benzinpreis API.
