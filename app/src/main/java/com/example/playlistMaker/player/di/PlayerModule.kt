package com.example.playlistMaker.player.di

import android.media.MediaPlayer
import com.example.playlistMaker.mediaLibrary.domain.interactor.FavoriteTrackInteractor
import com.example.playlistMaker.mediaLibrary.domain.interactor.PlaylistInteractor
import com.example.playlistMaker.player.presentation.view_model.PlayerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    factory { MediaPlayer() }
    viewModel { PlayerViewModel(
        get<MediaPlayer>(),
        get<FavoriteTrackInteractor>(),
        get<PlaylistInteractor>()) }
}
