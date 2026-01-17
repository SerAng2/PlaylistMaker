package com.example.playlistMaker.search.di

import android.util.Log
import androidx.room.Room
import com.example.playlistMaker.mediaLibrary.data.db.AppDatabase
import com.example.playlistMaker.search.data.repositoryImpl.PerformSearchRepositoryImpl
import com.example.playlistMaker.search.domain.repository.PerformSearchRepository
import com.example.playlistMaker.search.domain.use_case.PerformSearchUseCase
import com.example.playlistMaker.search.domain.use_case.impl.PerformSearchUseCaseImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val performSearchModule = module {
    single {
        Log.e("Room", "0")
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    single {
        Log.e("AppDatabase", "1")
        get<AppDatabase>().trackDao() }

    factory<PerformSearchRepository> {
        Log.e("PerformSearchRepository", "2")
        PerformSearchRepositoryImpl(get()) }

    single<PerformSearchUseCase> {
        Log.e("PerformSearchUseCase", "3")
        PerformSearchUseCaseImpl(get()) }
}
