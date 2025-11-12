package com.example.my.presentation

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.example.my.data.repository.PerformSearchRepositoryImpl
import com.example.my.data.repository.SearchHistoryRepositoryImpl
import com.example.my.data.repository.SwitchThemeRepositoryImpl
import com.example.my.domain.impl.SearchHistoryInteractorImpl
import com.example.my.domain.impl.SupportInteractorImpl
import com.example.my.domain.interactor.SearchHistoryInteractor
import com.example.my.domain.interactor.SupportInteractor
import com.example.my.domain.repository.HistoryRepository
import com.example.my.domain.repository.PerformSearchRepository
import com.example.my.domain.repository.SwitchThemeRepository
import com.example.my.domain.usecase.GetPerformSearchUseCase
import com.example.my.domain.usecase.SwitchThemeUseCase
import com.google.gson.Gson

object Creator {
    lateinit var appContext: Context
    private var gson: Gson? = null
    private var sharedPreferences: SharedPreferences? = null
    private var repository: HistoryRepository? = null
    private var switchThemeRepository: SwitchThemeRepository? = null


    fun init(context: Context) {

        appContext = context.applicationContext
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
        gson = Gson()
        repository = SearchHistoryRepositoryImpl(sharedPreferences, gson)
        switchThemeRepository = SwitchThemeRepositoryImpl(sharedPreferences, gson)
    }

    private fun switchTheme(): SwitchThemeRepository {
        return switchThemeRepository ?: throw IllegalStateException("Call Creator.init() first")
    }

    fun provideSwitchThemeUseCase(): SwitchThemeUseCase {
        return SwitchThemeUseCase(switchTheme())
    }

    private fun historyRepository(): HistoryRepository {
        return repository ?: throw IllegalStateException("Call Creator.init() first")
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(historyRepository())
    }

    fun provideSupportInteractor(): SupportInteractor {
        return SupportInteractorImpl(appContext)
    }

    private fun performSearchRepository(): PerformSearchRepository {
        return PerformSearchRepositoryImpl()
    }

    fun provideGetPerformSearchUseCase(): GetPerformSearchUseCase {
        return GetPerformSearchUseCase(performSearchRepository())
    }
}