package com.example.my.setting.domain.use_case

import com.example.my.setting.domain.model.SwitchTheme
import com.example.my.setting.domain.repository.SwitchThemeRepository

class SwitchThemeUseCase(private val repository: SwitchThemeRepository) {

    fun switchTheme(darkThemeEnabled: Boolean): SwitchTheme {
        val setting = SwitchTheme(isDarkTheme = darkThemeEnabled)
        repository.saveSwitchTheme(setting)
        return setting
    }

    fun getCurrentTheme(): SwitchTheme {
        return repository.getSwitchTheme()
    }
}