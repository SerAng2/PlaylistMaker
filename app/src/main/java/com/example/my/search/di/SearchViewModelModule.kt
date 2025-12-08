package com.example.my.search.di

import com.example.my.search.presentation.view_model.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val searchViewModelModule = module {
    viewModel {
        SearchViewModel(
            get(), get()
        )
    }
}
