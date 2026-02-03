package com.example.playlistMaker.mediaLibrary.di

import com.example.playlistMaker.mediaLibrary.data.repositoryImpl.FavoriteTracksRepositoryImpl
import com.example.playlistMaker.mediaLibrary.data.repositoryImpl.PlaylistRepositoryImpl
import com.example.playlistMaker.mediaLibrary.domain.use_case.CreatePlaylistUseCase
import com.example.playlistMaker.mediaLibrary.domain.interactor.FavoriteTrackInteractor
import com.example.playlistMaker.mediaLibrary.domain.interactor.PlaylistInteractor
import com.example.playlistMaker.mediaLibrary.domain.interactorImpl.FavoriteTrackInteractorImpl
import com.example.playlistMaker.mediaLibrary.domain.interactorImpl.PlaylistInteractorImpl
import com.example.playlistMaker.mediaLibrary.domain.repository.FavoriteTracksRepository
import com.example.playlistMaker.mediaLibrary.domain.repository.PlaylistRepository
import org.koin.dsl.module

val mediaLibraryDomainModule = module {

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }
    single<FavoriteTrackInteractor> { FavoriteTrackInteractorImpl(get()) }
    single<PlaylistInteractor> {PlaylistInteractorImpl(get())
    }
    single<PlaylistRepository> { PlaylistRepositoryImpl(get(), get(), get()) }
    single { CreatePlaylistUseCase(get<PlaylistRepository>()) }
}
