package com.example.my.creator

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.example.my.search.data.repository.PerformSearchRepositoryImpl
import com.example.my.search.data.repository.SearchHistoryRepositoryImpl
import com.example.my.setting.data.repository.SwitchThemeRepositoryImpl
import com.example.my.search.domain.impl.SearchHistoryInteractorImpl
import com.example.my.setting.domain.impl.SupportInteractorImpl
import com.example.my.search.domain.interactor.SearchHistoryInteractor
import com.example.my.setting.domain.interactor.SupportInteractor
import com.example.my.search.domain.repository.HistoryRepository
import com.example.my.search.domain.repository.PerformSearchRepository
import com.example.my.setting.domain.repository.SwitchThemeRepository
import com.example.my.search.domain.use_case.GetPerformSearchUseCase
import com.example.my.setting.domain.use_case.SwitchThemeUseCase
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