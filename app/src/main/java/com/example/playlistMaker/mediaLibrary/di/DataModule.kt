package com.example.playlistMaker.mediaLibrary.di

import androidx.room.Room
import com.example.playlistMaker.mediaLibrary.data.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "app_database")
            .build()
    }

    single { get<AppDatabase>().trackDao() }
}
