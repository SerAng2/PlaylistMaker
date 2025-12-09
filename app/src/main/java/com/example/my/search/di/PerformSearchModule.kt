package com.example.my.search.di

import com.example.my.search.data.repository.PerformSearchRepositoryImpl
import com.example.my.search.domain.repository.PerformSearchRepository
import com.example.my.search.domain.use_case.PerformSearchUseCase
import com.example.my.search.domain.use_case.impl.PerformSearchUseCaseImpl
import org.koin.dsl.module

val performSearchModule = module {
    factory<PerformSearchRepository> { PerformSearchRepositoryImpl() }
    single<PerformSearchUseCase> { PerformSearchUseCaseImpl(get()) }
}
