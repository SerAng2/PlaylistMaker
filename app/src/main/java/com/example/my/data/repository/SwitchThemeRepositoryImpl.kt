package com.example.my.data.repository

import android.content.SharedPreferences
import com.example.my.domain.models.SwitchTheme
import com.example.my.domain.repository.SwitchThemeRepository
import com.google.gson.Gson
import kotlin.jvm.java

class SwitchThemeRepositoryImpl(
    private val sharedPreferences: SharedPreferences?,
    private val gson: Gson?
    ) : SwitchThemeRepository {

    override fun saveSwitchTheme(settings: SwitchTheme) {
        val json = gson?.toJson(settings)
        sharedPreferences?.edit()?.putString("theme_settings", json)?.apply()
    }

    override fun getSwitchTheme(): SwitchTheme {
        val json = sharedPreferences?.getString("theme_settings", null)
        return json?.let { gson?.fromJson(it, SwitchTheme::class.java) } ?: SwitchTheme()
    }
}