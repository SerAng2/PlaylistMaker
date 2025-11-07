package com.example.my.presentation

import android.content.Context
import android.content.SharedPreferences
import com.example.my.data.repository.PerformSearchRepositoryImpl
import com.example.my.domain.impl.SupportInteractorImpl
import com.example.my.domain.impl.SearchHistoryInteractorImpl
import com.example.my.domain.repository.PerformSearchRepository
import com.example.my.domain.interactor.SearchHistoryInteractor
import com.example.my.domain.interactor.SupportInteractor
import com.example.my.domain.usecase.GetPerformSearchUseCase
import com.example.my.domain.usecase.GetSearchHistoryUseCase
import com.example.my.domain.usecase.GetSupportUseCase

object  Creator {
    lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }
    val sharedPreferences: SharedPreferences by lazy {
        appContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    private val searchHistoryInteractor: SearchHistoryInteractor by lazy {
        SearchHistoryInteractorImpl(sharedPreferences)
    }

    val getSearchHistoryUseCase: GetSearchHistoryUseCase by lazy {
        GetSearchHistoryUseCase(searchHistoryInteractor)
    }

    private fun supportRepository() : SupportInteractor {
        return SupportInteractorImpl(appContext)
    }

    fun provideSupportUseCase() : GetSupportUseCase {
        return GetSupportUseCase(supportRepository())
    }

   private fun performSearchRepository() : PerformSearchRepository {
        return PerformSearchRepositoryImpl()
    }

    fun provideGetPerformSearchUseCase(): GetPerformSearchUseCase {
        return GetPerformSearchUseCase(performSearchRepository())
    }
}