package com.example.my.setting.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.my.creator.Creator
import com.example.my.setting.di.appModule
import com.example.my.setting.domain.model.SwitchTheme
import com.example.my.setting.ui.di.viewModelModule
import org.koin.android.ext.koin.androidContext  // Новый импорт
import org.koin.core.context.startKoin

class App : Application() {

    private val switchThemeUseCase by lazy { Creator.provideSwitchThemeUseCase() }
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        Creator.init(this)

        startKoin {
            androidContext(this@App)
            modules(appModule, viewModelModule)
        }

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