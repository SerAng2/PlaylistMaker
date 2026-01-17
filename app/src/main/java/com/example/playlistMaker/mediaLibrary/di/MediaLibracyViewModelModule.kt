package com.example.playlistMaker.mediaLibrary.di

import com.example.playlistMaker.mediaLibrary.presentation.view_model.FavoriteTracksViewModel
import com.example.playlistMaker.mediaLibrary.presentation.view_model.MediaLibraryViewModel
import com.example.playlistMaker.mediaLibrary.presentation.view_model.PlaylistsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val mediaLibraryViewModelModule = module {
    viewModel { MediaLibraryViewModel() }
    viewModel { FavoriteTracksViewModel(get()) }
    viewModel { PlaylistsViewModel() }
}