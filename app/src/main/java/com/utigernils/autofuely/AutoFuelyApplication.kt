package com.utigernils.autofuely

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.utigernils.autofuely.data.repository.PreferenceRepository

class AutoFuelyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val preferenceRepository = PreferenceRepository(this)
        when (preferenceRepository.getThemeMode()) {
            PreferenceRepository.THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            PreferenceRepository.THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}
