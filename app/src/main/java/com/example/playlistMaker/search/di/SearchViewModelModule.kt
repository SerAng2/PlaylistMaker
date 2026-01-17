package com.example.playlistMaker.search.di

import com.example.playlistMaker.search.presentation.view_model.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val searchViewModelModule = module {
    viewModel {
        SearchViewModel(
            get(), get()
        )
    }
}
