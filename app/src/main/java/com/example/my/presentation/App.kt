package com.example.my.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.my.domain.models.SwitchTheme
import com.example.my.presentation.Creator.provideSwitchThemeUseCase

class App : Application() {

    private val switchThemeUseCase by lazy { provideSwitchThemeUseCase() }
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        Creator.init(this)

        val currentSettings: SwitchTheme = switchThemeUseCase.getCurrentTheme()
        darkTheme = currentSettings.isDarkTheme
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        switchThemeUseCase.switchTheme(darkThemeEnabled)

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
