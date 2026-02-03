package com.example.playlistMaker.search.di

import android.content.Context
import com.example.playlistMaker.search.data.repositoryImpl.SearchHistoryRepositoryImpl
import com.example.playlistMaker.search.domain.interactorImpl.SearchHistoryInteractorImpl
import com.example.playlistMaker.search.domain.interactor.SearchHistoryInteractor
import com.example.playlistMaker.search.domain.repository.HistoryRepository
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val historyRepositoryModule = module {
    single { androidContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE) }
    single { Gson() }
    factory<HistoryRepository> { SearchHistoryRepositoryImpl(get(), get()) }
    factory<SearchHistoryInteractor> { SearchHistoryInteractorImpl(get()) }
}
