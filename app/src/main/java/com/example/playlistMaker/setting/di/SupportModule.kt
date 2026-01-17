package com.example.playlistMaker.setting.di

import com.example.playlistMaker.setting.data.impl.SupportNavigatorImpl
import com.example.playlistMaker.setting.data.impl.SupportStringsRepositoryImpl
import com.example.playlistMaker.setting.domain.impl.SupportInteractorImpl
import com.example.playlistMaker.setting.domain.interactor.SupportInteractor
import com.example.playlistMaker.setting.domain.interactor.SupportNavigator
import com.example.playlistMaker.setting.domain.interactor.SupportStringsRepository
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
