package com.example.my.player.di

import android.media.MediaPlayer
import com.example.my.player.presentation.view_model.PlayerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    factory { MediaPlayer() }
    viewModel { PlayerViewModel(get()) }
}
