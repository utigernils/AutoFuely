package com.example.autofuely.car

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session

class FuelCarSession : Session() {
    override fun onCreateScreen(intent: Intent): Screen {
        return MainMapScreen(carContext)
    }
}