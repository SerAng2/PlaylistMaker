package com.example.my

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    var darkTheme = false
        private set

    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        darkTheme = prefs.getBoolean("darkTheme", false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("darkTheme", darkThemeEnabled).apply()
    }
}
