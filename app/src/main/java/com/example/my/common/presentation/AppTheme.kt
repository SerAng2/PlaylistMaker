package com.example.my.common.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.my.mediaLibrary.di.mediaLibraryViewModelModule
import com.example.my.player.di.playerModule
import com.example.my.search.di.historyRepositoryModule
import com.example.my.search.di.performSearchModule
import com.example.my.search.di.searchViewModelModule
import com.example.my.setting.di.settingViewModelModule
import com.example.my.setting.di.supportModule
import com.example.my.setting.di.switchThemeModule
import com.example.my.setting.domain.model.SwitchTheme
import com.example.my.setting.domain.use_case.SwitchThemeUseCase
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppTheme() : Application() {

    var darkTheme = false
    private val switchTheme: SwitchThemeUseCase by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@AppTheme)
            modules(
                supportModule,
                settingViewModelModule,
                historyRepositoryModule,
                performSearchModule,
                searchViewModelModule,
                switchThemeModule,
                playerModule,
                mediaLibraryViewModelModule
            )
        }

        val currentSettings: SwitchTheme = switchTheme.getCurrentTheme()
        darkTheme = currentSettings.isDarkTheme
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        switchTheme.switchTheme(darkThemeEnabled)

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}