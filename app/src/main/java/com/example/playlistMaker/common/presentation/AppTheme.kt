package com.example.playlistMaker.common.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistMaker.mediaLibrary.di.dataModule
import com.example.playlistMaker.mediaLibrary.di.mediaLibraryViewModelModule
import com.example.playlistMaker.mediaLibrary.di.repositoryModule
import com.example.playlistMaker.player.di.playerModule
import com.example.playlistMaker.search.di.historyRepositoryModule
import com.example.playlistMaker.search.di.performSearchModule
import com.example.playlistMaker.search.di.searchViewModelModule
import com.example.playlistMaker.setting.di.settingViewModelModule
import com.example.playlistMaker.setting.di.supportModule
import com.example.playlistMaker.setting.di.switchThemeModule
import com.example.playlistMaker.setting.domain.model.SwitchTheme
import com.example.playlistMaker.setting.domain.use_case.SwitchThemeUseCase
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
                dataModule,
                repositoryModule,
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