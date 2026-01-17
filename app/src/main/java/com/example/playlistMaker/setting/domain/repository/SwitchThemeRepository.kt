package com.example.playlistMaker.setting.domain.repository

import com.example.playlistMaker.setting.domain.model.SwitchTheme

interface SwitchThemeRepository {
    fun saveSwitchTheme(setting: SwitchTheme)
    fun getSwitchTheme(): SwitchTheme
}
