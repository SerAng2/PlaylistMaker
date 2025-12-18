package com.example.my.setting.domain.use_case.impl

import com.example.my.setting.domain.model.SwitchTheme
import com.example.my.setting.domain.repository.SwitchThemeRepository
import com.example.my.setting.domain.use_case.SwitchThemeUseCase

class SwitchThemeUseCaseImpl(private val repository: SwitchThemeRepository) : SwitchThemeUseCase {

    override fun switchTheme(darkThemeEnabled: Boolean): SwitchTheme {
        val setting = SwitchTheme(isDarkTheme = darkThemeEnabled)
        repository.saveSwitchTheme(setting)
        return setting
    }

    override fun getCurrentTheme(): SwitchTheme {
        return repository.getSwitchTheme()
    }
}
