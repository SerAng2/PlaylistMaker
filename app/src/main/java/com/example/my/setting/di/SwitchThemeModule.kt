package com.example.my.setting.di

import android.content.Context
import com.example.my.setting.data.repository.SwitchThemeRepositoryImpl
import com.example.my.setting.domain.repository.SwitchThemeRepository
import com.example.my.setting.domain.use_case.SwitchThemeUseCase
import com.example.my.setting.domain.use_case.impl.SwitchThemeUseCaseImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val switchThemeModule = module {
    single { androidContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE) }
    single<SwitchThemeRepository> { SwitchThemeRepositoryImpl(get(), get()) }
    single<SwitchThemeUseCase> { SwitchThemeUseCaseImpl (get()) }
}
