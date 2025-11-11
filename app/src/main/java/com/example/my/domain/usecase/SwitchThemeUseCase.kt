package com.example.my.domain.usecase

import com.example.my.domain.models.SwitchTheme
import com.example.my.domain.repository.SwitchThemeRepository

class SwitchThemeUseCase(private val repository: SwitchThemeRepository) {

    fun switchTheme(darkThemeEnabled: Boolean): SwitchTheme {
        val setting = SwitchTheme(isDarkTheme = darkThemeEnabled)
        repository.saveSwitchTheme(setting)
        return setting
    }

    fun getCurrentTheme(): SwitchTheme {
        return  repository.getSwitchTheme()
    }
}