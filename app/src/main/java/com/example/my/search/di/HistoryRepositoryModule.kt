package com.example.my.search.di

import android.content.Context
import com.example.my.search.data.repository.SearchHistoryRepositoryImpl
import com.example.my.search.domain.impl.SearchHistoryInteractorImpl
import com.example.my.search.domain.interactor.SearchHistoryInteractor
import com.example.my.search.domain.repository.HistoryRepository
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val historyRepositoryModule = module {
    single { androidContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE) }
    single { Gson() }
    factory<HistoryRepository> { SearchHistoryRepositoryImpl(get(), get()) }
    factory<SearchHistoryInteractor> { SearchHistoryInteractorImpl(get()) }
}
