package com.example.my.setting.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.my.creator.Creator
import com.example.my.setting.domain.model.SwitchTheme

class App : Application() {

    private val switchThemeUseCase by lazy { Creator.provideSwitchThemeUseCase() }
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