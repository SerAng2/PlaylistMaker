package com.example.my.setting.di

import com.example.my.setting.presentation.view_model.SettingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingViewModelModule = module {
    viewModel {
        SettingViewModel(
            get(), get()
        )
    }
}
