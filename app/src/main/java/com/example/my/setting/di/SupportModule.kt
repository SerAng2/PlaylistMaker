package com.example.my.setting.di

import com.example.my.setting.data.impl.SupportNavigatorImpl
import com.example.my.setting.data.impl.SupportStringsRepositoryImpl
import com.example.my.setting.domain.impl.SupportInteractorImpl
import com.example.my.setting.domain.interactor.SupportInteractor
import com.example.my.setting.domain.interactor.SupportNavigator
import com.example.my.setting.domain.interactor.SupportStringsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val supportModule = module {

    single<SupportStringsRepository> { SupportStringsRepositoryImpl(androidContext()) }

    single<SupportNavigator> {
        SupportNavigatorImpl(
            androidContext(),
            get()
        )
    }

    single<SupportInteractor> { SupportInteractorImpl(get()) }
}
