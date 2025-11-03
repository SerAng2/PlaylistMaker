package com.example.my

import android.content.Context
import android.content.SharedPreferences
import com.example.my.data.repository.PerformSearchRepositoryImpl
import com.example.my.data.repository.SearchHistoryRepositoryImpl
import com.example.my.data.repository.SupportRepositoryImpl
import com.example.my.domain.repository.PerformSearchRepository
import com.example.my.domain.repository.SearchHistoryRepository
import com.example.my.domain.repository.SupportRepository
import com.example.my.domain.usecase.GetPerformSearchUseCase
import com.example.my.domain.usecase.GetSearchHistoryUseCase
import com.example.my.domain.usecase.GetSupportUseCase

class Creator(private val context: Context) {
    val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    private val searchHistoryRepository: SearchHistoryRepository by lazy {
        SearchHistoryRepositoryImpl(sharedPreferences)
    }

    val getSearchHistoryUseCase: GetSearchHistoryUseCase by lazy {
        GetSearchHistoryUseCase(searchHistoryRepository)
    }

    private fun provideSupportRepository() : SupportRepository {
        return SupportRepositoryImpl()
    }

    fun provideSupportUseCase() : GetSupportUseCase {
        return GetSupportUseCase(provideSupportRepository())
    }

   private fun performSearchRepository() : PerformSearchRepository {
        return PerformSearchRepositoryImpl()
    }

    fun provideGetPerformSearchUseCase(): GetPerformSearchUseCase {
        return GetPerformSearchUseCase(performSearchRepository())
    }
}
