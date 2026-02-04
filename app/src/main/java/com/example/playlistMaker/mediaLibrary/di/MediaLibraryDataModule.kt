package com.example.playlistMaker.mediaLibrary.di

import androidx.room.Room
import com.example.playlistMaker.mediaLibrary.data.repositoryImpl.FileManagerRepositoryImpl
import com.example.playlistMaker.mediaLibrary.data.db.AppDatabase
import com.example.playlistMaker.mediaLibrary.data.db.PlaylistDatabase
import com.example.playlistMaker.mediaLibrary.data.db.PlaylistTrackDatabase
import com.example.playlistMaker.mediaLibrary.data.mapper.TrackDbConvertor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val mediaLibraryDataModule = module {

    factory { TrackDbConvertor() }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "app_database")
            .build()
    }
    single { get<AppDatabase>().trackDao() }

    single {
        Room.databaseBuilder(androidContext(), PlaylistDatabase::class.java, "playlist_database")
            .build()
    }
    single { get<PlaylistDatabase>().playlistDao() }

    single {
        Room.databaseBuilder(androidContext(), PlaylistTrackDatabase::class.java, "playlist_track_database")
        .build()
    }
    single { get<PlaylistTrackDatabase>().playlistTrackDao() }


}
