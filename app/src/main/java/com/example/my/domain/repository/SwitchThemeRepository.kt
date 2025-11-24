package com.example.my.domain.repository

import com.example.my.domain.models.SwitchTheme

interface SwitchThemeRepository {
    fun saveSwitchTheme(setting: SwitchTheme)
    fun getSwitchTheme(): SwitchTheme
}