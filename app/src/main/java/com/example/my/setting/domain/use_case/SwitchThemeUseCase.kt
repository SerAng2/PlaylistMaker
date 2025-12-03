package com.example.my.setting.domain.use_case

import com.example.my.setting.domain.model.SwitchTheme

interface SwitchThemeUseCase {
    fun switchTheme(darkThemeEnabled: Boolean): SwitchTheme
    fun getCurrentTheme(): SwitchTheme
}