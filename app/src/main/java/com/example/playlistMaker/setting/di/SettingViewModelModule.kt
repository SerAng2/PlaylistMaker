package com.example.playlistMaker.setting.di

import com.example.playlistMaker.setting.presentation.view_model.SettingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingViewModelModule = module {
    viewModel {
        SettingViewModel(
            get(), get()
        )
    }
}
