package com.example.my.setting.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.my.creator.Creator

class SettingViewModel() : ViewModel() {

    fun shareApp() = Creator.provideSupportInteractor().shareApp()

    fun contactSupport() = Creator.provideSupportInteractor().contactSupport()
    fun openUserAgreement() = Creator.provideSupportInteractor().openUserAgreement()

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingViewModel()
            }
        }
    }
}