package com.example.playlistMaker.setting.di

import android.content.Context
import com.example.playlistMaker.setting.data.repository.SwitchThemeRepositoryImpl
import com.example.playlistMaker.setting.domain.repository.SwitchThemeRepository
import com.example.playlistMaker.setting.domain.use_case.SwitchThemeUseCase
import com.example.playlistMaker.setting.domain.use_case.impl.SwitchThemeUseCaseImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val switchThemeModule = module {
    single { androidContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE) }
    single<SwitchThemeRepository> { SwitchThemeRepositoryImpl(get(), get()) }
    single<SwitchThemeUseCase> { SwitchThemeUseCaseImpl (get()) }
}
