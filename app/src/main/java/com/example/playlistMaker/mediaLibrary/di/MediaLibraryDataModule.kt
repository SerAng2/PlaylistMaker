package com.example.playlistMaker.mediaLibrary.di

import androidx.room.Room
import com.example.playlistMaker.mediaLibrary.data.db.AppDatabase
import com.example.playlistMaker.mediaLibrary.data.mapper.TrackDbConvertor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val mediaLibraryDataModule = module {

    factory { TrackDbConvertor() }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "app_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().trackDao() }
    single { get<AppDatabase>().tracksDao() }
    single { get<AppDatabase>().playlistDao() }
    single { get<AppDatabase>().playlistTrackDao() }

    }
