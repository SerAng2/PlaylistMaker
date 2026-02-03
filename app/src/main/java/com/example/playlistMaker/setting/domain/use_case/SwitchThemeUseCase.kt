package com.example.playlistMaker.setting.domain.use_case

import com.example.playlistMaker.setting.domain.model.SwitchTheme

interface SwitchThemeUseCase {
    fun switchTheme(darkThemeEnabled: Boolean): SwitchTheme
    fun getCurrentTheme(): SwitchTheme
}
