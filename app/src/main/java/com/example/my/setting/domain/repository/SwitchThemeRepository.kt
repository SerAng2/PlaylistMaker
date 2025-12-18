package com.example.my.setting.domain.repository

import com.example.my.setting.domain.model.SwitchTheme

interface SwitchThemeRepository {
    fun saveSwitchTheme(setting: SwitchTheme)
    fun getSwitchTheme(): SwitchTheme
}
