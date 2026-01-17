package com.example.playlistMaker.mediaLibrary.di

import com.example.playlistMaker.mediaLibrary.data.mapper.TrackDbConvertor
import com.example.playlistMaker.mediaLibrary.data.repositoryImpl.FavoriteTracksRepositoryImpl
import com.example.playlistMaker.mediaLibrary.domain.interactorImpl.FavoriteTrackInteractorImpl
import com.example.playlistMaker.mediaLibrary.domain.interactor.FavoriteTrackInteractor
import com.example.playlistMaker.mediaLibrary.domain.repository.FavoriteTracksRepository
import org.koin.dsl.module

val repositoryModule = module {

    factory { TrackDbConvertor() }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }
    single<FavoriteTrackInteractor> { FavoriteTrackInteractorImpl(get()) }
}
